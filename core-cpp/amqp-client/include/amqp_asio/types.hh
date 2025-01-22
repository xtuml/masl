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
#include "wrapper.hh"

#include <array>
#include <boost/endian.hpp>
#include <variant>
#include <vector>

#include <fmt/chrono.h>
#include <fmt/format.h>
#include <fmt/ranges.h>
#include <fmt/std.h>

namespace amqp_asio::types {

    struct DescriptorDefinition {
        std::string_view name;
        uint32_t domain_id;
        uint32_t descriptor_id;

        [[nodiscard]]
        constexpr std::uint64_t code() const {
            return std::uint64_t(domain_id) << 32 | descriptor_id;
        };
    };

    class any_t;

    using null_t = std::monostate;
    using boolean_t = bool;
    using ubyte_t = std::uint8_t;
    using ushort_t = std::uint16_t;
    using uint_t = std::uint32_t;
    using ulong_t = std::uint64_t;

    using byte_t = std::int8_t;
    using short_t = std::int16_t;
    using int_t = std::int32_t;
    using long_t = std::int64_t;

    using float_t = float;
    using double_t = double;

    using timestamp_t = std::chrono::time_point<std::chrono::system_clock, std::chrono::milliseconds>;

    using binary_t = std::vector<std::byte>;
    using string_t = std::string;

    struct decimal32_tag;
    struct decimal64_tag;
    struct decimal128_tag;
    using decimal32_t = wrapper_t<std::array<std::byte, 4>, decimal32_tag>;
    using decimal64_t = wrapper_t<std::array<std::byte, 8>, decimal64_tag>;
    using decimal128_t = wrapper_t<std::array<std::byte, 16>, decimal128_tag>;

    struct char_t : public wrapper_t<char32_t, char_t> {

        using wrapper_t<char32_t, char_t>::wrapper_t;

        explicit operator std::string() const {
            std::string utf8;
            if (value <= 0x7F) {
                utf8.push_back(value & 0x7f);
            } else if (value <= 0x7FF) {
                utf8.push_back(0xC0 | (value >> 6));   /* 110xxxxx */
                utf8.push_back(0x80 | (value & 0x3F)); /* 10xxxxxx */
            } else if (value <= 0xFFFF) {
                utf8.push_back(0xE0 | (value >> 12));         /* 1110xxxx */
                utf8.push_back(0x80 | ((value >> 6) & 0x3F)); /* 10xxxxxx */
                utf8.push_back(0x80 | (value & 0x3F));        /* 10xxxxxx */
            } else if (value <= 0x10FFFF) {
                utf8.push_back(0xF0 | (value >> 18));          /* 11110xxx */
                utf8.push_back(0x80 | ((value >> 12) & 0x3F)); /* 10xxxxxx */
                utf8.push_back(0x80 | ((value >> 6) & 0x3F));  /* 10xxxxxx */
                utf8.push_back(0x80 | (value & 0x3F));         /* 10xxxxxx */
            }
            return utf8;
        }

        auto operator<=>(const char_t &) const = default;

        friend auto format_as(char_t value) {
            return static_cast<std::string>(value);
        }
    };

    struct uuid_tag;
    using uuid_t = wrapper_t<std::array<std::byte, 16>, uuid_tag>;

    struct symbol_tag;
    using symbol_t = wrapper_t<std::string, symbol_tag>;

    template <typename T>
    using array_t = std::vector<T>;

    using any_array_t = std::vector<any_t>;
    struct any_list_tag;
    using any_list_t = wrapper_t<std::vector<any_t>, any_list_tag>;

    template <typename K, typename V>
    using map_t = std::vector<std::pair<K, V>>;

    using any_map_t = map_t<any_t, any_t>;

    using any_value_t = std::variant<
        null_t,
        boolean_t,
        ubyte_t,
        ushort_t,
        uint_t,
        ulong_t,
        byte_t,
        short_t,
        int_t,
        long_t,
        float_t,
        double_t,
        decimal32_t,
        decimal64_t,
        decimal128_t,
        char_t,
        timestamp_t,
        uuid_t,
        binary_t,
        string_t,
        symbol_t,
        any_array_t,
        any_list_t,
        any_map_t>;

    using scalar_t = std::variant<
        null_t,
        boolean_t,
        ubyte_t,
        ushort_t,
        uint_t,
        ulong_t,
        byte_t,
        short_t,
        int_t,
        long_t,
        float_t,
        double_t,
        decimal32_t,
        decimal64_t,
        decimal128_t,
        char_t,
        timestamp_t,
        uuid_t,
        binary_t,
        string_t,
        symbol_t>;

    using SymbolicDescriptor = std::string;

    struct NumericDescriptor {
        std::uint32_t domain_id;
        std::uint32_t descriptor_id;

        [[nodiscard]]
        constexpr std::uint64_t code() const {
            return std::uint64_t(domain_id) << 32 | descriptor_id;
        };

        friend std::string format_as(NumericDescriptor v) {
            return fmt::format("{:#08x}:{:#08x}", v.domain_id, v.descriptor_id);
        }

        auto operator<=>(const NumericDescriptor &) const = default;
    };

    using Descriptor = std::variant<SymbolicDescriptor, NumericDescriptor>;
    using Descriptors = std::vector<Descriptor>;

    struct any_t : public any_value_t {

        any_t() = default;

        any_t(const any_t &) = default;

        any_t(any_t &&) = default;

        any_t &operator=(const any_t &) = default;

        any_t &operator=(any_t &&) = default;

        ~any_t() = default;

        template <typename T>
            requires(!copy_constructor_args<any_t, T>)
        any_t(T &&v)
            : any_value_t(std::forward<T>(v)) {}

        const any_value_t &value() const {
            return static_cast<const any_value_t &>(*this);
        }

        any_value_t &value() {
            return static_cast<any_value_t &>(*this);
        }

        template <typename T>
        any_t(Descriptors descriptors, T &&v)
            : any_value_t{v}, descriptors(std::move(descriptors)) {}

        template <typename T>
        any_t(std::string name, T &&v)
            : any_value_t{std::forward<T>(v)}, descriptors{} {
            descriptors.emplace_back(std::move(name));
        }

        template <typename T>
        any_t(std::uint32_t domain_id, std::uint32_t descriptor_id, T &&v)
            : any_value_t{std::forward<T>(v)}, descriptors{} {
            descriptors.emplace_back(domain_id, descriptor_id);
        }

        Descriptors descriptors;

        any_t inner() const & {
            Descriptors inner_descriptors;
            if ( descriptors.size() > 1 ) {
                inner_descriptors.insert(inner_descriptors.begin(),descriptors.begin()+1,descriptors.end());
            }
            return {inner_descriptors,value()};
        }

        any_t inner() && {
            if( descriptors.size() > 1) {
                descriptors.erase(descriptors.begin());
            }
            return std::move(*this);
        }

        auto operator<=>(const any_t &) const = default;

        friend void to_json(nlohmann::json &j, const any_t &v) {
            j = detail::amqp_value_to_json(v.value());
        }
    };

} // namespace amqp_asio::types

template <>
struct fmt::formatter<amqp_asio::types::any_t> : fmt::formatter<string_view> {

    auto format(const amqp_asio::types::any_t &data, format_context &ctx) const {
        return std::visit(
            [&](const auto &v) {
                return formatter<string_view>::format(fmt::format("{}: {}", data.descriptors, v), ctx);
            },
            data.value() /* get rid of .value() after gcc 11.2, when std::vist works on derived classes */
        );
    }
};
