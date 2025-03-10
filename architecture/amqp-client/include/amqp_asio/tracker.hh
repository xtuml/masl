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

#include "messages.hh"
#include <asio/any_io_executor.hpp>
#include <asio/async_result.hpp>
#include <asio/awaitable.hpp>
#include <asio/co_spawn.hpp>
#include <memory>

namespace amqp_asio {

    class Tracker {
      public:
        class Impl {
          public:
            virtual const messages::Message &message() const = 0;

            virtual asio::any_io_executor get_executor() const = 0;

            virtual asio::awaitable<void> await_sent() = 0;
            virtual asio::awaitable<void> await_settled() = 0;
            virtual bool is_settled() const = 0;

            virtual ~Impl() = default;
        };

        Tracker() = default;
        Tracker(std::shared_ptr<Impl> pimpl)
            : pimpl_{pimpl} {}

        explicit operator bool() const noexcept;

        const messages::Message &message() const {
            return pimpl_->message();
        }

        asio::awaitable<void> await_sent();
        asio::awaitable<void> await_settled();

        bool is_settled() const {
            return pimpl_->is_settled();
        }

      private:
        std::shared_ptr<Impl> pimpl_;
    };
} // namespace amqp_asio