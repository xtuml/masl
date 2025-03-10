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

#include "messages.hh"
#include "fsm.hh"
#include "overload.hh"
#include "amqp_asio/exceptions.hh"
#include <asio/awaitable.hpp>
#include <asio/experimental/awaitable_operators.hpp>
#include <asio/experimental/channel.hpp>

#include <asio/detached.hpp>
#include <logging/log.hh>
#include <nlohmann/json.hpp>

namespace amqp_asio {
    using namespace asio::experimental::awaitable_operators;

    std::string unique_name();

    struct LinkFlow {
        messages::SequenceNo delivery_count{};
        messages::uint_t link_credit{};
        messages::uint_t available{};
        messages::boolean_t drain{};

        auto operator<=>(const LinkFlow &) const = default;

        friend void to_json(nlohmann::json &j, const LinkFlow &f) {
            j = nlohmann::json{
                {"delivery_count", f.delivery_count},
                {"link_credit", f.link_credit},
                {"available", f.available},
                {"drain", f.drain},
            };
        }
    };

    template <typename Session>
    class LinkImpl : public std::enable_shared_from_this<LinkImpl<Session>> {
      public:
        using Handle = messages::Handle;
        using Role = messages::Role;
        using Self = std::shared_ptr<LinkImpl>;
        using Properties = std::optional<messages::Fields>;

      protected:
        LinkImpl(std::string name, std::shared_ptr<Session> session, Properties properties)
            : name_{std::move(name)},
              properties_{std::move(properties)},
              output_handle_{session->next_output_handle()},
              log_{"amqp_asio.link.{}", name_},
              session_(std::move(session)),
              incoming_message_queue_{get_executor(), std::numeric_limits<std::size_t>::max()},
              sm_(get_executor(), "amqp_asio.link.sm.{}", name_) {}

        LinkImpl(LinkImpl &&) = delete;

        LinkImpl(LinkImpl const &) = delete;

        LinkImpl &operator=(LinkImpl &&) = delete;

        LinkImpl &operator=(LinkImpl const &) = delete;


      public:
        asio::any_io_executor get_executor() const {
            return session_->get_executor();
        }

        auto self() const {
            return this->shared_from_this();
        }
        auto self() {
            return this->shared_from_this();
        }

        virtual asio::awaitable<void> init() {
            register_local_link();
            spawn_cancellable_loop(
                get_executor(),
                [self = self()]() -> asio::awaitable<void> {
                    co_await self->process_incoming_message();
                    co_return;
                },
                log_
            );
            sm_.start(co_await asio::this_coro::executor, self());
            co_return;
        }

        asio::awaitable<void> attach(std::optional<std::string> address) {
            log_.debug("Attaching");
            co_await sm_.generate_event(AttachRequest{std::move(address)});
            co_await sm_.template wait_for_state<Attached,Detached>();
            if (sm_.template in_state<Detached>()) {
                log_.error("Attach Session failed");
                throw AMQPException(make_error_code(error::SessionAttachError));
            }
            co_return;
        }

        asio::awaitable<void> detach() {
            log_.debug("Detaching");
            co_await sm_.generate_event(DetachRequest{});
            co_await sm_.template wait_for_state<Detached>();
            co_return;
        }

        std::string name() const {
            return name_;
        }

        Handle output_handle() const {
            return output_handle_;
        }
        Handle input_handle() const {
            return input_handle_;
        }

        std::shared_ptr<Session> session() {
            return session_;
        }

        using Performative = std::variant<messages::Attach, messages::Detach, messages::Transfer, messages::Flow>;

        asio::awaitable<void> push_message(Performative &&performative, messages::AmqpPayload &&payload = {}) {
            co_await incoming_message_queue_.async_send(std::error_code{}, std::move(performative), std::move(payload));
        }

        virtual asio::awaitable<void>
        receive_transfer(const messages::Transfer &transfer, messages::AmqpPayload &&payload) {
            co_return;
        }

        virtual asio::awaitable<bool> receive_attach_response(const messages::Attach &response) {
            co_return false;
        }

        asio::awaitable<void> update_flow() {
            co_await session_->update_flow(output_handle(), flow());
            co_return;
        }

        virtual asio::awaitable<void> flow_updated(const messages::Flow &flow) {
            co_return;
        }

        virtual asio::awaitable<void> shutdown() {
            co_return;
        }

        virtual asio::awaitable<void> shutdown_outgoing() {
            co_return;
        }

        const LinkFlow &flow() const {
            return flow_;
        };

        LinkFlow &flow() {
            return flow_;
        };

        bool is_detached() {
            return std::holds_alternative<Detached>(sm_.current_state());
        }

        const Properties& properties() {
            return properties_;
        }

      private:


        virtual void register_local_link() = 0;
        virtual void deregister_local_link() = 0;

