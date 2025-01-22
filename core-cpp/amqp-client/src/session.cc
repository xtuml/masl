/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include "amqp_asio/session.hh"

namespace amqp_asio {

        asio::awaitable<void> Session::end() {
            co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->end());
        }
        asio::awaitable<Sender> Session::open_sender(std::string address, SenderOptions options) {
            co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->open_sender(address, options));
        }

        asio::awaitable<Sender> Session::open_sender(SenderOptions options ) {
            co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->open_sender({}, options));
        }

        asio::awaitable<Receiver> Session::open_receiver(std::string address, ReceiverOptions options) {
            co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->open_receiver(address, options));
        }

        Session::operator bool() const noexcept {
            return static_cast<bool>(pimpl_);
        }


} // namespace amqp_asio