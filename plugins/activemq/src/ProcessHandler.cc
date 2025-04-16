#include "ProcessHandler.hh"
#include "Consumer.hh"
#include "Producer.hh"

#include "activemq/ActiveMQ.hh"
#include "swa/CommandLine.hh"
#include "swa/Process.hh"

#include <asio/co_spawn.hpp>
#include <asio/detached.hpp>
#include <asio/ssl.hpp>
#include <uuid/uuid.h>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        struct SSLConfig {
            std::string certificate_file;
            std::string private_key_file;
            std::string ca_file;
            std::string password;

            [[nodiscard]]
            bool use_ssl() const {
                return !certificate_file.empty() || !ca_file.empty() || !private_key_file.empty();
            }

            [[nodiscard]]
            asio::ssl::context context() const {
                asio::ssl::context ctx(asio::ssl::context::tlsv13);

                ctx.set_verify_mode(asio::ssl::verify_peer);
                if (!ca_file.empty()) {
                    ctx.load_verify_file(ca_file);
                }

                if (!password.empty()) {
                    ctx.set_password_callback([password = password](auto size, auto purpose) -> std::string {
                        return password;
                    });
                }

                if (!certificate_file.empty()) {
                    ctx.use_certificate_file(certificate_file, asio::ssl::context::pem);
                }
                if (!private_key_file.empty()) {
                    ctx.use_private_key_file(private_key_file, asio::ssl::context::pem);
                    if (certificate_file.empty()) {
                        ctx.use_certificate_file(private_key_file, asio::ssl::context::pem);
                    }
                }
                return ctx;
            }
        };

        struct BrokerConfig {
            std::string hostname;
            std::string port;
            std::string username;
            std::string password;
            SSLConfig ssl;
        };

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

                        BrokerConfig config;

                        // setup ssl options
                        config.ssl.certificate_file = SWA::CommandLine::getInstance().getOption(SslCertOption);
                        config.ssl.private_key_file = SWA::CommandLine::getInstance().getOption(SslKeyOption);
                        config.ssl.ca_file = SWA::CommandLine::getInstance().getOption(SslCertAuthOption);
                        config.ssl.password = SWA::CommandLine::getInstance().getOption(SslPasswordOption);

                        // setup broker options
                        config.hostname = SWA::CommandLine::getInstance().getOption(BrokerOption);
                        config.port = SWA::CommandLine::getInstance().getOption(PortNoOption, config.ssl.use_ssl() ? "5671" : "5672");
                        config.username = SWA::CommandLine::getInstance().getOption(UsernameOption, "admin");
                        config.password = SWA::CommandLine::getInstance().getOption(PasswordOption, "admin");

                        // create connection
                        if (config.ssl.use_ssl()) {
                            auto ssl_ctx = config.ssl.context();
                            conn = amqp_asio::Connection::create_amqps(getName(), SWA::Process::getInstance().getIOContext().get_executor(), ssl_ctx);
                        } else {
                            conn = amqp_asio::Connection::create_amqp(getName(), SWA::Process::getInstance().getIOContext().get_executor());
                        }
                        co_await conn.open(
                            amqp_asio::ConnectionOptions()
                                .hostname(config.hostname)
                                .port(config.port)
                                .sasl_options(amqp_asio::SaslOptions().authname(config.username).password(config.password))
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

        std::unique_ptr<InterDomainMessaging::Consumer> ProcessHandler::createConsumer(std::string topic) {
            return std::make_unique<Consumer>(topic, *this);
        }

        std::unique_ptr<InterDomainMessaging::Producer> ProcessHandler::createProducer(std::string topic) {
            return std::make_unique<Producer>(topic, *this);
        }

        ProcessHandler &ProcessHandler::getInstance() {
            static ProcessHandler instance;
            return instance;
        }

        bool registered = ProcessHandler::registerSingleton(&ProcessHandler::getInstance);

    } // namespace ActiveMQ

} // namespace InterDomainMessaging
