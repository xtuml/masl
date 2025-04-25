#ifndef ActiveMQ_ProcessHandler_HH
#define ActiveMQ_ProcessHandler_HH

#include "idm/ProcessHandler.hh"

#include "amqp_asio/condition_var.hh"
#include "amqp_asio/connection.hh"
#include "amqp_asio/session.hh"

#include "logging/log.hh"

#include <map>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        class Consumer;

        class ProcessHandler : public InterDomainMessaging::ProcessHandler {
          public:
            ProcessHandler();
            std::shared_ptr<InterDomainMessaging::Consumer> createConsumer(std::string topic) override;
            std::shared_ptr<InterDomainMessaging::Producer> createProducer(std::string topic) override;

            void setConsumerConfig(std::string consumerId, std::string paramName, std::string paramValue) override;
            void setConsumerConfig(std::string consumerId, std::string paramName, int paramValue) override;
            void setConsumerConfig(std::string consumerId, std::string paramName, bool paramValue) override;

            amqp_asio::Session getSession() {
                return session;
            }

            asio::awaitable<void> isInitialised() {
                return initialisedCond.wait([this] {
                    return initialised;
                });
            }

            std::string getName() {
                return name;
            }

            static ProcessHandler &getInstance();

          private:
            std::string name;
            xtuml::logging::Logger log;
            amqp_asio::Connection conn;
            amqp_asio::Session session;
            bool initialised;
            amqp_asio::ConditionVar initialisedCond;
            std::map<std::string, std::shared_ptr<Consumer>> consumers;
        };

    } // namespace ActiveMQ

} // namespace InterDomainMessaging

#endif
