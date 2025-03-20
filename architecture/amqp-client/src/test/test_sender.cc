/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include "../sender.hh"
#include "../messages.hh"
#include "amqp_asio/options.hh"
#include "amqp_asio/condition_var.hh"
#include "session_mock.hh"
#include "test_coro.hh"
#include "utils.hh"
#include <asio/defer.hpp>

using namespace amqp_asio::testing;
using namespace amqp_asio;
using namespace amqp_asio::messages;

struct SenderTest : public AsyncTest {
    using enum amqp_asio::DeliveryMode;
    using enum SenderSettleMode;
    using enum ReceiverSettleMode;

    using Message = amqp_asio::messages::Message;

    template <typename Msg>
    void send_peer_message(Msg &&msg) {
        spawn(sender->push_message(std::forward<Msg>(msg)));
    }

    asio::awaitable<void> init() {
        session = std::make_shared<StrictSession>(get_executor());
        co_return;
    }

    asio::awaitable<void> attach(amqp_asio::SenderOptions options = {}, std::optional<ulong_t> max_message_size = {}) {
        co_return co_await attach({}, options, max_message_size);
    }

    asio::awaitable<void> attach(
        std::optional<std::string> address,
        amqp_asio::SenderOptions options = {},
        std::optional<ulong_t> max_message_size = {}
    ) {

        std::optional<SenderSettleMode> ssm;
        std::optional<ReceiverSettleMode> rsm;
        switch (options.delivery_mode()) {
            case any:
                rsm = second;
                break;
            case at_most_once:
                ssm = settled;
                break;
            case at_least_once:
                ssm = unsettled;
                break;
            case exactly_once:
                ssm = unsettled;
                rsm = second;
                break;
        }

        EXPECT_CALL(*session, next_output_handle()).WillOnce(Return(2));
        sender = co_await SenderImpl<StrictSession>::create(session, options);
        auto attach_done = promise(sender->attach(address));

        auto attach = co_await session->attach_calls.pop();

        co_await sender->push_message(Attach{
            .name = attach.name,
            .handle = 42,
            .role = Role::receiver,
            .snd_settle_mode = attach.snd_settle_mode,
            .rcv_settle_mode = attach.rcv_settle_mode,
            .max_message_size = max_message_size
        });

        if (options.name()) {
            EXPECT_EQ(attach.name, options.name().value());
        }

        EXPECT_EQ(attach.handle, 2u);
        EXPECT_EQ(attach.role, Role::sender);
        EXPECT_EQ(attach.snd_settle_mode, ssm);
        EXPECT_EQ(attach.rcv_settle_mode, rsm);
        if (address) {
            EXPECT_EQ(std::get<AddressString>(std::get<Target>(attach.target.value()).address.value()), address);
        } else {
            EXPECT_FALSE(std::get<Target>(attach.target.value()).address);
        }
        EXPECT_EQ(attach.initial_delivery_count, 0);

        auto [handle, flow] = co_await session->update_flow_calls.pop();
        EXPECT_EQ(handle, 2u);
        EXPECT_EQ(flow.delivery_count, 0u);
        EXPECT_EQ(flow.link_credit, 0u);
        EXPECT_EQ(flow.available, 0u);
        EXPECT_EQ(flow.drain, false);

        co_await attach_done(asio::deferred);
    }

    asio::awaitable<void> update_flow(int count, int credit, bool drain) {
        co_await sender->push_message(Flow{.delivery_count = count, .link_credit = credit, .drain = drain});
    }

    asio::awaitable<void> detach() {
        auto detach_done = promise(sender->detach());
        auto detach = co_await session->detach_calls.pop();
        co_await sender->push_message(Detach{.handle = 42, .closed = true});

        EXPECT_EQ(detach.handle, 2u);
        EXPECT_EQ(detach.closed, true);

        co_await detach_done(asio::deferred);
    }

    asio::awaitable<void> peer_detach() {
        co_await sender->push_message(Detach{.handle = 42, .closed = true});
        auto detach = co_await session->detach_calls.pop();
        EXPECT_EQ(detach.handle, 2u);
        EXPECT_EQ(detach.closed, true);
    }

