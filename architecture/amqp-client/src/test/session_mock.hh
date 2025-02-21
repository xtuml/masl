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

#include "../link.hh"
#include "../receiver.hh"
#include "../sender.hh"
#include <asio/co_spawn.hpp>
#include <asio/detached.hpp>
#include <gmock/gmock.h>
#include <gtest/gtest.h>

namespace amqp_asio {

    inline void PrintTo(const LinkFlow &value, std::ostream *s) {
        *s << fmt::format("{}", nlohmann::json(value).dump());
    }

} // namespace amqp_asio

namespace amqp_asio::testing {
    using namespace ::testing;

    template <typename... T>
    class CallQueue {
      public:
        // void for zero args, simple for one arg, tuple for multiple
        using Ret = std::conditional_t<
            sizeof...(T) == 0,
            void,
            std::conditional_t<sizeof...(T) == 1, std::tuple_element_t<0, std::tuple<T...>>, std::tuple<T...>>>;

        CallQueue(asio::any_io_executor executor, std::string name)
            : calls_{executor, std::numeric_limits<std::size_t>::max()}, timeout_(executor), name_{name} {}

        template <typename... A>
        void push(A &&...args) {
            calls_.async_send(std::error_code{}, std::forward<A>(args)..., asio::detached);
        }
        asio::awaitable<Ret> pop() {
            timeout_.expires_after(100ms);
            timeout_.async_wait([&](auto ec) {
                if ( ec != asio::error::operation_aborted ) {
                    ADD_FAILURE() << "Timeout waiting for " << name_;
                    calls_.cancel();
                }
            });
            auto ret = co_await calls_.async_receive();
            timeout_.cancel();
            co_return ret;
        }
        bool empty() {
            return !calls_.ready();
        }

        ~CallQueue() {
            EXPECT_TRUE(empty()) << "Pending calls to " << name_ << " on test exit";
        }

      private:
        asio::experimental::channel<void(std::error_code, T...)> calls_;
        asio::steady_timer timeout_;
        std::string name_;
    };

    struct SessionMock {

        using Link = LinkImpl<StrictMock<SessionMock>>;
        using Receiver = ReceiverImpl<StrictMock<SessionMock>>;
        using Delivery = DeliveryImpl<Receiver>;
        using Sender = SenderImpl<StrictMock<SessionMock>>;
        using Tracker = TrackerImpl<Sender>;

        SessionMock(asio::any_io_executor executor)
            : executor_{executor},
              open_calls{executor, "send_message(Open)"},
              begin_calls{executor, "send_message(Begin)"},
              attach_calls{executor, "send_message(Attach)"},
              flow_calls{executor, "send_message(Flow)"},
              transfer_calls{executor, "send_message(Transfer)"},
              disposition_calls{executor, "send_message(Disposition)"},
              detach_calls{executor, "send_message(Detach)"},
              end_calls{executor, "send_message(End)"},
              close_calls{executor, "send_message(Close)"},
              update_flow_calls{executor, "update_flow()"} {}

        asio::any_io_executor get_executor() {
            return executor_;
        }

        void register_local_sender(std::shared_ptr<Sender>) {}
        void register_local_receiver(std::shared_ptr<Receiver>) {}
        void register_remote_link(std::shared_ptr<Link>) {}
        void deregister_local_sender(std::shared_ptr<Sender>) {}
        void deregister_local_receiver(std::shared_ptr<Receiver>) {}
        void deregister_remote_link(std::shared_ptr<Link>) {}

        asio::awaitable<void> send_message(messages::Performative performative, messages::AmqpPayload payload = {}) {
            std::visit(
                overload{
                    [&](messages::Open m) {
                        open_calls.push(m);
                    },
                    [&](messages::Begin m) {
                        begin_calls.push(m);
                    },
                    [&](messages::Attach m) {
                        attach_calls.push(m);
                    },
                    [&](messages::Flow m) {
                        flow_calls.push(m);
                    },
                    [&](messages::Transfer m) {
                        transfer_calls.push(m, payload);
                    },
                    [&](messages::Disposition m) {
                        disposition_calls.push(m);
                    },
                    [&](messages::Detach m) {
                        detach_calls.push(m);
                    },
                    [&](messages::End m) {
                        end_calls.push(m);
                    },
                    [&](messages::Close m) {
                        close_calls.push(m);
                    }
                },
                performative
            );
            co_return;
        }
        MOCK_METHOD(void, send_message_async, (messages::Performative performative, messages::AmqpPayload payload));

        asio::awaitable<void> update_flow(messages::Handle handle, const LinkFlow &flow) {
            update_flow_calls.push(handle, flow);
            co_return;
        }

        MOCK_METHOD(messages::Handle, next_output_handle, ());

        MOCK_METHOD(void, settle_delivery, (messages::DeliveryNumber));
        MOCK_METHOD(void, settle_tracker, (messages::DeliveryNumber));

        MOCK_METHOD(void, register_unsettled_delivery, (const std::shared_ptr<Delivery> &delivery));
        MOCK_METHOD(void, register_unsettled_tracker, (const std::shared_ptr<Tracker> &tracker));

        messages::DeliveryNumber next_delivery_number() {
            return next_delivery_no_++;
        }

        asio::awaitable<messages::Open> open_sent() {
            co_return co_await open_calls.pop();
        }

        asio::awaitable<messages::Attach> attach_sent() {
            co_return co_await attach_calls.pop();
        }

        asio::awaitable<messages::Flow> flow_sent() {
            co_return co_await flow_calls.pop();
        }

        asio::awaitable<std::tuple<messages::Transfer, messages::AmqpPayload>> transfer_sent() {
            co_return co_await transfer_calls.pop();
        }

        asio::awaitable<messages::Disposition> disposition_sent() {
            co_return co_await disposition_calls.pop();
        }
        asio::awaitable<messages::Detach> detach_sent() {
            co_return co_await detach_calls.pop();
        }
        asio::awaitable<messages::End> end_sent() {
            co_return co_await end_calls.pop();
        }
        asio::awaitable<messages::Close> close_sent() {
            co_return co_await close_calls.pop();
        }
        asio::awaitable<std::tuple<messages::Handle, LinkFlow>> update_flow_called() {
            co_return co_await update_flow_calls.pop();
        }

      private:
        asio::any_io_executor executor_;
        messages::DeliveryNumber next_delivery_no_{};

      public:
        CallQueue<messages::Open> open_calls;
        CallQueue<messages::Begin> begin_calls;
        CallQueue<messages::Attach> attach_calls;
        CallQueue<messages::Flow> flow_calls;
        CallQueue<messages::Transfer, messages::AmqpPayload> transfer_calls;
        CallQueue<messages::Disposition> disposition_calls;
        CallQueue<messages::Detach> detach_calls;
        CallQueue<messages::End> end_calls;
        CallQueue<messages::Close> close_calls;
        CallQueue<messages::Handle, LinkFlow> update_flow_calls;
    };

    using StrictSession = StrictMock<SessionMock>;

} // namespace amqp_asio::testing
