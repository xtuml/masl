/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include "amqp_asio/connection.hh"
#include "connection.hh"

namespace amqp_asio {

    Connection Connection::create(std::string name, std::unique_ptr<AnySocket> socket) {
        return Connection{std::make_shared<ConnectionImpl>(std::move(name), std::move(socket))};
    }

    asio::awaitable<void> Connection::open(ConnectionOptions options) {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->open(std::move(options)));
    }

    asio::awaitable<void> Connection::close(std::string condition, std::string description) {
        co_return co_await asio::co_spawn(
            pimpl_->get_executor(), pimpl_->close(std::move(condition), std::move(description))
        );
    }

    asio::awaitable<Session> Connection::open_session(SessionOptions options) {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->open_session(std::move(options)));
    }

    Connection::operator bool() const noexcept {
        return static_cast<bool>(pimpl_);
    }

} // namespace amqp_asio