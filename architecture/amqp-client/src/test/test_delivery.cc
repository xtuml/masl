/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include "../delivery.hh"
#include "../messages.hh"
#include "amqp_asio/condition_var.hh"
#include "test_coro.hh"

#include <gmock/gmock.h>
#include <gtest/gtest.h>

using namespace testing;
using namespace amqp_asio::testing;
using namespace amqp_asio;
namespace messages = amqp_asio::messages;

struct ReceiverMock {

    ReceiverMock(asio::any_io_executor executor)
        : executor_{executor} {}

    asio::any_io_executor get_executor() const {
        return executor_;
    }
    messages::ReceiverSettleMode default_settle_mode() {
        return messages::ReceiverSettleMode::first;
    }

    MOCK_METHOD(void, settle, (messages::DeliveryNumber id));

    asio::awaitable<void> publish_pending_delivery() {
        co_return publish_pending_delivery_async();
    }
    MOCK_METHOD(void, publish_pending_delivery_async, ());

    asio::awaitable<void>
    send_disposition(messages::DeliveryNumber id, bool settled, std::optional<messages::DeliveryState> state) {
        co_return send_disposition_async(id, settled, state);
    }

    MOCK_METHOD(
        void,
        send_disposition_async,
        (messages::DeliveryNumber id, bool settled, std::optional<messages::DeliveryState> state)
    );

  private:
    asio::any_io_executor executor_;
};

using StrictReceiver = StrictMock<ReceiverMock>;

struct DeliveryTest : public AsyncTest {

    messages::DeliveryNumber id{1};
    messages::DeliveryTag tag{std::byte(0), std::byte(1)};

    messages::Data data1{std::byte(1), std::byte(2)};
    messages::Data data2{std::byte(3), std::byte(4)};
    messages::Data data3{std::byte(5), std::byte(6)};
    messages::AmqpPayload payload1{std::byte(1), std::byte(2)};
    messages::AmqpPayload payload2{std::byte(1), std::byte(2), std::byte(3), std::byte(4)};
    messages::AmqpPayload payload3{std::byte(1), std::byte(2), std::byte(3), std::byte(4), std::byte(5), std::byte(6)};
};

TEST_F(DeliveryTest, Abort) {
    run([this]() -> asio::awaitable<void> {
        auto receiver = std::make_shared<StrictReceiver>(get_executor());

        auto delivery = co_await DeliveryImpl<StrictReceiver>::create(
            receiver, id, tag, messages::ReceiverSettleMode::first, false
        );

        EXPECT_CALL(*receiver, settle(id));
        co_await delivery->transfer({.aborted = true}, data1);
        co_return;
    });
}

TEST_F(DeliveryTest, PreSettledSingle) {
    run([this]() -> asio::awaitable<void> {
        auto published = Notification(get_executor());
        auto receiver = std::make_shared<StrictReceiver>(get_executor());

        auto delivery = co_await DeliveryImpl<StrictReceiver>::create(
            receiver, id, tag, messages::ReceiverSettleMode::first, false
        );

        EXPECT_CALL(*receiver, publish_pending_delivery_async()).WillOnce([&]() {
            published.notify();
        });
        EXPECT_CALL(*receiver, settle(id));

        co_await delivery->transfer({.settled = true, .more = false}, data1);
        co_await published.wait();
        EXPECT_EQ(delivery->raw_payload(), payload1);
        co_await delivery->accept();
        co_return;
    });
}

TEST_F(DeliveryTest, PreSettledMulti) {
    run([this]() -> asio::awaitable<void> {
        auto published = Notification(get_executor());
        auto receiver = std::make_shared<StrictReceiver>(get_executor());

        auto delivery = co_await DeliveryImpl<StrictReceiver>::create(
            receiver, id, tag, messages::ReceiverSettleMode::first, false
        );
        EXPECT_CALL(*receiver, publish_pending_delivery_async()).WillOnce([&]() {
            published.notify();
        });
        EXPECT_CALL(*receiver, settle(1));

        co_await delivery->transfer({.settled = true, .more = true}, data1);
        co_await delivery->transfer({.more = true}, data2);
        co_await delivery->transfer({.more = false}, data3);
        co_await published.wait();
        EXPECT_EQ(delivery->raw_payload(), payload3);
        co_await delivery->accept();

        co_return;
    });
}

