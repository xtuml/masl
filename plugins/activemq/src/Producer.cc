#include "Producer.hh"

#include "idm/ProcessHandler.hh"
#include "swa/Process.hh"

#include <asio/co_spawn.hpp>
#include <asio/detached.hpp>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        Producer::Producer(const std::string topic, ProcessHandler &proc)
            : topic(topic), log("idm.activemq.producer.{}", topic), initialised(proc.getContext().get_executor()), proc(proc) {
            auto executor = proc.getContext().get_executor();
            asio::co_spawn(
                executor,
                [this, topic, &proc]() mutable -> asio::awaitable<void> {
                    co_await proc.getInitialised().wait();
                    sender = co_await proc.getSession().open_sender(
                        amqp_asio::SenderOptions()
                            .name("idm.activemq." + SWA::Process::getInstance().getName() + ".producer.sender." + topic)
                            .delivery_mode(amqp_asio::DeliveryMode::at_least_once)
                    );
                    initialised.notify();
                    log.debug("Producer initialised");
                },
                asio::detached
            );
        }

        void Producer::produce(std::string data) {
            auto executor = proc.getContext().get_executor();
            asio::co_spawn(
                executor,
                [this, data]() -> asio::awaitable<void> {
                    log.debug("Sending message");
                    // TODO not sure why this is not working
                    /*co_await initialised.wait();*/
                    co_await sender.send(data, amqp_asio::messages::Properties{.to = topic});
                    log.debug("Done sending");
                },
                asio::detached
            );
        }

    } // namespace ActiveMQ

} // namespace InterDomainMessaging
