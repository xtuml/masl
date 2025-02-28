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

#include <algorithm>
#include <fmt/format.h>
#include <nlohmann/json.hpp>
#include <ranges>
#include <vector>

#include "types.hh"

namespace amqp_asio::types {

    template <typename E>
    concept enumeration = std::is_enum_v<E>;

    template <enumeration E>
    using EnumName = std::pair<E, std::string>;

    template <enumeration E>
    using EnumNameLookup = std::vector<EnumName<E>>;

    template <typename E>
    concept named_enumeration = enumeration<E> && requires(E e) {
        { enum_name_lookup(e) } -> std::convertible_to<EnumNameLookup<E>>;
    };

    template <typename E>
    concept symbolic_enumeration =
        enumeration<E> && named_enumeration<E> && requires(E e) { symbolic_enumeration_check(e); };

    template <symbolic_enumeration E>
    using EnumSymbol = std::pair<E, symbol_t>;

    template <symbolic_enumeration E>
    using EnumSymbolLookup = std::vector<EnumSymbol<E>>;

    template <named_enumeration E>
    inline const EnumNameLookup<E> &enum_name_lookup() {
        return enum_name_lookup(E{});
    }

    template <symbolic_enumeration E>
    inline const auto &enum_symbol_lookup() {
        static EnumSymbolLookup<E> lookup = []() {
            EnumSymbolLookup<E> result;
            std::ranges::copy(
                std::views::transform(
                    enum_name_lookup<E>(),
                    [](const auto &v) {
                        return EnumSymbol{v.first, symbol_t{v.second}};
                    }
                ),
                std::back_inserter(result)
            );
            return result;
        }();
        return lookup;
    }

    template <named_enumeration E>
    inline const std::string &enum_to_name(const E &e) {
        auto &lookup = enum_name_lookup<E>();

        if (auto it = std::ranges::find(lookup, e, &EnumName<E>::first); it != std::end(lookup)) {
            return it->second;
        } else {
            return std::begin(lookup)->second;
        }
    }

    template <symbolic_enumeration E>
    inline const symbol_t &enum_to_symbol(const E &e) {
        auto &lookup = enum_symbol_lookup<E>();

        if (auto it = std::ranges::find(lookup, e, &EnumSymbol<E>::first); it != std::end(lookup)) {
            return it->second;
        } else {
            return std::begin(lookup)->second;
        }
    }
    template <symbolic_enumeration E>
    inline E symbol_to_enum(const symbol_t &s) {
        auto &lookup = enum_symbol_lookup<E>();

        if (auto it = std::ranges::find(lookup, s, &EnumSymbol<E>::second); it != std::end(lookup)) {
            return it->first;
        } else {
            return std::begin(lookup)->first;
        }
    }

    template <symbolic_enumeration E>
    inline std::size_t enum_max_symbol_length() {
        static const size_t max = []() {
            auto &lookup = enum_symbol_lookup<E>();
            return std::ranges::max(
                       lookup,
                       {},
                       [](const auto &e) {
                           return e.second.value.size();
                       }
            ).second.value.size();
        }();
        return max;
    }

} // namespace amqp_asio::types

#define NAMED_ENUM(ENUM_TYPE, ...)                                                                                     \
    NLOHMANN_JSON_SERIALIZE_ENUM(ENUM_TYPE, __VA_ARGS__);                                                              \
    inline const auto &enum_name_lookup(ENUM_TYPE adl_dummy) {                                                         \
        static const EnumNameLookup<ENUM_TYPE> l = __VA_ARGS__;                                                        \
        return l;                                                                                                      \
    }                                                                                                                  \
    inline const std::string &format_as(ENUM_TYPE e) {                                                                 \
        return amqp_asio::types::enum_to_name(e);                                                                      \
    }

#define NUMERIC_ENUM(ENUM_TYPE, ...) NAMED_ENUM(ENUM_TYPE, __VA_ARGS__)

#define SYMBOLIC_ENUM(ENUM_TYPE, ...)                                                                                  \
    NAMED_ENUM(ENUM_TYPE, __VA_ARGS__)                                                                                 \
    void symbolic_enumeration_check(ENUM_TYPE);
