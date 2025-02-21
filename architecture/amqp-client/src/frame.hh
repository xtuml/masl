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
#include <asio/buffer.hpp>
#include <asio/read.hpp>
#include <asio/awaitable.hpp>
#include <boost/endian.hpp>
#include <fmt/format.h>
#include <fmt/ranges.h>
#include <fmt/std.h>
#include <vector>
#include "amqp_asio/any_socket.hh"

namespace amqp_asio {

    struct FrameHeader {
        boost::endian::big_uint32_at size{};
        boost::endian::big_uint8_at doff{};
        boost::endian::big_uint8_at type{};
        boost::endian::big_uint16_at type_specific{};
    };

    static_assert(sizeof(FrameHeader) == 8);

    class Frame {
      public:
        Frame() = default;
        Frame(const Frame &) = default;
        Frame(Frame &&) = default;
        Frame &operator=(const Frame &) = default;
        Frame &operator=(Frame &&) = default;

        uint8_t type() const {
            return header_.type;
        }
        uint16_t type_specific() const {
            return header_.type_specific;
        }

        const std::vector<std::byte>& body() const & {
            return body_;
        }

        std::vector<std::byte> body() && {
            return std::move(body_);
        }

        Frame(uint8_t type, uint16_t type_specific = {}) {
            header_.type = type;
            header_.type_specific = type_specific;
            calc_sizes();
        }

        Frame& ext_header(std::vector<std::byte> v) {
            extended_header_ = std::move(v);
            calc_sizes();
            return *this;
        }

        Frame& body(std::vector<std::byte> v) {
            body_ = std::move(v);
            calc_sizes();
            return *this;
        }

        Frame& supplementary(std::vector<std::byte> v) {
            supplementary_.emplace_back(std::move(v));
            calc_sizes();
            return *this;
        }

        asio::awaitable<void> read(AnySocket& socket) {
            auto hbuf = asio::buffer(&header_, sizeof(header_));
            co_await socket.read(hbuf);

            auto extended_header_size = header_.doff * 4 - sizeof(FrameHeader);
            auto body_size = header_.size - extended_header_size - sizeof(FrameHeader);

            extended_header_.resize(extended_header_size);
            body_.resize(body_size);

            auto bbuf = std::vector{asio::buffer(extended_header_), asio::buffer(body_)};
            co_await socket.read(bbuf);
            supplementary_.clear();
        }

    private:
        void calc_sizes() {
              auto body_size = body_.size() + std::accumulate(supplementary_.begin(), supplementary_.end(), 0, [](auto sum, auto &&v) {
                return sum + v.size();
            });
            header_.size = sizeof(FrameHeader) + this->extended_header_.size() + body_size;
            header_.doff = (sizeof(FrameHeader) + this->extended_header_.size()) / sizeof(std::uint32_t);
        }

        FrameHeader header_{sizeof(FrameHeader)};
        std::vector<std::byte> extended_header_{};
        std::vector<std::byte> body_{};
        std::vector<std::vector<std::byte>> supplementary_{};

        friend auto buffer(const Frame &frame) {
            using asio::buffer;
            auto buf = std::vector{buffer(&frame.header_, sizeof(header_)), buffer(frame.extended_header_), buffer(frame.body_)};
            for (const auto& b : frame.supplementary_) {
                buf.emplace_back(buffer(b));
            }
            return buf;
        }

    };

} // namespace amqp_asio