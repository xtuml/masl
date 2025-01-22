/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include "delivery.hh"

namespace amqp_asio {
    messages::Message Delivery::message() const {
        return pimpl_->message();
    }

    asio::awaitable<void> Delivery::accept() {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->accept());
    }

    asio::awaitable<void> Delivery::reject(std::optional<messages::Error> error) {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->reject(std::move(error)));
    }

    asio::awaitable<void> Delivery::release() {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->release());
    }

    Delivery::operator bool() const noexcept {
        return static_cast<bool>(pimpl_);
    }

} // namespace amqp_asio