TEST_F(DeliveryTest, InterleavedSettle) {
    run([this]() -> asio::awaitable<void> {
        auto receiver = std::make_shared<StrictReceiver>(get_executor());
        auto published = Notification(get_executor());

        auto delivery = co_await DeliveryImpl<StrictReceiver>::create(
            receiver, id, tag, messages::ReceiverSettleMode::first, false
        );
        MockFunction<void(std::string name)> check;

        EXPECT_CALL(*receiver, publish_pending_delivery_async()).WillOnce([&]() {
            published.notify();
        });
        EXPECT_CALL(*receiver, settle(1));

        co_await delivery->transfer({.settled = false, .more = true}, data1);
        co_await delivery->disposition(true, {}, false);
        co_await delivery->transfer({.more = true}, data2);
        co_await delivery->transfer({.more = false}, data3);
        co_await published.wait();
        EXPECT_EQ(delivery->raw_payload(), payload3);
        co_await delivery->accept();

        co_return;
    });
}
TEST_F(DeliveryTest, InterleavedSettleAutoAccept) {
    run([this]() -> asio::awaitable<void> {
        auto published = Notification(get_executor());
        auto receiver = std::make_shared<StrictReceiver>(get_executor());

        auto delivery =
            co_await DeliveryImpl<StrictReceiver>::create(receiver, id, tag, messages::ReceiverSettleMode::first, true);
        MockFunction<void(std::string name)> check;

        EXPECT_CALL(*receiver, publish_pending_delivery_async()).WillOnce([&]() {
            published.notify();
        });
        EXPECT_CALL(*receiver, settle(1));

        co_await delivery->transfer({.settled = false, .more = true}, data1);
        co_await delivery->disposition(true, {}, false);
        co_await delivery->transfer({.more = true}, data2);
        co_await delivery->transfer({.more = false}, data3);
        co_await published.wait();
        EXPECT_EQ(delivery->raw_payload(), payload3);

        co_return;
    });
}
TEST_F(DeliveryTest, PreSettledAutoAccept) {
    run([this]() -> asio::awaitable<void> {
        auto published = Notification(get_executor());
        auto receiver = std::make_shared<StrictReceiver>(get_executor());

        auto delivery =
            co_await DeliveryImpl<StrictReceiver>::create(receiver, id, tag, messages::ReceiverSettleMode::first, true);

        EXPECT_CALL(*receiver, publish_pending_delivery_async()).WillOnce([&]() {
            published.notify();
        });
        EXPECT_CALL(*receiver, settle(id));

        co_await delivery->transfer({.settled = true, .more = false}, data1);
        co_await published.wait();
        EXPECT_EQ(delivery->raw_payload(), payload1);
        co_return;
    });
}

TEST_F(DeliveryTest, PostSettle) {
    run([this]() -> asio::awaitable<void> {
        auto published = Notification(get_executor());
        auto receiver = std::make_shared<StrictReceiver>(get_executor());

        auto delivery = co_await DeliveryImpl<StrictReceiver>::create(
            receiver, id, tag, messages::ReceiverSettleMode::first, false
        );

        EXPECT_CALL(*receiver, publish_pending_delivery_async()).WillOnce([&]() {
            published.notify();
        });
        EXPECT_CALL(*receiver, settle(1));

        co_await delivery->transfer({.settled = false, .more = true}, data1);
        co_await delivery->transfer({.more = false}, data2);
        co_await published.wait();
        EXPECT_EQ(delivery->raw_payload(), payload2);
        co_await delivery->disposition(true, {}, false);
        co_await delivery->accept();

        co_return;
    });
}

TEST_F(DeliveryTest, SettleFirstSingle) {
    run([this]() -> asio::awaitable<void> {
        auto published = Notification(get_executor());
        auto receiver = std::make_shared<StrictReceiver>(get_executor());

        auto delivery = co_await DeliveryImpl<StrictReceiver>::create(
            receiver, id, tag, messages::ReceiverSettleMode::first, false
        );

        EXPECT_CALL(*receiver, publish_pending_delivery_async()).WillOnce([&]() {
            published.notify();
        });

        std::optional<messages::DeliveryState> accepted = messages::Accepted{};
        EXPECT_CALL(*receiver, send_disposition_async(id, true, accepted));
        EXPECT_CALL(*receiver, settle(id));

        co_await delivery->transfer({}, data1);
        co_await published.wait();
        EXPECT_EQ(delivery->raw_payload(), payload1);
        co_await delivery->accept();
        co_return;
    });
}

