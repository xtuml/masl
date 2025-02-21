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

#include "amqp_asio/exceptions.hh"
#include "messages.hh"
#include "decoder.hh"
#include "encoder.hh"
#include "co_mutex.hh"
#include "amqp_asio/any_socket.hh"
#include "frame.hh"
#include "protocol.hh"
#include "sasl_messages.hh"
#include <asio/awaitable.hpp>
#include <asio/connect.hpp>
#include <asio/experimental/awaitable_operators.hpp>
#include <asio/ip/tcp.hpp>
#include <asio/read.hpp>
#include <asio/ssl/stream.hpp>
#include <asio/write.hpp>
#include <logging/log.hh>

namespace amqp_asio {

    class Stream  {
      public:
        Stream(std::unique_ptr<AnySocket> socket)
            : log_("amqp_asio.stream"),
              socket_{std::move(socket)},
              strand_(socket_->get_executor()),
              read_mutex{strand_},
              write_mutex{strand_} {}

        Stream(Stream &&) = delete;

        Stream(Stream const &) = delete;

        Stream &operator=(Stream &&) = delete;

        Stream &operator=(Stream const &) = delete;

        auto get_executor() const {
            return strand_;
        }

        asio::awaitable<void> connect_client(std::string_view host, std::string_view service) {
            co_await socket_->connect(std::move(host),std::move(service));
            co_return;
        }

        asio::awaitable<void> close() {
            co_await socket_->close();
            co_return;
        }

        asio::awaitable<void> write_protocol_message(const protocol::ProtocolMessage &protocol) {
            co_return co_await write_packet(protocol);
        }

        asio::awaitable<void> write_sasl_message(const sasl::SaslMessage &message) {
            co_return co_await write_frame(Frame{SASL_MESSAGE}.body(types::Encoder().encode(message)));
        }

        asio::awaitable<void> write_empty_frame() {
            co_await write_frame(Frame{AMQP_MESSAGE, 0});
        }

        asio::awaitable<void> write_amqp_message(uint16_t channel, const messages::Performative &performative) {
            co_return co_await write_frame(Frame{AMQP_MESSAGE, channel}.body(types::Encoder().encode(performative)));
        }

        asio::awaitable<void> write_amqp_message(messages::AmqpMessage message) {
            types::Encoder encoder;
            encoder.encode(message.performative);
            co_return co_await write_frame(Frame{AMQP_MESSAGE, message.channel}
                                               .body(std::move(encoder).buffer())
                                               .supplementary(std::move(message.payload)));
        }

        asio::awaitable<protocol::ProtocolMessage> read_protocol_message() {
            co_return co_await read_packet<protocol::ProtocolMessage>();
        }

        asio::awaitable<void> read_protocol_message(const protocol::ProtocolMessage &expected) {
            auto protocol = co_await read_protocol_message();
            if (protocol == expected) [[likely]] {
                co_return;
            }
            log_.error("Unexpected protocol {} received. Expected {}", protocol, expected);
            throw AMQPException(make_error_code(error::ProtocolMismatch));
        }

        asio::awaitable<sasl::SaslMessage> read_sasl_message() {
            auto frame = co_await read_frame();
            if (frame.type() != SASL_MESSAGE) {
                log_.error("Unexpected message type {} received.", frame.type());
                throw AMQPException(make_error_code(error::UnexpectedMessage));
            }
            types::Decoder decoder{frame.body()};
            co_return decoder.template decode<sasl::SaslMessage>();
        }

        asio::awaitable<messages::AmqpMessage> read_amqp_message() {
            auto frame = co_await read_frame();
            if (frame.type() != AMQP_MESSAGE) {
                log_.error("Unexpected message type {} received.", frame.type());
                throw AMQPException(make_error_code(error::UnexpectedMessage));
            }
            messages::AmqpMessage result;
            result.channel = frame.type_specific();
            if (!frame.body().empty()) {
                types::Decoder decoder{frame.body()};
                result.performative = decoder.template decode<messages::Performative>();
                result.payload = decoder.remainder();
            }
            co_return result;
        }

      private:
        asio::awaitable<Frame> read_frame() {
            auto guard = co_await read_mutex.guard();
            Frame frame;
            co_await frame.read(*socket_);
            co_return frame;
        }

        template <typename Packet>
        asio::awaitable<Packet> read_packet() {
            auto guard = co_await read_mutex.guard();
            Packet packet;
            using asio::buffer;
            auto buf = buffer(packet);
            co_await socket_->read(buf);
            co_return packet;
        }

        asio::awaitable<void> write_frame(const Frame &frame) {
            co_return co_await write_packet(frame);
        }

        template <typename Packet>
        asio::awaitable<void> write_packet(const Packet &packet) {
            auto guard = co_await write_mutex.guard();
            using asio::buffer;
            auto buf = buffer(packet);
            co_await socket_->write(buf);
            co_return;
        }

      private:
        constexpr static uint8_t AMQP_MESSAGE = 0x00;
        constexpr static uint8_t SASL_MESSAGE = 0x01;

        xtuml::logging::Logger log_;
        std::unique_ptr<AnySocket> socket_;
        asio::any_io_executor strand_;
        co_mutex read_mutex;
        co_mutex write_mutex;
    };

} // namespace amqp_asio