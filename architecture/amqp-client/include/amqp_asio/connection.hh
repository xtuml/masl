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

#include "any_socket.hh"
#include "options.hh"
#include "session.hh"

#include <asio/async_result.hpp>
#include <asio/awaitable.hpp>
#include <asio/basic_stream_socket.hpp>
#include <asio/co_spawn.hpp>
#include <asio/ip/tcp.hpp>
#include <asio/ssl/stream.hpp>
#include <memory>

namespace amqp_asio {
    namespace protocol {
        using amqp = asio::ip::tcp::socket;
        using amqps = asio::ssl::stream<amqp>;
    } // namespace protocol

    class Connection {
      public:
        template <typename Socket>
        static Connection create(std::string name, std::unique_ptr<Socket> socket) {
            return create(std::move(name), AnySocket::create(std::move(socket)));
        }

        template <typename Socket, typename... Args>
        static Connection create(std::string name, Args &&...args) {
            return create(std::move(name), std::make_unique<Socket>(std::forward<Args>(args)...));
        }

        template <typename... Args>
        static Connection create_amqp(std::string name, Args &&...args) {
            return create<protocol::amqp>(std::move(name), std::forward<Args>(args)...);
        }

        template <typename... Args>
        static Connection create_amqps(std::string name, Args &&...args) {
            return create<protocol::amqps>(std::move(name), std::forward<Args>(args)...);
        }

        Connection() = default;

        class Impl {
          public:
            virtual asio::awaitable<void> open(ConnectionOptions options) = 0;

            virtual asio::awaitable<void> close(std::string condition, std::string description) = 0;

            virtual asio::awaitable<std::shared_ptr<Session::Impl>> open_session(SessionOptions options) = 0;

            [[nodiscard]] virtual asio::any_io_executor get_executor() const = 0;

            virtual ~Impl() = default;
        };

        asio::awaitable<void> open(ConnectionOptions options = {});

        asio::awaitable<void> close(std::string condition = "", std::string description = "");

        asio::awaitable<Session> open_session(SessionOptions options = {});

        explicit operator bool() const noexcept;

      private:
        explicit(false) Connection(std::shared_ptr<Impl> pimpl)
            : pimpl_{std::move(pimpl)} {}

        static Connection create(std::string name, std::unique_ptr<AnySocket> socket);

        std::shared_ptr<Impl> pimpl_;
    };
} // namespace amqp_asio
