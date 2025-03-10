/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include "tracker.hh"

#include <boost/uuid/random_generator.hpp>

#include <cstddef>
#include <vector>

namespace amqp_asio {

    std::vector<std::byte> unique_tag() {
        static boost::uuids::random_generator randomUuid;
        auto uuid = randomUuid();
        std::vector<std::byte> result(uuid.size());
        std::transform(uuid.begin(), uuid.end(), result.begin(), [](auto c) {
            return std::byte(c);
        });
        return result;
    }
    asio::awaitable<void> Tracker::await_sent() {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->await_sent());
    }
    asio::awaitable<void> Tracker::await_settled() {
        co_return co_await asio::co_spawn(pimpl_->get_executor(), pimpl_->await_settled());
    }

    Tracker::operator bool() const noexcept {
        return static_cast<bool>(pimpl_);
    }

} // namespace amqp_asio