    void check_delivery_mode(
        std::optional<amqp_asio::DeliveryMode> attach_mode,
        std::optional<amqp_asio::DeliveryMode> send_mode,
        bool settled,
        std::optional<ReceiverSettleMode> rcv_mode = {}
    ) {
        run([&, this]() -> asio::awaitable<void> {
            co_await attach(amqp_asio::SenderOptions().delivery_mode(attach_mode));
            EXPECT_CALL(*session, register_unsettled_tracker);
            EXPECT_CALL(*session, settle_tracker);

            co_await update_flow(0, 1, false);
            auto tracker = co_await sender->send(Message{}, send_mode);

            auto [tx, payload] = co_await session->transfer_calls.pop();

            co_await tracker->await_sent();

            EXPECT_EQ(tx.settled.value_or(false), settled);
            if (rcv_mode) {
                auto default_rcv_mode = attach_mode && attach_mode.value() == exactly_once ? second : first;
                EXPECT_EQ(tx.rcv_settle_mode.value_or(default_rcv_mode), rcv_mode.value());
            }

            // Cleanup
            if ( !tracker->is_settled() ) {
                co_await std::static_pointer_cast<SenderImpl<StrictSession>::Tracker>(tracker)->disposition(true,{},false);
            }
            co_await tracker->await_settled();

            co_await detach();
            co_return;
        });
    }

    std::shared_ptr<StrictSession> session;
    std::shared_ptr<SenderImpl<StrictSession>> sender;

    std::string default_name = "xxx";
};

TEST_F(SenderTest, UnsolicitedAttach) {
    run([this]() -> asio::awaitable<void> {
        EXPECT_CALL(*session, next_output_handle()).WillOnce(Return(2));

        sender = co_await SenderImpl<StrictSession>::create(session, amqp_asio::SenderOptions().name("xxx"));
        co_await sender->push_message(Attach{.name = "xxx", .handle = 42, .role = Role::receiver});
        auto attach = co_await session->attach_calls.pop();
        EXPECT_EQ(attach.name, "xxx");
        EXPECT_EQ(attach.handle, 2u);
        EXPECT_EQ(attach.role, Role::sender);

        auto detach = co_await session->detach_calls.pop();
        EXPECT_EQ(detach.handle, 2u);
        EXPECT_TRUE(detach.closed);

        co_await sender->push_message(Detach{.handle = 42, .closed = true});

        co_return;
    });
}

TEST_F(SenderTest, CreateAndDetach) {
    run([this]() -> asio::awaitable<void> {
        co_await attach();
        co_await detach();
        co_return;
    });
}

TEST_F(SenderTest, PeerDetach) {
    run([this]() -> asio::awaitable<void> {
        co_await attach();
        co_await peer_detach();
        co_return;
    });
}

TEST_F(SenderTest, SendNoCredit) {
    run([this]() -> asio::awaitable<void> {
        co_await attach();

        EXPECT_CALL(*session, register_unsettled_tracker);
        EXPECT_CALL(*session, settle_tracker);

        auto tracker = co_await sender->send(Message{}, at_most_once);
        co_await wait_a_bit();
        EXPECT_EQ(sender->flow().available, 1u);
        EXPECT_EQ(sender->flow().delivery_count, 0u);
        EXPECT_EQ(sender->flow().link_credit, 0u);

        co_await update_flow(0, 1, false);
        auto [tx, payload] = co_await session->transfer_calls.pop();
        EXPECT_EQ(tx.delivery_id, 0);

        co_await tracker->await_sent();
        EXPECT_EQ(sender->flow().available, 0u);
        EXPECT_EQ(sender->flow().delivery_count, 1u);
        EXPECT_EQ(sender->flow().link_credit, 0u);

        co_await detach();
        co_return;
    });
}

TEST_F(SenderTest, SendOneCredit) {
    run([this]() -> asio::awaitable<void> {
        co_await attach();

        EXPECT_CALL(*session, register_unsettled_tracker);
        EXPECT_CALL(*session, settle_tracker);

        co_await update_flow(0, 1, false);

        auto tracker = co_await sender->send(Message{}, at_most_once);
        auto [tx, payload] = co_await session->transfer_calls.pop();
        EXPECT_EQ(tx.delivery_id, 0u);

        co_await tracker->await_sent();
        EXPECT_EQ(sender->flow().available, 0u);
        EXPECT_EQ(sender->flow().delivery_count, 1u);
        EXPECT_EQ(sender->flow().link_credit, 0u);

        co_await detach();
        co_return;
    });
}

