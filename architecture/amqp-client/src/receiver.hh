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
#include "amqp_asio/options.hh"
#include "amqp_asio/receiver.hh"
#include "delivery.hh"
#include "link.hh"
#include "messages.hh"

#include <asio/awaitable.hpp>

namespace amqp_asio {

    template <typename Session>
    class ReceiverImpl : public amqp_asio::Receiver::Impl, public LinkImpl<Session> {
      public:
        using typename LinkImpl<Session>::Role;
        using Self = std::shared_ptr<ReceiverImpl>;
        using Delivery = DeliveryImpl<ReceiverImpl>;

        static asio::awaitable<Self> create(std::shared_ptr<Session> session, ReceiverOptions options) {
            auto link = std::make_shared<ReceiverImpl>(std::move(session), std::move(options));
            co_await link->init();
            co_return link;
        }

        ReceiverImpl(std::shared_ptr<Session> session, ReceiverOptions options)
            : LinkImpl<Session>(options.name().value_or(unique_name()), std::move(session), options.properties()),
              auto_credit_{options.auto_credit()},
              auto_credit_low_water_{options.auto_credit_low_water()},
              auto_credit_high_water_{options.auto_credit_high_water()},
              auto_accept{options.auto_accept()},
              delivery_queue_{this->get_executor(), std::numeric_limits<std::size_t>::max()},
              log_{"async_ampq.receiver.{}", this->name()} {
            flow().link_credit = options.initial_credit();
            if (auto_credit_ && flow().link_credit <= auto_credit_low_water_ &&
                flow().link_credit < auto_credit_high_water_) {
                flow().link_credit = auto_credit_high_water_;
            }
        }

        const LinkFlow &flow() const {
            return LinkImpl<Session>::flow();
        };

        LinkFlow &flow() {
            return LinkImpl<Session>::flow();
        };

        auto self() const {
            return std::static_pointer_cast<const ReceiverImpl>(this->shared_from_this());
        }
        auto self() {
            return std::static_pointer_cast<ReceiverImpl>(this->shared_from_this());
        }

        auto default_settle_mode() const {
            return receiver_settle_mode_;
        }

        asio::awaitable<std::shared_ptr<amqp_asio::Delivery::Impl>> receive() override {
            co_return co_await delivery_queue_.async_receive();
        }

        asio::awaitable<void>
        send_disposition(messages::DeliveryNumber id, bool settled, std::optional<messages::DeliveryState> state) {
            co_await this->session()->send_message(
                messages::Disposition{.role = messages::Role::receiver, .first = id, .settled = settled, .state = state}
            );
            co_return;
        }
        void settle(messages::DeliveryNumber id) {
            this->session()->settle_delivery(id);
        }

        asio::awaitable<void> publish_pending_delivery() {
            co_await delivery_queue_.async_send(std::error_code{}, std::move(pending_delivery_));
            co_return;
        }

        asio::any_io_executor get_executor() const override {
            return LinkImpl<Session>::get_executor();
        }

        asio::awaitable<void> detach() override {
            co_return co_await LinkImpl<Session>::detach();
        }

        asio::awaitable<void> drain() override {
            flow().drain = true;
            auto_credit_ = false;
            co_await this->update_flow();
        }

        asio::awaitable<void> add_credit(messages::uint_t credits) override {
            flow().link_credit += credits;
            flow().drain = false;
            auto_credit_ = false;
            co_await this->update_flow();
        }

        asio::awaitable<void> remove_credit(messages::uint_t credits) override {
            if (flow().link_credit <= credits) {
                flow().link_credit = 0;
            } else {
                flow().link_credit -= credits;
            }
            auto_credit_ = false;
            co_await this->update_flow();
        }

        asio::awaitable<void> set_credit(messages::uint_t credits) override {
            flow().link_credit = credits;
            flow().drain = false;
            auto_credit_ = false;
            co_await this->update_flow();
        }

        asio::awaitable<void> start_auto_credit() override {
            auto_credit_ = true;
            co_await auto_update_credit();
        }
        asio::awaitable<void> stop_auto_credit() override {
            auto_credit_ = false;
            co_return;
        }
        asio::awaitable<void> auto_credit_low_water(messages::uint_t credits) override {
            auto_credit_low_water_ = credits;
            co_await auto_update_credit();
        }

