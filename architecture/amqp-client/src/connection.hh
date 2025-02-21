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
#include "amqp_asio/any_socket.hh"
#include "amqp_asio/connection.hh"
#include "amqp_asio/options.hh"
#include "messages.hh"
#include "decoder.hh"
#include "encoder.hh"
#include "co_mutex.hh"
#include "fsm.hh"
#include "frame.hh"
#include "protocol.hh"
#include "sasl_context.hh"
#include "sasl_messages.hh"
#include "session.hh"
#include "stream.hh"
#include "overload.hh"
#include <asio/awaitable.hpp>
#include <asio/bind_cancellation_slot.hpp>
#include <asio/cancellation_signal.hpp>
#include <asio/detached.hpp>
#include <asio/experimental/awaitable_operators.hpp>
#include <asio/experimental/channel.hpp>
#include <asio/experimental/promise.hpp>
#include <asio/experimental/use_promise.hpp>
#include <asio/io_context.hpp>
#include <asio/steady_timer.hpp>
#include <asio/strand.hpp>
#include <fmt/format.h>
#include <logging/log.hh>
#include <map>

using namespace std::literals;

namespace amqp_asio {

    using namespace std::literals;

    class ConnectionImpl : public amqp_asio::Connection::Impl, public std::enable_shared_from_this<ConnectionImpl> {
      public:
        using Session = SessionImpl<ConnectionImpl>;

        template <typename M>
        using message_channel = asio::experimental::channel<void(std::error_code, M)>;

        using timer = asio::steady_timer;

        template <typename... Args>
        static std::shared_ptr<ConnectionImpl> create(Args &&...args) {
            return std::make_shared<ConnectionImpl>(std::forward<Args>(args)...);
        }

        template <typename Socket>
        explicit ConnectionImpl(std::string container_id, std::unique_ptr<Socket> socket)
            : container_id_(std::move(container_id)),
              log_("amqp_asio.connection.{}", container_id_),
              stream_(std::move(socket)),
              keep_alive_timer_(get_executor()),
              operation_timer_(get_executor()),
              outgoing_message_queue_(get_executor()),
              sm(get_executor(), "amqp_asio.connection.sm.{}", container_id_) {}

        ConnectionImpl(ConnectionImpl &&) = delete;

        ConnectionImpl(ConnectionImpl const &) = delete;

        ConnectionImpl &operator=(ConnectionImpl &&) = delete;

        ConnectionImpl &operator=(ConnectionImpl const &) = delete;

        asio::any_io_executor get_executor() const override {
            return stream_.get_executor();
        }

        auto self() const {
            return this->shared_from_this();
        }

        auto self() {
            return this->shared_from_this();
        }

        asio::awaitable<void> open(ConnectionOptions options) override {

            const static protocol::ProtocolMessage amqp_protocol{protocol::Protocol::AMQP};

            co_await stream_.connect_client(options.hostname(), options.port());

            if (options.sasl_options()) {
                co_await negotiate_sasl(*options.sasl_options());
            }

            this->options = options;
            co_await sm.generate_local_event(Start{});

            sm.start(get_executor(), self());

            spawn_cancellable(
                get_executor(),
                [self = self()]() -> asio::awaitable<void> {
                    co_await self->keep_alive();
                },
                log_
            );

            co_await (wait_for_opened() || wait_for_ended());
            if (error_) {
                std::rethrow_exception(error_);
            }
        }

        asio::awaitable<void> close(std::string condition = "", std::string description = "") override {
            co_await sm.generate_event(CloseRequest{messages::Error::make_error(condition, description)});
            co_await wait_for_ended();
            local_channels.clear();
            remote_channels.clear();
            if (error_) {
                std::rethrow_exception(error_);
            }
        }

        asio::awaitable<void> wait_for_opened() {
            co_return co_await sm.template wait_for_state<Idle>();
        }

        asio::awaitable<void> wait_for_ended() {
            co_await sm.template wait_for_state<Closed>();
        }

        asio::awaitable<std::shared_ptr<amqp_asio::Session::Impl>>
        open_session(SessionOptions options) override {
            auto session = co_await Session::create(
                next_channel++, self(), options.values_or(this->options.session_options())
            );
            local_channels.emplace(session->id(), session);
            co_await session->start();
            co_await session->wait_for_started();
            co_return session;
        }

        asio::awaitable<void>
        send_amqp_message(uint16_t channel, messages::Performative performative, messages::AmqpPayload message = {}) {
            co_await outgoing_message_queue_.async_send(
                {}, messages::AmqpMessage{channel, std::move(performative), std::move(message)}
            );
        }

        auto container_id() const {
            return container_id_;
        }

        void register_session(std::shared_ptr<Session> session) {
            local_channels.emplace(session->id(), session);
        }
        void deregister_session(std::shared_ptr<Session> session) {
            local_channels.erase(session->id());
        }

