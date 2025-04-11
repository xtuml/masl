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

    asio::awaitable<void> Receiver::drain() {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->drain());
    }
    asio::awaitable<void> Receiver::add_credit(messages::uint_t credits) {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->add_credit(credits));
    }
    asio::awaitable<void> Receiver::remove_credit(messages::uint_t credits) {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->remove_credit(credits));
    }
    asio::awaitable<void> Receiver::set_credit(messages::uint_t credits) {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->set_credit(credits));
    }

    asio::awaitable<void> Receiver::start_auto_credit() {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->start_auto_credit());
    }
    asio::awaitable<void> Receiver::stop_auto_credit() {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->stop_auto_credit());
    }
    asio::awaitable<void> Receiver::auto_credit_limits(messages::uint_t low_water, messages::uint_t high_water) {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->auto_credit_limits(low_water,high_water));
    }

    Receiver::operator bool() const noexcept {
        return static_cast<bool>(pimpl_);
    }

} // namespace amqp_asio