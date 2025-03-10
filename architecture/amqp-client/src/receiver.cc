/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include "amqp_asio/receiver.hh"

namespace amqp_asio {

    asio::awaitable<Delivery> Receiver::receive() {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->receive());
    }

    asio::awaitable<void> Receiver::detach() {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->detach());
    }

    Receiver::operator bool() const noexcept {
        return static_cast<bool>(pimpl_);
    }

} // namespace amqp_asio