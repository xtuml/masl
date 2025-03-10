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

#include "amqp_asio/messages.hh"
#include "amqp_asio/session.hh"
#include "delivery.hh"
#include "receiver.hh"
#include "sender.hh"
#include "tracker.hh"
#include <asio/awaitable.hpp>
#include <asio/experimental/channel.hpp>

#include <asio/detached.hpp>
#include <logging/log.hh>
#include <nlohmann/json.hpp>

namespace amqp_asio {

    template <typename Connection>
    class SessionImpl : public amqp_asio::Session::Impl, public std::enable_shared_from_this<SessionImpl<Connection>> {
      public:
        using Link = LinkImpl<SessionImpl>;
        using Sender = SenderImpl<SessionImpl>;
        using Receiver = ReceiverImpl<SessionImpl>;
        using Delivery = DeliveryImpl<Receiver>;
        using Tracker = TrackerImpl<Sender>;

        using ChannelId = messages::ChannelId;
        using Handle = messages::Handle;

        static asio::awaitable<std::shared_ptr<SessionImpl<Connection>>>
        create(ChannelId channel_id, std::shared_ptr<Connection> connection, SessionOptions options) {
            auto session =
                std::make_shared<SessionImpl<Connection>>(channel_id, std::move(connection), std::move(options));
            co_await session->init();
            co_return session;
        }

        SessionImpl(ChannelId channel_id, std::shared_ptr<Connection> connection, SessionOptions options)
            : id_{channel_id},
              log_{"amqp_asio.session.{}.{}", connection->container_id(), id_},
              connection_(std::move(connection)),
              options_(std::move(options)),
              incoming_message_queue_{connection_->get_executor(), std::numeric_limits<std::size_t>::max()},
              sm_(connection_->get_executor(), "amqp_asio.session.sm.{}.{}", connection_->container_id(), id_) {}

        asio::any_io_executor get_executor() const override {
            return connection_->get_executor();
        }

        asio::awaitable<void> init() {
            connection_->register_session(self());
            asio::co_spawn(
                co_await asio::this_coro::executor,
                [self = self()]() -> asio::awaitable<void> {
                    co_await self->receive_events();
                },
                asio::detached
            );
            sm_.start(co_await asio::this_coro::executor, self());
            co_return;
        }

        asio::awaitable<void> start() {
            log_.debug("Starting");
            co_await sm_.generate_local_event(Start{});
            co_await (wait_for_started() || wait_for_ended());
            if (error_) {
                std::rethrow_exception(error_);
            }
        }

        asio::awaitable<void> end() override {
            log_.debug("Ending");
            co_await sm_.generate_local_event(EndRequest{});
            co_await wait_for_ended();
            if (error_) {
                std::rethrow_exception(error_);
            }
        }

        asio::awaitable<void> update_flow(Handle handle, const LinkFlow &flow) {
            log_.debug("Update Flow");
            co_await sm_.generate_local_event(UpdateFlow{std::make_tuple(handle, flow)});
        }

        asio::awaitable<void> wait_for_started() {
            log_.debug("Waiting for started");
            co_await sm_.template wait_for_state<Mapped>();
        }

        asio::awaitable<void> wait_for_ended() {
            log_.debug("Waiting for ended");
            co_await sm_.template wait_for_state<Ended>();
        }

        asio::awaitable<std::shared_ptr<amqp_asio::Sender::Impl>>
        open_sender(std::optional<std::string> address, SenderOptions options) override {
            auto sender = co_await Sender::create(self(), options.values_or(options_.sender_options()));
            co_await sender->attach(std::move(address));
            co_return sender;
        }

        asio::awaitable<std::shared_ptr<amqp_asio::Receiver::Impl>>
        open_receiver(std::string address, ReceiverOptions options = {}) override {
            auto receiver = co_await Receiver::create(self(), options.values_or(options_.receiver_options()));
            co_await receiver->attach(address);
            co_return receiver;
        }

        void register_local_sender(std::shared_ptr<Sender> sender) {
            local_senders_.emplace(sender->name(), std::move(sender));
        }

        void register_local_receiver(std::shared_ptr<Receiver> receiver) {
            local_receivers_.emplace(receiver->name(), std::move(receiver));
        }

        void register_remote_link(std::shared_ptr<Link> link) {
            remote_links_.emplace(link->input_handle(), std::move(link));
        }

        void deregister_local_sender(std::shared_ptr<Sender> sender) {
            local_receivers_.erase(sender->name());
        }

        void deregister_local_receiver(std::shared_ptr<Receiver> receiver) {
            local_receivers_.erase(receiver->name());
        }

