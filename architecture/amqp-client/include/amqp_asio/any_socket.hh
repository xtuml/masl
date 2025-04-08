/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#pragma once

#include <asio/awaitable.hpp>
#include <asio/buffer.hpp>
#include <asio/connect.hpp>
#include <asio/experimental/awaitable_operators.hpp>
#include <asio/ip/tcp.hpp>
#include <asio/read.hpp>
#include <asio/write.hpp>
#include <asio/ssl.hpp>
#include <string_view>
#include <system_error>
#include <print>
namespace amqp_asio {

    static constexpr auto operation_timeout = std::chrono::seconds(3);

    using namespace asio::experimental::awaitable_operators;

    template <typename S, typename = void>
    struct has_async_shutdown : std::false_type {};

    template <typename S>
    struct has_async_shutdown<S, std::void_t<decltype(std::declval<S>().async_shutdown())>> : std::true_type {};

    template <typename S>
    constexpr bool has_async_shutdown_v = has_async_shutdown<S>::value;

    template <typename S, typename = void>
    struct has_async_handshake : std::false_type {};

    template <typename S>
    struct has_async_handshake<S, std::void_t<decltype(std::declval<S>().async_handshake(S::handshake_type::client))>> : std::true_type {};

    template <typename S>
    constexpr bool has_async_handshake_v = has_async_handshake<S>::value;

    template <typename>
    class AnySocketConcrete;

    class AnySocket {
      public:
        template <typename Socket>
        static std::unique_ptr<AnySocket> create(std::unique_ptr<Socket> socket) {
            return std::make_unique<AnySocketConcrete<Socket>>(std::move(socket));
        }

        virtual asio::awaitable<void> connect(std::string_view host, std::string_view service) = 0;
        virtual asio::awaitable<void> close() = 0;
        virtual asio::awaitable<void> read(asio::mutable_buffer &buffer) = 0;
        virtual asio::awaitable<void> read(std::vector<asio::mutable_buffers_1> &buffer) = 0;
        virtual asio::awaitable<void> write(const asio::const_buffer &buffer) = 0;
        virtual asio::awaitable<void> write(const std::vector<asio::const_buffers_1> &buffer) = 0;

        virtual asio::any_io_executor get_executor() = 0;

        virtual ~AnySocket() = default;
    };

    template <typename Socket>
    class AnySocketConcrete : public AnySocket {
      public:
        AnySocketConcrete(std::unique_ptr<Socket> socket)
            : socket_{std::move(socket)} {}
        asio::awaitable<void> connect(std::string_view host, std::string_view service) override {
            asio::ip::tcp::resolver resolver(co_await asio::this_coro::executor);
            co_await async_connect(socket_->lowest_layer(), co_await resolver.async_resolve(host, service));
            if constexpr ( has_async_handshake_v<std::decay_t<Socket>>) {
                std::println("Handshaking...");
                co_await socket_->async_handshake(Socket::handshake_type::client);
                std::println("Handshake complete");
            }
        }
        asio::awaitable<void> close() override {
            if constexpr (has_async_shutdown_v<std::decay_t<Socket>>) {
                co_await (socket_->async_shutdown(asio::use_awaitable) || force_close());
            } else {
                std::error_code ec;
                socket_->close(ec);
            }
            co_return;
        }

        asio::awaitable<void> force_close() {
            asio::steady_timer timeout(co_await asio::this_coro::executor, operation_timeout);
            co_await timeout.async_wait();
            std::error_code ec;
            socket_->next_layer().close(ec);
        }

        asio::awaitable<void> read(asio::mutable_buffer &buffer) override {
            co_await asio::async_read(*socket_, buffer);
        };
        asio::awaitable<void> read(std::vector<asio::mutable_buffers_1> &buffer) override {
            co_await asio::async_read(*socket_, buffer);
        };
        asio::awaitable<void> write(const asio::const_buffer &buffer) override {
            co_await asio::async_write(*socket_, buffer);
        };
        asio::awaitable<void> write(const std::vector<asio::const_buffers_1> &buffer) override {
            co_await asio::async_write(*socket_, buffer);
        };

        asio::any_io_executor get_executor() override {
            return socket_->get_executor();
        }

      private:
        std::unique_ptr<Socket> socket_;
    };

} // namespace amqp_asio