TEST_F(SenderTest, SendMultiSlowCredit) {
    run([this]() -> asio::awaitable<void> {
        co_await attach();

        EXPECT_CALL(*session, register_unsettled_tracker).Times(3);
        EXPECT_CALL(*session, settle_tracker).Times(3);

        std::vector<std::shared_ptr<amqp_asio::Tracker::Impl>> trackers;
        trackers.push_back(co_await sender->send(Message{}, at_most_once));
        trackers.push_back(co_await sender->send(Message{}, at_most_once));
        trackers.push_back(co_await sender->send(Message{}, at_most_once));

        for (std::uint32_t i = 0; i < 3; ++i) {
            co_await wait_a_bit();
            EXPECT_EQ(sender->flow().available, 3u - i);

            co_await update_flow(i, 1, false);
            auto [tx, payload] = co_await session->transfer_calls.pop();
            EXPECT_TRUE(session->transfer_calls.empty());
            EXPECT_EQ(tx.delivery_id, i);
            co_await trackers[i]->await_sent();
            EXPECT_EQ(sender->flow().available, 3u - i - 1u);
            EXPECT_EQ(sender->flow().delivery_count, i + 1u);
            EXPECT_EQ(sender->flow().link_credit, 0u);
        }

        co_await wait_a_bit();
        EXPECT_EQ(sender->flow().available, 0u);
        EXPECT_EQ(sender->flow().delivery_count, 3u);
        EXPECT_EQ(sender->flow().link_credit, 0u);

        co_await detach();
        co_return;
    });
}

TEST_F(SenderTest, SendMultiPlentyCredit) {
    run([this]() -> asio::awaitable<void> {
        co_await attach();

        EXPECT_CALL(*session, register_unsettled_tracker).Times(3);
        EXPECT_CALL(*session, settle_tracker).Times(3);

        auto t1 = co_await sender->send(Message{}, at_most_once);
        auto t2 = co_await sender->send(Message{}, at_most_once);
        auto t3 = co_await sender->send(Message{}, at_most_once);

        co_await update_flow(0, 100, false);

        for (int i = 0; i < 3; ++i) {
            auto [tx, payload] = co_await session->transfer_calls.pop();
            EXPECT_EQ(tx.delivery_id, i);
        }

        co_await t3->await_sent();

        EXPECT_TRUE(session->transfer_calls.empty());
        EXPECT_EQ(sender->flow().available, 0u);
        EXPECT_EQ(sender->flow().delivery_count, 3u);
        EXPECT_EQ(sender->flow().link_credit, 97u);

        co_await detach();
        co_return;
    });
}

TEST_F(SenderTest, SendMultiDrainCredit) {
    run([this]() -> asio::awaitable<void> {
        co_await attach();

        EXPECT_CALL(*session, register_unsettled_tracker).Times(4);
        EXPECT_CALL(*session, settle_tracker).Times(4);

        auto t1 = co_await sender->send(Message{}, at_most_once);
        auto t2 = co_await sender->send(Message{}, at_most_once);
        auto t3 = co_await sender->send(Message{}, at_most_once);

        co_await update_flow(0, 100, true);

        for (int i = 0; i < 3; ++i) {
            auto [tx, payload] = co_await session->transfer_calls.pop();
            EXPECT_EQ(tx.delivery_id, i);
        }

        EXPECT_TRUE(session->transfer_calls.empty());
        co_await t3->await_sent();

        auto [handle, flow] = co_await session->update_flow_calls.pop();
        EXPECT_EQ(handle, 2u);
        EXPECT_EQ(flow.delivery_count, 100u);
        EXPECT_EQ(flow.link_credit, 0u);
        EXPECT_EQ(flow.available, 0u);
        EXPECT_EQ(flow.drain, true);

        co_await wait_a_bit();
        EXPECT_TRUE(session->transfer_calls.empty());
        EXPECT_EQ(sender->flow().available, 0u);
        EXPECT_EQ(sender->flow().delivery_count, 100u);
        EXPECT_EQ(sender->flow().link_credit, 0u);

        auto t4 = co_await sender->send(Message{}, at_most_once);
        co_await wait_a_bit();
        EXPECT_TRUE(session->transfer_calls.empty());
        EXPECT_EQ(sender->flow().available, 1u);
        EXPECT_EQ(sender->flow().delivery_count, 100u);
        EXPECT_EQ(sender->flow().link_credit, 0u);

        co_await update_flow(100, 100, false);
        auto [tx, payload] = co_await session->transfer_calls.pop();
        EXPECT_EQ(tx.delivery_id, 3u);

        co_await t4->await_sent();
        EXPECT_EQ(sender->flow().available, 0u);
        EXPECT_EQ(sender->flow().delivery_count, 101u);
        EXPECT_EQ(sender->flow().link_credit, 99u);

        co_await detach();
        co_return;
    });
}

