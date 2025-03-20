/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include "../receiver.hh"
#include "../messages.hh"
#include "amqp_asio/options.hh"
#include "amqp_asio/condition_var.hh"
#include "session_mock.hh"
#include "test_coro.hh"

using namespace amqp_asio::testing;
using namespace amqp_asio;
using namespace amqp_asio::messages;

struct ReceiverTest : public AsyncTest {

    template <typename Msg>
    void send_peer_message(Msg &&msg) {
        spawn(receiver->push_message(std::forward<Msg>(msg)));
    }

    asio::awaitable<void> init() {
        session = std::make_shared<StrictSession>(get_executor());
        co_return;
    }

    template <typename Performative>
    auto holds_type() {
        return Truly([](auto tx) -> bool {
            return std::holds_alternative<Performative>(tx);
        });
    }

    asio::awaitable<void> attach(amqp_asio::ReceiverOptions options = {}) {
        EXPECT_CALL(*session, next_output_handle()).WillOnce(Return(2));
        receiver = co_await ReceiverImpl<StrictSession>::create(session, options);
        auto attach_done = promise(receiver->attach("addr"));
        auto attach = co_await session->attach_calls.pop();

        co_await receiver->push_message(Attach{.name = attach.name, .handle = 42, .role = Role::receiver});

        if (options.name()) {
            EXPECT_EQ(attach.name, options.name().value());
        }
        EXPECT_EQ(attach.handle, 2u);
        EXPECT_EQ(attach.role, Role::receiver);
        EXPECT_EQ(attach.source, Sources{Source{.address = "addr"}});

        auto [handle, flow] = co_await session->update_flow_calls.pop();
        EXPECT_EQ(handle, 2u);
        EXPECT_EQ(flow.delivery_count, 0u);
        EXPECT_EQ(flow.link_credit, std::max(options.initial_credit(), options.auto_credit_high_water()));
        EXPECT_EQ(flow.available, 0u);
        EXPECT_EQ(flow.drain, false);

        co_await attach_done(asio::deferred);
    }

    asio::awaitable<void> detach() {
        auto detach_done = promise(receiver->detach());
        auto detach = co_await session->detach_calls.pop();
        co_await receiver->push_message(Detach{.handle = 42, .closed = true});

        EXPECT_EQ(detach.handle, 2u);
        EXPECT_EQ(detach.closed, true);

        co_await detach_done(asio::deferred);
    }

    asio::awaitable<void> peer_detach() {
        send_peer_message(Detach{.handle = 42, .closed = true});
        auto detach = co_await session->detach_calls.pop();
        EXPECT_EQ(detach.handle, 2u);
        EXPECT_EQ(detach.closed, true);
    }

    std::shared_ptr<StrictSession> session;
    std::shared_ptr<ReceiverImpl<StrictSession>> receiver;
};

TEST_F(ReceiverTest, UnsolicitedAttach) {
    run([this]() -> asio::awaitable<void> {
        EXPECT_CALL(*session, next_output_handle()).WillOnce(Return(2));

        receiver = co_await ReceiverImpl<StrictSession>::create(session, amqp_asio::ReceiverOptions().name("xxx"));

        co_await receiver->push_message(Attach{.name = "xxx", .handle = 42, .role = Role::receiver});
        auto attach = co_await session->attach_calls.pop();
        EXPECT_EQ(attach.name, "xxx");
        EXPECT_EQ(attach.handle, 2u);
        EXPECT_EQ(attach.role, Role::receiver);

        auto detach = co_await session->detach_calls.pop();
        EXPECT_EQ(detach.handle, 2u);
        EXPECT_TRUE(detach.closed);

        send_peer_message(Detach{.handle = 42, .closed = true});

        co_return;
    });
}

TEST_F(ReceiverTest, CreateAndDetach) {
    run([this]() -> asio::awaitable<void> {
        co_await attach();
        co_await detach();
        co_return;
    });
}

TEST_F(ReceiverTest, PeerDetach) {
    run([this]() -> asio::awaitable<void> {
        co_await attach();
        co_await peer_detach();
        co_return;
    });
}

TEST_F(ReceiverTest, ReceiveSettled) {
    run([this]() -> asio::awaitable<void> {
        co_await attach();

        EXPECT_CALL(*session, register_unsettled_delivery(_));
        EXPECT_CALL(*session, settle_delivery(1));

        co_await receiver->push_message(
            Transfer{.handle = 42, .delivery_id = 1, .delivery_tag = {{std::byte(1)}}, .settled = true}
        );
        auto msg = co_await receiver->receive();

        co_await detach();
        co_return;
    });
}

