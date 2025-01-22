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
#include "codec.hh"

#include "messages.hh"
#include "overload.hh"
#include "amqp_asio/types.hh"
#include "amqp_asio/aggregate.hh"
#include <map>
#include <set>

namespace amqp_asio::types {

    template <typename T>
    Constructor strip_descriptors(const Constructor &constructor);

    template <typename T>
    struct ValueDecoder;

    template <>
    struct ValueDecoder<any_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            return any_t{constructor.descriptors, decoder.template decode<any_value_t>(constructor)};
        }

    };

    template <>
    struct ValueDecoder<any_value_t> {
        template <typename Decoder>
        static any_value_t decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::null:
                    return decoder.template decode<null_t>(constructor);
                case FormatCode::boolean:
                case FormatCode::boolean_true:
                case FormatCode::boolean_false:
                    return decoder.template decode<boolean_t>(constructor);
                case FormatCode::ubyte:
                    return decoder.template decode<ubyte_t>(constructor);
                case FormatCode::ushort:
                    return decoder.template decode<ushort_t>(constructor);
                case FormatCode::uint:
                case FormatCode::smalluint:
                case FormatCode::uint0:
                    return decoder.template decode<uint_t>(constructor);
                case FormatCode::ulong:
                case FormatCode::smallulong:
                case FormatCode::ulong0:
                    return decoder.template decode<ulong_t>(constructor);
                case FormatCode::byte:
                    return decoder.template decode<byte_t>(constructor);
                case FormatCode::short_:
                    return decoder.template decode<short_t>(constructor);
                case FormatCode::int_:
                case FormatCode::smallint:
                    return decoder.template decode<int_t>(constructor);
                case FormatCode::long_:
                case FormatCode::smalllong:
                    return decoder.template decode<long_t>(constructor);
                case FormatCode::float_:
                    return decoder.template decode<float>(constructor);
                case FormatCode::double_:
                    return decoder.template decode<double>(constructor);
                case FormatCode::decimal32:
                    return decoder.template decode<decimal32_t>(constructor);
                case FormatCode::decimal64:
                    return decoder.template decode<decimal64_t>(constructor);
                case FormatCode::decimal128:
                    return decoder.template decode<decimal128_t>(constructor);
                case FormatCode::char_:
                    return decoder.template decode<char_t>(constructor);
                case FormatCode::timestamp:
                    return decoder.template decode<timestamp_t>(constructor);
                case FormatCode::uuid:
                    return decoder.template decode<uuid_t>(constructor);
                case FormatCode::vbin8:
                case FormatCode::vbin32:
                    return decoder.template decode<binary_t>(constructor);
                case FormatCode::str8_utf8:
                case FormatCode::str32_utf8:
                    return decoder.template decode<string_t>(constructor);
                case FormatCode::sym8:
                case FormatCode::sym32:
                    return decoder.template decode<symbol_t>(constructor);
                case FormatCode::list0:
                case FormatCode::list8:
                case FormatCode::list32:
                    return decoder.template decode<any_list_t>(constructor);
                case FormatCode::map8:
                case FormatCode::map32:
                    return decoder.template decode<any_map_t>(constructor);
                case FormatCode::array8:
                case FormatCode::array32:
                    return decoder.template decode<any_array_t>(constructor);
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<null_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::null:
                    return null_t{};
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<boolean_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::boolean:
                    return decoder.template read_numeric<boolean_t>();
                case FormatCode::boolean_false:
                    return false;
                case FormatCode::boolean_true:
                    return true;
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<ubyte_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::ubyte:
                    return decoder.template read_numeric<ubyte_t>();
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<ushort_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::ushort:
                    return decoder.template read_numeric<ushort_t>();
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<uint_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::uint0:
                    return uint_t{};
                case FormatCode::smalluint:
                    return decoder.template read_numeric<uint_t, 8>();
                case FormatCode::uint:
                    return decoder.template read_numeric<uint_t>();
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<ulong_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::ulong0:
                    return ulong_t{};
                case FormatCode::smallulong:
                    return decoder.template read_numeric<ulong_t, 8>();
                case FormatCode::ulong:
                    return decoder.template read_numeric<ulong_t>();
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<byte_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::byte:
                    return decoder.template read_numeric<byte_t>();
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<short_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::short_:
                    return decoder.template read_numeric<short_t>();
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<int_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::smallint:
                    return decoder.template read_numeric<int_t, 8>();
                case FormatCode::int_:
                    return decoder.template read_numeric<int_t>();
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<long_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::smalllong:
                    return decoder.template read_numeric<long_t, 8>();
                case FormatCode::long_:
                    return decoder.template read_numeric<long_t>();
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<float_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::float_:
                    return decoder.template read_numeric<float_t>();
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<double_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::double_:
                    return decoder.template read_numeric<double_t>();
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<decimal32_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::decimal32:
                    return decimal32_t{decoder.template read_byte_array<decimal32_t::value_type>()};
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<decimal64_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::decimal64:
                    return decimal64_t{decoder.template read_byte_array<decimal64_t::value_type>()};
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<decimal128_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::decimal128:
                    return decimal128_t{decoder.template read_byte_array<decimal128_t::value_type>()};
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<char_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::char_:
                    return char_t{decoder.template read_numeric<char_t::value_type>()};
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <typename Duration>
    struct ValueDecoder<std::chrono::time_point<std::chrono::system_clock, Duration>> {
        using V = std::chrono::time_point<std::chrono::system_clock, Duration>;

        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::timestamp:
                    return V(std::chrono::duration_cast<Duration>(
                        std::chrono::milliseconds(decoder.template read_numeric<int64_t>())
                    ));
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <typename Rep, typename Period>
    struct ValueDecoder<std::chrono::duration<Rep, Period>> {
        using V = std::chrono::duration<Rep, Period>;

        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            return V{decoder.template decode<Rep>(constructor)};
        }

    };

    template <>
    struct ValueDecoder<uuid_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::uuid:
                    return uuid_t{decoder.template read_byte_array<uuid_t::value_type>()};
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<binary_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::vbin8:
                    return decoder.template read_byte_container<binary_t, 8>();
                case FormatCode::vbin32:
                    return decoder.template read_byte_container<binary_t, 32>();
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<string_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::str8_utf8:
                    return decoder.template read_byte_container<string_t, 8>();
                case FormatCode::str32_utf8:
                    return decoder.template read_byte_container<string_t, 32>();
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<symbol_t> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::sym8:
                    return symbol_t(decoder.template read_byte_container<typename symbol_t::value_type, 8>());
                case FormatCode::sym32:
                    return symbol_t(decoder.template read_byte_container<typename symbol_t::value_type, 32>());
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <>
    struct ValueDecoder<any_list_t> {
        template <typename Decoder>
        static any_list_t decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::list0:
                    return {};
                case FormatCode::list8:
                    return decoder.template read_list<8>();
                case FormatCode::list32:
                    return decoder.template read_list<32>();
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <typename K, typename V>
    struct ValueDecoder<map_t<K, V>> {
        template <typename Decoder>
        static map_t<K, V> decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::null:
                    return {};
                case FormatCode::map8:
                    return decoder.template read_map<K, V, 8>();
                case FormatCode::map32:
                    return decoder.template read_map<K, V, 32>();
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <typename T>
    struct ValueDecoder<array_t<T>> {
        template <typename Decoder>
        static array_t<T> decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::null:
                    return {};
                case FormatCode::array8:
                    return decoder.template read_array<T, 8>();
                case FormatCode::array32:
                    return decoder.template read_array<T, 32>();
                default:
                    return {decoder.template decode<T>(constructor)};
            }
        }

    };

    template <typename... T>
    struct ValueDecoder<std::tuple<T...>> {
        template <typename Decoder>
        static std::tuple<T...> decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::list0:
                    return {};
                case FormatCode::list8:
                    return decoder.template read_tuple<8, T...>();
                case FormatCode::list32:
                    return decoder.template read_tuple<32, T...>();
                default:
                    throw decoder.unexpected_format_code(constructor.code);
            }
        }

    };

    template <typename T>
    struct ValueDecoder<std::optional<T>> {
        template <typename Decoder>
        static std::optional<T> decode(Decoder &decoder, const Constructor &constructor) {
            switch (constructor.code) {
                case FormatCode::null:
                    return {};
                default:
                    return decoder.template decode<T>(constructor);
            }
        }

    };

    template <enumeration T>
    struct ValueDecoder<T> {
        template <typename Decoder>
        static T decode(Decoder &decoder, const Constructor &constructor) {
            return T{decoder.template decode<std::underlying_type_t<T>>(constructor)};
        }

    };

    template <symbolic_enumeration T>
    struct ValueDecoder<T> {
        template <typename Decoder>
        static T decode(Decoder &decoder, const Constructor &constructor) {
            return symbol_to_enum<T>(decoder.template decode<symbol_t>(constructor));
        }

    };

    template <typename T>
        requires std::derived_from<T, wrapper_t<typename T::value_type, T>>
    struct ValueDecoder<T> {
        template <typename Decoder>
        static T decode(Decoder &decoder, const Constructor &constructor) {
            return T(decoder.template decode<typename T::value_type>(strip_descriptors<T>(constructor)));
        }

    };

    template <typename... T>
    struct ValueDecoder<std::variant<T...>> {
        template <typename Decoder, typename... V>
        static auto make_name_dispatch() {
            using Variant = std::variant<V...>;

            std::map<std::string, std::function<Variant(Decoder &, const Constructor &)>> result;

            (..., [&]() {
                if constexpr (has_descriptor_v<V>) {
                    result.emplace(
                        std::string{V::amqp_descriptor.name},
                        [](Decoder &decoder, const Constructor &constructor) {
                            return Variant{decoder.template decode<T>(constructor)};
                        }
                    );
                }
            }());
            return result;
        };

        template <typename Decoder, typename... V>
        static auto make_code_dispatch() {
            using Variant = std::variant<V...>;

            std::map<uint64_t, std::function<Variant(Decoder &, const Constructor &)>> result;

            (..., [&]() {
                if constexpr (has_descriptor_v<V>) {
                    result.emplace(V::amqp_descriptor.code(), [](Decoder &decoder, const Constructor &constructor) {
                        return Variant{decoder.template decode<T>(constructor)};
                    });
                }
            }());
            return result;
        };

        template <typename Decoder, typename... V>
        static auto make_symbol_enum_lookup() {
            using Variant = std::variant<V...>;

            std::map<symbol_t, Variant> result;

            (..., [&]() {
                if constexpr (symbolic_enumeration<V>) {
                    for ( const auto& [e,s] : enum_symbol_lookup<V>() ) {
                        result.emplace(s,e);
                    }
                }
            }());
            return result;
        };

        template <typename Decoder>
        static std::variant<T...> decode(Decoder &decoder, const Constructor &constructor) {
            static const auto name_dispatch = make_name_dispatch<Decoder, T...>();
            static const auto code_dispatch = make_code_dispatch<Decoder, T...>();
            static const auto enum_lookup = make_symbol_enum_lookup<Decoder, T...>();
            for (auto &descriptor : constructor.descriptors) {
                if (std::holds_alternative<types::SymbolicDescriptor>(descriptor)) {
                    auto name = std::get<types::SymbolicDescriptor>(descriptor);
                    if (auto pos = name_dispatch.find(name); pos != name_dispatch.end()) {
                        return (pos->second)(decoder, constructor);
                    }
                } else {
                    auto code = std::get<types::NumericDescriptor>(descriptor).code();
                    if (auto pos = code_dispatch.find(code); pos != code_dispatch.end()) {
                        return (pos->second)(decoder, constructor);
                    }
                }
            }

            // No matching descriptor found, so try scalar decode
            any_value_t any_value = decoder.template decode<any_value_t>(constructor);
            // Convert any value variant to the required variant
            using Variant = std::variant<T...>;
            return std::visit(
                overload{
                    [](T value) -> Variant {
                        if constexpr ( std::is_same_v<std::decay_t<T>,symbol_t> ) {
                            // Look for any symbolic enums
                            if (auto pos = enum_lookup.find(value); pos != enum_lookup.end()) {
                                return pos->second;
                            }
                            else {
                                return Variant{std::move(value)};
                            } 
                        } else {
                            return Variant{std::move(value)};
                        }
                    }...,
                    [&](auto) -> Variant {
                        throw decoder.unexpected_format_code(constructor.code);
                    },
                },
                any_value
            );
        }
    };

    template <class Tuple>
    struct remove_tuple_cvref;

    template <class... Args>
    struct remove_tuple_cvref<std::tuple<Args...>> {
        using type = std::tuple<std::remove_reference_t<std::remove_cv_t<Args>>...>;
    };

    template <class Tuple>
    using remove_tuple_cvref_t = typename remove_tuple_cvref<Tuple>::type;

    template <typename T>
    Constructor strip_descriptors(const Constructor &constructor) {
        Constructor stripped{constructor.code};
        if constexpr (has_descriptor_v<T>) {
            if (auto found = std::ranges::find_if(
                    constructor.descriptors,
                    [](auto &d) {
                        return (std::holds_alternative<SymbolicDescriptor>(d) &&
                                T::amqp_descriptor.name == std::get<SymbolicDescriptor>(d)) ||
                               (std::holds_alternative<NumericDescriptor>(d) &&
                                T::amqp_descriptor.code() == std::get<NumericDescriptor>(d).code());
                    }
                );
                found != constructor.descriptors.end()) {
                return Constructor(constructor.code, Descriptors(found + 1, constructor.descriptors.end()));
            } else {
                throw DecodeException(fmt::format(
                    "Expected type with descriptor code {:#08x}:{:#08x} or name {}",
                    T::amqp_descriptor.domain_id,
                    T::amqp_descriptor.descriptor_id,
                    T::amqp_descriptor.name
                ));
            }
        } else {
            return constructor;
        }
    }

    template <typename T>
        requires std::is_aggregate_v<T>
    struct ValueDecoder<T> {
        template <typename Decoder>
        static auto decode(Decoder &decoder, const Constructor &constructor) {
            T result;
            auto tied_tuple = detail::tie(result);
            tied_tuple =
                decoder.template decode<remove_tuple_cvref_t<decltype(tied_tuple)>>(strip_descriptors<T>(constructor));
            return result;
        }
    };



    template<typename T>
    struct TypeDecoder {
        template <typename Decoder>
        static auto decode(Decoder &decoder) {
            return decoder.template decode<T>(decoder.read_constructor());
        }
    };

    template <>
    struct TypeDecoder<messages::Message> {
        template <typename Decoder>
        static auto decode(Decoder &decoder) {
            messages::Message message;

            while (decoder.available()) {
                auto section = ValueDecoder<messages::MessageSection>::decode(decoder, decoder.read_constructor());
                std::visit(
                    overload{
                        [&](messages::Header &&v) {
                            message.header = std::move(v);
                        },
                        [&](messages::DeliveryAnnotations &&v) {
                            message.delivery_annotations = std::move(v);
                        },
                        [&](messages::MessageAnnotations &&v) {
                            message.message_annotations = std::move(v);
                        },
                        [&](messages::Properties &&v) {
                            message.properties = std::move(v);
                        },
                        [&](messages::ApplicationProperties &&v) {
                            message.application_properties = std::move(v);
                        },
                        [&](messages::Data &&v) {
                            if ( std::holds_alternative<std::vector<messages::Data>>(message.data) ) {
                                std::get<std::vector<messages::Data>>(message.data).emplace_back(std::move(v));
                            } else {
                                message.data = std::vector<messages::Data>{std::move(v)};
                            }
                        },
                        [&](messages::AMQPSequence &&v) {
                            if ( std::holds_alternative<std::vector<messages::AMQPSequence>>(message.data) ) {
                                std::get<std::vector<messages::AMQPSequence>>(message.data).emplace_back(std::move(v));
                            } else {
                                message.data = std::vector<messages::AMQPSequence>{std::move(v)};
                            }
                        },
                        [&](messages::AMQPValue &&v) {
                            message.data = std::move(v);
                        },
                        [&](messages::Footer &&v) {
                            message.footer = std::move(v);
                        }
                    },
                    std::move(section)
                );
            }
            return message;
        }
    };

} // namespace amqp_asio::types