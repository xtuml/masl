// tag::setup[]
#include <amqp_asio/connection.hh>
#include <asio.hpp>
#include <map>
#include <fmt/format.h>

using namespace amqp_asio;

// end::setup[]

// tag::co-main[]
asio::awaitable<void> co_main() {
    // tag::connection-create[]
    auto connection = Connection::create_amqp("example", co_await asio::this_coro::executor);
    // end::connection-create[]
    // tag::connection-open[]
    co_await connection.open(ConnectionOptions{}.sasl_options(SaslOptions().authname("artemis").password("artemis")));
    // end::connection-open[]
    // tag::session-open[]
    auto session = co_await connection.open_session();
    // end::session-open[]
    // tag::sender-open[]
    auto sender = co_await session.open_sender("some-address");
    auto anon_sender = co_await session.open_sender();
    // end::sender-open[]
    // tag::receiver-open[]
    auto receiver = co_await session.open_receiver("some-address");
    // end::receiver-open[]
    // tag::send[]

    co_await sender.send("Hello, world!");

    co_await anon_sender.send("Hello, anonymous!", messages::Properties{.to="some-address"});

    nlohmann::json json({{"greeting", "Hello, world!"}});
    co_await sender.send_json(json);

    // end::send[]
    // tag::receive[]

    auto msg = co_await receiver.receive();
    fmt::println("Received: {}", msg.message().as_string());

    msg = co_await receiver.receive();
    fmt::println("Received: {}", msg.message().as_string());

    msg = co_await receiver.receive();
    fmt::println("Received: {}", msg.message().as_json().dump(2));

    // end::receive[]
    // tag::elipsis[]

    // ...

    // end::elipsis[]
    // tag::receiver-open[]
    co_await receiver.detach();
    // end::receiver-open[]
    // tag::sender-open[]
    co_await sender.detach();
    co_await anon_sender.detach();
    // end::sender-open[]
    // tag::session-open[]
    co_await session.end();
    // end::session-open[]
    // tag::connection-open[]
    co_await connection.close();
    // end::connection-open[]
    co_return;
}

// end::co-main[]
// tag::setup[]
int main() {
    asio::io_context io_context;
    asio::co_spawn(io_context.get_executor(), co_main(), asio::detached);
    io_context.run();
}
// end::setup[]
