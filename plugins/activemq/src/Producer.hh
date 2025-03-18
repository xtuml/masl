#ifndef ActiveMQ_Producer_HH
#define ActiveMQ_Producer_HH

#include "idm/Producer.hh"

#include "amqp_asio/sender.hh"
#include "amqp_asio/session.hh"
#include "logging/log.hh"

namespace InterDomainMessaging {

    namespace ActiveMQ {

        class Producer : public InterDomainMessaging::Producer {

          public:
            Producer(const std::string topic, amqp_asio::Session session);

            void produce(nlohmann::json data) {
                produce(data, std::nullopt);
            };

            void produce(nlohmann::json data, nlohmann::json partKey) {
                produce(data, std::make_optional<nlohmann::json>(partKey));
            };

          private:
            amqp_asio::Sender sender;
            std::string topic;
            xtuml::logging::Logger log;

            void produce(nlohmann::json data, std::optional<nlohmann::json> partKey);
        };

    } // namespace ActiveMQ

} // namespace InterDomainMessaging

#endif
