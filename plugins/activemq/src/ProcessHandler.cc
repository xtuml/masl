#include "ProcessHandler.hh"
#include "Consumer.hh"
#include "Producer.hh"

#include "activemq/ActiveMQ.hh"
#include "swa/CommandLine.hh"
#include "swa/Process.hh"

#include <asio/co_spawn.hpp>
#include <asio/detached.hpp>
#include <uuid/uuid.h>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        ProcessHandler::ProcessHandler()
            : log(xtuml::logging::Logger("idm.activemq.processhandler")), initialisedCond(SWA::Process::getInstance().getIOContext().get_executor()) {

            // set name
            uuid_t uuid;
            uuid_generate(uuid);
            char formatted_uuid[37];
            uuid_unparse(uuid, formatted_uuid);
            name = "idm.activemq." + SWA::Process::getInstance().getName() + "." + std::string(formatted_uuid);

            asio::co_spawn(
                SWA::Process::getInstance().getIOContext().get_executor(),
                [this]() -> asio::awaitable<void> {
                    try {

                        const std::string hostname = SWA::CommandLine::getInstance().getOption(BrokerOption);
                        const std::string username = SWA::CommandLine::getInstance().getOption(UsernameOption, "admin");
                        const std::string password = SWA::CommandLine::getInstance().getOption(PasswordOption, "admin");
                        const std::string port = SWA::CommandLine::getInstance().getOption(PortNoOption, "5672");

                        // create connection
                        conn =
                            amqp_asio::Connection::create_amqp("idm.activemq." + SWA::Process::getInstance().getName(), SWA::Process::getInstance().getIOContext().get_executor());
                        co_await conn.open(
                            amqp_asio::ConnectionOptions().hostname(hostname).port(port).sasl_options(amqp_asio::SaslOptions().authname(username).password(password))
                        );
                        log.debug("Connection open");

                        // open a session
                        session = co_await conn.open_session();
                        log.debug("Session open");

                        initialised = true;
                        initialisedCond.notify();

                    } catch (std::bad_variant_access &e) {
                        throw std::system_error(make_error_code(std::errc::bad_message));
                    } catch (const std::system_error &e) {
                        fmt::println("Error: {}", e.code().message());
                    }
                },
                asio::detached
            );
        }

        std::shared_ptr<InterDomainMessaging::Consumer> ProcessHandler::createConsumer(std::string topic) {
            auto consumer = std::make_shared<Consumer>(topic, *this);
            consumers[topic] = consumer;
            return consumer;
        }

        std::shared_ptr<InterDomainMessaging::Producer> ProcessHandler::createProducer(std::string topic) {
            return std::make_shared<Producer>(topic, *this);
        }

        void ProcessHandler::setConsumerConfig(std::string consumerId, std::string paramName, std::string paramValue) {
            // TODO
        }

        void ProcessHandler::setConsumerConfig(std::string consumerId, std::string paramName, int paramValue) {
            // TODO
        }

        void ProcessHandler::setConsumerConfig(std::string consumerId, std::string paramName, bool paramValue) {
            // TODO
        }

        ProcessHandler &ProcessHandler::getInstance() {
            static ProcessHandler instance;
            return instance;
        }

        bool registered = ProcessHandler::registerSingleton(&ProcessHandler::getInstance);

    } // namespace ActiveMQ

} // namespace InterDomainMessaging
