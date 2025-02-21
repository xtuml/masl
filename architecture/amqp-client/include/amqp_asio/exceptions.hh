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
#include <exception>
#include <system_error>

namespace amqp_asio {

    struct AMQPException : public std::system_error {
        using std::system_error::system_error;
    };

    struct DecodeException : public std::runtime_error {
        using std::runtime_error::runtime_error;
    };

    struct EncodeException : public std::runtime_error {
        using std::runtime_error::runtime_error;
    };

    enum class error { UnexpectedMessage = 1, DecodeError, EncodeError, SaslAuthenticationFailed, SaslSystemError, ProtocolMismatch, SessionAttachError };

    struct error_category_impl : public std::error_category {
        const char *name() const noexcept override {
            return "amqp_asio";
        }
        std::string message(int ev) const noexcept override {
            switch (error{ev}) {
                case error::ProtocolMismatch:
                    return "Mismatched protocol requested";
                case error::UnexpectedMessage:
                    return "Unexpected message received";
                case error::DecodeError:
                    return "Error decoding AMQP object";
                case error::EncodeError:
                    return "Error encoding AMQP object";
                case error::SaslAuthenticationFailed:
                    return "SASL authentication failed";
                case error::SaslSystemError:
                    return "SASL system error";
                case error::SessionAttachError:
                    return "Attach session failed";
                default:
                    return "Unknown AMQP error";
            }
        }
        static const std::error_category &category() {
            static error_category_impl instance;
            return instance;
        }
    };

    inline std::error_code make_error_code(error e) {
        return std::error_code(static_cast<int>(e), error_category_impl::category());
    }

    inline std::error_condition make_error_condition(error e) {
        return std::error_condition(static_cast<int>(e), error_category_impl::category());
    }

} // namespace amqp_asio

namespace std {
    template <>
    struct is_error_code_enum<amqp_asio::error> : public true_type {};
} // namespace std