TEST_F(SenderTest, SendMultiDelayCredit) {
    run([this]() -> asio::awaitable<void> {
        co_await attach();

        EXPECT_CALL(*session, register_unsettled_tracker).Times(9);
        EXPECT_CALL(*session, settle_tracker).Times(9);


        std::vector<std::shared_ptr<amqp_asio::Tracker::Impl>> trackers;
        for ( int i = 0; i < 9; ++i ) {
           trackers.push_back(co_await sender->send(Message{}, at_most_once));
        }

        co_await update_flow(0, 3, false);

        for (int i = 0; i < 3; ++i) {
            auto [tx, payload] = co_await session->transfer_calls.pop();
            EXPECT_EQ(tx.delivery_id, i);
        }
        co_await trackers[2]->await_sent();
        co_await wait_a_bit();
        EXPECT_TRUE(session->transfer_calls.empty());
        EXPECT_EQ(sender->flow().available, 6u);
        EXPECT_EQ(sender->flow().delivery_count, 3u);
        EXPECT_EQ(sender->flow().link_credit, 0u);

        co_await update_flow(2, 3, false);

        for (int i = 3; i < 5; ++i) {
            auto [tx, payload] = co_await session->transfer_calls.pop();
            EXPECT_EQ(tx.delivery_id, i);
        }

        co_await trackers[3]->await_sent();
        co_await wait_a_bit();
        EXPECT_TRUE(session->transfer_calls.empty());
        EXPECT_EQ(sender->flow().available, 4u);
        EXPECT_EQ(sender->flow().delivery_count, 5u);
        EXPECT_EQ(sender->flow().link_credit, 0u);

        co_await update_flow(4, 5, false);

        for (int i = 5; i < 9; ++i) {
            auto [tx, payload] = co_await session->transfer_calls.pop();
            EXPECT_EQ(tx.delivery_id, i);
        }

        co_await trackers[8]->await_sent();
        co_await wait_a_bit();
        EXPECT_TRUE(session->transfer_calls.empty());
        EXPECT_EQ(sender->flow().available, 0u);
        EXPECT_EQ(sender->flow().delivery_count, 9u);
        EXPECT_EQ(sender->flow().link_credit, 0u);

        co_await detach();
        co_return;
    });
}

TEST_F(SenderTest, SendDefaultSettle) {
    check_delivery_mode({}, {}, true);
}

TEST_F(SenderTest, SendAtMostOnce) {
    check_delivery_mode(at_most_once, {}, true);
}

TEST_F(SenderTest, SendAtMostOnceOverrideDefault) {
    check_delivery_mode({}, at_most_once, true);
}

TEST_F(SenderTest, SendAtMostOnceOverrideAtMostOnce) {
    check_delivery_mode(at_most_once, at_most_once, true);
}

TEST_F(SenderTest, SendAtMostOnceOverrideAtLeastOnce) {
    // Can't override
    check_delivery_mode(at_least_once, at_most_once, false, first);
}

TEST_F(SenderTest, SendAtMostOnceOverrideExactlyOnce) {
    // Can't override
    check_delivery_mode(exactly_once, at_most_once, false, second);
}

TEST_F(SenderTest, SendAtLeastOnce) {
    check_delivery_mode(at_least_once, {}, false, first);
}

TEST_F(SenderTest, SendAtLeastOnceOverrideDefault) {
    check_delivery_mode({}, at_least_once, true);
}

