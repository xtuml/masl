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

#include "amqp_asio/tracker.hh"
#include "messages.hh"
#include "decoder.hh"
#include "fsm.hh"
#include "amqp_asio/options.hh"

namespace amqp_asio {
    std::vector<std::byte> unique_tag();

    template <typename Sender>
    class TrackerImpl : public amqp_asio::Tracker::Impl, public std::enable_shared_from_this<TrackerImpl<Sender>> {
      public:
        using Self = std::shared_ptr<TrackerImpl>;

        using DeliveryNumber = messages::DeliveryNumber;
        using DeliveryTag = messages::DeliveryTag;
        using SenderSettleMode = messages::SenderSettleMode;
        using DeliveryState = messages::DeliveryState;
        using Payload = messages::AmqpPayload;
        using Message = messages::Message;
        using Accepted = messages::Accepted;
        using Error = messages::Error;
        using Rejected = messages::Rejected;
        using Released = messages::Released;

        static asio::awaitable<Self>
        create(std::shared_ptr<Sender> sender, DeliveryNumber id, Message message, std::optional<DeliveryMode> delivery_mode) {
            auto tracker = std::make_shared<TrackerImpl>(std::move(sender), id, std::move(message), delivery_mode);
            co_await tracker->init();
            co_return tracker;
        }

        TrackerImpl(std::shared_ptr<Sender> sender, DeliveryNumber id, Message message, std::optional<DeliveryMode> delivery_mode)
            : id_{id},
              tag_{unique_tag()},
              delivery_mode_{delivery_mode},
              message_{std::move(message)},
              sender_{std::move(sender)},
              sm_(get_executor(), "amqp_asio.tracker.sm.{}", id_) {}

        auto self() const {
            return this->shared_from_this();
        }
        auto self() {
            return this->shared_from_this();
        }

        asio::awaitable<void> init() {
            sm_.start(co_await asio::this_coro::executor, self());
            co_return;
        }

        asio::any_io_executor get_executor() const override {
            return sender_->get_executor();
        }

        const Message &message() const override {
            return message_;
        }

        DeliveryNumber id() const {
            return id_;
        }
        DeliveryTag tag() const {
            return tag_;
        }

        const std::optional<DeliveryMode> &delivery_mode() const {
            return delivery_mode_;
        }

        asio::awaitable<void> disposition(bool settled, std::optional<DeliveryState> state, bool batchable) {
            co_await sm_.generate_event(Disposition{std::make_tuple(settled, std::move(state), batchable)});
            co_return;
        }

        virtual asio::awaitable<void> await_sent() override {
            co_await sm_.template wait_for_state<Unsettled, Settled>();
        }
        virtual asio::awaitable<void> await_settled() override {
            co_await sm_.template wait_for_state<Settled>();
        }

        bool is_sent() const {
            return !sm_.template in_state<Pending>();
        }

        virtual bool is_settled() const override {
            return sm_.template in_state<Settled>();
        }

        asio::awaitable<void> sent(bool settled, messages::ReceiverSettleMode rcv_mode) {
            co_await sm_.generate_event(Sent{std::make_tuple(settled, rcv_mode)});
            co_return;
        };

      private:
        // https://youtu.be/QO5Zp3tPqdo The AMQP 1.0 Protocol - 3/6 - Message Transfers
        static constexpr auto plantuml_fsm = R"(
@startuml
state Pending
state SendComplete  #line.dotted
state Unsettled
state Settled
state CheckingDisposition  #line.dotted

Pending --> SendComplete : Sent(settled,rcv_mode)
SendComplete --> Settled : Settle
SendComplete --> Unsettled : Done
Unsettled --> CheckingDisposition : Disposition(disposition)
CheckingDisposition --> Settled : Settle
CheckingDisposition --> Unsettled : Done

SendComplete: \
if settled:\n\
    generate Settle to self\n\
else:\n\
    self.rcv_mode = rcv_mode\n\
    generate Done to self\n\

CheckingDisposition:\
self.remote_state = disposition.state\n\
if disposition.settled:\n\
    generate Settle to self\n\
elif is_complete(self.remote_state) and self.rcv_mode == second:\n\
    send_disposition(true,self.remote_state)\n\
    generate Settled to self\n\
else:\n\
    generate Done to self
@enduml
        )";

