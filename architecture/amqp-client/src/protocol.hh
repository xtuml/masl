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
#include <asio/buffer.hpp>


// https://bugzilla.redhat.com/show_bug.cgi?id=130601
#undef major
#undef minor

namespace amqp_asio {
    namespace protocol {
        enum class Protocol : uint8_t { AMQP = 0x00, TLS = 0x02, SASL = 0x03 };

        struct ProtocolMessage {
            ProtocolMessage(
                Protocol protocol = Protocol::AMQP, uint8_t major = 1, uint8_t minor = 0, uint8_t revision = 0
            )
                : protocol(protocol), major(major), minor(minor), revision(revision) {}

            char amqp[4] = {'A', 'M', 'Q', 'P'};
            Protocol protocol{Protocol::AMQP};
            uint8_t major{1};
            uint8_t minor{};
            uint8_t revision{};

            friend auto buffer(ProtocolMessage &m) {
                return asio::buffer(&m, sizeof(m));
            }

            friend auto buffer(const ProtocolMessage &m) {
                return asio::buffer(&m, sizeof(m));
            }

            friend std::string format_as(const ProtocolMessage &m) {
                return fmt::format(
                    "{}.{}.{}.{}.{}",
                    std::string(m.amqp, sizeof(amqp)),
                    static_cast<uint8_t>(m.protocol),
                    m.major,
                    m.minor,
                    m.revision
                );
            }

            auto operator<=>(const ProtocolMessage &rhs) const = default;
        };
    } // namespace protocol
} // namespace amqp_asio