        void deregister_remote_link(std::shared_ptr<Link> link) {
            remote_links_.erase(link->input_handle());
        }

        ChannelId id() const {
            return id_;
        }

        auto next_output_handle() {
            return next_output_handle_++;
        }
        auto next_delivery_number() {
            return next_delivery_number_++;
        }

        void register_unsettled_delivery(const std::shared_ptr<Delivery> &delivery) {
            unsettled_deliveries_.emplace(delivery->id(), delivery);
        }

        void settle_delivery(messages::DeliveryNumber id) {
            unsettled_deliveries_.erase(id);
        }
        void register_unsettled_tracker(const std::shared_ptr<Tracker> &tracker) {
            unsettled_trackers_.emplace(tracker->id(), tracker);
        }

        void settle_tracker(messages::DeliveryNumber id) {
            unsettled_trackers_.erase(id);
        }

        using Performative = std::variant<
            messages::Begin,
            messages::End,
            messages::Attach,
            messages::Flow,
            messages::Transfer,
            messages::Disposition,
            messages::Detach>;

        asio::awaitable<void> push_message(Performative &&performative, messages::AmqpPayload &&payload) {
            co_await incoming_message_queue_.async_send(std::error_code{}, std::move(performative), std::move(payload));
        }

        asio::awaitable<void> send_message(messages::Performative performative, messages::AmqpPayload message = {}) {
            co_await connection_->send_amqp_message(id_, std::move(performative), std::move(message));
        }

      private:
        static constexpr auto plantuml_fsm = R"(
            @startuml
            state Beginning #line.dotted
            state BeginReceived #line.dotted
            state Attach #line.dotted
            state FlowReceived #line.dotted
            state UpdatingFlow #line.dotted
            state Transfer #line.dotted
            state Disposition #line.dotted
            state Detach #line.dotted
            state EndReceived #line.dotted
            state Ending #line.dotted

            Unmapped          --> BeginSent         : Start
            Unmapped          --> BeginReceived     : BeginMsg
            BeginSent         --> Beginning         : BeginMsg
            Beginning         --> Mapped            : Done
            BeginReceived     --> Mapped            : Done

            Mapped            --> Attach            : AttachMsg
            Attach            --> Mapped            : Done

            Mapped            --> FlowReceived      : FlowMsg
            FlowReceived      --> Mapped            : Done

            Mapped            --> UpdatingFlow      : UpdateFlow
            UpdatingFlow      --> Mapped            : Done

            Mapped            --> Transfer          : TransferMsg
            Transfer          --> Mapped            : Done

            Mapped            --> Disposition       : DispositionMsg
            Disposition       --> Mapped            : Done

            Mapped            --> Detach            : DetachMsg
            Detach            --> Mapped            : Done


            Mapped            --> EndReceived       : EndMsg
            EndReceived       --> Ended             : Done
            Mapped            --> EndSent           : EndRequest
            EndSent           --> Ending            : EndMsg
            Ending            --> Ended             : Done


            BeginSent --> Ended : Abort
            EndSent --> Ended : Abort
            Mapped --> Ended : Abort

            BeginSent: send Begin
            BeginReceived: send Begin
            BeginReceived: generate Done
            Beginning : generate Done
            EndReceived : send End
            EndReceived : generate Done
            EndSent : send End
            Ending : generate Done

            @enduml
        )";
        using Self = std::shared_ptr<SessionImpl>;