        using Sent = fsm::Event<"Sent", std::tuple<bool, messages::ReceiverSettleMode>>;
        using Done = fsm::Event<"Done">;
        using Settle = fsm::Event<"Settle">;
        using Disposition = fsm::Event<"Disposition", std::tuple<bool, std::optional<DeliveryState>, bool>>;

        using Pending = fsm::State<"Pending">;
        struct SendComplete : fsm::State<"SendComplete"> {
            asio::awaitable<void> operator()(Self self, std::tuple<bool, messages::ReceiverSettleMode>&& args) {
                auto [settled,rcv_mode] = std::move(args);
                co_await self->state_send_complete(settled,rcv_mode);
                co_return;
            }
        };
        using Unsettled = fsm::State<"Unsettled">;
        struct CheckingDisposition : fsm::State<"CheckingDisposition"> {
            asio::awaitable<void>
            operator()(Self self, std::tuple<bool, std::optional<DeliveryState>, bool> &&disposition) {
                auto [settled, state, batchable] = std::move(disposition);
                co_await self->state_checking_disposition(settled, state, batchable);
                co_return;
            }
        };
        struct Settled : fsm::State<"Settled", fsm::Terminal> {
            asio::awaitable<void>
            operator()(Self self) {
                co_await self->state_settled();
                co_return;
            }
        };

        using StateMachine = fsm::StateMachine<
            fsm::Transition<Pending, SendComplete, Sent>,
            fsm::Transition<SendComplete, Settled, Settle>,
            fsm::Transition<SendComplete, Unsettled, Done>,
            fsm::Transition<Unsettled, CheckingDisposition, Disposition>,
            fsm::Transition<CheckingDisposition, Settled, Settle>,
            fsm::Transition<CheckingDisposition, Unsettled, Done>>;

        asio::awaitable<void> state_send_complete(bool settled, messages::ReceiverSettleMode rcv_mode) {
            if (settled) {
                co_await sm_.generate_local_event(Settle{});
            } else {
                rcv_mode_ = rcv_mode;
                co_await sm_.generate_local_event(Done{});
            }
            co_return;
        }

        asio::awaitable<void>
        state_checking_disposition(bool settled, std::optional<DeliveryState> state, bool batchable) {
            if (state) {
                remote_state_ = state;
            }

            if (settled) {
                co_await sm_.generate_local_event(Settle{});
            } else if (in_terminal_state() &&
                       rcv_mode_ == messages::ReceiverSettleMode::second) {
                co_await sender_->send_disposition(id_, true, remote_state_);
                co_await sm_.generate_local_event(Settle{});
            } else {
                co_await sm_.generate_local_event(Done{});
            }

            co_return;
        }

        asio::awaitable<void>
        state_settled() {
            sender_->settle(id_);
            co_return;
        }

        bool in_terminal_state() const {
            return remote_state_ && (std::holds_alternative<messages::Accepted>(remote_state_.value()) ||
                                     std::holds_alternative<messages::Rejected>(remote_state_.value()) ||
                                     std::holds_alternative<messages::Released>(remote_state_.value()) ||
                                     std::holds_alternative<messages::Modified>(remote_state_.value()));
        }

      private:
        DeliveryNumber id_{};
        DeliveryTag tag_{};
        std::optional<DeliveryMode> delivery_mode_{};
        messages::ReceiverSettleMode rcv_mode_{};
        Message message_{};
        std::optional<DeliveryState> remote_state_{};
        Payload payload_{};
        std::shared_ptr<Sender> sender_{};
        StateMachine sm_;
    };

} // namespace amqp_asio
