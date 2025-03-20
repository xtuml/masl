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

#include "amqp_asio/condition_var.hh"
#include <asio/co_spawn.hpp>
#include <asio/detached.hpp>
#include <asio/io_context.hpp>
#include <asio/steady_timer.hpp>
#include <chrono>
#include <gmock/gmock.h>
#include <gtest/gtest.h>
#include <logging/log.hh>
#include <asio/experimental/promise.hpp>
#include <asio/experimental/use_promise.hpp>

using namespace std::literals;

namespace amqp_asio::testing {
    using namespace ::testing;

    class AsyncTest : public Test {
      protected:
        AsyncTest()
            : executor_(ctx_.get_executor()),
              timer_(executor_),
              log{"test.{}.{}", UnitTest::GetInstance()->current_test_info()->test_suite_name(), UnitTest::GetInstance()->current_test_info()->name()} {}

        template <typename Fn>
        void run(Fn &&fn) {
            asio::co_spawn(executor_, run_async(std::forward<Fn>(fn)), asio::detached);
            ctx_.run();
        }

        virtual asio::awaitable<void> init() {
            co_return;
        }

        virtual asio::awaitable<void> teardown() {
            co_return;
        }

        auto get_executor() {
            return executor_;
        }

        asio::awaitable<void> wait_a_bit(std::chrono::milliseconds d = 10ms) {
            timer_.expires_from_now(d);
            co_await timer_.async_wait();
        }

        template <typename Coro>
        void spawn(Coro &&coro) {
            asio::co_spawn(get_executor(), std::forward<Coro>(coro), asio::detached);
        }

        template <typename Coro>
        auto promise(Coro &&coro) {
            return asio::co_spawn(get_executor(), std::forward<Coro>(coro), asio::experimental::use_promise);
        }

      private:
        template <typename Fn>
        asio::awaitable<void> run_async(Fn &&fn) {
            co_await init();
            co_await fn();
            co_await teardown();
            co_return;
        }

      private:
        asio::io_context ctx_;
        asio::any_io_executor executor_;
        asio::steady_timer timer_;

      protected:
        xtuml::logging::Logger log;
    };

    class Notification {
      public:
        Notification(asio::any_io_executor executor)
            : condition_(executor) {}

        void notify() {
            condition_.notify();
        }

        asio::awaitable<void> wait() {
            co_await condition_.wait();
            co_return;
        }

      private:
        amqp_asio::ConditionVar condition_;
    };

} // namespace amqp_asio::testing