      private:
        asio::awaitable<void> keep_alive() {
            if (peer_idle_timeout > std::chrono::milliseconds::zero()) {
                log_.debug("Keep alive starting with interval {}", peer_idle_timeout);
                keep_alive_timer_.expires_after(peer_idle_timeout);
                co_await keep_alive_timer_.async_wait();
                while (std::holds_alternative<Idle>(sm.current_state())) {
                    log_.debug("Writing keep alive frame");
                    co_await stream_.write_empty_frame();
                    keep_alive_timer_.expires_at(keep_alive_timer_.expiry() + peer_idle_timeout);
                    co_await keep_alive_timer_.async_wait();
                }
                log_.debug("Keep alive ended.");
            } else {
                log_.debug("Keep alive not required.");
            }
        }

      private:
        asio::awaitable<std::vector<std::byte>> negotiate_sasl(SaslOptions options) {
            const static protocol::ProtocolMessage sasl_protocol{protocol::Protocol::SASL};
            co_await stream_.write_protocol_message(sasl_protocol);
            co_await stream_.read_protocol_message(sasl_protocol);

            auto mechanisms = get_message<sasl::SaslMechanisms>(co_await stream_.read_sasl_message());

            SaslClientContext context{options};
            std::vector<std::string> mech(
                std::begin(mechanisms.sasl_server_mechanisms), std::end(mechanisms.sasl_server_mechanisms)
            );
            auto sasl_data = context.start(mech);
            co_await stream_.write_sasl_message(sasl::SaslInit{types::symbol_t{context.mechanism()}, sasl_data});
            auto sasl_message = co_await stream_.read_sasl_message();
            while (std::holds_alternative<sasl::SaslChallenge>(sasl_message)) {
                auto challenge = std::get<sasl::SaslChallenge>(std::move(sasl_message));
                co_await stream_.write_sasl_message(sasl::SaslResponse{context.challenge(challenge.challenge)});
                sasl_message = co_await stream_.read_sasl_message();
            }
            auto outcome = get_message<sasl::SaslOutcome>(std::move(sasl_message));
            switch (outcome.code) {
                case sasl::SaslCode::ok:
                    co_return std::move(outcome.additional_data).value_or(std::vector<std::byte>());
                case sasl::SaslCode::auth:
                    log_.error("SASL Authentication failed");
                    throw AMQPException(make_error_code(error::SaslAuthenticationFailed));
                default:
                    log_.error("SASL System error");
                    throw AMQPException(make_error_code(error::SaslSystemError));
            }
        }

        void start_timeout(const asio::steady_timer::duration &expiry) {
            if (expiry > 0s) {
                operation_timer_.expires_after(expiry);
                spawn_cancellable(
                    get_executor(),
                    [self = self()]() -> asio::awaitable<void> {
                        co_await self->operation_timer_.async_wait();
                        co_await self->sm.generate_event(Timeout{});
                    },
                    log_
                );
            }
        }

        void cancel_timeout() {
            operation_timer_.cancel();
        }

        template <typename T, typename Msg>
        T get_message(Msg &&msg) {
            if (std::holds_alternative<T>(msg)) [[likely]] {
                return std::get<T>(std::forward<Msg>(msg));
            }
            auto name = std::visit(
                []<typename M>(const M &) {
                    return M::amqp_descriptor.name;
                },
                msg
            );
            log_.error("Unexpected message type {} received. Expected {}", name, T::amqp_descriptor.name);
            throw AMQPException(make_error_code(error::UnexpectedMessage));
        }

      private:
        using Self = std::shared_ptr<ConnectionImpl>;
        using ChannelId = messages::ChannelId;

        asio::awaitable<std::shared_ptr<Session>> find_session(ChannelId channel, const auto &) {
            auto session =
                remote_channels.contains(channel) ? remote_channels[channel].lock() : std::shared_ptr<Session>{};
            if (!session) {
                log_.error("Session {} not found", channel);
            }
            co_return session;
        }

        asio::awaitable<std::shared_ptr<Session>> find_session(ChannelId channel, const messages::Begin &begin) {

            if (begin.remote_channel) {
                auto local_channel = begin.remote_channel.value();
                if (local_channels.contains(local_channel)) {
                    auto session = local_channels[local_channel];
                    remote_channels.emplace(channel, session);
                    co_return session;
                } else {
                    log_.error("Local session {} not found", local_channel);
                    co_return std::shared_ptr<Session>{};
                }
            } else {
                auto session = co_await Session::create(next_channel++, self(), options.session_options());
                remote_channels.emplace(channel, session);
                co_return session;
            }
        }

