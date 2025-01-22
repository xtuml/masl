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

    class Delivery {
      public:
        class Impl {
          public:
            virtual messages::Message message() const = 0;
            virtual asio::awaitable<void> accept() = 0;
            virtual asio::awaitable<void> reject(std::optional<messages::Error> error) = 0;
            virtual asio::awaitable<void> release() = 0;

            virtual asio::any_io_executor get_executor() const = 0;

            virtual ~Impl() = default;
        };

        Delivery() = default;
        Delivery(std::shared_ptr<Impl> pimpl)
            : pimpl_{pimpl} {}

        messages::Message message() const;

        explicit operator bool() const noexcept;

        asio::awaitable<void> accept();

        asio::awaitable<void> reject(std::optional<messages::Error> error = {});

        asio::awaitable<void> release();

      private:
        std::shared_ptr<Impl> pimpl_;
    };

} // namespace amqp_asio