        using Unmapped = fsm::State<"Unmapped">;
        struct BeginSent : fsm::State<"BeginSent"> {
            asio::awaitable<void> operator()(Self self) {
                co_await self->state_begin_sent();
            }
        };
        struct BeginReceived : fsm::State<"BeginReceived"> {
            asio::awaitable<void> operator()(Self self, messages::Begin &&begin) {
                co_await self->state_begin_received(std::move(begin));
            }
        };
        struct Beginning : fsm::State<"Beginning"> {
            asio::awaitable<void> operator()(Self self, messages::Begin &&begin) {
                co_await self->state_beginning(std::move(begin));
            }
        };
        struct Mapped : fsm::State<"Mapped"> {
            asio::awaitable<void> operator()(Self self) {
                co_return;
            }
        };
        struct Attach : fsm::State<"Attach"> {
            asio::awaitable<void> operator()(Self self, messages::Attach &&attach) {
                co_await self->state_attach(std::move(attach));
            }
        };
        struct FlowReceived : fsm::State<"FlowReceived"> {
            asio::awaitable<void> operator()(Self self, messages::Flow &&flow) {
                co_await self->state_flow_received(flow);
            }
        };
        struct UpdatingFlow : fsm::State<"UpdatingFlow"> {
            asio::awaitable<void> operator()(Self self, std::tuple<Handle, LinkFlow> &&update) {
                auto [handle, flow] = std::move(update);
                co_await self->state_updating_flow(handle, flow);
            }
        };
        struct Transfer : fsm::State<"Transfer"> {
            asio::awaitable<void> operator()(Self self, std::tuple<messages::Transfer, messages::AmqpPayload> &&tx) {
                auto [performative, payload] = std::move(tx);
                co_await self->state_transfer(std::move(performative), std::move(payload));
            }
        };
        struct Disposition : fsm::State<"Disposition"> {
            asio::awaitable<void> operator()(Self self, messages::Disposition &&disposition) {
                co_await self->state_disposition(std::move(disposition));
            }
        };
        struct Detach : fsm::State<"Detach"> {
            asio::awaitable<void> operator()(Self self, messages::Detach &&detach) {
                co_await self->state_detach(std::move(detach));
            }
        };
        struct EndSent : fsm::State<"EndSent"> {
            asio::awaitable<void> operator()(Self self) {
                co_await self->state_end_sent();
            }
        };
        struct EndReceived : fsm::State<"EndReceived"> {
            asio::awaitable<void> operator()(Self self, messages::End &&end) {
                co_await self->state_end_received(end);
            }
        };
        struct Ending : fsm::State<"Ending"> {
            asio::awaitable<void> operator()(Self self, messages::End &&end) {
                co_await self->state_ending(end);
            }
        };
        struct Ended : fsm::State<"Ended", fsm::Terminal> {
            asio::awaitable<void> operator()(Self self) {
                co_await self->state_ended();
            }
        };

        using Start = fsm::Event<"Start">;
        using BeginMsg = fsm::Event<"BeginMsg", messages::Begin>;
        using Done = fsm::Event<"Done">;
        using EndMsg = fsm::Event<"EndMsg", messages::End>;
        using EndRequest = fsm::Event<"EndRequest">;
        using UpdateFlow = fsm::Event<"UpdateFlow", std::tuple<Handle, LinkFlow>>;

        using AttachMsg = fsm::Event<"AttachMsg", messages::Attach>;
        using FlowMsg = fsm::Event<"FlowMsg", messages::Flow>;
        using TransferMsg = fsm::Event<"TransferMsg", std::tuple<messages::Transfer, messages::AmqpPayload>>;
        using DispositionMsg = fsm::Event<"DispositionMsg", messages::Disposition>;
        using DetachMsg = fsm::Event<"DetachMsg", messages::Detach>;

        using StateMachine = fsm::StateMachine<
            fsm::Transition<Unmapped, BeginSent, Start>,
            fsm::Transition<Unmapped, BeginReceived, BeginMsg>,
            fsm::Transition<BeginSent, Beginning, BeginMsg>,
            fsm::Transition<Beginning, Mapped, Done>,
            fsm::Transition<BeginReceived, Mapped, Done>,

            fsm::Transition<Mapped, Attach, AttachMsg>,
            fsm::Transition<Attach, Mapped, Done>,
            fsm::Transition<Mapped, FlowReceived, FlowMsg>,
            fsm::Transition<FlowReceived, Mapped, Done>,
            fsm::Transition<Mapped, UpdatingFlow, UpdateFlow>,
            fsm::Transition<UpdatingFlow, Mapped, Done>,
            fsm::Transition<Mapped, Transfer, TransferMsg>,
            fsm::Transition<Transfer, Mapped, Done>,
            fsm::Transition<Mapped, Disposition, DispositionMsg>,
            fsm::Transition<Disposition, Mapped, Done>,
            fsm::Transition<Mapped, Detach, DetachMsg>,
            fsm::Transition<Detach, Mapped, Done>,

            fsm::Transition<Mapped, EndReceived, EndMsg>,
            fsm::Transition<EndReceived, Ended, Done>,
            fsm::Transition<Mapped, EndSent, EndRequest>,
            fsm::Transition<EndSent, Ending, EndMsg>,
            fsm::Transition<Ending, Ended, Done>,

            fsm::Transition<EndSent, fsm::Ignore, AttachMsg>,
            fsm::Transition<EndSent, fsm::Ignore, TransferMsg>,
            fsm::Transition<EndSent, fsm::Ignore, DispositionMsg>,
            fsm::Transition<EndSent, fsm::Ignore, DetachMsg>,
            fsm::Transition<EndSent, fsm::Ignore, FlowMsg>>;

