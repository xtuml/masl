/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include "amqp_asio/sender.hh"

namespace amqp_asio {

    asio::awaitable<Tracker> Sender::send(messages::Message message, std::optional<DeliveryMode> mode) {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->send(std::move(message), mode));
    }

    asio::awaitable<Tracker> Sender::send(
        messages::binary_t payload, std::optional<messages::Properties> properties, std::optional<DeliveryMode> mode
    ) {
        auto data = std::vector<amqp_asio::messages::Data>{amqp_asio::messages::Data{std::move(payload)}};
        co_return co_await send(
            messages::Message{
                .properties = std::move(properties),
                .data = std::move(data),
            },
            std::move(mode)
        );
    }

    asio::awaitable<Tracker> Sender::send(
        std::string_view payload, std::optional<messages::Properties> properties, std::optional<DeliveryMode> mode
    ) {
        std::vector<std::byte> bytes;
        bytes.reserve(payload.size());
        std::transform(payload.begin(), payload.end(), std::back_inserter(bytes), [](auto c) {
            return static_cast<std::byte>(c);
        });
        co_return co_await send(std::move(bytes), std::move(properties), std::move(mode));
    }

    asio::awaitable<Tracker> Sender::send_json(
        const nlohmann::json &payload, std::optional<messages::Properties> properties, std::optional<DeliveryMode> mode
    ) {
        properties = properties.value_or(messages::Properties{});
        properties->content_type = properties->content_type.value_or(messages::symbol_t{"application/json"});
        co_return co_await send(payload.dump(), std::move(properties), std::move(mode));
    }

    asio::awaitable<void> Sender::detach() {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->detach());
    }

    Sender::operator bool() const noexcept {
        return static_cast<bool>(pimpl_);
    }

} // namespace amqp_asio