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
#include <asio/experimental/concurrent_channel.hpp>

namespace amqp_asio {

    struct co_mutex {
        struct unlocker {
            void operator()(co_mutex *m) const noexcept {
                m->unlock();
            }
        };

        template <typename Exec>
        co_mutex(Exec &&executor)
            : channel(std::move(executor), 1) {}

        asio::awaitable<std::unique_ptr<co_mutex, unlocker>> guard() {
            co_await lock();
            co_return std::unique_ptr<co_mutex, unlocker>(this);
        }

      private:
        asio::awaitable<void> lock() {
            co_await channel.async_send();
        }

        void unlock() {
            channel.try_receive([](auto...) {});
        }

        asio::experimental::concurrent_channel<void()> channel;
    };

} // namespace amqp_asio