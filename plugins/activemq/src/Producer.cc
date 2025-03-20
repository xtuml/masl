#include "Producer.hh"

#include "idm/ProcessHandler.hh"

#include <asio/co_spawn.hpp>
#include <asio/detached.hpp>
#include <asio/use_future.hpp>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        Producer::Producer(const std::string topic, amqp_asio::Session session)
            : topic(topic), log("idm.activemq.producer.{}", topic) {
            auto executor = ProcessHandler::getInstance().getContext().get_executor();
            sender = asio::co_spawn(
                executor,
                session.open_sender(amqp_asio::SenderOptions().name("idm.activemq.producer.sender." + topic).delivery_mode(amqp_asio::DeliveryMode::at_least_once)),
                asio::use_future
            );
        }

        void Producer::produce(std::string data) {
            auto executor = ProcessHandler::getInstance().getContext().get_executor();
            asio::co_spawn(
                executor,
                [this, data]() -> asio::awaitable<void> {
                    co_await sender.get().send_json(data, amqp_asio::messages::Properties{.to = topic});
                },
                asio::detached
            );
        }

    } // namespace ActiveMQ

} // namespace InterDomainMessaging
