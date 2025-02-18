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

awaitable<void> main_loop(
    asio::io_context &ctx,
    std::string hostname,
    std::string port,
    std::string username,
    std::string password,
    bool use_rabbitmq = false
) {

    auto log = xtuml::logging::Logger("amqp_asio.main_loop");

    asio::signal_set signals(ctx, SIGINT, SIGTERM);
    signals.async_wait([&ctx](auto, auto) {
        ctx.stop();
    });

    try {
        auto executor = co_await this_coro::executor;
        asio::steady_timer timer(executor);

        auto c = Connection::create_amqp("test-container", executor);
        co_await c.open(ConnectionOptions().hostname(hostname).port(port).sasl_options(
            SaslOptions().authname(username).password(password)
        ));

        log.info("Connection open");

        auto session = co_await c.open_session();

        std::string topic_prefix = "";
        std::string queue_prefix = "";

        if (use_rabbitmq) {
            auto mgt = co_await RabbitMQManagement::create(session);
            co_await mgt.bind_topic("example.*");
            co_await mgt.detach();

            queue_prefix = "/queues/";
            topic_prefix = "/exchanges/amq.topic/";
        }

        log.info("Session open");
        timer.expires_after(10s);

        auto sender =
            co_await session.open_sender(SenderOptions().name("sender").delivery_mode(DeliveryMode::at_least_once));
        auto receiver = co_await session.open_receiver(queue_prefix + "example.*", ReceiverOptions().name("receiver"));

        spawn_cancellable_loop(
            executor,
            [&]() -> asio::awaitable<void> {
                auto delivery = co_await receiver.receive();
                log.info("Received message {}", delivery.message().as_string());
                co_await delivery.accept();
            },
            log
        );

        spawn_cancellable_loop(
            executor,
            [&]() -> asio::awaitable<void> {
                auto json = nlohmann::json({{"hello","world"}});
                co_await sender.send("hello",amqp_asio::messages::Properties{.to = topic_prefix + "example.channel"});
                co_await sender.send_json(json,amqp_asio::messages::Properties{.to = topic_prefix + "example.channel"});
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

    std::string hostname;
    std::string port;
    std::string username;
    std::string password;
    bool rabbitmq{};
    auto conn_opts = options.add_option_group("Connection");
    conn_opts->add_option("--hostname", hostname, "Broker hostname")->default_val("localhost");
    conn_opts->add_option("--port", port, "Broker port")->default_val("5672");
    conn_opts->add_option("--username", username, "Broker username")->default_val("guest");
    conn_opts->add_option("--password", password, "Broker password")->default_val("guest");
    conn_opts->add_flag("--rabbitmq", rabbitmq, "Use RabbitMQ Broker Management");
    try {
        options.parse(argc, argv);
    } catch (const CLI::ParseError &e) {
        return options.exit(e);
    }

    try {
        asio::io_context io_context(1);
        auto ex = io_context.get_executor();
        log.info("Running");
        co_spawn(ex, main_loop(io_context, hostname, port, username, password, rabbitmq), detached);
        io_context.run();

    } catch (std::exception &e) {
        log.error("Exception: {}", e);
        return 1;
    }

    return 0;
}
