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

#include <logging/log.hh>
#include <memory>
#include <sasl/sasl.h>
#include <string>
#include <vector>

#include "amqp_asio/options.hh"

namespace amqp_asio {

    class SaslClientContext {
      public:
        SaslClientContext(SaslOptions options);

        SaslClientContext(const SaslClientContext &) = default;
        SaslClientContext(SaslClientContext &&) = default;
        SaslClientContext &operator=(const SaslClientContext &) = default;
        SaslClientContext &operator=(SaslClientContext &&) = default;
        ~SaslClientContext() = default;

        std::vector<std::byte> start(const std::vector<std::string> &mechanisms);

        [[nodiscard]]
        std::vector<std::byte> challenge(const std::vector<std::byte> &chall) const;

        [[nodiscard]]
        const std::string &mechanism() const {
            return mechanism_;
        }

      private:
        void init_callbacks();

        void interact(sasl_interact_t *interactions) const;

        std::shared_ptr<sasl_conn_t> connection_;

        SaslOptions options_{};

        std::string mechanism_{};

        std::vector<sasl_callback_t> sasl_callbacks_;
        xtuml::logging::Logger log_{"amqp_asio.sasl"};
    };

} // namespace amqp_asio::sasl