        asio::awaitable<void> send_amqp_messages() {
            while (true) {
                auto message = co_await outgoing_message_queue_.async_receive();
                log_.trace("Sending message: {}", nlohmann::json(message).dump(2));
                co_await stream_.write_amqp_message(std::move(message));
            }
        }

        asio::awaitable<void> receive_amqp_messages() {
            while (true) {
                auto message = co_await stream_.read_amqp_message();
                log_.trace("Received message: {}", nlohmann::json(message).dump(2));
                if (message.performative) {
                    co_await std::visit(
                        overload{
                            [this](messages::Open &&open) -> asio::awaitable<void> {
                                co_await this->sm.generate_event(OpenMsg{std::move(open)});
                            },
                            [this](messages::Close &&close) -> asio::awaitable<void> {
                                co_await this->sm.generate_event(CloseMsg{std::move(close)});
                            },
                            [this, &message]<typename P>(P &&performative) -> asio::awaitable<void> {
                                log_.debug(
                                    "Message {} received on channel {}",
                                    std::decay_t<P>::amqp_descriptor.name,
                                    message.channel
                                );
                                auto session = co_await find_session(message.channel, performative);
                                if (session) {
                                    co_await session->push_message(
                                        std::forward<P>(performative), std::move(message.payload)
                                    );
                                }
                            }
                        },
                        std::move(message.performative.value())
                    );
                } else {
                    log_.debug("Empty frame received");
                }
            }
        }

        static constexpr auto plantuml_fsm = R"(
            @startuml
            state HeaderReceived #line.dotted
            state CloseReceived #line.dotted
            state Opening #line.dotted
            state Closing #line.dotted

            Init             --> HeaderSent       : Start
            HeaderSent       --> HeaderReceived   : Header
            HeaderReceived   --> OpenSent         : ValidHeader
            HeaderReceived   --> Closed           : InvalidHeader
            OpenSent         --> Opening          : OpenMsg
            Opening          --> Idle             : Done
            Idle             --> CloseReceived    : CloseMsg
            CloseReceived    --> Closed           : Done
            Idle             --> CloseSent        : CloseRequest
            CloseSent        --> Closing          : CloseMsg
            CloseSent        --> Closed           : Timeout
            Closing          --> Closed           : Done


            HeaderSent : send header
            HeaderReceived: if header valid: generate ValidHeader
            HeaderReceived: else: generate InvalidHeader
            OpenSent: send Open
            Opening: setup options
            Opening: generate Done
            CloseReceived : send Close
            CloseReceived : generate Done
            CloseSent : send Close
            Closed: close stream
            Closing : generate Done

            @enduml
        )";
        using Init = fsm::State<"Init">;
        struct HeaderSent : fsm::State<"HeaderSent"> {
            asio::awaitable<void> operator()(Self self) {
                co_await self->enter_state_header_sent();
            }
        };
        struct HeaderReceived : fsm::State<"HeaderReceived"> {
            asio::awaitable<void> operator()(Self self, protocol::ProtocolMessage &&hdr) {
                co_await self->enter_state_header_received(std::move(hdr));
            };
        };
        struct OpenSent : fsm::State<"OpenSent"> {
            asio::awaitable<void> operator()(Self self) {
                co_await self->enter_state_open_sent();
            }
        };
        struct Opening : fsm::State<"Opening"> {
            asio::awaitable<void> operator()(Self self, messages::Open &&open) {
                co_await self->enter_state_opening(std::move(open));
            };
        };
        struct Idle : fsm::State<"Idle"> {};
        struct CloseReceived : fsm::State<"CloseReceived"> {
            asio::awaitable<void> operator()(Self self, messages::Close &&close) {
                co_await self->enter_state_close_received(std::move(close));
            };
        };
        struct CloseSent : fsm::State<"CloseSent"> {
            asio::awaitable<void> operator()(Self self) {
                co_await self->enter_state_close_sent();
            }
        };
        struct Closing : fsm::State<"Closing"> {
            asio::awaitable<void> operator()(Self self, messages::Close &&close) {
                co_await self->enter_state_closing(std::move(close));
            }
        };
        struct Closed : fsm::State<"Closed", fsm::Terminal> {
            asio::awaitable<void> operator()(Self self) {
                co_await self->enter_state_closed();
            }
        };

        using Start = fsm::Event<"Start">;
        using Header = fsm::Event<"Header", protocol::ProtocolMessage>;
        using ValidHeader = fsm::Event<"ValidHeader">;
        using InvalidHeader = fsm::Event<"InvalidHeader">;
        using OpenMsg = fsm::Event<"OpenMsg", messages::Open>;
        using CloseMsg = fsm::Event<"CloseMsg", messages::Close>;
        using CloseRequest = fsm::Event<"CloseRequest", std::optional<messages::Error>>;
        using Timeout = fsm::Event<"Timeout">;
        using Done = fsm::Event<"Done">;

