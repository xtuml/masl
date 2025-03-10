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
#include "options.hh"
#include "tracker.hh"
#include <asio/any_io_executor.hpp>
#include <asio/async_result.hpp>
#include <asio/awaitable.hpp>
#include <asio/co_spawn.hpp>
#include <memory>

namespace amqp_asio {

    class Sender {
      public:
        class Impl {
          public:
            virtual asio::awaitable<std::shared_ptr<Tracker::Impl>>
            send(messages::Message message, std::optional<DeliveryMode> mode) = 0;

            virtual asio::awaitable<void> detach() = 0;

            virtual asio::any_io_executor get_executor() const = 0;

            virtual ~Impl() = default;
        };
        Sender() = default;
        Sender(std::shared_ptr<Impl> pimpl)
            : pimpl_{pimpl} {}

        explicit operator bool() const noexcept;

        asio::awaitable<Tracker> send(messages::Message message, std::optional<DeliveryMode> mode = {});

        asio::awaitable<Tracker> send(
            std::vector<std::byte> payload,
            std::optional<messages::Properties> properties = {},
            std::optional<DeliveryMode> mode = {}
        );
        asio::awaitable<Tracker> send(
            std::string_view payload,
            std::optional<messages::Properties> properties = {},
            std::optional<DeliveryMode> mode = {}
        );
        asio::awaitable<Tracker> send_json(
            const nlohmann::json &payload,
            std::optional<messages::Properties> properties = {},
            std::optional<DeliveryMode> mode = {}
        );

        asio::awaitable<void> detach();

      private:
        std::shared_ptr<Impl> pimpl_;
    };

} // namespace amqp_asio