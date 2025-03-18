#include "Producer.hh"

#include "idm/ProcessHandler.hh"

#include <asio/co_spawn.hpp>
#include <asio/use_future.hpp>
#include <future>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        Producer::Producer(const std::string topic, amqp_asio::Session session)
            : topic(topic), log("idm.activemq.producer.{}", topic) {
            auto executor = ProcessHandler::getInstance().getContext().get_executor();
            std::future<amqp_asio::Sender> future = asio::co_spawn(
                executor,
                session.open_sender(amqp_asio::SenderOptions().name("idm.activemq.producer.sender." + topic).delivery_mode(amqp_asio::DeliveryMode::at_least_once)),
                asio::use_future
            );
            sender = future.get();
        }

        void Producer::produce(nlohmann::json data, std::optional<nlohmann::json> partKey) {
            // TODO partKey is ignored in this implementation
            auto executor = ProcessHandler::getInstance().getContext().get_executor();
            std::future<void> future = asio::co_spawn(
                executor,
                [this, data]() -> asio::awaitable<void> {
                    co_await sender.send_json(data, amqp_asio::messages::Properties{.to = topic});
                },
                asio::use_future
            );
            future.wait();
        }

    } // namespace ActiveMQ

} // namespace InterDomainMessaging
