#include "amqp_asio/connection.hh"
#include "amqp_asio/spawn.hh"
#include "rabbitmq_mgt.hh"
#include <asio/co_spawn.hpp>
#include <asio/connect.hpp>
#include <asio/deferred.hpp>
#include <asio/detached.hpp>
#include <asio/io_context.hpp>
#include <asio/ip/tcp.hpp>
#include <asio/signal_set.hpp>
#include <asio/spawn.hpp>
#include <asio/ssl.hpp>
#include <asio/strand.hpp>
#include <asio/write.hpp>

#include <CLI/CLI.hpp>
#include <fmt/format.h>
#include <logging/log.hh>

using namespace amqp_asio;
using namespace std::literals;
using asio::awaitable;
using asio::co_spawn;
using asio::detached;
using asio::use_awaitable;
namespace this_coro = asio::this_coro;

struct BrokerConfig {
    std::string hostname;
    std::string port;
    std::string username;
    std::string password;
    bool rabbitmq{};
};

struct SSLConfig {
    bool use = false;
    std::string certificate_file;
    std::string private_key_file;
    std::string ca_file;
    std::string password;

    [[nodiscard]]
    bool use_ssl() const {
        return use || !certificate_file.empty() || !ca_file.empty() || !private_key_file.empty();
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

struct Config {
    std::string name;
    BrokerConfig broker;
    SSLConfig ssl;
};

awaitable<void> main_loop(asio::io_context &ctx, const Config &config) {

    auto log = xtuml::logging::Logger("amqp_asio.main_loop");

    asio::signal_set signals(ctx, SIGINT, SIGTERM);
    signals.async_wait([&ctx](auto, auto) {
        ctx.stop();
    });

    try {
        auto executor = co_await this_coro::executor;
        asio::steady_timer timer(executor);
        auto ssl_ctx = config.ssl.context();
        Connection c = config.ssl.use_ssl() ? Connection::create_amqps(config.name, executor, ssl_ctx)
                                            : Connection::create_amqp(config.name, executor);

        co_await c.open(
            ConnectionOptions()
                .hostname(config.broker.hostname)
                .port(config.broker.port)
                .sasl_options(SaslOptions().authname(config.broker.username).password(config.broker.password))
        );

        log.info("Connection open");

        auto session = co_await c.open_session();

        std::string send_prefix = "topic://";
        std::string recv_prefix = "topic://";

        if (config.broker.rabbitmq) {
            auto mgt = co_await RabbitMQManagement::create(session);
            co_await mgt.bind_topic("example.*");
            co_await mgt.detach();

            recv_prefix = "/queues/";
            send_prefix = "/exchanges/amq.topic/";
        }

        log.info("Session open");
        timer.expires_after(10s);

        auto sender =
            co_await session.open_sender(SenderOptions().name("sender").delivery_mode(DeliveryMode::at_least_once));
        auto receiver = co_await session.open_receiver(recv_prefix + "example.*", ReceiverOptions().name("receiver"));

        spawn_cancellable_loop(
            executor,
            [&]() -> asio::awaitable<void> {
                auto delivery = co_await receiver.receive();
                // log.info("Received message {}", nlohmann::json(delivery.message()).dump(2));
                log.info("Received message {}", delivery.message().as_string());
                co_await delivery.accept();
            },
            log
        );

        spawn_cancellable_loop(
            executor,
            [&]() -> asio::awaitable<void> {
                auto json = nlohmann::json({{"hello", "world"}});
                co_await sender.send("hello", amqp_asio::messages::Properties{.to = send_prefix + "example.channel"});
                co_await sender.send_json(
                    json, amqp_asio::messages::Properties{.to = send_prefix + "example.channel"}
                );
                asio::steady_timer timer(executor, 1s);
                co_await timer.async_wait();
                co_return;
            },
            log
        );

        co_await timer.async_wait(asio::deferred);
        co_await receiver.detach();
        co_await sender.detach();

        co_await session.end();
        co_await c.close();
        log.info("Connection closed");

    } catch (std::bad_variant_access &e) {
        throw std::system_error(make_error_code(std::errc::bad_message));
    } catch (const std::system_error &e) {
        fmt::println("Error: {}", e.code().message());
    }
    signals.cancel();
}

int main(int argc, char *argv[]) {
    auto log = xtuml::logging::Logger("amqp_asio.main");

    CLI::App options{"AMQP ASIO Example"};

    auto log_opts = options.add_option_group("Logging");
    log_opts
        ->add_option_function<std::string>(
            "--log-config",
            [](auto &&filename) {
                std::fstream file{filename};
                if (file) {
                    xtuml::logging::Logger::load_config(filename, 1s);
                } else {
                    xtuml::logging::Logger{""}.error("Error opening log config: {}", filename);
                }
            },
            "logging configuration file"
        )
        ->envname("LOG_CONFIG");

    Config config;
    options.add_option("--name", config.name, "Client name")->default_val("example-client");
    auto conn_opts = options.add_option_group("Broker");
    conn_opts->add_option("--hostname", config.broker.hostname, "Broker hostname")->default_val("localhost");
    conn_opts->add_option("--port", config.broker.port, "Broker port");
    conn_opts->add_option("--username", config.broker.username, "Broker username")->default_val("guest");
    conn_opts->add_option("--password", config.broker.password, "Broker password")->default_val("guest");
    conn_opts->add_flag("--rabbitmq", config.broker.rabbitmq, "Use RabbitMQ Broker Management");

    auto ssl_opts = options.add_option_group("SSL");
    ssl_opts->add_flag("--ssl", config.ssl.use, "Connect using SSL");
    ssl_opts->add_option("--ssl_cert", config.ssl.certificate_file, "SSL Certificate (pem)");
    ssl_opts->add_option("--ssl_key", config.ssl.private_key_file, "SSL Private Key (pem)");
    ssl_opts->add_option("--ssl_ca", config.ssl.ca_file, "SSL CA (pem)");
    ssl_opts->add_option("--ssl_password", config.ssl.password, "SSL password");
    try {
        options.parse(argc, argv);
        if ( config.broker.port.empty()) {
            config.broker.port = config.ssl.use_ssl()? "5671" : "5672";
        }
    } catch (const CLI::ParseError &e) {
        return options.exit(e);
    }



    try {
        asio::io_context io_context(1);
        auto ex = io_context.get_executor();
        log.info("Running");
        co_spawn(ex, main_loop(io_context, config), detached);
        io_context.run();

    } catch (std::exception &e) {
        log.error("Exception: {}", e);
        return 1;
    }

    return 0;
}
