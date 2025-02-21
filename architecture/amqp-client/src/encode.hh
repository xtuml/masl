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
#include "amqp_asio/types.hh"
#include "amqp_asio/aggregate.hh"
#include "codec.hh"
#include <boost/integer.hpp>
#include <fmt/format.h>

namespace amqp_asio::types {

        struct InvalidFormatCode : public std::invalid_argument {
            explicit InvalidFormatCode(FormatCode code)
                : std::invalid_argument(fmt::format("Invalid format code {:#04x}", static_cast<uint8_t>(code))) {}
        };

        template <typename T>
        struct ValueEncoder;

        template <>
        struct ValueEncoder<null_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::null};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, null_t) {
                return {FormatCode::null};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, null_t) {
                switch (constructor.code) {
                    case FormatCode::null:
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<boolean_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &value) {
                bool all_true = true;
                bool all_false = true;
                for (const auto &v : value) {
                    auto b = static_cast<boolean_t>(v);
                    all_true &= b;
                    all_false &= !b;
                    if (!all_true && !all_false) {
                        return {FormatCode::boolean};
                    }
                }
                return {all_true ? FormatCode::boolean_true : FormatCode::boolean_false};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, boolean_t v) {
                return {v ? FormatCode::boolean_true : FormatCode::boolean_false};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, boolean_t v) {
                switch (constructor.code) {
                    case FormatCode::boolean_true:
                        return;
                    case FormatCode::boolean_false:
                        return;
                    case FormatCode::boolean:
                        encoder.template write_numeric<boolean_t>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<ubyte_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::ubyte};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, ubyte_t) {
                return {FormatCode::ubyte};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, ubyte_t v) {
                switch (constructor.code) {
                    case FormatCode::ubyte:
                        encoder.template write_numeric<ubyte_t>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<ushort_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::ushort};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, ushort_t) {
                return {FormatCode::ushort};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, ushort_t v) {
                switch (constructor.code) {
                    case FormatCode::ushort:
                        encoder.template write_numeric<ushort_t>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<uint_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &value) {
                bool all_zero = true;
                for (auto v : value) {
                    auto i = static_cast<uint_t>(v);
                    all_zero &= (i == 0);
                    if (i > std::numeric_limits<uint8_t>::max()) {
                        return {FormatCode::uint};
                    }
                }
                return {all_zero ? FormatCode::uint0 : FormatCode::smalluint};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, uint_t v) {
                return {v == 0 ? FormatCode::uint0 : v <= 255u ? FormatCode::smalluint : FormatCode::uint};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, uint_t v) {
                switch (constructor.code) {
                    case FormatCode::uint0:
                        return;
                    case FormatCode::smalluint:
                        encoder.template write_numeric<uint_t, 8>(v);
                        return;
                    case FormatCode::uint:
                        encoder.template write_numeric<uint_t>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<ulong_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &value) {
                bool all_zero = true;
                for (uint_t v : value) {
                    auto i = static_cast<ulong_t>(v);
                    all_zero &= (i == 0);
                    if (i > std::numeric_limits<uint8_t>::max()) {
                        return {FormatCode::ulong};
                    }
                }
                return {all_zero ? FormatCode::ulong0 : FormatCode::smallulong};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, ulong_t v) {
                return {v == 0 ? FormatCode::ulong0 : v <= 255u ? FormatCode::smallulong : FormatCode::ulong};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, ulong_t v) {
                switch (constructor.code) {
                    case FormatCode::ulong0:
                        return;
                    case FormatCode::smallulong:
                        encoder.template write_numeric<ulong_t, 8>(v);
                        return;
                    case FormatCode::ulong:
                        encoder.template write_numeric<ulong_t>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<byte_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::byte};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, byte_t) {
                return {FormatCode::byte};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, byte_t v) {
                switch (constructor.code) {
                    case FormatCode::byte:
                        encoder.template write_numeric<byte_t>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<short_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::short_};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, short_t) {
                return {FormatCode::short_};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, short_t v) {
                switch (constructor.code) {
                    case FormatCode::short_:
                        encoder.template write_numeric<short_t>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<int_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &value) {
                for (auto v : value) {
                    auto i = static_cast<int_t>(v);
                    if (i < std::numeric_limits<std::int8_t>::min() || i > std::numeric_limits<std::int8_t>::max()) {
                        return {FormatCode::int_};
                    }
                }
                return {FormatCode::smallint};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, int_t v) {
                return {v >= -128 && v <= 127 ? FormatCode::smallint : FormatCode::int_};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, int_t v) {
                switch (constructor.code) {
                    case FormatCode::smallint:
                        encoder.template write_numeric<int_t, 8>(v);
                        return;
                    case FormatCode::int_:
                        encoder.template write_numeric<int_t>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<long_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &value) {
                for (long_t v : value) {
                    auto i = static_cast<long_t>(v);
                    if (i < std::numeric_limits<std::int8_t>::min() || i > std::numeric_limits<std::int8_t>::max()) {
                        return {FormatCode::long_};
                    }
                }
                return {FormatCode::smalllong};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, long_t v) {
                return {v >= -128 && v <= 127 ? FormatCode::smalllong : FormatCode::long_};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, long_t v) {
                switch (constructor.code) {
                    case FormatCode::smalllong:
                        encoder.template write_numeric<long_t, 8>(v);
                        return;
                    case FormatCode::long_:
                        encoder.template write_numeric<long_t>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<float_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::float_};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, float_t) {
                return {FormatCode::float_};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, float_t v) {
                switch (constructor.code) {
                    case FormatCode::float_:
                        encoder.template write_numeric<float_t>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<double_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::double_};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, double_t) {
                return {FormatCode::double_};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, double_t v) {
                switch (constructor.code) {
                    case FormatCode::double_:
                        encoder.template write_numeric<double_t>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<decimal32_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::decimal32};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, const decimal32_t &) {
                return {FormatCode::decimal32};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const decimal32_t &v) {
                switch (constructor.code) {
                    case FormatCode::decimal32:
                        encoder.template write_byte_array<decimal32_t::value_type>(v.value);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<decimal64_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::decimal64};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, const decimal64_t &) {
                return {FormatCode::decimal64};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const decimal64_t &v) {
                switch (constructor.code) {
                    case FormatCode::decimal64:
                        encoder.template write_byte_array<decimal64_t::value_type>(v.value);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<decimal128_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::decimal128};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, const decimal128_t &) {
                return {FormatCode::decimal128};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const decimal128_t &v) {
                switch (constructor.code) {
                    case FormatCode::decimal128:
                        encoder.template write_byte_array<decimal128_t::value_type>(v.value);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<char_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::char_};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, char_t v) {
                return {FormatCode::char_};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, char_t v) {
                switch (constructor.code) {
                    case FormatCode::char_:
                        encoder.template write_numeric<char_t::value_type>(v.value);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <typename Duration>
        struct ValueEncoder<std::chrono::time_point<std::chrono::system_clock, Duration>> {
            using V = std::chrono::time_point<std::chrono::system_clock, Duration>;

            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::timestamp};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, V v) {
                return {FormatCode::timestamp};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, V v) {
                switch (constructor.code) {
                    case FormatCode::timestamp:
                        encoder.template write_numeric<int64_t>(
                            std::chrono::duration_cast<std::chrono::milliseconds>(v.time_since_epoch()).count()
                        );
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <typename Rep, typename Period>
        struct ValueEncoder<std::chrono::duration<Rep, Period>> {
            using V = std::chrono::duration<Rep, Period>;

            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &encoder, const Container &v) {
                return encoder.template array_constructor<Rep>(v);
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &encoder, V v) {
                return encoder.template constructor<Rep>(v.count());
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, V v) {
                encoder.write_value(v.count(), constructor);
            }
        };

        template <>
        struct ValueEncoder<uuid_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::uuid};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, uuid_t v) {
                return {FormatCode::uuid};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, uuid_t v) {
                switch (constructor.code) {
                    case FormatCode::uuid:
                        encoder.template write_byte_array<uuid_t::value_type>(v.value);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<binary_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &value) {
                for (const binary_t &v : value) {
                    if (v.size() > std::numeric_limits<uint8_t>::max()) {
                        return {FormatCode::vbin32};
                    }
                }
                return {FormatCode::vbin8};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, const binary_t &v) {
                return {v.size() <= 255 ? FormatCode::vbin8 : FormatCode::vbin32};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const binary_t &v) {
                switch (constructor.code) {
                    case FormatCode::vbin8:
                        encoder.template write_byte_container<binary_t, 8>(v);
                        return;
                    case FormatCode::vbin32:
                        encoder.template write_byte_container<binary_t>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<string_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &value) {
                for (const string_t &v : value) {
                    if (v.size() > std::numeric_limits<uint8_t>::max()) {
                        return {FormatCode::str32_utf8};
                    }
                }
                return {FormatCode::str8_utf8};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, const string_t &v) {
                return {v.size() <= 255 ? FormatCode::str8_utf8 : FormatCode::str32_utf8};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const string_t &v) {
                switch (constructor.code) {
                    case FormatCode::str8_utf8:
                        encoder.template write_byte_container<string_t, 8>(v);
                        return;
                    case FormatCode::str32_utf8:
                        encoder.template write_byte_container<string_t>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<symbol_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &value) {
                for (const symbol_t &v : value) {
                    if (v.value.size() > std::numeric_limits<uint8_t>::max()) {
                        return {FormatCode::sym32};
                    }
                }
                return {FormatCode::sym8};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, const symbol_t &v) {
                return {v.value.size() <= std::numeric_limits<uint8_t>::max() ? FormatCode::sym8 : FormatCode::sym32};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const symbol_t &v) {
                switch (constructor.code) {
                    case FormatCode::sym8:
                        encoder.template write_byte_container<typename symbol_t::value_type, 8>(v.value);
                        return;
                    case FormatCode::sym32:
                        encoder.template write_byte_container<typename symbol_t::value_type>(v.value);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <>
        struct ValueEncoder<any_list_t> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::list32};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, const any_list_t &v) {
                // Don't know size up front, so can't encode as list8.
                // It's possible we could squish it up once it's been written and size
                // is known, but extra complexity and copying is not worth the 6 byte
                // saving!
                return {v.value.empty() ? FormatCode::list0 : FormatCode::list32};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const any_list_t &v) {
                switch (constructor.code) {
                    case FormatCode::list0:
                        return;
                    case FormatCode::list8:
                        encoder.template write_list<8>(v);
                        return;
                    case FormatCode::list32:
                        encoder.template write_list<32>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <typename K, typename V>
        struct ValueEncoder<map_t<K, V>> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::map32};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, const map_t<K, V> &v) {
                // Don't know size up front, so can't encode as map8.
                // It's possible we could squish it up once it's been written and size
                // is known, but extra complexity and copying is not worth the 6 byte
                // saving!
                return {FormatCode::map32};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const map_t<K, V> &v) {
                switch (constructor.code) {
                    case FormatCode::map8:
                        encoder.template write_map<8>(v);
                        return;
                    case FormatCode::map32:
                        encoder.template write_map<32>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <typename T>
        struct ValueEncoder<array_t<T>> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {FormatCode::array32};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, const array_t<T> &v) {
                // Don't know size up front, so can't encode as array8.
                // It's possible we could squish it up once it's been written and size
                // is known, but extra complexity and copying is not worth the 6 byte
                // saving!
                return {FormatCode::array32};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const array_t<T> &v) {
                switch (constructor.code) {
                    case FormatCode::array8:
                        encoder.template write_array<8>(v);
                        return;
                    case FormatCode::array32:
                        encoder.template write_array<32>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <typename T>
        struct homogeneous_vector_ref;

        template <typename... T>
        struct homogeneous_vector_ref<std::variant<T...>> {
            using type = std::variant<std::vector<std::reference_wrapper<const T>>...>;
        };

        template <typename T>
        using homogeneous_vector_ref_t = typename homogeneous_vector_ref<T>::type;

        template <typename... T>
        struct ValueEncoder<std::variant<T...>> {

            template <typename... V>
            static auto homogeneous_vector_converter(const std::vector<std::variant<V...>> &var) {
                homogeneous_vector_ref_t<std::variant<V...>> result;

                for (const auto &element : var) {
                    std::visit(
                        [&](const auto &e) {
                            using E = std::reference_wrapper<std::remove_reference_t<decltype(e)>>;
                            if (result.index() != element.index()) {
                                std::visit(
                                    [](const auto &r) {
                                        if (!r.empty()) {
                                            throw EncodeException("Cannot encode heterogeneous array");
                                        }
                                    },
                                    result
                                );
                                result = std::vector<E>{};
                            };
                            std::get<std::vector<E>>(result).push_back(std::cref(e));
                        },
                        element
                    );
                }

                return result;
            }

            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &encoder, const Container &value) {
                return encoder.variant_array_constructor(homogeneous_vector_converter(value));
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &encoder, const std::variant<T...> &value) {
                return std::visit(
                    [&](const auto &v) {
                        return encoder.constructor(v);
                    },
                    value
                );
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const std::variant<T...> &value) {
                return std::visit(
                    [&](const auto &v) {
                        encoder.write_value(v, constructor);
                    },
                    value
                );
            }
        };

        template <>
        struct ValueEncoder<any_t> {
            static auto homogeneous_vector_converter(const std::vector<any_t> &var) {

                homogeneous_vector_ref_t<any_value_t> result;

                for (const auto &element : var) {
                    std::visit(
                        [&](const auto &e) {
                            using E = std::reference_wrapper<std::remove_reference_t<decltype(e)>>;
                            if (result.index() != element.index()) {
                                std::visit(
                                    [](const auto &r) {
                                        if (!r.empty()) {
                                            throw EncodeException("Cannot encode heterogeneous array");
                                        }
                                    },
                                    result
                                );
                                result = std::vector<E>{};
                            };
                            std::get<std::vector<E>>(result).push_back(std::cref(e));
                        },
                        element.value()
                    );
                }

                return result;
            }

            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &encoder, const Container &value) {
                return encoder.variant_array_constructor(homogeneous_vector_converter(value));
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &encoder, const any_t &value) {
                auto c = std::visit(
                    [&](const auto &v) {
                        return encoder.constructor(v);
                    },
                    static_cast<const any_value_t &>(value)
                );
                c.descriptors = value.descriptors;
                return c;
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const any_t &value) {
                return std::visit(
                    [&](const auto &v) {
                        encoder.write_value(v, constructor);
                    },
                    static_cast<const any_value_t &>(value)
                );
            }
        };

        template <typename T>
        struct ValueEncoder<std::optional<T>> {
            template <typename Encoder>
            static Constructor constructor(const Encoder &encoder, const std::optional<T> &v) {
                if (!v) {
                    return {FormatCode::null};
                } else {
                    return encoder.template constructor<T>(v.value());
                }
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const std::optional<T> &v) {
                switch (constructor.code) {
                    case FormatCode::null:
                        return;
                    default:
                        encoder.template write_value<T>(v.value(), constructor);
                }
            }
        };

        template <symbolic_enumeration T>
        struct ValueEncoder<T> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {enum_max_symbol_length<T>() <= std::numeric_limits<uint8_t>::max() ? FormatCode::sym8 : FormatCode::sym32};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, const T &) {
                return {enum_max_symbol_length<T>() <= std::numeric_limits<uint8_t>::max() ? FormatCode::sym8 : FormatCode::sym32};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const T &v) {
                encoder.template write_value<symbol_t>( enum_to_symbol(v), constructor);
            }
        };

        template <enumeration T>
        struct ValueEncoder<T> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &encoder, const Container &value) {
                return encoder.template array_constructor<Container, std::underlying_type_t<T>>(value);
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &encoder, const T &v) {
                return encoder.template constructor<std::underlying_type_t<T>>(static_cast<std::underlying_type_t<T>>(v)
                );
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const T &v) {
                encoder.template write_value<std::underlying_type_t<T>>(
                    static_cast<std::underlying_type_t<T>>(v), constructor
                );
            }
        };

        template <typename... T>
        struct ValueEncoder<std::tuple<T...>> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return {std::tuple_size_v<std::tuple<T...>> == 0 ? FormatCode::list0 : FormatCode::list32};
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, const std::tuple<T...> &v) {
                // Don't know size up front, so can't encode as list8.
                // It's possible we could squish it up once it's been written and size
                // is known, but extra complexity and copying is not worth the 6 byte
                // saving!
                return {std::tuple_size_v<std::tuple<T...>> == 0 ? FormatCode::list0 : FormatCode::list32};
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const std::tuple<T...> &v) {
                switch (constructor.code) {
                    case FormatCode::list0:
                        return;
                    case FormatCode::list8:
                        encoder.template write_tuple<8>(v);
                        return;
                    case FormatCode::list32:
                        encoder.template write_tuple<32>(v);
                        return;
                    default:
                        throw InvalidFormatCode(constructor.code);
                }
            }
        };

        template <typename T>
            requires std::is_aggregate_v<T>
        struct ValueEncoder<T> {

            static Constructor constructor() {
                auto format_code = detail::arity<T> ? FormatCode::list32 : FormatCode::list0;
                if constexpr (has_descriptor_v<T>) {
                    return {
                        format_code, {NumericDescriptor{T::amqp_descriptor.domain_id, T::amqp_descriptor.descriptor_id}}
                    };
                } else {
                    return {format_code};
                }
            };

            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &, const Container &) {
                return constructor();
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &, const T &v) {
                return constructor();
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const T &v) {
                auto tied_tuple = detail::tie(v);
                encoder.template write_value(tied_tuple, constructor);
            }
        };

        template <typename T>
            requires std::derived_from<T, wrapper_t<typename T::value_type, T>>
        struct ValueEncoder<T> {
            template <typename Encoder, typename Container>
            static Constructor array_constructor(const Encoder &encoder, const Container &container) {
                using RefContainer = std::vector<std::reference_wrapper<const typename T::value_type>>;
                RefContainer ref_container;
                ref_container.reserve(container.size());
                std::transform(
                    std::begin(container),
                    std::end(container),
                    std::back_inserter(ref_container),
                    [](auto &v) {
                        return std::ref(v.value);
                    }
                );
                auto c = encoder.template array_constructor<RefContainer, typename T::value_type>(ref_container);

                if constexpr (has_descriptor_v<T>) {
                    c.descriptors.push_back(
                        NumericDescriptor{T::amqp_descriptor.domain_id, T::amqp_descriptor.descriptor_id}
                    );
                }
                return c;
            }

            template <typename Encoder>
            static Constructor constructor(const Encoder &encoder, const T &v) {
                auto c = encoder.template constructor<typename T::value_type>(v.value);

                if constexpr (has_descriptor_v<T>) {
                    c.descriptors.push_back(
                        NumericDescriptor{T::amqp_descriptor.domain_id, T::amqp_descriptor.descriptor_id}
                    );
                }
                return c;
            }

            template <typename Encoder>
            static void encode(Encoder &encoder, const Constructor &constructor, const T &v) {
                encoder.template write_value(v.value, constructor);
            }
        };

        template <typename T>
        struct TypeEncoder {
            template <typename Encoder>
            static void encode(Encoder &encoder, const T &value) {
                auto c = encoder.constructor(value);
                encoder.write_constructor(c);
                encoder.write_value(value, c);
            }
        };

        template <>
        struct TypeEncoder<messages::Message> {

            template <typename Encoder>
            static void encode(Encoder &encoder, const messages::Message &message) {
                encode_section(encoder, message.header);
                encode_section(encoder, message.delivery_annotations);
                encode_section(encoder, message.message_annotations);
                encode_section(encoder, message.properties);
                encode_section(encoder, message.application_properties);
                encode_data(encoder, message.data);
                encode_section(encoder, message.footer);
            }

          private:
            template <typename Encoder>
            static void encode_data(Encoder &encoder, const messages::MessagePayload &data) {
                std::visit(
                    overload{
                        [&encoder](const std::vector<messages::Data> &v) {
                            std::ranges::for_each(v, [&encoder](const auto &e) {
                                encoder.encode(e);
                            });
                        },
                        [&encoder](const std::vector<messages::AMQPSequence> &v) {
                            std::ranges::for_each(v, [&encoder](const auto &e) {
                                encoder.encode(e);
                            });
                        },
                        [&encoder](const messages::AMQPValue &v) {
                            encoder.encode(v);
                        }
                    },
                    data
                );
            }

            template <typename Encoder, typename T>
            static void encode_section(Encoder &encoder, const std::optional<T> &v) {
                if (v) {
                    encoder.encode(v);
                }
            }
        };
} // namespace amqp_asio::types