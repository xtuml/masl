#ifndef ActiveMQ_Producer_HH
#define ActiveMQ_Producer_HH

#include "idm/Producer.hh"

#include "amqp_asio/sender.hh"
#include "amqp_asio/session.hh"
#include "logging/log.hh"

#include <future>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        class Producer : public InterDomainMessaging::Producer {

          public:
            Producer(const std::string topic, amqp_asio::Session session);

            void produce(std::string data) override;

            void produce(std::string data, std::string partKey) override {
                // partition key is ignored in this implementation
                produce(data);
            };

          private:
            std::future<amqp_asio::Sender> sender;
            std::string topic;
            xtuml::logging::Logger log;
        };

    } // namespace ActiveMQ

} // namespace InterDomainMessaging

#endif
