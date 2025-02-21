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
#include <asio/co_spawn.hpp>
#include <asio/detached.hpp>
#include <asio/this_coro.hpp>
#include <logging/log.hh>

namespace amqp_asio {

    void spawn_cancellable(asio::any_io_executor executor, std::function<asio::awaitable<void>()> fn, xtuml::logging::Logger log);
    void spawn_cancellable_loop(asio::any_io_executor executor, std::function<asio::awaitable<void>()> fn, xtuml::logging::Logger log);

}