        static constexpr auto plantuml_fsm = R"(
@startuml
state Attaching #line.dotted
state AttachReceived  #line.dotted
state Receiving #line.dotted
state Sending #line.dotted
state DetachReceived  #line.dotted
state Detaching #line.dotted
state FlowReceived  #line.dotted
state FlowSent  #line.dotted


Init --> AttachSent : AttachRequest
Init --> AttachReceived : AttachMsg
AttachSent --> Attaching : AttachMsg
Attaching --> Attached : Done
Attaching --> DetachSent : DetachRequest
AttachReceived --> Attached : Done
AttachReceived --> DetachSent : DetachRequest
Attached --> Sending : TransferRequest
Attached --> FlowReceived : FlowMsg
Attached --> FlowSent : FlowRequest
FlowSent --> Attached : Done
FlowReceived --> Attached : Done
Sending --> Attached : Done
Attached --> Receiving : TransferMsg
Receiving --> Attached : Done
Attached --> DetachSent : DetachRequest 
Attached --> DetachReceived : DetachMsg
DetachSent --> Detaching : DetachMsg
Detaching --> Detached : Done
DetachReceived --> Detached : Done

Detached --> [*]

AttachSent: send Attach

Attaching: if ok:\n\
    generate Done\n\
else:\n\
    generate DetachRequest

AttachReceived:\
if ok:\n\
    send Attach\n\
    generate Done\n\
else:\n\
    send Attach\n\
    generate DetachRequest

DetachSent: send Detach
DetachReceived: send Detach\ngenerateDone

Detaching: generate Done

@enduml
        )";

        using Init = fsm::State<"Init">;
        struct AttachSent : fsm::State<"AttachSent"> {
            asio::awaitable<void> operator()(Self self, std::optional<std::string> address) {
                co_await self->state_attach_sent(std::move(address));
                co_return;
            }
        };
        struct AttachReceived : fsm::State<"AttachReceived"> {
            asio::awaitable<void> operator()(Self self, const messages::Attach &attach) {
                co_await self->state_attach_received(attach);
                co_return;
            }
        };
        struct Attaching : fsm::State<"Attaching"> {
            asio::awaitable<void> operator()(Self self, const messages::Attach &attach) {
                co_await self->state_attaching(attach);
                co_return;
            }
        };
        struct Attached : fsm::State<"Attached"> {
            asio::awaitable<void> operator()(Self self) {
                co_await self->state_attached();
                co_return;
            }
        };
        struct Receiving : fsm::State<"Receiving"> {
            asio::awaitable<void> operator()(Self self, std::tuple<messages::Transfer, messages::AmqpPayload> &&tx) {
                auto [transfer, payload] = std::move(tx);
                co_await self->state_receiving(transfer, std::move(payload));
                co_return;
            }
        };
        struct Sending : fsm::State<"Sending"> {
            asio::awaitable<void> operator()(Self self) {
                co_await self->state_sending();
                co_return;
            }
        };

        struct FlowReceived : fsm::State<"FlowReceived"> {
            asio::awaitable<void> operator()(Self self, const messages::Flow &flow) {
                co_await self->state_flow_received(flow);
                co_return;
            }
        };

        struct FlowSent : fsm::State<"FlowSent"> {
            asio::awaitable<void> operator()(Self self) {
                co_await self->state_flow_sent();
                co_return;
            }
        };

        struct DetachSent : fsm::State<"DetachSent"> {
            asio::awaitable<void> operator()(Self self) {
                co_await self->state_detach_sent();
                co_return;
            }
        };
        struct DetachReceived : fsm::State<"DetachReceived"> {
            asio::awaitable<void> operator()(Self self, const messages::Detach &detach) {
                co_await self->state_detach_received(detach);
                co_return;
            }
        };
        struct Detaching : fsm::State<"Detaching"> {
            asio::awaitable<void> operator()(Self self, const messages::Detach &detach) {
                co_await self->state_detaching(detach);
                co_return;
            }
        };
        struct Detached : fsm::State<"Detached", fsm::Terminal> {
            asio::awaitable<void> operator()(Self self) {
                co_await self->state_detached();
                co_return;
            }
        };

        using AttachMsg = fsm::Event<"AttachMsg", messages::Attach>;
        using TransferMsg = fsm::Event<"TransferMsg", std::tuple<messages::Transfer, messages::AmqpPayload>>;
        using FlowMsg = fsm::Event<"FlowMsg", messages::Flow>;
        using DetachMsg = fsm::Event<"DetachMsg", messages::Detach>;
        using Done = fsm::Event<"Done">;
        using AttachRequest = fsm::Event<"AttachRequest", std::optional<std::string>>;
        using TransferRequest = fsm::Event<"TransferRequest">;
        using FlowRequest = fsm::Event<"FlowRequest">;
        using DetachRequest = fsm::Event<"DetachRequest">;

