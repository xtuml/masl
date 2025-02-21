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
#include <map>
#include <set>

namespace amqp_asio::sasl {

    struct SaslMechanisms {
        static constexpr types::DescriptorDefinition amqp_descriptor = {"amqp:sasl-mechanisms:list", 0x00, 0x40};

        std::vector<types::symbol_t> sasl_server_mechanisms;
    };

    struct SaslInit {
        static constexpr types::DescriptorDefinition amqp_descriptor = {"amqp:sasl-init:list", 0x00, 0x41};

        types::symbol_t mechanism{};
        std::optional<types::binary_t> initial_response{};
        std::optional<types::string_t> hostname{};
    };

    struct SaslChallenge {
        static constexpr types::DescriptorDefinition amqp_descriptor = {"amqp:sasl-challenge:list", 0x00, 0x42};

        types::binary_t challenge{};
    };

    struct SaslResponse {
        static constexpr types::DescriptorDefinition amqp_descriptor = {"amqp:sasl-response:list", 0x00, 0x43};

        types::binary_t response{};
    };

    enum class SaslCode : types::ubyte_t { ok = 0, auth = 1, sys = 2, sys_perm = 3, sys_temp = 4 };

    struct SaslOutcome {
        static constexpr types::DescriptorDefinition amqp_descriptor = {"amqp:sasl-outcome:list", 0x00, 0x44};

        SaslCode code{};
        std::optional<types::binary_t> additional_data{};
    };

    using SaslMessage = std::variant<SaslMechanisms, SaslInit, SaslChallenge, SaslResponse, SaslOutcome>;

} // namespace amqp_asio::sasl
