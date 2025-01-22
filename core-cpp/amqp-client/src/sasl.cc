/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include "sasl_context.hh"
#include <fmt/format.h>
#include <fmt/ranges.h>

namespace amqp_asio {

    SaslClientContext::SaslClientContext(SaslOptions options) : options_(std::move(options)) {
        if (auto err = sasl_client_init(nullptr)) {
            throw std::runtime_error(fmt::format("sasl_client_init failed: code {}", err));
        }
    }

    void SaslClientContext::init_callbacks() {
        sasl_callbacks_.clear();
        if (options_.user()) {
            sasl_callbacks_.push_back({SASL_CB_USER, nullptr, nullptr});
        }
        if (options_.authname()) {
            sasl_callbacks_.push_back({SASL_CB_AUTHNAME, nullptr, nullptr});
        }
        if (options_.password()) {
            sasl_callbacks_.push_back({SASL_CB_PASS, nullptr, nullptr});
        }
        sasl_callbacks_.push_back({SASL_CB_LIST_END, nullptr, nullptr});
    }

    std::vector<std::byte> SaslClientContext::start(const std::vector<std::string> &mechanisms) {
        log_.debug("Negotiating SASL");
        sasl_conn_t *conn{};
        init_callbacks();
        if (auto err = sasl_client_new("amqp", "localhost", nullptr, nullptr, sasl_callbacks_.data(), 0, &conn)) {
            log_.error("sasl_client_new failed: code {}", err);
            throw std::runtime_error(fmt::format("sasl_client_new failed: code {}", err));
        }
        connection_ = std::shared_ptr<sasl_conn_t>(conn, [](sasl_conn_t *ptr) {
            sasl_dispose(&ptr);
        });

        std::string mechlist = fmt::format("{}", fmt::join(mechanisms, " "));
        const char *clientout{};
        unsigned outlen{};
        const char *mechusing{};
        sasl_interact_t *interactions{};

        auto status = SASL_INTERACT;
        while (status == SASL_INTERACT) {
            log_.debug("Calling sasl_client_start");
            status =
                sasl_client_start(connection_.get(), mechlist.c_str(), &interactions, &clientout, &outlen, &mechusing);
            if (status == SASL_INTERACT) {
                interact(interactions);
            }
        }
        if (status != SASL_OK) {
            log_.error("sasl_client_start failed: code {}", status);
            throw std::runtime_error(fmt::format("sasl_client_start failed: code {}", status));
        };

        mechanism_ = mechusing;

        log_.debug("mech = {}, data={}", mechusing, std::string(clientout, outlen));
        return {
            reinterpret_cast<const std::byte *>(clientout), reinterpret_cast<const std::byte *>(clientout) + outlen
        };
    }

    std::vector<std::byte> SaslClientContext::challenge(const std::vector<std::byte> &chall) const {
        sasl_interact_t *interactions{};
        const char *clientout{};
        unsigned outlen{};
        auto status = SASL_INTERACT;
        while (status == SASL_INTERACT) {
            status = sasl_client_step(
                connection_.get(),
                reinterpret_cast<const char *>(chall.data()),
                chall.size(),
                &interactions,
                &clientout,
                &outlen
            );
            if (status == SASL_INTERACT) {
                interact(interactions);
            }
        }
        if (status != SASL_OK && status != SASL_CONTINUE) {
            log_.error("sasl_client_step failed: code {}", status);
            throw std::runtime_error(fmt::format("sasl_client_step failed: code {}", status));
        }
        return {
            reinterpret_cast<const std::byte *>(clientout), reinterpret_cast<const std::byte *>(clientout) + outlen
        };
    }

    void SaslClientContext::interact(sasl_interact_t *interactions) const {
        for (auto interaction = interactions; interaction && interaction->id != SASL_CB_LIST_END; ++interaction) {
            switch (interaction->id) {
                case SASL_CB_AUTHNAME:
                    log_.debug("Setting authname={}", options_.authname().value());
                    interaction->result = options_.authname().value().c_str();
                    interaction->len = options_.authname().value().size();
                    break;
                case SASL_CB_USER:
                    log_.debug("Setting user={}", options_.user().value());
                    interaction->result = options_.user().value().c_str();
                    interaction->len = options_.user().value().size();
                    break;
                case SASL_CB_PASS:
                    log_.debug("Setting password={}", options_.password().value());
                    interaction->result = options_.password().value().c_str();
                    interaction->len = options_.password().value().size();
                    break;
            }
        }
    }

} // namespace amqp_asio::sasl
