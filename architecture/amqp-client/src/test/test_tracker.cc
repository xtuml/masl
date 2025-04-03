/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include "../tracker.hh"
#include "../messages.hh"
#include "amqp_asio/condition_var.hh"
#include "test_coro.hh"

#include <gmock/gmock.h>
#include <gtest/gtest.h>

using namespace testing;
using namespace amqp_asio::testing;
using namespace amqp_asio;
namespace msg = amqp_asio::messages;

struct SenderMock {

    SenderMock(asio::any_io_executor executor)
        : executor_{executor} {}

    asio::any_io_executor get_executor() const {
        return executor_;
    }

    MOCK_METHOD(void, settle, (msg::DeliveryNumber id));

    asio::awaitable<void>
    send_disposition(msg::DeliveryNumber id, bool settled, std::optional<msg::DeliveryState> state) {
        co_return send_disposition_async(id, settled, state);
    }

    MOCK_METHOD(
        void,
        send_disposition_async,
        (msg::DeliveryNumber id, bool settled, std::optional<msg::DeliveryState> state)
    );

  private:
    asio::any_io_executor executor_;
};

using StrictSender = StrictMock<SenderMock>;

struct TrackerTest : public AsyncTest {
    msg::DeliveryNumber id{1};
    msg::Message message{};

};


TEST_F(TrackerTest, AtMostOnce) {
    run([this]() -> asio::awaitable<void> {
        auto published = Notification(get_executor());
        auto sender = std::make_shared<StrictSender>(get_executor());

        auto tracker = co_await TrackerImpl<StrictSender>::create(
            sender, message, {}
        );

        EXPECT_FALSE(tracker->is_sent());

        EXPECT_CALL(*sender, settle(id));

        co_await tracker->start(id);

        co_await tracker->sent(true,msg::ReceiverSettleMode::first);

        co_await tracker->await_settled();

        EXPECT_TRUE(tracker->is_settled());

        co_return;
    });
}

TEST_F(TrackerTest, AtLeastOnce) {
    run([this]() -> asio::awaitable<void> {
        auto published = Notification(get_executor());
        auto sender = std::make_shared<StrictSender>(get_executor());

        auto tracker = co_await TrackerImpl<StrictSender>::create(
            sender, message, {}
        );

        EXPECT_FALSE(tracker->is_sent());

        EXPECT_CALL(*sender, settle(id));

        co_await tracker->start(id);
        co_await tracker->sent(false,msg::ReceiverSettleMode::first);
        co_await tracker->await_sent();
        EXPECT_FALSE(tracker->is_settled());

        co_await tracker->disposition(true,{},false);
        co_await tracker->await_settled();
        EXPECT_TRUE(tracker->is_settled());

        co_return;
    });
}

TEST_F(TrackerTest, ExactlyOnce) {
    run([this]() -> asio::awaitable<void> {
        auto published = Notification(get_executor());
        auto sender = std::make_shared<StrictSender>(get_executor());

        auto tracker = co_await TrackerImpl<StrictSender>::create(
            sender, message, {}
        );

        EXPECT_FALSE(tracker->is_sent());


        co_await tracker->start(id);
        co_await tracker->sent(false,msg::ReceiverSettleMode::second);
        co_await tracker->await_sent();
        EXPECT_FALSE(tracker->is_settled());

        EXPECT_CALL(*sender,send_disposition_async(1,true, std::make_optional(msg::DeliveryState{msg::Accepted{}})));
        EXPECT_CALL(*sender, settle(id));
 
        co_await tracker->disposition(false,msg::Accepted{},false);

        co_await tracker->await_settled();
        EXPECT_TRUE(tracker->is_settled());

        co_return;
    });
}

TEST_F(TrackerTest, ExactlyOnceNonFinal) {
    run([this]() -> asio::awaitable<void> {
        auto published = Notification(get_executor());
        auto sender = std::make_shared<StrictSender>(get_executor());

        auto tracker = co_await TrackerImpl<StrictSender>::create(
            sender, message, {}
        );

        EXPECT_FALSE(tracker->is_sent());


        co_await tracker->start(id);
        co_await tracker->sent(false,msg::ReceiverSettleMode::second);
        co_await tracker->await_sent();
        EXPECT_FALSE(tracker->is_settled());

        co_await tracker->disposition(false,msg::Received{},false);
        co_await wait_a_bit();
        EXPECT_FALSE(tracker->is_settled());

        EXPECT_CALL(*sender,send_disposition_async(1,true, std::make_optional(msg::DeliveryState{msg::Accepted{}})));
        EXPECT_CALL(*sender, settle(id));

        co_await tracker->disposition(false,msg::Accepted{},false);
        co_await tracker->await_settled();
        EXPECT_TRUE(tracker->is_settled());

        co_return;
    });
}