TEST_F(ReceiverTest, ReceiveUnsettled) {
    run([this]() -> asio::awaitable<void> {
        co_await attach();

        EXPECT_CALL(*session, register_unsettled_delivery(_));
        EXPECT_CALL(*session, settle_delivery(1));

        co_await receiver->push_message(
           Transfer{.handle = 42, .delivery_id = 1, .delivery_tag = {{std::byte(1)}}, .settled = false}
        );

        auto disposition = co_await session->disposition_calls.pop();
        EXPECT_EQ(disposition.role, Role::receiver);
        EXPECT_EQ(disposition.first,1u);
        EXPECT_EQ(disposition.settled,true);
        EXPECT_EQ(disposition.state,DeliveryState{Accepted{}});

        auto msg = co_await receiver->receive();

        co_await detach();
        co_return;
    });
}

TEST_F(ReceiverTest, ReceiveUnsettledDelayAccept) {
    run([this]() -> asio::awaitable<void> {

        co_await attach(amqp_asio::ReceiverOptions().auto_accept(false));

        EXPECT_CALL(*session, register_unsettled_delivery(_));

        co_await receiver->push_message(
            Transfer{.handle = 42, .delivery_id = 1, .delivery_tag = {{std::byte(1)}}, .settled = false}
        );

        auto msg = std::static_pointer_cast<SessionMock::Delivery>(co_await receiver->receive());
        EXPECT_CALL(*session, settle_delivery(1));
        co_await msg->accept();

        auto disposition = co_await session->disposition_calls.pop();
        EXPECT_EQ(disposition.role, Role::receiver);
        EXPECT_EQ(disposition.first,1u);
        EXPECT_EQ(disposition.settled,true);
        EXPECT_EQ(disposition.state,DeliveryState{Accepted{}});

        co_await detach();
        co_return;
    });
}

TEST_F(ReceiverTest, ReceiveSettleSecond) {
    run([this]() -> asio::awaitable<void> {

        co_await attach();

        EXPECT_CALL(*session, register_unsettled_delivery(_));

        co_await receiver->push_message(Transfer{
            .handle = 42,
            .delivery_id = 1,
            .delivery_tag = {{std::byte(1)}},
            .settled = false,
            .rcv_settle_mode = ReceiverSettleMode::second
        });

        auto disposition = co_await session->disposition_calls.pop();
        EXPECT_EQ(disposition.role, Role::receiver);
        EXPECT_EQ(disposition.first,1u);
        EXPECT_EQ(disposition.settled,false);
        EXPECT_EQ(disposition.state,DeliveryState{Accepted{}});

        auto msg = std::static_pointer_cast<SessionMock::Delivery>(co_await receiver->receive());

        EXPECT_CALL(*session, settle_delivery(1));
        co_await msg->disposition(true, Accepted{}, false);
        co_await detach();
        co_return;
    });
}

TEST_F(ReceiverTest, ReceiveSettleSecondDelayAccept) {
    run([this]() -> asio::awaitable<void> {
        co_await attach(amqp_asio::ReceiverOptions().auto_accept(false));

        EXPECT_CALL(*session, register_unsettled_delivery(_));
        co_await receiver->push_message(Transfer{
            .handle = 42,
            .delivery_id = 1,
            .delivery_tag = {{std::byte(1)}},
            .settled = false,
            .rcv_settle_mode = ReceiverSettleMode::second
        });

        auto msg = std::static_pointer_cast<SessionMock::Delivery>(co_await receiver->receive());
        co_await msg->accept();

        auto disposition = co_await session->disposition_calls.pop();
        EXPECT_EQ(disposition.role, Role::receiver);
        EXPECT_EQ(disposition.first,1u);
        EXPECT_EQ(disposition.settled,false);
        EXPECT_EQ(disposition.state,DeliveryState{Accepted{}});

        EXPECT_CALL(*session, settle_delivery(1u));
        co_await msg->disposition(true, Accepted{}, false);
        co_await detach();
        co_return;
    });
}

TEST_F(ReceiverTest, AutoCredit) {
    run([this]() -> asio::awaitable<void> {
        co_await attach(amqp_asio::ReceiverOptions().auto_credit_low_water(1).auto_credit_high_water(3));

        EXPECT_CALL(*session, register_unsettled_delivery(_));
        EXPECT_CALL(*session, settle_delivery(1));
        co_await receiver->push_message(
            Transfer{.handle = 42, .delivery_id = 1, .delivery_tag = {{std::byte(1)}}, .settled = true}
        );
        co_await receiver->receive();

        EXPECT_CALL(*session, register_unsettled_delivery(_));
        EXPECT_CALL(*session, settle_delivery(2));
        co_await receiver->push_message(
            Transfer{.handle = 42, .delivery_id = 2, .delivery_tag = {{std::byte(2)}}, .settled = true}
        );
        co_await receiver->receive();

        auto [handle,flow] = co_await session->update_flow_calls.pop();
        EXPECT_EQ(handle,2u);
        EXPECT_EQ(flow.delivery_count,2u );
        EXPECT_EQ(flow.link_credit,3u );
        EXPECT_EQ(flow.available,0u );
        EXPECT_EQ(flow.drain,false );


        co_await detach();
        co_return;
    });
}
