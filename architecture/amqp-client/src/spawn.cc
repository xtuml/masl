/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include "amqp_asio/spawn.hh"

#include <cxxabi.h>
#include <string>
#include <type_traits>

#include <asio/awaitable.hpp>
#include <asio/co_spawn.hpp>
#include <asio/detached.hpp>
#include <asio/error.hpp>
#include <asio/this_coro.hpp>
#include <functional>
#include <logging/log.hh>

namespace amqp_asio {

    void spawn_cancellable(
        asio::any_io_executor executor, std::function<asio::awaitable<void>()> fn, xtuml::logging::Logger log
    ) {
        asio::co_spawn(
            executor,
            [fn = std::move(fn), log = std::move(log)]() -> asio::awaitable<void> {
                try {
                    co_await fn();
                } catch (const std::system_error &e) {
                    if (e.code() == std::errc::operation_canceled || e.code() == asio::error::operation_aborted) {
                        co_return;
                    } else {
                        throw;
                    }
                } catch (const std::exception &e) {
                    log.error("{}", e.what());
                }
                co_return;
            },
            asio::detached
        );
    }
    void spawn_cancellable_loop(
        asio::any_io_executor executor, std::function<asio::awaitable<void>()> fn, xtuml::logging::Logger log
    ) {
        asio::co_spawn(
            executor,
            [fn = std::move(fn), log = std::move(log)]() -> asio::awaitable<void> {
                while (true) {
                    try {
                        co_await fn();
                    } catch (const std::system_error &e) {
                        if (e.code() == std::errc::operation_canceled || e.code() == asio::error::operation_aborted) {
                            co_return;
                        } else {
                            throw;
                        }
                    } catch (const std::exception &e) {
                        log.error("{}", e.what());
                    }
                }
                co_return;
            },
            asio::detached
        );
    }

} // namespace amqp_asio
