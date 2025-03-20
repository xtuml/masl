#include "Producer.hh"

#include "idm/ProcessHandler.hh"

#include <asio/co_spawn.hpp>
#include <asio/detached.hpp>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        Producer::Producer(const std::string topic, ProcessHandler& proc)
            : topic(topic), log("idm.activemq.producer.{}", topic), initialised(proc.getContext().get_executor()), proc(proc) {
            auto executor = proc.getContext().get_executor();
            asio::co_spawn(
                executor,
                [this, topic, &proc]() mutable -> asio::awaitable<void> {
                    co_await proc.getInitialised().wait();
                    sender = co_await proc.getSession().open_sender(amqp_asio::SenderOptions().name("idm.activemq.producer.sender." + topic).delivery_mode(amqp_asio::DeliveryMode::at_least_once));
                    initialised.notify();
                },
                asio::detached
            );
        }

        void Producer::produce(std::string data) {
            auto executor = proc.getContext().get_executor();
            asio::co_spawn(
                executor,
                [this, data]() -> asio::awaitable<void> {
                    co_await initialised.wait();
                    co_await sender.send_json(data, amqp_asio::messages::Properties{.to = topic});
                },
                asio::detached
            );
        }

    } // namespace ActiveMQ

} // namespace InterDomainMessaging
