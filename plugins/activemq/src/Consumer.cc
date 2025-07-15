#include "Consumer.hh"

#include "amqp_asio/delivery.hh"
#include "amqp_asio/spawn.hh"
#include "idm/ProcessHandler.hh"
#include "swa/CommandLine.hh"
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
                    // get receiver options
                    bool disableAutoCredit = SWA::CommandLine::getInstance().optionPresent(DisableAutoCreditOption);
                    int autoCreditHighWater = SWA::CommandLine::getInstance().getIntOption(AutoCreditHighWaterOption, 10);
                    int autoCreditLowWater = SWA::CommandLine::getInstance().getIntOption(AutoCreditLowWaterOption, 5);
                    int initialCredit = SWA::CommandLine::getInstance().getIntOption(InitialCreditOption, 0);
                    receiver = co_await proc.getSession().open_receiver(
                        topic_prefix + topic,
                        amqp_asio::ReceiverOptions()
                            .name(getName())
                            .auto_credit(!disableAutoCredit)
                            .auto_credit_low_water(autoCreditLowWater)
                            .auto_credit_high_water(autoCreditHighWater)
                            .initial_credit(initialCredit)
                    );
                    log.debug("Created receiver");
                    amqp_asio::spawn_cancellable_loop(
                        SWA::Process::getInstance().getIOContext().get_executor(),
                        [this, handler]() mutable -> asio::awaitable<void> {
                            auto delivery = co_await receiver.receive();
                            log.debug("Received message {}", delivery.message().as_string());
                            SWA::Process::getInstance().getIOContext().post(
                                SWA::Process::getInstance().wrapProcessingThread(
                                    "idm.activemq." + SWA::Process::getInstance().getName() + ".receiver." + topic + ".message", [this, handler, delivery]() {
                                        amqp_asio::Delivery msg(delivery);
                                        Callable service = handler->getInvoker(msg.message().as_string());
                                        service();
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

        void Consumer::setProperty(const std::string &name, int value) {
            asio::co_spawn(
                SWA::Process::getInstance().getIOContext().get_executor(),
                [name, self = self(), value]() mutable -> asio::awaitable<void> {
                    co_await self->isInitialised();
                    if (name == "auto_credit_high_water") {
                        co_await self->receiver.auto_credit_high_water(value);
                    } else if (name == "auto_credit_low_water") {
                        co_await self->receiver.auto_credit_high_water(value);
                    } else if (name == "credit") {
                        co_await self->receiver.set_credit(value);
                    }
                },
                asio::detached
            );
        }

        void Consumer::setProperty(const std::string &name, bool value) {
            asio::co_spawn(
                SWA::Process::getInstance().getIOContext().get_executor(),
                [name, self = self(), value]() mutable -> asio::awaitable<void> {
                    if (name == "auto_credit") {
                        co_await self->isInitialised();
                        if (value) {
                            co_await self->receiver.start_auto_credit();
                        } else {
                            co_await self->receiver.stop_auto_credit();
                        }
                    }
                },
                asio::detached
            );
        }
    } // namespace ActiveMQ

} // namespace InterDomainMessaging
