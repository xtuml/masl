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

#include "receiver.hh"
#include "sender.hh"

#include <asio/awaitable.hpp>
#include <asio/co_spawn.hpp>
#include <memory>

namespace amqp_asio {

    class Session {
      public:
        class Impl {
          public:
            virtual asio::awaitable<void> end() = 0;

            virtual asio::awaitable<std::shared_ptr<Sender::Impl>>
            open_sender(std::optional<std::string> address, SenderOptions options) = 0;

            virtual asio::awaitable<std::shared_ptr<Receiver::Impl>>
            open_receiver(std::string address, ReceiverOptions options) = 0;

            virtual asio::any_io_executor get_executor() const = 0;

            virtual ~Impl() = default;
        };

        Session() = default;
        Session(std::shared_ptr<Impl> pimpl)
            : pimpl_{pimpl} {}

        asio::awaitable<void> end();
        asio::awaitable<Sender> open_sender(std::string address, SenderOptions options = {});

        asio::awaitable<Sender> open_sender(SenderOptions options = {});

        asio::awaitable<Receiver> open_receiver(std::string address, ReceiverOptions options = {});

        explicit operator bool() const noexcept;

      private:
        std::shared_ptr<Impl> pimpl_;
    };

} // namespace amqp_asio