        using StateMachine = fsm::StateMachine<
            fsm::Transition<Init, AttachSent, AttachRequest>,
            fsm::Transition<Init, AttachReceived, AttachMsg>,
            fsm::Transition<AttachSent, Attaching, AttachMsg>,
            fsm::Transition<Attaching, Attached, Done>,
            fsm::Transition<Attaching, DetachSent, DetachRequest>,
            fsm::Transition<AttachReceived, Attached, Done>,
            fsm::Transition<AttachReceived, DetachSent, DetachRequest>,
            fsm::Transition<Attached, Receiving, TransferMsg>,
            fsm::Transition<Receiving, Attached, Done>,
            fsm::Transition<Attached, FlowReceived, FlowMsg>,
            fsm::Transition<FlowReceived, Attached, Done>,
            fsm::Transition<Attached, FlowSent, FlowRequest>,
            fsm::Transition<FlowSent, Attached, Done>,
            fsm::Transition<Attached, Sending, TransferRequest>,
            fsm::Transition<Sending, Attached, Done>,
            fsm::Transition<Attached, DetachSent, DetachRequest>,
            fsm::Transition<Attached, DetachReceived, DetachMsg>,
            fsm::Transition<DetachSent, Detaching, DetachMsg>,
            fsm::Transition<Detaching, Detached, Done>,
            fsm::Transition<DetachReceived, Detached, Done>,

            fsm::Transition<DetachSent, fsm::Ignore, FlowMsg>,
            fsm::Transition<DetachSent, fsm::Ignore, TransferMsg>

            >;

        using message_channel = asio::experimental::channel<void(std::error_code, Performative, messages::AmqpPayload)>;

        asio::awaitable<void> process_incoming_message() {
            auto [performative, payload] = co_await incoming_message_queue_.async_receive();
            co_await std::visit(
                overload{
                    [this](messages::Attach &&attach) -> asio::awaitable<void> {
                        co_await sm_.generate_event(AttachMsg{std::move(attach)});
                    },
                    [this](messages::Detach &&detach) -> asio::awaitable<void> {
                        co_await sm_.generate_event(DetachMsg{std::move(detach)});
                    },
                    [this](messages::Flow &&flow) -> asio::awaitable<void> {
                        co_await sm_.generate_event(FlowMsg{std::move(flow)});
                    },
                    [this, payload = std::move(payload)](messages::Transfer &&transfer) -> asio::awaitable<void> {
                        co_await sm_.generate_event(TransferMsg{std::make_tuple(std::move(transfer), std::move(payload))
                        });
                    },
                },
                std::move(performative)
            );
            co_return;
        }

        virtual messages::Attach create_attach_request(std::optional<std::string> address) = 0;
        virtual messages::Attach create_attach_rejection(const messages::Attach &request) = 0;

        messages::Detach create_detach_request(bool closed) {
            return messages::Detach{.handle = output_handle_, .closed = closed};
        };

        asio::awaitable<void> state_attach_sent(std::optional<std::string> address) {
            co_await session_->send_message(create_attach_request(std::move(address)));
            co_return;
        }

        asio::awaitable<void> state_attach_received(const messages::Attach &request) {
            // Reject everything
            input_handle_ = request.handle;
            session_->register_remote_link(self());
            co_await session_->send_message(create_attach_rejection(request));
            co_await sm_.generate_local_event(DetachRequest());
            co_return;
        }

        asio::awaitable<void> state_attaching(const messages::Attach &response) {
            input_handle_ = response.handle;
            session_->register_remote_link(self());
            if (co_await receive_attach_response(response)) {
                co_await sm_.generate_local_event(Done());
            } else {
                co_await sm_.generate_local_event(DetachRequest());
            }
            co_return;
        }

        asio::awaitable<void> state_receiving(const messages::Transfer &transfer, messages::AmqpPayload &&payload) {
            co_await receive_transfer(transfer, std::move(payload));
            co_await sm_.generate_local_event(Done());
            co_return;
        }

        asio::awaitable<void> state_sending() {
            co_await sm_.generate_local_event(Done());
            co_return;
        }

        asio::awaitable<void> state_flow_received(const messages::Flow &flow) {
            co_await flow_updated(flow);
            co_await sm_.generate_local_event(Done());
            co_return;
        }

        asio::awaitable<void> state_attached() {
            co_return;
        }

        asio::awaitable<void> state_flow_sent() {
            co_await update_flow();
            co_await sm_.generate_local_event(Done());
            co_return;
        }

        asio::awaitable<void> state_detach_sent() {
            co_await shutdown_outgoing();
            co_await session_->send_message(create_detach_request(true));
            co_return;
        }

        asio::awaitable<void> state_detach_received(const messages::Detach &request) {
            co_await session_->send_message(create_detach_request(true));
            co_await sm_.generate_local_event(Done());
            if (request.error) {
                std::visit(
                    [&](auto &condition) {
                        log_.error(
                            "Detach on error: {} {}", condition, request.error.value().description.value_or("<unknown>")
                        );
                    },
                    request.error.value().condition
                );
            }
            co_return;
        }

        asio::awaitable<void> state_detaching(const messages::Detach &response) {
            co_await sm_.generate_local_event(Done());
            co_return;
        }



        asio::awaitable<void> state_detached() {
            deregister_local_link();
            session_->deregister_remote_link(self());
            incoming_message_queue_.close();
            co_await shutdown();
            co_return;
        }

      private:
        std::string name_;
        Properties properties_;
        Handle output_handle_{};
        Handle input_handle_{};
        xtuml::logging::Logger log_;
        LinkFlow flow_;

        std::shared_ptr<Session> session_;
        message_channel incoming_message_queue_;
        StateMachine sm_;
    };

} // namespace amqp_asio