TEST_F(DeliveryTest, SettleFirstMulti) {
    run([this]() -> asio::awaitable<void> {
        auto published = Notification(get_executor());
        auto receiver = std::make_shared<StrictReceiver>(get_executor());

        auto delivery = co_await DeliveryImpl<StrictReceiver>::create(
            receiver, id, tag, messages::ReceiverSettleMode::first, false
        );

        EXPECT_CALL(*receiver, publish_pending_delivery_async()).WillOnce([&]() {
            published.notify();
        });

        std::optional<messages::DeliveryState> accepted = messages::Accepted{};
        EXPECT_CALL(*receiver, send_disposition_async(id, true, accepted));
        EXPECT_CALL(*receiver, settle(id));

        co_await delivery->transfer({.more = true}, data1);
        co_await delivery->transfer({.more = true}, data2);
        co_await delivery->transfer({.more = false}, data3);
        co_await published.wait();
        EXPECT_EQ(delivery->raw_payload(), payload3);
        co_await delivery->accept();

        co_return;
    });
}

TEST_F(DeliveryTest, SettleFirstAutoAccept) {
    run([this]() -> asio::awaitable<void> {
        auto published = Notification(get_executor());
        auto receiver = std::make_shared<StrictReceiver>(get_executor());

        auto delivery =
            co_await DeliveryImpl<StrictReceiver>::create(receiver, id, tag, messages::ReceiverSettleMode::first, true);

        EXPECT_CALL(*receiver, publish_pending_delivery_async()).WillOnce([&]() {
            published.notify();
        });

        std::optional<messages::DeliveryState> accepted = messages::Accepted{};
        EXPECT_CALL(*receiver, send_disposition_async(id, true, accepted));
        EXPECT_CALL(*receiver, settle(id));

        co_await delivery->transfer({}, data1);
        co_await published.wait();
        EXPECT_EQ(delivery->raw_payload(), payload1);
        co_return;
    });
}

TEST_F(DeliveryTest, SettleSecond) {
    run([this]() -> asio::awaitable<void> {
        auto disposition_sent = Notification(get_executor());
        auto published = Notification(get_executor());

        auto receiver = std::make_shared<StrictReceiver>(get_executor());

        auto delivery = co_await DeliveryImpl<StrictReceiver>::create(
            receiver, id, tag, messages::ReceiverSettleMode::second, false
        );

        EXPECT_CALL(*receiver, publish_pending_delivery_async()).WillOnce([&]() {
            published.notify();
        });

        std::optional<messages::DeliveryState> accepted = messages::Accepted{};
        EXPECT_CALL(*receiver, send_disposition_async(id, false, accepted)).WillOnce([&]() {
            disposition_sent.notify();
        });

        EXPECT_CALL(*receiver, settle(id));

        co_await delivery->transfer({}, data1);
        co_await published.wait();
        EXPECT_EQ(delivery->raw_payload(), payload1);
        co_await delivery->accept();
        co_await disposition_sent.wait();
        co_await delivery->disposition(true, {}, false);
        co_return;
    });
}

TEST_F(DeliveryTest, SettleSecondAutoAccept) {
    run([this]() -> asio::awaitable<void> {
        auto disposition_sent = Notification(get_executor());
        auto published = Notification(get_executor());

        auto receiver = std::make_shared<StrictReceiver>(get_executor());

        auto delivery = co_await DeliveryImpl<StrictReceiver>::create(
            receiver, id, tag, messages::ReceiverSettleMode::second, true
        );

        EXPECT_CALL(*receiver, publish_pending_delivery_async()).WillOnce([&]() {
            published.notify();
        });

        std::optional<messages::DeliveryState> accepted = messages::Accepted{};
        EXPECT_CALL(*receiver, send_disposition_async(id, false, accepted)).WillOnce([&]() {
            disposition_sent.notify();
        });

        EXPECT_CALL(*receiver, settle(id));

        co_await delivery->transfer({}, data1);
        co_await published.wait();
        EXPECT_EQ(delivery->raw_payload(), payload1);
        co_await disposition_sent.wait();
        co_await delivery->disposition(true, {}, false);
        co_return;
    });
}
