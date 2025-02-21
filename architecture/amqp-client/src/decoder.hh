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

#include "decode.hh"
#include "amqp_asio/types.hh"
#include <iterator>
#include <ranges>

namespace amqp_asio::types {

    template <std::random_access_iterator Iterator>
    class Decoder {
      public:
        template <std::ranges::random_access_range Range>
        Decoder(const Range &range) : Decoder(std::cbegin(range), std::cend(range)) {}

        Decoder(Iterator begin, Iterator end) : begin(std::move(begin)), end(std::move(end)), pos(this->begin) {}


        template <typename T>
        T decode() {
            return TypeDecoder<T>::decode(*this);
        }

        template <typename T>
        void decode(T &value) {
            value = decode<T>();
        }

        [[nodiscard]] std::size_t available() const {
            return std::distance(pos, end);
        }

        [[nodiscard]] auto remainder() const {
            return std::vector(pos, end);
        }

        auto read_constructor() {
            auto format_code = read_format_code();

            Descriptors descriptors;
            while (format_code == FormatCode::descriptor) {
                if (auto descriptor = decode<any_t>(); std::holds_alternative<symbol_t>(descriptor)) {
                    descriptors.emplace_back(SymbolicDescriptor(std::get<symbol_t>(descriptor)));
                } else if (std::holds_alternative<ulong_t>(descriptor)) {
                    auto id = std::get<ulong_t>(descriptor);
                    descriptors.emplace_back(
                        NumericDescriptor{static_cast<uint32_t>(id >> 32), static_cast<std::uint32_t>(id & 0xffffffff)}
                    );
                }
                format_code = read_format_code();
            }
            return Constructor{format_code, descriptors};
        }


      private:
        template <typename>
        friend class TypeDecoder;
        template <typename>
        friend class ValueDecoder;

        [[nodiscard]] std::size_t offset() const {
            return std::distance(begin, pos);
        }

        void ensure_available(std::size_t bytes) {
            if (available() < bytes) {
                throw DecodeException(
                    fmt::format("At position {}, Expected {} bytes, only {} available.", offset(), bytes, available())
                );
            }
        }

        auto unexpected_format_code(FormatCode code) {
            return DecodeException(
                fmt::format("At position {}: Unexpected format code {:#04x}", offset(), static_cast<uint8_t>(code))
            );
        }

        void ensure_used(Iterator start, std::size_t size) {
            if (std::size_t consumed = std::distance(start, pos); consumed != size) {
                throw DecodeException(
                    fmt::format("At position {}, Size {} bytes, {} consumed.", offset(), size, consumed)
                );
            }
        }

        auto read_format_code() {
            const std::byte ext_type_mask{0x0F};
            ensure_available(1);
            auto code = static_cast<FormatCode>(*pos++);
            if ((static_cast<std::byte>(code) & ext_type_mask) == ext_type_mask) {
                ensure_available(1);
                // ext-type - not used at the moment, but skip it if it's there.
                ++pos;
            }
            return code;
        }

        template <class Result, std::size_t NBits = sizeof(Result) * CHAR_BIT>
        auto read_numeric() {
            boost::endian::endian_buffer<boost::endian::order::big, Result, NBits> buffer;
            constexpr std::size_t size = NBits / CHAR_BIT;
            ensure_available(size);
            std::copy_n(pos, size, reinterpret_cast<typename Iterator::value_type *>(buffer.data()));
            pos += size;
            return buffer.value();
        }

        template <typename Result, std::size_t SizeBits = 32>
        auto read_byte_container() {
            static_assert(sizeof(typename Result::value_type) == 1);
            std::size_t size = read_numeric<std::size_t, SizeBits>();
            ensure_available(size);
            Result result(size, typename Result::value_type{});
            std::copy_n(pos, size, reinterpret_cast<typename Iterator::value_type *>(result.data()));
            pos += size;
            return result;
        }