        using TransferNumber = messages::TransferNumber;
        using Window = messages::Window;

        struct SessionMessage {
            Performative performative;
            messages::AmqpPayload payload;
        };

        using message_channel = asio::experimental::channel<void(std::error_code, Performative, messages::AmqpPayload)>;

        asio::awaitable<void> receive_events() {
            while (true) {
                auto [performative, payload] = co_await incoming_message_queue_.async_receive();
                co_await std::visit(
                    overload{
                        [this](messages::Begin &&begin) -> asio::awaitable<void> {
                            co_await sm_.generate_event(BeginMsg{std::move(begin)});
                        },
                        [this](messages::End &&end) -> asio::awaitable<void> {
                            co_await sm_.generate_event(EndMsg{std::move(end)});
                        },
                        [this](messages::Attach &&attach) -> asio::awaitable<void> {
                            co_await sm_.generate_event(AttachMsg{std::move(attach)});
                        },
                        [this](messages::Flow &&flow) -> asio::awaitable<void> {
                            co_await sm_.generate_event(FlowMsg{std::move(flow)});
                        },
                        [this, payload = std::move(payload)](messages::Transfer &&transfer) -> asio::awaitable<void> {
                            co_await sm_.generate_event(
                                TransferMsg{std::make_tuple(std::move(transfer), std::move(payload))}
                            );
                        },
                        [this](messages::Disposition &&disposition) -> asio::awaitable<void> {
                            co_await sm_.generate_event(DispositionMsg{std::move(disposition)});
                        },
                        [this](messages::Detach &&detach) -> asio::awaitable<void> {
                            co_await sm_.generate_event(DetachMsg{std::move(detach)});
                        },
                    },
                    std::move(performative)
                );
            }
            co_return;
        }

        asio::awaitable<void> send_begin() {
            co_await send_message(messages::Begin{
                .next_outgoing_id = next_outgoing_id_,
                .incoming_window = incoming_window_,
                .outgoing_window = outgoing_window_,
            });
        }

        asio::awaitable<void> state_begin_sent() {
            co_await send_begin();
            co_return;
        }
        asio::awaitable<void> done() {
            co_await sm_.generate_local_event(Done{});
        }

        void set_remote_windows(const messages::Begin &begin) {
            next_incoming_id_ = begin.next_outgoing_id;
            remote_incoming_window_ = begin.incoming_window;
            remote_outgoing_window_ = begin.outgoing_window;
            log_.debug(
                "next_incoming_id = {}, remote_incoming_window = {}, remote_outgoing_window() = {}",
                next_incoming_id_,
                remote_incoming_window_,
                remote_outgoing_window_
            );
        }

        asio::awaitable<void> state_begin_received(const messages::Begin &begin) {
            set_remote_windows(begin);
            co_await send_begin();
            co_await done();
            co_return;
        }

        asio::awaitable<void> state_beginning(const messages::Begin &begin) {
            set_remote_windows(begin);
            co_await done();
            co_return;
        }

        asio::awaitable<void> state_flow_received(messages::Flow flow) {
            log_.debug("Handling flow: {}", nlohmann::json(flow).dump(2));
            next_incoming_id_ = flow.next_outgoing_id;
            remote_outgoing_window_ = flow.outgoing_window;
            remote_incoming_window_ = flow.next_incoming_id.value_or(flow.incoming_window - next_outgoing_id_);

            if (flow.handle) {
                auto link = find_link(flow.handle.value());
                if (link) {
                    co_await link->push_message(std::move(flow));
                }
            }

            co_await done();
            co_return;
        }

        asio::awaitable<void> state_updating_flow(Handle handle, const LinkFlow &link_flow) {
            auto flow = messages::Flow{
                .next_incoming_id = next_incoming_id_,
                .incoming_window = incoming_window_,
                .outgoing_window = outgoing_window_,
                .handle = handle,
                .delivery_count = link_flow.delivery_count,
                .link_credit = link_flow.link_credit,
                .available = link_flow.available,
                .drain = link_flow.drain
            };

            log_.debug("Updating flow: {}", nlohmann::json(flow).dump(2));

            co_await send_message(std::move(flow));

            co_await done();
            co_return;
        }

        std::shared_ptr<Link> find_link(Handle handle) {
            auto link = remote_links_.contains(handle) ? remote_links_[handle].lock() : std::shared_ptr<Link>{};
            if (!link) {
                log_.error("Link {} not found", handle);
            }
            return link;
        }

