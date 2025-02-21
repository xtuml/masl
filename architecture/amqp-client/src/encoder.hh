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

#include "codec.hh"
#include "encode.hh"

namespace amqp_asio::types {

    class Encoder {
      public:
        using Buffer = std::vector<std::byte>;

        template <class T>
        const Buffer &encode(const T &value) & {
            TypeEncoder<T>::encode(*this,value);
            return buffer_;
        }

        template <class T>
        Buffer encode(const T &value) && {
            TypeEncoder<T>::encode(*this,value);
            return std::move(buffer_);
        }

        const Buffer &buffer() const & {
            return buffer_;
        }

        Buffer buffer() && {
            return std::move(buffer_);
        }

        void clear() {
            buffer_.clear();
        }

      private:
        template <typename>
        friend class TypeEncoder;
        template <typename>
        friend class ValueEncoder;

        template <class T>
        auto constructor(const T &value) const {
            return ValueEncoder<T>::constructor(*this, value);
        }

        template <typename T, typename V = typename T::value_type>
        auto array_constructor(const T &value) const {
            return ValueEncoder<V>::array_constructor(*this, value);
        }

        template <class T>
        auto variant_array_constructor(const T &value) const {
            return std::visit(
                [&](const auto &v) {
                    return ValueEncoder<std::remove_const_t<
                        typename std::decay_t<decltype(v)>::value_type::type>>::array_constructor(*this, v);
                },
                value
            );
        }

        template <class T>
        void write_value(const T &value, const Constructor &constructor) {
            ValueEncoder<T>::encode(*this, constructor, value);
        }

        void write_constructor(const Constructor &constructor) {
            for (const auto &descriptor : constructor.descriptors) {
                buffer_.push_back(static_cast<std::byte>(FormatCode::descriptor));
                std::visit(
                    overload{
                        [&](const NumericDescriptor &v) {
                            encode(v.code());
                        },
                        [&](const SymbolicDescriptor &v) {
                            encode(symbol_t{v});
                        }
                    },
                    descriptor
                );
            }
            buffer_.push_back(static_cast<std::byte>(constructor.code));
        }

        template <typename T, std::size_t SizeBits = 32>
        void write_byte_container(const T &value) {
            static_assert(sizeof(typename T::value_type) == 1);

            write_numeric<std::size_t, SizeBits>(value.size());
            auto start = reinterpret_cast<const Buffer::value_type *>(value.data());
            buffer_.insert(std::end(buffer_), start, start + value.size());
        }

        template <typename T>
        void write_byte_array(const T &value) {
            static_assert(sizeof(typename T::value_type) == 1);
            auto start = reinterpret_cast<const Buffer::value_type *>(value.data());
            buffer_.insert(std::end(buffer_), start, start + value.size());
        }

        template <typename T, std::size_t NBits = sizeof(T) * CHAR_BIT>
        void write_numeric(T value) {
            boost::endian::endian_buffer<boost::endian::order::big, T, NBits> vbuffer{value};
            constexpr std::size_t size = NBits / CHAR_BIT;
            auto start = reinterpret_cast<std::byte *>(vbuffer.data());
            buffer_.insert(std::end(buffer_), start, start + size);
        }

        // Overwrite the numeric value at the specified offset.
        template <typename T, std::size_t NBits = sizeof(T) * CHAR_BIT>
        void overwrite_numeric(std::size_t offset, T value) {
            boost::endian::endian_buffer<boost::endian::order::big, T, NBits> vbuffer{value};
            constexpr std::size_t size = NBits / CHAR_BIT;
            auto start = reinterpret_cast<std::byte *>(vbuffer.data());
            std::copy(start, start + size, std::begin(buffer_) + offset);
        }

        template <std::size_t SizeBits = 32>
        void write_list(const any_list_t &v) {
            std::size_t size_start_index = buffer_.size();
            // Write size zero for now, fill in later.
            write_numeric<std::size_t, SizeBits>(0);
            std::size_t data_start_index = buffer_.size();
            write_numeric<std::size_t, SizeBits>(v.value.size());
            for (const auto &elt : v.value) {
                encode(elt);
            }
            // Now we can fill in the size
            overwrite_numeric<std::size_t, SizeBits>(size_start_index, buffer_.size() - data_start_index);
        }

        template <std::size_t SizeBits = 32, typename K, typename V>
        void write_map(const map_t<K, V> &value) {
            std::size_t size_start_index = buffer_.size();
            // Write size zero for now, fill in later.
            write_numeric<std::size_t, SizeBits>(0);
            std::size_t data_start_index = buffer_.size();
            write_numeric<std::size_t, SizeBits>(value.size() * 2);
            for (const auto &[k, v] : value) {
                encode(k);
                encode(v);
            }
            // Now we can fill in the size
            overwrite_numeric<std::size_t, SizeBits>(size_start_index, buffer_.size() - data_start_index);
        }

        template <std::size_t SizeBits = 32, typename T>
        void write_array(const array_t<T> &value) {
            std::size_t size_start_index = buffer_.size();
            // Write size zero for now, fill in later.
            write_numeric<std::size_t, SizeBits>(0);
            std::size_t data_start_index = buffer_.size();
            write_numeric<std::size_t, SizeBits>(value.size());
            auto constructor = array_constructor(value);
            write_constructor(constructor);
            for (const auto &v : value) {
                write_value(v, constructor);
            }
            // Now we can fill in the size
            overwrite_numeric<std::size_t, SizeBits>(size_start_index, buffer_.size() - data_start_index);
        }

        template <std::size_t SizeBits = 32, typename... Ts>
        void write_tuple(const std::tuple<Ts...> &value) {
            std::size_t size_start_index = buffer_.size();
            // Write size zero for now, fill in later.
            write_numeric<std::size_t, SizeBits>(0);
            std::size_t count_start_index = buffer_.size();
            write_numeric<std::size_t, SizeBits>(0);
            std::size_t null_count{};
            std::size_t elt_count{};
            auto write_nulls = [&,this](){
                            while (null_count) {
                                encode(null_t{});
                                ++elt_count;
                                --null_count;
                            }

            };
            for_each_tuple_elt(
                [&](const auto &v) {
                    using T = std::remove_cv_t<std::remove_reference_t<decltype(v)>>;
                    if constexpr (is_optional_v<T>) {
                        if (!v) {
                            ++null_count;
                        } else {
                            write_nulls();
                            encode(v);
                            ++elt_count;
                        }
                    } else if constexpr (is_multiple_v<T>) {
                        if (v.empty()) {
                            ++null_count;
                        } else if (v.size() == 1) {
                            write_nulls();
                            encode(v.front());
                            ++elt_count;
                        } else {
                            write_nulls();
                            encode(v);
                            ++elt_count;
                        }
                    } else {
                        write_nulls();
                        encode(v);
                        ++elt_count;
                    }
                },
                value
            );

            // Now we can fill in the size and count
            overwrite_numeric<std::size_t, SizeBits>(size_start_index, buffer_.size() - count_start_index);
            overwrite_numeric<std::size_t, SizeBits>(count_start_index, elt_count);
        }

        Buffer buffer_;
    };

} // namespace amqp_asio::types