        template <typename Result>
        auto read_byte_array() {
            static_assert(sizeof(typename Result::value_type) == 1);

            Result result{};
            ensure_available(result.size());
            std::copy_n(pos, result.size(), reinterpret_cast<typename Iterator::value_type *>(result.data()));
            pos += result.size();
            return result;
        }

        template <std::size_t SizeBits = 32>
        auto read_list() {
            std::size_t size = read_numeric<std::size_t, SizeBits>();
            auto start_pos = pos;
            ensure_available(size);
            std::size_t n_elts = read_numeric<std::size_t, SizeBits>();
            any_list_t result;
            result.value.reserve(n_elts);
            for (std::size_t i = 0; i < n_elts; ++i) {
                result.value.emplace_back(decode<any_t>());
            }
            ensure_used(start_pos, size);
            return result;
        }

        template <typename T, std::size_t SizeBits = 32>
        auto read_array() {
            std::size_t size = read_numeric<std::size_t, SizeBits>();
            auto start_pos = pos;
            ensure_available(size);
            std::size_t n_elts = read_numeric<std::size_t, SizeBits>();
            auto constructor = read_constructor();

            std::vector<T> result;
            result.reserve(n_elts);
            for (std::size_t i = 0; i < n_elts; ++i) {
                result.emplace_back(decode<T>(constructor));
            }
            ensure_used(start_pos, size);
            return result;
        }

        template <typename K, typename V, std::size_t SizeBits = 32>
        auto read_map() {
            std::size_t size = read_numeric<std::size_t, SizeBits>();
            auto start_pos = pos;
            ensure_available(size);
            std::size_t n_elts = read_numeric<std::size_t, SizeBits>();
            std::vector<std::pair<K, V>> result;
            result.reserve(n_elts / 2);
            for (std::size_t i = 0; i < n_elts / 2; ++i) {
                auto k = decode<K>();
                auto v = decode<V>();
                result.emplace_back(std::move(k), std::move(v));
            }
            ensure_used(start_pos, size);
            return result;
        }

        template <std::size_t SizeBits = 32, typename... T>
        auto read_tuple() {
            std::size_t size = read_numeric<std::size_t, SizeBits>();
            auto start_pos = pos;
            ensure_available(size);
            std::size_t n_elts = read_numeric<std::size_t, SizeBits>();
            std::size_t count = 0;
            std::tuple<T...> result;

            for_each_tuple_elt(
                [&](auto &v) {
                    if (count++ < n_elts) {
                        decode(v);
                    }
                },
                result
            );

            ensure_used(start_pos, size);
            return result;
        }

        template <typename T>
        T decode(const Constructor &constructor) {
            return ValueDecoder<T>::decode(*this, constructor);
        }

        template <typename T>
        void decode(const Constructor &constructor, T &value) {
            value = decode<T>(constructor);
        }

        Iterator begin;
        Iterator end;
        Iterator pos;
    };

    template <std::ranges::range Range>
    Decoder(Range) -> Decoder<typename std::decay_t<Range>::const_iterator>;

    template <typename T, std::random_access_iterator Iterator>
    T decode(Iterator pos, Iterator end) {
        auto decoder = Decoder(pos, end);
        return decoder.template decode<T>();
    }

    template <typename Performative, typename Payload, std::random_access_iterator Iterator>
    std::pair<Performative, std::vector<Payload>> decode(Iterator pos, Iterator end) {
        auto decoder = Decoder(pos, end);
        auto performative = decoder.template decode<Performative>();
        std::vector<Payload> payload;
        while (decoder.available()) {
            payload.push_back(decoder.template decode<Payload>());
        }
        return {std::move(performative), std::move(payload)};
    }

    template <typename Performative, typename Payload, std::ranges::random_access_range Range>
    auto decode(Range &&range) {
        return decode<Performative, Payload>(std::cbegin(range), std::cend(range));
    }

    template <typename T, std::ranges::random_access_range Range>
    T decode(Range &&range) {
        return decode<T>(std::cbegin(range), std::cend(range));
    }
} // namespace amqp_asio::types