        asio::awaitable<void> state_attach(messages::Attach attach) {
            log_.debug("Handling attach: {}", nlohmann::json(attach).dump(2));
            if (attach.role == messages::Role::receiver) {
                // Other end is receiver, so we are sender

                if (local_senders_.contains(attach.name)) {
                    auto link = local_senders_[attach.name];
                    co_await link->push_message(std::move(attach));
                } else {
                    auto opts = this->options_.sender_options();
                    auto sender = co_await Sender::create(self(), opts.name(attach.name));
                    co_await sender->push_message(std::move(attach));
                }
            } else {
                if (local_receivers_.contains(attach.name)) {
                    auto link = local_receivers_[attach.name];
                    co_await link->push_message(std::move(attach));
                } else {
                    auto opts = this->options_.receiver_options();
                    auto receiver = co_await Receiver::create(self(), opts.name(attach.name));
                    co_await receiver->push_message(std::move(attach));
                }
            }
            co_await done();
            co_return;
        }

        asio::awaitable<void> state_disposition(const messages::Disposition &disposition) {
            if (disposition.role == messages::Role::sender) {
                auto start = unsettled_deliveries_.lower_bound(disposition.first);
                auto end = unsettled_deliveries_.upper_bound(disposition.last.value_or(disposition.first));

                std::for_each(start, end, [&disposition](auto entry) -> asio::awaitable<void> {
                    co_await entry.second->disposition(
                        disposition.settled.value_or(false), disposition.state, disposition.batchable.value_or(false)
                    );
                });

            } else {
                auto start = unsettled_trackers_.lower_bound(disposition.first);
                auto end = unsettled_trackers_.upper_bound(disposition.last.value_or(disposition.first));

                std::for_each(start, end, [&disposition](auto entry) -> asio::awaitable<void> {
                    co_await entry.second->disposition(
                        disposition.settled.value_or(false), disposition.state, disposition.batchable.value_or(false)
                    );
                });
            }
            co_await done();
            co_return;
        }

        asio::awaitable<void> state_detach(messages::Detach detach) {
            log_.debug("Handling detach: {}", nlohmann::json(detach).dump(2));
            auto link = find_link(detach.handle);
            if (link) {
                co_await link->push_message(std::move(detach));
            }
            co_await done();
            co_return;
        }

        asio::awaitable<void> state_transfer(messages::Transfer transfer, messages::AmqpPayload &&payload) {
            log_.debug("Handling transfer: {}", nlohmann::json(transfer).dump(2));
            ++next_incoming_id_;
            --remote_outgoing_window_;
            auto link = find_link(transfer.handle);
            if (link) {
                co_await link->push_message(std::move(transfer), std::move(payload));
            }
            co_await done();
            co_return;
        }

        asio::awaitable<void> state_end_sent() {
            co_await send_message(messages::End{});
            co_return;
        }

        asio::awaitable<void> state_ending(const messages::End &) {
            co_await done();
            co_return;
        }

        asio::awaitable<void> state_end_received(const messages::End &) {
            co_await send_message(messages::End{});
            co_await done();
            co_return;
        }

        asio::awaitable<void> state_ended() {
            incoming_message_queue_.close();
            connection_->deregister_session(self());
            local_senders_.clear();
            local_receivers_.clear();
            remote_links_.clear();
            co_return;
        }

        auto self() const {
            return this->shared_from_this();
        }
        auto self() {
            return this->shared_from_this();
        }

      private:
        ChannelId id_;
        xtuml::logging::Logger log_;
        TransferNumber next_incoming_id_{};
        Window incoming_window_{std::numeric_limits<Window>::max() / 2};
        TransferNumber next_outgoing_id_{};
        Window outgoing_window_{std::numeric_limits<Window>::max() / 2};
        Window remote_incoming_window_{};
        Window remote_outgoing_window_{};
        messages::Handle next_output_handle_{};
        messages::DeliveryNumber next_delivery_number_{};

        std::map<messages::DeliveryNumber, std::shared_ptr<Delivery>> unsettled_deliveries_{};
        std::map<messages::DeliveryNumber, std::shared_ptr<Tracker>> unsettled_trackers_{};

        std::shared_ptr<Connection> connection_;
        SessionOptions options_;
        message_channel incoming_message_queue_;
        std::exception_ptr error_;
        StateMachine sm_;

        std::map<std::string, std::shared_ptr<Sender>> local_senders_;
        std::map<std::string, std::shared_ptr<Receiver>> local_receivers_;
        std::map<Handle, std::weak_ptr<Link>> remote_links_;
    };
} // namespace amqp_asio