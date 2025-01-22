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

#include "amqp_asio/session.hh"
#include "amqp_asio/sender.hh"
#include "amqp_asio/receiver.hh"
#include "amqp_asio/messages.hh"

#include <asio/awaitable.hpp>


class RabbitMQManagement {
    using Session = amqp_asio::Session;
    using Receiver = amqp_asio::Receiver;
    using Sender = amqp_asio::Sender;
    using ReceiverOptions = amqp_asio::ReceiverOptions;
    using SenderOptions = amqp_asio::SenderOptions;
    using symbol_t = amqp_asio::messages::symbol_t;
    using any_map_t = amqp_asio::messages::any_map_t;
    using enum amqp_asio::DeliveryMode;
    using Message = amqp_asio::messages::Message;
    using Properties = amqp_asio::messages::Properties;
    using AMQPValue = amqp_asio::messages::AMQPValue;
    using Fields = amqp_asio::messages::Fields;
    using ApplicationProperties = amqp_asio::messages::ApplicationProperties;
  public:
    static asio::awaitable<RabbitMQManagement> create(Session &session) {
        auto properties = Fields{{symbol_t{"paired"}, true}};
        co_return RabbitMQManagement(
            co_await session.open_sender(
                "/management",
                SenderOptions()
                    .name("management-link-pair")
                    .delivery_mode(at_most_once)
                    .properties(properties)
            ),
            co_await session.open_receiver(
                "/management", ReceiverOptions().name("management-link-pair").properties(properties)
            )

        );
    }

    asio::awaitable<void> create_queue(std::string name) {
        auto request = Message{
            .properties = Properties{.to = "/queues/" + name, .subject = "PUT", .reply_to = "$me"},
            .application_properties =
                ApplicationProperties{
                    {"http:request", "1.1"},
                },
            .data = AMQPValue{any_map_t{}},
        };

        co_await sender_.send(request);
        co_await receiver_.receive();
        co_return;
    }

    asio::awaitable<void> bind_exchange(std::string key, std::string queue) {
        auto request = Message{
            .properties = Properties{.to = "/bindings", .subject = "POST", .reply_to = "$me"},
            .application_properties =
                ApplicationProperties{
                    {"http:request", "1.1"},
                },
            .data = AMQPValue{any_map_t{
                {"binding_key", "example.*"},
                {"source", "amq.topic"},
                {"destination_queue", "example.*"},
                {"arguments", any_map_t{}},
            }},
        };
        co_await sender_.send(request);
        co_await receiver_.receive();
        co_return;
    }

    asio::awaitable<void> bind_topic(std::string topic) {
        co_await create_queue(topic);
        co_await bind_exchange(topic, topic);
    }

    asio::awaitable<void> detach() {
        co_await sender_.detach();
        co_await receiver_.detach();
        co_return;
    }

  private:
    RabbitMQManagement(Sender sender, Receiver receiver)
        : sender_(std::move(sender)), receiver_(std::move(receiver)) {}

    Sender sender_;
   Receiver receiver_;
};
