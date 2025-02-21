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

#include "amqp_asio/types.hh"
#include "overload.hh"
#include <boost/endian.hpp>
#include <boost/integer.hpp>
#include <functional>
#include <type_traits>

namespace amqp_asio::types {

    template <typename T>
    struct is_optional : std::false_type {};

    template <typename T>
    struct is_optional<std::optional<T>> : std::true_type {};

    template <typename T>
    inline constexpr bool is_optional_v = is_optional<T>::value;

    template <typename T>
    struct is_multiple : std::false_type {};

    template <typename K, typename V>
    struct is_multiple<std::vector<std::pair<K, V>>> : std::false_type {};

    template <typename T>
    struct is_multiple<std::vector<T>> : std::true_type {};

    template <typename T>
    inline constexpr bool is_multiple_v = is_multiple<T>::value;

    template <typename Fn, typename Tuple>
    void for_each_tuple_elt(Fn &&fn, Tuple &&tuple) {
        std::apply(
            [&](auto &&...x) {
                (..., std::invoke(fn, std::forward<decltype(x)>(x)));
            },
            std::forward<Tuple>(tuple)
        );
    }

    struct AMQPException : public std::runtime_error {
        using std::runtime_error::runtime_error;
    };

    template <typename T, typename = int>
    struct has_descriptor : std::false_type {};

    template <typename T>
    struct has_descriptor<T, decltype((void)T::amqp_descriptor, 0)> : std::true_type {};

    template <typename T>
    constexpr bool has_descriptor_v = has_descriptor<T>::value;

    enum class FormatCode : uint8_t {
        descriptor = 0x00,
        null = 0x40,
        boolean = 0x56,
        boolean_true = 0x41,
        boolean_false = 0x42,
        ubyte = 0x50,
        ushort = 0x60,
        uint = 0x70,
        smalluint = 0x52,
        uint0 = 0x43,
        ulong = 0x80,
        smallulong = 0x53,
        ulong0 = 0x44,
        byte = 0x51,
        short_ = 0x61,
        int_ = 0x71,
        smallint = 0x54,
        long_ = 0x81,
        smalllong = 0x55,
        float_ = 0x72,
        double_ = 0x82,
        decimal32 = 0x74,
        decimal64 = 0x84,
        decimal128 = 0x94,
        char_ = 0x73,
        timestamp = 0x83,
        uuid = 0x98,
        vbin8 = 0xA0,
        vbin32 = 0xB0,
        str8_utf8 = 0xA1,
        str32_utf8 = 0xB1,
        sym8 = 0xA3,
        sym32 = 0xB3,
        list0 = 0x45,
        list8 = 0xC0,
        list32 = 0xD0,
        map8 = 0xC1,
        map32 = 0xD1,
        array8 = 0xE0,
        array32 = 0xF0
    };

    struct Constructor {
        FormatCode code{};
        Descriptors descriptors{};

        Constructor inner() const {
            Descriptors inner_descriptors;
            if (descriptors.size() > 1) {
                inner_descriptors.insert(inner_descriptors.begin(), descriptors.begin() + 1, descriptors.end());
            }
            return {code, inner_descriptors};
        }
    };

    // endian_buffer is only valid where the int type is the same size
    // or the next largest if not multiple of 8, so need to select
    // the correct type based on the size.
    template <typename T, std::size_t NBits = sizeof(T) * CHAR_BIT>
    using BigEndianBuf = boost::endian::endian_buffer<
        boost::endian::order::big,
        std::conditional_t<
            NBits == sizeof(T) * CHAR_BIT,
            T,
            std::conditional_t<
                std::is_unsigned_v<T>,
                typename boost::uint_t<NBits>::exact,
                typename boost::int_t<NBits>::exact>>,
        NBits>;

} // namespace amqp_asio::types