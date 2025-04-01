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
#include "amqp_asio/sender.hh"
#include "decoder.hh"
#include "encoder.hh"
#include "link.hh"
#include "messages.hh"
#include "tracker.hh"
#include <asio/awaitable.hpp>
#include <asio/strand.hpp>

namespace amqp_asio {

    template <typename Session>
    class SenderImpl : public amqp_asio::Sender::Impl, public LinkImpl<Session> {
      public:
        using Self = std::shared_ptr<SenderImpl>;
        using Tracker = TrackerImpl<SenderImpl>;

        static asio::awaitable<Self> create(std::shared_ptr<Session> session, SenderOptions options) {
            auto link = std::make_shared<SenderImpl>(std::move(session), std::move(options));
            co_await link->init();
            co_return link;
        }

        SenderImpl(std::shared_ptr<Session> session, SenderOptions options)
            : LinkImpl<Session>(options.name().value_or(unique_name()), std::move(session), options.properties()),
              send_strand_{make_strand(this->get_executor())},
              message_queue_{this->get_executor(), options.max_queue()},
              credit_watch_{this->get_executor()},
              log_{"amqp_asio.sender.{}", this->name()} {
            switch (options.delivery_mode()) {
                case DeliveryMode::any:
                    receiver_settle_mode_ = messages::ReceiverSettleMode::second;
                    break;
                case DeliveryMode::at_most_once:
                    sender_settle_mode_ = messages::SenderSettleMode::settled;
                    break;
                case DeliveryMode::at_least_once:
                    sender_settle_mode_ = messages::SenderSettleMode::unsettled;
                    break;
                case DeliveryMode::exactly_once:
                    sender_settle_mode_ = messages::SenderSettleMode::unsettled;
                    receiver_settle_mode_ = messages::ReceiverSettleMode::second;
                    break;
            }
        }

        asio::awaitable<void> init() override {
            co_await LinkImpl<Session>::init();
            spawn_cancellable_loop(
                this->get_executor(),
                [self = self()]() -> asio::awaitable<void> {
                    co_await self->send_next_message();
                    co_return;
                },
                log_
            );
            co_return;
        }

        auto self() const {
            return std::static_pointer_cast<const SenderImpl>(this->shared_from_this());
        }
        auto self() {
            return std::static_pointer_cast<SenderImpl>(this->shared_from_this());
        }

        asio::awaitable<std::shared_ptr<amqp_asio::Tracker::Impl>>
        send(messages::Message message, std::optional<DeliveryMode> mode = {}) override {
            // Do sends on a strand to prevent races between getting the delivery-id and pushing to the queue
            co_return co_await asio::co_spawn(
                send_strand_,
                [this, message = std::move(message), mode](
                ) -> asio::awaitable<std::shared_ptr<amqp_asio::Tracker::Impl>> {
                    auto tracker = co_await Tracker::create(
                        self(), this->session()->next_delivery_number(), std::move(message), mode
                    );
                    this->session()->register_unsettled_tracker(tracker);

                    co_await message_queue_.async_send({}, tracker);
                    ++this->flow().available;
                    co_return tracker;
                }
            );
        }

        asio::awaitable<void>
        send_disposition(messages::DeliveryNumber id, bool settled, std::optional<messages::DeliveryState> state) {
            co_await this->session()->send_message(
                messages::Disposition{.role = messages::Role::sender, .first = id, .settled = settled, .state = state}
            );
            co_return;
        }

        asio::any_io_executor get_executor() const override {
            return LinkImpl<Session>::get_executor();
        }

        asio::awaitable<void> detach() override {
            co_return co_await LinkImpl<Session>::detach();
        }

        void settle(messages::DeliveryNumber id) {
            this->session()->settle_tracker(id);
        }

      private:
        void register_local_link() override {
            this->session()->register_local_sender(self());
        }
        void deregister_local_link() override {
            this->session()->deregister_local_sender(self());
        }

        messages::Attach create_attach_request(std::optional<std::string> address) override {

            auto result = messages::Attach{
                .name = this->name(),
                .handle = this->output_handle(),
                .role = messages::Role::sender,
                .snd_settle_mode = sender_settle_mode_ == messages::SenderSettleMode::mixed
                                       ? std::optional<messages::SenderSettleMode>{}
                                       : sender_settle_mode_,
                .rcv_settle_mode = receiver_settle_mode_ == messages::ReceiverSettleMode::first
                                       ? std::optional<messages::ReceiverSettleMode>{}
                                       : receiver_settle_mode_,
                .source = messages::Source{.address = address},
                .target = messages::Target{.address = address},
                .initial_delivery_count{this->flow().delivery_count},
                .properties = this->properties()
            };

            return result;
        }

