#include "Consumer.hh"

#include "amqp_asio/spawn.hh"
#include "idm/ProcessHandler.hh"
#include "swa/Process.hh"

#include <asio/co_spawn.hpp>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        void Consumer::receive(std::shared_ptr<ServiceHandler> handler) {

            // loop and wait for messages
            asio::co_spawn(
                SWA::Process::getInstance().getIOContext().get_executor(),
                [this, handler]() -> asio::awaitable<void> {
                    co_await proc.isInitialised();
                    receiver = co_await proc.getSession().open_receiver(topic_prefix + topic, amqp_asio::ReceiverOptions().name(getName()));
                    log.debug("Created receiver");
                    amqp_asio::spawn_cancellable_loop(
                        SWA::Process::getInstance().getIOContext().get_executor(),
                        [this, handler]() mutable -> asio::awaitable<void> {
                            // Queue the message to be handled by the event loop
                            auto delivery = co_await receiver.receive();
                            log.debug("Received message {}", delivery.message().as_string());
                            amqp_asio::Delivery msg(delivery);
                            messageQueue.push(std::move(msg));
                            SWA::Process::getInstance().getIOContext().post(
                                SWA::Process::getInstance().wrapProcessingThread(
                                    "idm.activemq." + SWA::Process::getInstance().getName() + ".receiver." + topic + ".message", [this, handler]() {
                                        // drain the message queue
                                        while (!messageQueue.empty()) {
                                            auto msg = std::move(messageQueue.front());
                                            messageQueue.pop();

                                            // get the service invoker
                                            Callable service = handler->getInvoker(msg.message().as_string());

                                            // run the service
                                            service();

                                            // accept delivery
                                            asio::co_spawn(SWA::Process::getInstance().getIOContext().get_executor(), msg.accept(), [](std::exception_ptr eptr) {
                                                if (eptr) {
                                                    std::rethrow_exception(eptr);
                                                }
                                            });
                                        }
                                    }
                                )
                            );
                        },
                        log
                    );
                    log.debug("Listening for messages");
                    initialised = true;
                    initialisedCond.notify();
                },
                [](std::exception_ptr eptr) {
                    if (eptr) {
                        std::rethrow_exception(eptr);
                    }
                }
            );
        }

        void Consumer::setConfig(std::string paramName, int paramValue) {
            if (paramName == "auto_credit_high_water" || paramName == "auto_credit_low_water" || paramName == "credit") {
                asio::co_spawn(
                    SWA::Process::getInstance().getIOContext().get_executor(),
                    [this, paramName, paramValue]() -> asio::awaitable<void> {
                        co_await isInitialised();
                        if (paramName == "auto_credit_high_water") {
                            co_await receiver.auto_credit_limits(-1, paramValue);
                        } else if (paramName == "auto_credit_low_water") {
                            co_await receiver.auto_credit_limits(paramValue, -1);
                        } else if (paramName == "credit") {
                            co_await receiver.set_credit(paramValue);
                        }
                    },
                    asio::detached
                );
            }
        }

        void Consumer::setConfig(std::string paramName, bool paramValue) {
            if (paramName == "auto_credit") {
                asio::co_spawn(
                    SWA::Process::getInstance().getIOContext().get_executor(),
                    [this, paramValue]() -> asio::awaitable<void> {
                        co_await isInitialised();
                        if (paramValue) {
                            co_await receiver.start_auto_credit();
                        } else {
                            co_await receiver.stop_auto_credit();
                        }
                    },
                    asio::detached
                );
            }
        }
    } // namespace ActiveMQ

} // namespace InterDomainMessaging