TEST_F(SenderTest, SendAtLeastOnceOverrideAtMostOnce) {
    // Can't override
    check_delivery_mode(at_most_once, at_most_once, true);
}
TEST_F(SenderTest, SendAtLeastOnceOverrideAtLeastOnce) {
    check_delivery_mode(at_least_once, at_least_once, false, first);
}

TEST_F(SenderTest, SendAtLeastOnceOverrideExactlyOnce) {
    check_delivery_mode(exactly_once, at_least_once, false, first);
}

TEST_F(SenderTest, SendExactlyOnce) {
    check_delivery_mode(exactly_once, {}, false, second);
}

TEST_F(SenderTest, SendExactlyOnceOverrideDefault) {
    // Can't override
    check_delivery_mode({}, exactly_once, true);
}

TEST_F(SenderTest, SendAtExactlyOnceOverrideAtMostOnce) {
    // Can't override
    check_delivery_mode(at_most_once, at_most_once, true);
}

TEST_F(SenderTest, SendAtExactlyOnceOverrideAtLeastOnce) {
    // Can't override
    check_delivery_mode(at_least_once, exactly_once, false, first);
}

TEST_F(SenderTest, SendAtExactlyOnceOverrideExactlyOnce) {
    check_delivery_mode(exactly_once, exactly_once, false, second);
}

TEST_F(SenderTest, SendSinglePart) {
    run([this]() -> asio::awaitable<void> {
        co_await attach();

        EXPECT_CALL(*session, register_unsettled_tracker);
        EXPECT_CALL(*session, settle_tracker);

        co_await update_flow(0, 1, false);

        Data data{vector_from_hex(R"('hello world!')")};

        auto payload = vector_from_hex(R"(00 53 75 a0 0c 'hello world!')");

        auto message = Message{.data = std::vector<Data>{data}};
        co_await sender->send(message, at_most_once);

        auto [tx, p] = co_await session->transfer_calls.pop();

        EXPECT_EQ(tx.delivery_id, 0);

        EXPECT_FALSE(tx.more);

        EXPECT_EQ(payload, p);

        EXPECT_EQ(sender->flow().available, 0u);
        EXPECT_EQ(sender->flow().delivery_count, 1u);
        EXPECT_EQ(sender->flow().link_credit, 0u);

        co_await detach();
        co_return;
    });
}

TEST_F(SenderTest, SendMultiPart) {
    run([this]() -> asio::awaitable<void> {
        // Set max message len on attach
        co_await attach({}, {}, 6);

        EXPECT_CALL(*session, register_unsettled_tracker);
        EXPECT_CALL(*session, settle_tracker);


        co_await update_flow(0, 1, false);

        Data data{vector_from_hex(R"('hello world!')")};

        auto payload1 = vector_from_hex(R"(00 53 75 a0 0c 'h' )");
        auto payload2 = vector_from_hex(R"('ello w')");
        auto payload3 = vector_from_hex(R"('orld!')");

        auto message = Message{.data = std::vector<Data>{data}};
        log.info("Send");
        co_await sender->send(message, at_most_once);

        log.info("Receive 1");
        auto [tx1, p1] = co_await session->transfer_calls.pop();
        log.info("Receive 2");
        auto [tx2, p2] = co_await session->transfer_calls.pop();
        log.info("Receive 3");
        auto [tx3, p3] = co_await session->transfer_calls.pop();

        log.info("Checking");
        EXPECT_EQ(tx1.delivery_id, 0u);
        EXPECT_FALSE(tx2.delivery_id.has_value());
        EXPECT_FALSE(tx3.delivery_id.has_value());

        EXPECT_TRUE(tx1.more);
        EXPECT_TRUE(tx2.more);
        EXPECT_FALSE(tx3.more);

        EXPECT_EQ(payload1, p1);
        EXPECT_EQ(payload2, p2);
        EXPECT_EQ(payload3, p3);

        EXPECT_EQ(sender->flow().available, 0u);
        EXPECT_EQ(sender->flow().delivery_count, 1u);
        EXPECT_EQ(sender->flow().link_credit, 0u);

        log.info("Detaching");
        co_await detach();
        log.info("Done");
        co_return;
    });
}
