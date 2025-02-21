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

#include "json.hh"
#include <array>

namespace amqp_asio::types {

    template <typename T, typename... Args>
    concept copy_constructor_args = sizeof...(Args) == 1 && (std::derived_from<std::remove_cvref_t<Args>, T> && ...);

    template <typename T, typename>
    struct wrapper_t {
        template <typename... Args>
            requires(!copy_constructor_args<wrapper_t, Args...>)
        explicit wrapper_t(Args &&...args)
            : value(std::forward<Args>(args)...) {}

        wrapper_t() = default;

        wrapper_t(const wrapper_t &) = default;

        wrapper_t(wrapper_t &&) = default;

        wrapper_t &operator=(const wrapper_t &) = default;

        wrapper_t &operator=(wrapper_t &&) = default;

        ~wrapper_t() = default;

        using value_type = T;
        value_type value{};

        operator value_type() const {
            return value;
        }

        auto operator<=>(const wrapper_t &) const = default;

        friend auto format_as(const wrapper_t &value) {
            return value.value;
        }

        friend void to_json(nlohmann::json &j, const wrapper_t &v) {
            j = detail::amqp_value_to_json(v.value);
        }
    };

    template <class T>
        requires requires { typename T::value_type; }
    struct wrapped_type {
        using type = typename T::value_type;
    };

    template <typename T, typename Tag>
    struct wrapped_type<wrapper_t<T, Tag>> {
        using type = typename wrapped_type<T>::type;
    };

    template <typename T>
    using wrapped_type_t = typename wrapped_type<T>::type;

    template <typename T>
    concept init_list_constructible = std::constructible_from<std::initializer_list<wrapped_type_t<T>>>;

    template <init_list_constructible T, typename Tag>
    struct wrapper_t<T, Tag> {

        template <typename... Args>
            requires(!copy_constructor_args<wrapper_t, Args...>)
        explicit wrapper_t(Args &&...args)
            : value(std::forward<Args>(args)...) {}

        explicit wrapper_t(const std::initializer_list<wrapped_type_t<T>> &il)
            : value(il) {}

        explicit wrapper_t(std::initializer_list<wrapped_type_t<T>> &&il)
            : value(std::move(il)) {}

        wrapper_t() = default;

        wrapper_t(const wrapper_t &) = default;

        wrapper_t(wrapper_t &&) = default;

        wrapper_t &operator=(const wrapper_t &) = default;

        wrapper_t &operator=(wrapper_t &&) = default;

        ~wrapper_t() = default;

        using value_type = T;
        value_type value{};

        operator value_type() const {
            return value;
        }

        auto operator<=>(const wrapper_t &) const = default;

        friend auto format_as(const wrapper_t &value) {
            return value.value;
        }

        friend void to_json(nlohmann::json &j, const wrapper_t &v) {
            j = detail::amqp_value_to_json(v.value);
        }
    };
} // namespace amqp_asio::types