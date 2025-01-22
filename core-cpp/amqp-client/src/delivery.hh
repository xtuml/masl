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

#include "amqp_asio/delivery.hh"
#include "messages.hh"
#include "decoder.hh"
#include "fsm.hh"

namespace amqp_asio {
    template <typename Receiver>
    class DeliveryImpl : public amqp_asio::Delivery::Impl, public std::enable_shared_from_this<DeliveryImpl<Receiver>> {
      public:
        using Self = std::shared_ptr<DeliveryImpl>;

        using DeliveryNumber = messages::DeliveryNumber;
        using DeliveryTag = messages::DeliveryTag;
        using ReceiverSettleMode = messages::ReceiverSettleMode;
        using DeliveryState = messages::DeliveryState;
        using Payload = messages::AmqpPayload;
        using Message = messages::Message;
        using Accepted = messages::Accepted;
        using Error = messages::Error;
        using Rejected = messages::Rejected;
        using Released = messages::Released;

        static asio::awaitable<Self> create(
            std::shared_ptr<Receiver> receiver,
            DeliveryNumber id,
            DeliveryTag tag,
            ReceiverSettleMode settle_mode,
            bool auto_accept
        ) {
            auto delivery = std::make_shared<DeliveryImpl>(std::move(receiver), id, tag, settle_mode, auto_accept);
            co_await delivery->init();
            co_return delivery;
        }

        DeliveryImpl(
            std::shared_ptr<Receiver> receiver,
            DeliveryNumber id,
            DeliveryTag tag,
            ReceiverSettleMode settle_mode,
            bool auto_accept
        )
            : id_{id},
              tag_{std::move(tag)},
              rcv_settle_mode_{settle_mode},
              auto_accept_(auto_accept),
              receiver_{std::move(receiver)},
              sm_(get_executor(), "amqp_asio.delivery.sm.{}", id_) {}

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
            return receiver_->get_executor();
        }

        Message message() const override {
            types::Decoder decoder{payload_};
            return decoder.template decode<Message>();
        }

        const Payload& raw_payload() const {
            return payload_;
        }

        DeliveryNumber id() const {
            return id_;
        }
        DeliveryTag tag() const {
            return tag_;
        }

        struct TransferFlags {
            bool aborted{};
            bool settled{};
            bool more{};
            bool batchable{};
        };

        asio::awaitable<void> transfer(TransferFlags flags, const Payload &payload) {
            co_await sm_.generate_event(Transfer{std::make_tuple(flags, payload)});
            co_return;
        }

        asio::awaitable<void> disposition(bool settled, std::optional<DeliveryState> state, bool batchable) {
            co_await sm_.generate_event(Disposition{std::make_tuple(settled, std::move(state), batchable)});
            co_return;
        }

        asio::awaitable<void> accept() override {
            co_await sm_.generate_event(UpdateLocal{Accepted{}});
            co_return;
        }

        asio::awaitable<void> reject(std::optional<Error> error = {}) override {
            co_await sm_.generate_event(UpdateLocal{Rejected{.error = std::move(error)}});
            co_return;
        }

        asio::awaitable<void> release() override {
            co_await sm_.generate_event(UpdateLocal{Released{}});
            co_return;
        }

