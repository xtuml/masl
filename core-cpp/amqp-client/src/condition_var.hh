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
#include <asio/redirect_error.hpp>
#include <asio/steady_timer.hpp>
#include <asio/deferred.hpp>
#include <asio/error.hpp>
#include <chrono>


namespace amqp_asio {

    class ConditionVar {
      public:
        template <typename Exec>
        ConditionVar(Exec &&exec)
            : t{std::forward<Exec>(exec)} {
            t.expires_at(std::chrono::steady_clock::time_point::max());
        }

        template <class Pred>
        asio::awaitable<void> wait(Pred predicate) {
            while (!predicate()) {
                co_await wait();
            }
        }

        asio::awaitable<void> wait() {
            if ( cancelled ) {
                throw std::system_error(std::make_error_code(std::errc::operation_canceled));
            }
            std::error_code ec{};
            co_await t.async_wait(asio::redirect_error(asio::deferred, ec));
            if ( ec != asio::error::operation_aborted) {
                throw std::system_error(ec);
            } else if ( cancelled ) {
                throw std::system_error(std::make_error_code(std::errc::operation_canceled));
            }
        }

        void cancel() {
            cancelled = true;
            t.cancel();
        }

        void notify() {
            t.cancel();
        }

        void notify_one() {
            t.cancel_one();
        }

      private:
        asio::steady_timer t;
        std::atomic<bool> cancelled = false;
    };

} // namespace amqp_asio
