#include "Consumer.hh"

#include "amqp_asio/delivery.hh"
#include "amqp_asio/spawn.hh"
#include "idm/ProcessHandler.hh"
#include "swa/Process.hh"

#include <asio/co_spawn.hpp>
#include <asio/detached.hpp>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        void Consumer::receive(std::shared_ptr<ServiceHandler> handler) {

            // create a signal listener
            listener = std::make_unique<SWA::RealTimeSignalListener>(
                [this, handler](int pid, int uid) {
                    //  // drain the message queue
                    if (!messageQueue.empty()) {
                        auto msgs = messageQueue.dequeue_all();
                        for (auto it = msgs.begin(); it != msgs.end(); it++) {
                            auto msg = std::move(*it);

                            // get the service invoker
                            Callable service = handler->getInvoker(msg.message().as_string());

                            // run the service
                            service();

                            // accept delivery
                            auto executor = proc.getContext().get_executor();
                            asio::co_spawn(
                                executor,
                                [msg]() mutable -> asio::awaitable<void> {
                                    co_await msg.accept();
                                },
                                asio::detached
                            );
                        }
                    }
                },
                SWA::Process::getInstance().getActivityMonitor()
            );
            log.debug("Created listener");

            // loop and wait for messages
            auto executor = proc.getContext().get_executor();
            asio::co_spawn(
                executor,
                [this, executor]() -> asio::awaitable<void> {
                    co_await proc.isInitialised();
                    auto receiver = co_await proc.getSession().open_receiver(topic, amqp_asio::ReceiverOptions().name(getName()));
                    log.debug("Created receiver");
                    amqp_asio::spawn_cancellable_loop(
                        executor,
                        [this, receiver]() mutable -> asio::awaitable<void> {
                            // Queue the message to be handled on the main thread
                            auto delivery = co_await receiver.receive();
                            log.debug("Received message {}", delivery.message().as_string());
                            amqp_asio::Delivery msg(delivery);
                            messageQueue.enqueue(msg);
                            listener->queueSignal();
                        },
                        log
                    );
                    log.debug("Listening for messages");
                },
                asio::detached
            );
        }

    } // namespace ActiveMQ

} // namespace InterDomainMessaging