        using StateMachine = fsm::StateMachine<
            fsm::Transition<Init, HeaderSent, Start>,
            fsm::Transition<HeaderSent, HeaderReceived, Header>,
            fsm::Transition<HeaderReceived, OpenSent, ValidHeader>,
            fsm::Transition<HeaderReceived, Closed, InvalidHeader>,
            fsm::Transition<OpenSent, Opening, OpenMsg>,
            fsm::Transition<Opening, Idle, Done>,
            fsm::Transition<Idle, CloseReceived, CloseMsg>,
            fsm::Transition<CloseReceived, Closed, Done>,
            fsm::Transition<Idle, CloseSent, CloseRequest>,
            fsm::Transition<CloseSent, Closing, CloseMsg>,
            fsm::Transition<CloseSent, Closed, Timeout>,
            fsm::Transition<Closing, Closed, Done>>;

        asio::awaitable<void> enter_state_header_sent() {
            asio::co_spawn(
                get_executor(),
                [self = self()]() -> asio::awaitable<void> {
                    auto hdr = co_await self->stream_.read_protocol_message();
                    co_await self->sm.generate_event(Header{std::move(hdr)});
                },
                asio::detached
            );
            co_await stream_.write_protocol_message(protocol::Protocol::AMQP);
        }

        asio::awaitable<void> enter_state_header_received(const protocol::ProtocolMessage &hdr) {
            const static auto expected = protocol::ProtocolMessage(protocol::Protocol::AMQP);
            if (hdr == expected) {
                log_.debug("Valid protocol {} received", hdr);
                asio::co_spawn(
                    get_executor(),
                    [self = self()]() -> asio::awaitable<void> {
                        co_await (self->receive_amqp_messages() && self->send_amqp_messages());
                    },
                    asio::detached
                );
                co_await sm.generate_local_event(ValidHeader{});
            } else {
                log_.error("Unexpected protocol {} received. Expected {}", hdr, expected);
                error_ = std::make_exception_ptr(AMQPException(make_error_code(error::ProtocolMismatch)));
                co_await sm.generate_local_event(InvalidHeader{});
            }
            co_return;
        }

        asio::awaitable<void> enter_state_open_sent() {
            auto desired = std::vector{types::symbol_t{"ANONYMOUS-RELAY"}};
            co_await send_amqp_message(
                0,
                messages::Open{
                    .container_id = container_id_,
                    .hostname = options.hostname(),
                    .idle_time_out = options.idle_timeout(),
                    .desired_capabilities = desired,
                }
            );
        }

        asio::awaitable<void> enter_state_opening(const messages::Open &open) {
            max_frame_size_ = open.max_frame_size.value_or(std::numeric_limits<types::uint_t>::max());
            peer_idle_timeout = std::chrono::milliseconds(open.idle_time_out.value_or(0s));
            channel_max = open.channel_max.value_or(std::numeric_limits<types::ushort_t>::max());
            asio::co_spawn(
                get_executor(),
                [self = self()]() -> asio::awaitable<void> {
                    co_await self->keep_alive();
                },
                asio::detached
            );
            co_await sm.generate_local_event(Done{});
            co_return;
        }

        asio::awaitable<void> enter_state_close_received(const messages::Close &close) {
            if (close.error) {
                log_.error(
                    "Close requested by peer: {}, {}", close.error.value().condition, close.error.value().description
                );
            }
            keep_alive_timer_.cancel();
            co_await send_amqp_message(0, messages::Close{});
            co_await sm.generate_local_event(Done{});
        }

        asio::awaitable<void> enter_state_close_sent() {
            keep_alive_timer_.cancel();
            co_await send_amqp_message(0, messages::Close{});
            start_timeout(options.close_timeout());
        }

        asio::awaitable<void> enter_state_closing(const messages::Close &close) {
            cancel_timeout();
            co_await sm.generate_local_event(Done{});
        }

        asio::awaitable<void> enter_state_closed() {
            co_await stream_.close();
        }

      private:
        std::string container_id_;
        xtuml::logging::Logger log_;
        Stream stream_;
        timer keep_alive_timer_;
        timer operation_timer_;
        message_channel<messages::AmqpMessage> outgoing_message_queue_;
        types::uint_t max_frame_size_{};
        types::ushort_t channel_max{};
        std::chrono::milliseconds local_idle_timeout{};
        std::chrono::milliseconds peer_idle_timeout{};
        std::map<types::ushort_t, std::shared_ptr<Session>> local_channels;
        std::map<types::ushort_t, std::weak_ptr<Session>> remote_channels;
        types::ushort_t next_channel{};
        ConnectionOptions options{};
        std::exception_ptr error_;
        StateMachine sm;
    };

} // namespace amqp_asio