        messages::Attach create_attach_rejection(const messages::Attach &request) override {
            return messages::Attach{
                .name = this->name(),
                .handle = this->output_handle(),
                .role = messages::Role::sender,
                .source = request.source
            };
        }

        asio::awaitable<bool> receive_attach_response(const messages::Attach &response) override {
            sender_settle_mode_ = response.snd_settle_mode.value_or(messages::SenderSettleMode::mixed);
            receiver_settle_mode_ = response.rcv_settle_mode.value_or(messages::ReceiverSettleMode::first);
            max_message_size_ = response.max_message_size.value_or(0);
            co_await this->update_flow();
            co_return true;
        }

        asio::awaitable<void> flow_updated(const messages::Flow &update) override {
            if (update.link_credit) {
                log_.debug("Updating credit from: {}", this->flow().link_credit);
                this->flow().link_credit =
                    update.delivery_count.value_or(0) + update.link_credit.value() - this->flow().delivery_count;
                credit_watch_.notify();
                log_.debug("Updated credit to: {}", this->flow().link_credit);
            }
            if (update.drain) {
                this->flow().drain = update.drain.value();
                co_await check_drain();
            }
            co_return;
        }

        asio::awaitable<void> check_drain() {
            if (this->flow().drain && !this->flow().available) {
                this->flow().delivery_count += this->flow().link_credit;
                this->flow().link_credit = 0;
                co_await this->update_flow();
            }
        }

        asio::awaitable<void> send_next_message() {
            auto tracker = co_await message_queue_.async_receive();
            co_await wait_for_credit();

            log_.debug("Sending message\n{}", nlohmann::json(tracker->message()).dump(2));

            types::Encoder encoder;
            encoder.encode(tracker->message());
            auto buffer = std::move(encoder).buffer();

            auto transfer = messages::Transfer{
                .handle = this->output_handle(),
                .delivery_id = tracker->id(),
                .delivery_tag = tracker->tag(),
            };

            if (sender_settle_mode_ == messages::SenderSettleMode::settled ||
                (sender_settle_mode_ == messages::SenderSettleMode::mixed &&
                 tracker->delivery_mode().value_or(DeliveryMode::at_least_once) == DeliveryMode::at_most_once)) {
                transfer.settled = true;
            }
            if (receiver_settle_mode_ == messages::ReceiverSettleMode::second &&
                tracker->delivery_mode().value_or(DeliveryMode::exactly_once) == DeliveryMode::at_least_once) {
                transfer.rcv_settle_mode = messages::ReceiverSettleMode::first;
            }

            if (max_message_size_ == 0 || buffer.size() <= max_message_size_) {
                co_await this->session()->send_message(transfer, std::move(buffer));

            } else {
                transfer.more = true;
                for (std::size_t start = 0; start < buffer.size(); start += max_message_size_) {
                    auto end = start + max_message_size_;
                    if (end >= buffer.size()) {
                        end = buffer.size();
                        transfer.more.reset();
                    }
                    co_await this->session()->send_message(
                        transfer, messages::AmqpPayload{buffer.begin() + start, buffer.begin() + end}
                    );
                    // Only needed for first
                    transfer.delivery_id.reset();
                    transfer.delivery_tag.reset();
                }
            }
            --this->flow().link_credit;
            --this->flow().available;
            ++this->flow().delivery_count;
            co_await check_drain();
            co_await tracker->sent(
                transfer.settled.value_or(false), transfer.rcv_settle_mode.value_or(receiver_settle_mode_)
            );
        }

        asio::awaitable<void> wait_for_credit() {
            if (this->flow().link_credit == 0) {
                log_.debug("Waiting for credit");
            }
            co_await credit_watch_.wait([this]() {
                return this->flow().link_credit > 0;
            });
            log_.debug("Available credit: {}", this->flow().link_credit);
        }

        asio::awaitable<void> shutdown_outgoing() override {
            message_queue_.close();
            credit_watch_.cancel();
            co_return;
        }

        asio::awaitable<void> shutdown() override {
            message_queue_.close();
            credit_watch_.cancel();
            co_return;
        }

        asio::any_io_executor send_strand_;
        messages::SenderSettleMode sender_settle_mode_{messages::SenderSettleMode::mixed};
        messages::ReceiverSettleMode receiver_settle_mode_{messages::ReceiverSettleMode::first};
        messages::ulong_t max_message_size_{};
        using message_channel = asio::experimental::channel<void(std::error_code, std::shared_ptr<Tracker>)>;
        message_channel message_queue_;
        ConditionVar credit_watch_;

        xtuml::logging::Logger log_;
    };

} // namespace amqp_asio