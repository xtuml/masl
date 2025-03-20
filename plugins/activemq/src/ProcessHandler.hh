#ifndef ActiveMQ_ProcessHandler_HH
#define ActiveMQ_ProcessHandler_HH

#include "idm/ProcessHandler.hh"

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
            static ProcessHandler &getInstance();

          private:
            xtuml::logging::Logger log;
            amqp_asio::Connection conn;
            amqp_asio::Session session;
            std::future<void> initialised;
        };

    } // namespace ActiveMQ

} // namespace InterDomainMessaging

#endif