        asio::awaitable<void> auto_credit_high_water(messages::uint_t credits) override {
            auto_credit_high_water_ = credits;
            co_await auto_update_credit();
        }

        asio::awaitable<void> auto_credit_limits(messages::uint_t low_water, messages::uint_t high_water) override {
            auto_credit_low_water_ = low_water;
            auto_credit_high_water_ = high_water;
            co_await auto_update_credit();
        }

      private:
        void register_local_link() override {
            this->session()->register_local_receiver(self());
        }
        void deregister_local_link() override {
            this->session()->deregister_local_receiver(self());
        }

        asio::awaitable<void> bump_flow() {
            ++flow().delivery_count;
            --flow().link_credit;
            if (flow().available > 0) {
                --flow().available;
            }
            co_await auto_update_credit();
            log_.debug("{}", nlohmann::json(flow()).dump());
            co_return;
        }

        asio::awaitable<void> auto_update_credit() {
            if (auto_credit_ && flow().link_credit <= auto_credit_low_water_ &&
                flow().link_credit < auto_credit_high_water_) {
                flow().drain = false;
                flow().link_credit = auto_credit_high_water_;
                co_await this->update_flow();
            }
        }

        asio::awaitable<void> flow_updated(const messages::Flow &update) override {
            if (update.available) {
                flow().available = update.available.value();
            }
            if (update.delivery_count) {
                flow().delivery_count = update.delivery_count.value();
            }
            co_return;
        }

        messages::Attach create_attach_request(std::optional<std::string> address) override {
            return messages::Attach{
                .name = this->name(),
                .handle = this->output_handle(),
                .role = Role::receiver,
                .source = messages::Source{.address = address},
                .target = messages::Target{.address = address},
                .properties = this->properties(),
            };
        }

        messages::Attach create_attach_rejection(const messages::Attach &request) override {
            flow().delivery_count = request.initial_delivery_count.value_or(0);
            receiver_settle_mode_ = request.rcv_settle_mode.value_or(messages::ReceiverSettleMode::first);
            return messages::Attach{
                .name = this->name(), .handle = this->output_handle(), .role = Role::receiver, .target = request.target
            };
        }

        asio::awaitable<bool> receive_attach_response(const messages::Attach &response) override {
            flow().delivery_count = response.initial_delivery_count.value_or(0);
            receiver_settle_mode_ = response.rcv_settle_mode.value_or(messages::ReceiverSettleMode::first);
            co_await this->update_flow();
            co_return true;
        }

        asio::awaitable<void> receive_transfer(const messages::Transfer &transfer, messages::AmqpPayload &&payload) {
            log_.debug("receive_transfer");
            if (!transfer.more.value_or(false)) {
                co_await bump_flow();
            }
            if (!pending_delivery_) {
                log_.debug("creating delivery");
                pending_delivery_ = co_await Delivery::create(
                    self(),
                    transfer.delivery_id.value(),
                    transfer.delivery_tag.value(),
                    transfer.rcv_settle_mode.value_or(default_settle_mode()),
                    auto_accept
                );
                this->session()->register_unsettled_delivery(pending_delivery_);
            }

            log_.debug("updating delivery");
            co_await pending_delivery_->transfer(
                {
                    .aborted = transfer.aborted.value_or(false),
                    .settled = transfer.settled.value_or(false),
                    .more = transfer.more.value_or(false),
                    .batchable = transfer.batchable.value_or(false),
                },
                payload
            );
            co_return;
        }

        bool auto_credit_{};
        messages::uint_t auto_credit_low_water_{};
        messages::uint_t auto_credit_high_water_{};
        messages::ReceiverSettleMode receiver_settle_mode_{};
        bool auto_accept{};

        using delivery_channel = asio::experimental::channel<void(std::error_code, std::shared_ptr<Delivery>)>;

        std::shared_ptr<Delivery> pending_delivery_;
        delivery_channel delivery_queue_;
        xtuml::logging::Logger log_;
    };

} // namespace amqp_asio