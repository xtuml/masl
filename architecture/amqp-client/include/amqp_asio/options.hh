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
#include <chrono>
#include <optional>
#include <string>
#include <vector>

#define DEFINE_OPTION(name, type)                                                                                      \
  private:                                                                                                             \
    std::optional<type> name##_;                                                                                       \
                                                                                                                       \
  public:                                                                                                              \
    auto &name(std::optional<type> t) & {                                                                              \
        name##_ = std::move(t);                                                                                        \
        return *this;                                                                                                  \
    }                                                                                                                  \
    auto &&name(std::optional<type> t) && {                                                                            \
        name##_ = std::move(t);                                                                                        \
        return std::move(*this);                                                                                       \
    }                                                                                                                  \
    auto name() const {                                                                                                \
        return name##_.value_or(defaults().name##_.value_or(last_resort().name##_.value()));                           \
    }

#define DEFINE_OPTIONAL_OPTION(name, type)                                                                             \
  private:                                                                                                             \
    std::optional<type> name##_;                                                                                       \
                                                                                                                       \
  public:                                                                                                              \
    auto &name(std::optional<type> t) & {                                                                              \
        name##_ = std::move(t);                                                                                        \
        return *this;                                                                                                  \
    }                                                                                                                  \
    auto &&name(std::optional<type> t) && {                                                                            \
        name##_ = std::move(t);                                                                                        \
        return std::move(*this);                                                                                       \
    }                                                                                                                  \
    const auto &name() const {                                                                                         \
        return name##_ ? name##_ : (defaults().name##_ ? defaults().name##_ : last_resort().name##_);                  \
    }

#define DEFINE_SUB_OPTION(name, type)                                                                                  \
  private:                                                                                                             \
    type name##_;                                                                                                      \
                                                                                                                       \
  public:                                                                                                              \
    const auto &name() const {                                                                                         \
        return name##_;                                                                                                \
    }

#define OR_OPTION(name) name(name##_ ? name##_ : rhs.name##_)

namespace amqp_asio {
    using namespace std::literals;

    enum class DeliveryMode { any, at_most_once, at_least_once, exactly_once };

    class SenderOptions {
        DEFINE_OPTIONAL_OPTION(name, std::string);
        DEFINE_OPTION(delivery_mode, DeliveryMode);
        DEFINE_OPTION(max_queue, std::size_t);
        DEFINE_OPTIONAL_OPTION(properties, messages::Fields);

      public:
        static SenderOptions &defaults() {
            static SenderOptions opts;
            return opts;
        }

        auto values_or(const SenderOptions &rhs) const {
            return SenderOptions().OR_OPTION(name).OR_OPTION(delivery_mode).OR_OPTION(max_queue).OR_OPTION(properties);
        }

        auto operator<=>(const SenderOptions &) const = default;

      private:
        static const SenderOptions &last_resort() {
            static SenderOptions opts = SenderOptions()
                                            .delivery_mode(DeliveryMode::at_most_once)
                                            .max_queue(std::numeric_limits<std::size_t>::max());
            return opts;
        }
    };

    class ReceiverOptions {
        DEFINE_OPTIONAL_OPTION(name, std::string);
        DEFINE_OPTION(auto_credit, bool);
        DEFINE_OPTION(auto_credit_low_water, messages::uint_t);
        DEFINE_OPTION(auto_credit_high_water, messages::uint_t);
        DEFINE_OPTION(initial_credit, messages::uint_t);
        DEFINE_OPTION(auto_accept, bool);
        DEFINE_OPTIONAL_OPTION(properties, messages::Fields);

      public:
        static ReceiverOptions &defaults() {
            static ReceiverOptions opts;
            return opts;
        }

        auto values_or(const ReceiverOptions &rhs) const {
            return ReceiverOptions()
                .OR_OPTION(name)
                .OR_OPTION(auto_credit)
                .OR_OPTION(auto_credit_low_water)
                .OR_OPTION(auto_credit_high_water)
                .OR_OPTION(initial_credit)
                .OR_OPTION(auto_accept)
                .OR_OPTION(properties);
        }

        auto operator<=>(const ReceiverOptions &) const = default;

      private:
        static const ReceiverOptions &last_resort() {
            static ReceiverOptions opts =
                ReceiverOptions().auto_credit(true).auto_credit_low_water(5).auto_credit_high_water(10).initial_credit(0).auto_accept(true);
            return opts;
        }
    };

    struct SessionOptions {
        DEFINE_SUB_OPTION(sender_options, SenderOptions);
        DEFINE_SUB_OPTION(receiver_options, ReceiverOptions);

        auto values_or(const SessionOptions &rhs) const {
            SessionOptions result;
            result.sender_options_ = sender_options().values_or(rhs.sender_options());
            result.receiver_options_ = receiver_options().values_or(rhs.receiver_options());
            return result;
        }

        auto operator<=>(const SessionOptions &) const = default;
    };

    class SaslOptions {
        DEFINE_OPTIONAL_OPTION(authname, std::string);
        DEFINE_OPTIONAL_OPTION(user, std::string);
        DEFINE_OPTIONAL_OPTION(password, std::string);
        DEFINE_OPTION(allow_insecure, bool);
        DEFINE_OPTION(allowed_mechanisms, std::vector<std::string>);

      public:
        static SaslOptions &defaults() {
            static SaslOptions opts;
            return opts;
        }
        auto values_or(const SaslOptions &rhs) const {
            return SaslOptions()
                .OR_OPTION(authname)
                .OR_OPTION(user)
                .OR_OPTION(password)
                .OR_OPTION(allow_insecure)
                .OR_OPTION(allowed_mechanisms);
        }
        auto operator<=>(const SaslOptions &) const = default;

      private:
        static const SaslOptions &last_resort() {
            static SaslOptions opts =
                SaslOptions().allow_insecure(false).allowed_mechanisms(std::vector<std::string>());
            return opts;
        }
    };

    struct ConnectionOptions {
        DEFINE_OPTION(hostname, std::string);
        DEFINE_OPTION(port, std::string);
        DEFINE_OPTION(idle_timeout, std::chrono::milliseconds);
        DEFINE_OPTION(operation_timeout, std::chrono::milliseconds);
        DEFINE_OPTION(close_timeout, std::chrono::milliseconds);
        DEFINE_OPTIONAL_OPTION(sasl_options, SaslOptions);
        DEFINE_SUB_OPTION(session_options, SessionOptions);

      public:
        static ConnectionOptions &defaults() {
            static ConnectionOptions opts;
            return opts;
        }
        auto values_or(const ConnectionOptions &rhs) const {
            auto result = ConnectionOptions()
                              .OR_OPTION(hostname)
                              .OR_OPTION(port)
                              .OR_OPTION(idle_timeout)
                              .OR_OPTION(operation_timeout)
                              .OR_OPTION(close_timeout)
                              .OR_OPTION(sasl_options);
            result.session_options_ = session_options().values_or(rhs.session_options());
        }

        auto operator<=>(const ConnectionOptions &) const = default;

      private:
        static const ConnectionOptions &last_resort() {
            static ConnectionOptions opts = ConnectionOptions()
                                                .hostname("localhost")
                                                .port("5672")
                                                .idle_timeout(30s)
                                                .operation_timeout(10s)
                                                .close_timeout(10s);
            return opts;
        }
    };

} // namespace amqp_asio

#undef DEFINE_OPTION
#undef MERGE_OPTION
