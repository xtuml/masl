#include "ProcessHandler.hh"
#include "Consumer.hh"
#include "Producer.hh"

#include "activemq/ActiveMQ.hh"
#include "swa/CommandLine.hh"

#include <asio/co_spawn.hpp>
#include <asio/use_future.hpp>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        ProcessHandler::ProcessHandler()
            : log(xtuml::logging::Logger("idm.activemq.processhandler")) {

            auto executor = getContext().get_executor();
            initialised = asio::co_spawn(
                executor,
                [this, executor]() -> asio::awaitable<void> {
                    try {

                        const std::string hostname = SWA::CommandLine::getInstance().getOption(BrokerOption);
                        const std::string username = SWA::CommandLine::getInstance().getOption(UsernameOption, "admin");
                        const std::string password = SWA::CommandLine::getInstance().getOption(PasswordOption, "admin");
                        const std::string port = SWA::CommandLine::getInstance().getOption(PortNoOption, "5672");

                        // create connection
                        conn = amqp_asio::Connection::create_amqp("idm.activemq", executor);
                        co_await conn.open(
                            amqp_asio::ConnectionOptions().hostname(hostname).port(port).sasl_options(amqp_asio::SaslOptions().authname(username).password(password))
                        );
                        log.debug("Connection open");

                        // open a session
                        session = co_await conn.open_session();
                        log.debug("Session open");

                    } catch (std::bad_variant_access &e) {
                        throw std::system_error(make_error_code(std::errc::bad_message));
                    } catch (const std::system_error &e) {
                        fmt::println("Error: {}", e.code().message());
                    }
                },
                asio::use_future
            );
        }

        std::unique_ptr<InterDomainMessaging::Consumer> ProcessHandler::createConsumer(std::string topic) {
            initialised.wait();
            return std::make_unique<Consumer>(topic, session);
        }

        std::unique_ptr<InterDomainMessaging::Producer> ProcessHandler::createProducer(std::string topic) {
            initialised.wait();
            return std::make_unique<Producer>(topic, session);
        }

        ProcessHandler &ProcessHandler::getInstance() {
            static ProcessHandler instance;
            return instance;
        }

        bool registered = ProcessHandler::registerSingleton(&ProcessHandler::getInstance);

    } // namespace ActiveMQ

} // namespace InterDomainMessaging
