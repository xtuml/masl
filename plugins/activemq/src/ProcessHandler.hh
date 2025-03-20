#ifndef ActiveMQ_ProcessHandler_HH
#define ActiveMQ_ProcessHandler_HH

#include "idm/ProcessHandler.hh"

#include "amqp_asio/condition_var.hh"
#include "amqp_asio/connection.hh"
#include "amqp_asio/session.hh"

#include "logging/log.hh"

#include <asio/io_context.hpp>
#include <future>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        class ProcessHandler : public InterDomainMessaging::ProcessHandler {
          public:
            ProcessHandler();
            std::unique_ptr<InterDomainMessaging::Consumer> createConsumer(std::string topic) override;
            std::unique_ptr<InterDomainMessaging::Producer> createProducer(std::string topic) override;
            amqp_asio::Session getSession() {
                return session;
            }
            amqp_asio::ConditionVar& getInitialised() {
                return initialised;
            }
            static ProcessHandler &getInstance();

          private:
            xtuml::logging::Logger log;
            amqp_asio::Connection conn;
            amqp_asio::Session session;
            amqp_asio::ConditionVar initialised;
        };

    } // namespace ActiveMQ

} // namespace InterDomainMessaging

#endif