      private:
        // https://youtu.be/QO5Zp3tPqdo The AMQP 1.0 Protocol - 3/6 - Message Transfers
        static constexpr auto plantuml_fsm = R"(
@startuml

state HandlingTransfer #line.dotted
state Completing #line.dotted
state UnsettledCheckingDisposition #line.dotted
state CheckingInterleavedDisposition #line.dotted
state CompleteCheckingDisposition #line.dotted
state Settling #line.dotted
state UpdatingLocalState #line.dotted

[*] --> AwaitingTransfer

AwaitingTransfer -[#green]-> HandlingTransfer : Transfer(transfer, payload)

AwaitingTransfer -r[#green]-> CheckingInterleavedDisposition : Disposition(disposition)
CheckingInterleavedDisposition -l[dashed,#blue]-> AwaitingTransfer : Settle
CheckingInterleavedDisposition -l[dashed]-> AwaitingTransfer : Done


HandlingTransfer -[dashed,#blue]-> SettledIncomplete : Settle
HandlingTransfer -[dashed]-> UnsettledIncomplete : Done
HandlingTransfer -[dashed,#orange]-> Completing : UpdateLocal(state)
HandlingTransfer -[dashed]-> Aborted : Abort
HandlingTransfer --> AwaitingTransfer : More

UnsettledIncomplete -[#orange]-> Completing : UpdateLocal(state)
Completing -[dashed,#blue]-> Settling : Settle
Completing -[dashed]-> UnsettledComplete : Done

SettledIncomplete -[#orange]-> UpdatingLocalState : UpdateLocal(state)
UpdatingLocalState -[dashed]-> SettledComplete : Done

UnsettledIncomplete -[#green]-> UnsettledCheckingDisposition : Disposition(disposition)
UnsettledCheckingDisposition -[dashed]-> UnsettledIncomplete : Done
UnsettledCheckingDisposition -[dashed,#blue]-> SettledIncomplete : Settle

UnsettledComplete -[#green]-> CompleteCheckingDisposition : Disposition(disposition)
CompleteCheckingDisposition -[dashed,#blue]-> Settling : Settle
CompleteCheckingDisposition -[dashed]-> UnsettledComplete : Done


Aborted -[dotted]-> [*]
SettledComplete -[dotted]-> [*]

Settling -[dashed]-> SettledComplete : Done

note bottom of CheckingInterleavedDisposition
def check_disposition(disposition):
    self.remote_settled |= disposition.settled
    self.remote_state = disposition.state
    if self.remote_settled:
        generate Settle to self
    else:
        generate Done to self
end note

HandlingTransfer: \
if transfer.abort:\n\
  generate Abort to self\n\
else:\n\
    self.payload += transfer.payload\n\
    self.remote_settled = self.remote_settled or transfer.settled\n\
    if transfer.more:\n\
        generate More to self\n\
    else:\n\
        self.receiver.publish(self)\n\
        if self.auto_accept:\n\
            generate UpdateLocal(Accepted) to self\n\
        elif self.remote_settled:\n\
            generate Settle to self\n\
        else:\n\
            generate Done to self

CheckingInterleavedDisposition:\
 check_disposition(settled, state)

UnsettledCheckingDisposition:\
 check_disposition(settled, state)

CompleteCheckingDisposition:\
 check_disposition(settled, state)


Completing:\
self.local_state = state\n\
local_settled = self.rcv_mode == first\n\
if not self.remote_settled:\n\
    self.receiver.send_disposition(local_settled,self.local_state)\n\
if local_settled or remote_settled:\n\
    generate Settle to self\n\
else:\n\
    generate Done to self

UpdatingLocalState:\
self.local_state = state\n\
generate Done to self

SettledIncomplete:\
self.receiver.unsettled.remove(self)

Settling:\
self.remote_settled = true\n\
self.receiver.unsettled.remove(self)\n\
generate Done to self

Aborted:\
self.receiver.unsettled.remove(self)

@enduml
        )";

        using Transfer = fsm::Event<"Transfer", std::tuple<TransferFlags, Payload>>;
        using UpdateLocal = fsm::Event<"UpdateLocal", DeliveryState>;
        using Disposition = fsm::Event<"Disposition", std::tuple<bool, std::optional<DeliveryState>, bool>>;
        using More = fsm::Event<"More">;
        using Done = fsm::Event<"Done">;
        using Settle = fsm::Event<"Settle">;
        using Abort = fsm::Event<"Abort">;

        using AwaitingTransfer = fsm::State<"AwaitingTransfer">;
        struct CheckingInterleavedDisposition : fsm::State<"CheckingInterleavedDisposition"> {
            asio::awaitable<void>
            operator()(Self self, std::tuple<bool, std::optional<DeliveryState>, bool> &&disposition) {
                auto [settled, state, batchable] = std::move(disposition);
                co_await self->check_disposition(settled, std::move(state), batchable);
                co_return;
            }
        };
        struct HandlingTransfer : fsm::State<"HandlingTransfer"> {
            asio::awaitable<void> operator()(Self self, const std::tuple<TransferFlags, Payload> &tx) {
                const auto &[flags, payload] = tx;
                co_await self->state_handling_transfer(flags, payload);
                co_return;
            }
        };
        using UnsettledIncomplete = fsm::State<"UnsettledIncomplete">;
        struct Aborted : fsm::State<"Aborted", fsm::Terminal> {
            asio::awaitable<void> operator()(Self self) {
                co_await self->state_aborted();
                co_return;
            }
        };
        struct Completing : fsm::State<"Completing"> {
            asio::awaitable<void> operator()(Self self, const DeliveryState &state) {
                co_await self->state_completing(state);
                co_return;
            }
        };
        struct UnsettledCheckingDisposition : fsm::State<"UnsettledCheckingDisposition"> {
            asio::awaitable<void>
            operator()(Self self, std::tuple<bool, std::optional<DeliveryState>, bool> &&disposition) {
                auto [settled, state, batchable] = std::move(disposition);
                co_await self->check_disposition(settled, std::move(state), batchable);
                co_return;
            }
        };
        using UnsettledComplete = fsm::State<"UnsettledComplete">;
        struct SettledIncomplete : fsm::State<"SettledIncomplete"> {
            asio::awaitable<void> operator()(Self self) {
                co_await self->state_settled_incomplete();
                co_return;
            }
        };
        struct CompleteCheckingDisposition : fsm::State<"CompleteCheckingDisposition"> {
            asio::awaitable<void>
            operator()(Self self, std::tuple<bool, std::optional<DeliveryState>, bool> &&disposition) {
                auto [settled, state, batchable] = std::move(disposition);
                co_await self->check_disposition(settled, std::move(state), batchable);
                co_return;
            }
        };
        struct UpdatingLocalState : fsm::State<"UpdatingLocalState"> {
            asio::awaitable<void> operator()(Self self, const DeliveryState &state) {
                co_await self->state_updating_local_state(state);
                co_return;
            }
        };

        struct Settling : fsm::State<"Settling"> {
            asio::awaitable<void> operator()(Self self) {
                co_await self->state_settling();
                co_return;
            }
        };
        using SettledComplete = fsm::State<"SettledComplete", fsm::Terminal>;

        using StateMachine = fsm::StateMachine<
            fsm::Transition<AwaitingTransfer, HandlingTransfer, Transfer>,

            fsm::Transition<AwaitingTransfer, CheckingInterleavedDisposition, Disposition>,
            fsm::Transition<CheckingInterleavedDisposition, AwaitingTransfer, Settle>,
            fsm::Transition<CheckingInterleavedDisposition, AwaitingTransfer, Done>,

            fsm::Transition<HandlingTransfer, SettledIncomplete, Settle>,
            fsm::Transition<HandlingTransfer, UnsettledIncomplete, Done>,
            fsm::Transition<HandlingTransfer, Completing, UpdateLocal>,
            fsm::Transition<HandlingTransfer, Aborted, Abort>,
            fsm::Transition<HandlingTransfer, AwaitingTransfer, More>,

            fsm::Transition<UnsettledIncomplete, Completing, UpdateLocal>,
            fsm::Transition<Completing, Settling, Settle>,
            fsm::Transition<Completing, UnsettledComplete, Done>,

            fsm::Transition<SettledIncomplete, UpdatingLocalState, UpdateLocal>,
            fsm::Transition<UpdatingLocalState, SettledComplete, Done>,

            fsm::Transition<UnsettledIncomplete, UnsettledCheckingDisposition, Disposition>,
            fsm::Transition<UnsettledCheckingDisposition, UnsettledIncomplete, Done>,
            fsm::Transition<UnsettledCheckingDisposition, SettledIncomplete, Settle>,

            fsm::Transition<UnsettledComplete, CompleteCheckingDisposition, Disposition>,
            fsm::Transition<CompleteCheckingDisposition, Settling, Settle>,
            fsm::Transition<CompleteCheckingDisposition, UnsettledComplete, Done>,

            fsm::Transition<Settling, SettledComplete, Done>

            >;

        asio::awaitable<void> state_handling_transfer(TransferFlags flags, const Payload &payload) {
            if (flags.aborted) {
                co_await sm_.generate_local_event(Abort());
            } else {
                payload_.insert(payload_.end(), payload.begin(), payload.end());
                batchable_ = batchable_ || flags.batchable;
                remote_settled_ = remote_settled_ || flags.settled;
                if (flags.more) {
                    co_await sm_.generate_local_event(More());
                } else {
                    co_await receiver_->publish_pending_delivery();
                    if (auto_accept_) {
                        co_await sm_.generate_local_event(UpdateLocal{Accepted{}});
                    } else if (remote_settled_) {
                        co_await sm_.generate_local_event(Settle{});
                    } else {
                        co_await sm_.generate_local_event(Done{});
                    }
                }
            }
            co_return;
        }

        asio::awaitable<void> state_completing(const DeliveryState &state) {
            local_state_ = state;
            bool local_settled = rcv_settle_mode_ == ReceiverSettleMode::first;
            if (!remote_settled_) {
                co_await receiver_->send_disposition(id_, local_settled, local_state_);
            }
            if (local_settled || remote_settled_) {
                co_await sm_.generate_local_event(Settle{});
            } else {
                co_await sm_.generate_local_event(Done{});
            }
            co_return;
        }

        asio::awaitable<void> check_disposition(bool settled, std::optional<DeliveryState> state, bool batchable) {
            remote_settled_ |= settled;
            remote_state_ = std::move(state);
            batchable_ |= batchable;
            if (remote_settled_) {
                co_await sm_.generate_local_event(Settle{});
            } else {
                co_await sm_.generate_local_event(Done{});
            }
            co_return;
        }

        asio::awaitable<void> state_settled_incomplete() {
            receiver_->settle(id_);
            co_return;
        }

        asio::awaitable<void> state_aborted() {
            receiver_->settle(id_);
            co_return;
        }

        asio::awaitable<void> state_updating_local_state(const DeliveryState &state) {
            local_state_ = state;
            co_await sm_.generate_local_event(Done{});
            co_return;
        }
        asio::awaitable<void> state_settling() {
            receiver_->settle(id_);
            co_await sm_.generate_local_event(Done{});
            co_return;
        }

      private:
        DeliveryNumber id_{};
        DeliveryTag tag_{};
        ReceiverSettleMode rcv_settle_mode_{};
        bool auto_accept_{false};
        bool remote_settled_{false};
        bool batchable_{false};
        std::optional<DeliveryState> local_state_{};
        std::optional<DeliveryState> remote_state_{};
        Payload payload_{};
        std::shared_ptr<Receiver> receiver_{};
        StateMachine sm_;
    };

} // namespace amqp_asio
