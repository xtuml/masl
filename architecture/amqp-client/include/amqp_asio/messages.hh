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

#include "enum.hh"
#include "json.hh"
#include "types.hh"

namespace amqp_asio::messages {
    using amqp_asio::types::to_json;

    using namespace amqp_asio::types;

    // 2.8.6 Milliseconds
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-milliseconds
    // <type name="milliseconds" class="restricted" source="uint"/>
    using Milliseconds = std::chrono::duration<uint_t, std::milli>;

    // 2.8.10 Sequence No
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-sequence-no
    // <type name="sequence-no" class="restricted" source="uint"/>
    using SequenceNo = uint_t;

    // 2.8.13 Fields
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-fields
    // <type name="fields" class="restricted" source="map"/>
    using Fields = map_t<symbol_t, any_t>;

    // 2.8.15 AMQP Error
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-amqp-error
    // <type name="amqp-error" class="restricted" source="symbol" provides="error-condition">
    //     <choice name="internal-error" value="amqp:internal-error"/>
    //     <choice name="not-found" value="amqp:not-found"/>
    //     <choice name="unauthorized-access" value="amqp:unauthorized-access"/>
    //     <choice name="decode-error" value="amqp:decode-error"/>
    //     <choice name="resource-limit-exceeded" value="amqp:resource-limit-exceeded"/>
    //     <choice name="not-allowed" value="amqp:not-allowed"/>
    //     <choice name="invalid-field" value="amqp:invalid-field"/>
    //     <choice name="not-implemented" value="amqp:not-implemented"/>
    //     <choice name="resource-locked" value="amqp:resource-locked"/>
    //     <choice name="precondition-failed" value="amqp:precondition-failed"/>
    //     <choice name="resource-deleted" value="amqp:resource-deleted"/>
    //     <choice name="illegal-state" value="amqp:illegal-state"/>
    //     <choice name="frame-size-too-small" value="amqp:frame-size-too-small"/>
    // </type>
    enum class AmqpError {
        internal_error,
        not_found,
        unauthorized_access,
        decode_error,
        resource_limit_exceeded,
        not_allowed,
        invalid_field,
        not_implemented,
        resource_locked,
        precondition_failed,
        resource_deleted,
        illegal_state,
        frame_size_too_small,
    };
    SYMBOLIC_ENUM(
        AmqpError,
        {
            {AmqpError::internal_error, "amqp:internal-error"},
            {AmqpError::not_found, "amqp:not-found"},
            {AmqpError::unauthorized_access, "amqp:unauthorized-access"},
            {AmqpError::decode_error, "amqp:decode-error"},
            {AmqpError::resource_limit_exceeded, "amqp:resource-limit-exceeded"},
            {AmqpError::not_allowed, "amqp:not-allowed"},
            {AmqpError::invalid_field, "amqp:invalid-field"},
            {AmqpError::not_implemented, "amqp:not-implemented"},
            {AmqpError::resource_locked, "amqp:resource-locked"},
            {AmqpError::precondition_failed, "amqp:precondition-failed"},
            {AmqpError::resource_deleted, "amqp:resource-deleted"},
            {AmqpError::illegal_state, "amqp:illegal-state"},
            {AmqpError::frame_size_too_small, "amqp:frame-size-too-small"},
        }
    );
    // 2.8.16 Connection Error
    // <type name="connection-error" class="restricted" source="symbol" provides="error-condition">
    //     <choice name="connection-forced" value="amqp:connection:forced"/>
    //     <choice name="framing-error" value="amqp:connection:framing-error"/>
    //     <choice name="redirect" value="amqp:connection:redirect"/>
    // </type>
    enum class ConnectionError { connection_forced, framing_error, redirect };
    SYMBOLIC_ENUM(
        ConnectionError,
        {
            {ConnectionError::connection_forced, "amqp:connection:connection-forced"},
            {ConnectionError::framing_error, "amqp:connection:framing-error"},
            {ConnectionError::redirect, "amqp:connection:redirect"},
        }
    )

    // 2.7.17 Session Error
    // <type name="session-error" class="restricted" source="symbol" provides="error-condition">
    //   <choice name="window-violation" value="amqp:session:window-violation"/>
    //   <choice name="errant-link" value="amqp:session:errant-link"/>
    //   <choice name="handle-in-use" value="amqp:session:handle-in-use"/>
    //   <choice name="unattached-handle" value="amqp:session:unattached-handle"/>
    // </type>
    enum class SessionError {
        window_violation,
        errant_link,
        handle_in_use,
        unattached_handle,
    };
    SYMBOLIC_ENUM(
        SessionError,
        {
            {SessionError::window_violation, "amqp:session:window-violation"},
            {SessionError::errant_link, "amqp:session:errant-link"},
            {SessionError::handle_in_use, "amqp:session:handle-in-use"},
            {SessionError::unattached_handle, "amqp:session:unattached-handle"},
        }
    );

    // 2.8.18 Link Error
    // Symbols used to indicate link error conditions.
    // <type name="link-error" class="restricted" source="symbol" provides="error-condition">
    //     <choice name="detach-forced" value="amqp:link:detach-forced"/>
    //     <choice name="transfer-limit-exceeded" value="amqp:link:transfer-limit-exceeded"/>
    //     <choice name="message-size-exceeded" value="amqp:link:message-size-exceeded"/>
    //     <choice name="redirect" value="amqp:link:redirect"/>
    //     <choice name="stolen" value="amqp:link:stolen"/>
    // </type>
    enum class LinkError { detach_forced, transfer_limit_exceeded, message_size_exceeded, redirect, stolen };
    SYMBOLIC_ENUM(
        LinkError,
        {
            {LinkError::detach_forced, "amqp:link:detach-forced"},
            {LinkError::transfer_limit_exceeded, "amqp:link:transfer-limit-exceeded"},
            {LinkError::message_size_exceeded, "amqp:link:message-size-exceeded"},
            {LinkError::redirect, "amqp:link:redirect"},
            {LinkError::stolen, "amqp:link:stolen"},
        }
    );
    // 4.5.8 Transaction Error
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transactions-v1.0-os.html#type-transaction-error
    // <type name="transaction-error" class="restricted" source="symbol" provides="error-condition">
    //     <choice name="unknown-id" value="amqp:transaction:unknown-id"/>
    //     <choice name="transaction-rollback" value="amqp:transaction:rollback"/>
    //     <choice name="transaction-timeout" value="amqp:transaction:timeout"/>
    // </type>
    enum class TransactionError {
        unknown_id,
        transaction_rollback,
        transaction_timeout,
    };
    SYMBOLIC_ENUM(
        TransactionError,
        {
            {TransactionError::unknown_id, "amqp:transaction:unknown-id"},
            {TransactionError::transaction_rollback, "amqp:transaction:rollback"},
            {TransactionError::transaction_timeout, "amqp:transaction:timeout"},
        }
    );

    // provides = "error-condition"
    using ErrorCondition =
        std::variant<symbol_t, AmqpError, ConnectionError, SessionError, LinkError, TransactionError>;

    // 2.8.14 Error
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-error
    // <type name="error" class="composite" source="list">
    //     <descriptor name="amqp:error:list" code="0x00000000:0x0000001d"/>
    //     <field name="condition" type="symbol" requires="error-condition" mandatory="true"/>
    //     <field name="description" type="string"/>
    //     <field name="info" type="fields"/>
    // </type>
    struct Error {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:error:list", 0x00, 0x1d};
        static constexpr const char *const elt_names[] = {
            "condition",
            "description",
            "info",
        };
        ErrorCondition condition{};
        std::optional<string_t> description{};
        std::optional<Fields> info{};

        auto operator<=>(const Error &) const = default;

        static std::optional<Error> make_error(std::string condition = "", std::string description = "") {
            if (condition.empty())
                return std::nullopt;
            Error error{.condition = types::symbol_t{condition}};
            if (!description.empty()) {
                error.description = description;
            }
            return error;
        }
    };

    // 3.2.10 Annotations
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-annotations
    // <type name="annotations" class="restricted" source="map"/>
    using Annotations = map_t<std::variant<ulong_t, symbol_t>, any_t>;

    // 3.2.11 Message Id Ulong
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-message-id-ulong
    // <type name="message-id-ulong" class="restricted" source="ulong" provides="message-id"/>
    using MessageIdUlong = ulong_t;

    // 3.2.12 Message Id Uuid
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-message-id-uuid
    // <<type name="message-id-uuid" class="restricted" source="uuid" provides="message-id"/>
    using MessageIdUuid = uuid_t;

    // 3.2.13 Message Id Binary
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-message-id-binary
    // <type name="message-id-binary" class="restricted" source="binary" provides="message-id"/>
    using MessageIdBinary = binary_t;

    // 3.2.14 Message Id String
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-message-id-string
    // <type name="message-id-string" class="restricted" source="string" provides="message-id"/>
    using MessageIdString = string_t;

    // provides = "message-id"
    using MessageId = std::variant<MessageIdUlong, MessageIdUuid, MessageIdBinary, MessageIdString>;

    // 3.2.15 Address String
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-address-string
    // <type name="address-string" class="restricted" source="string" provides="address"/>
    using AddressString = string_t;

    // provides = "address"
    using Address = std::variant<AddressString>;

    // 3.2.1 Header
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-header
    // <type name="header" class="composite" source="list" provides="section">
    //     <descriptor name="amqp:header:list" code="0x00000000:0x00000070"/>
    //     <field name="durable" type="boolean" default="false"/>
    //     <field name="priority" type="ubyte" default="4"/>
    //     <field name="ttl" type="milliseconds"/>
    //     <field name="first-acquirer" type="boolean" default="false"/>
    //     <field name="delivery-count" type="uint" default="0"/>
    // </type>
    struct Header {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:header:list", 0x00, 0x70};
        static constexpr const char *const elt_names[] = {
            "durable", "priority", "ttl", "first-acquirer", "delivery-count"
        };
        std::optional<boolean_t> durable{};
        std::optional<ubyte_t> priority{};
        std::optional<Milliseconds> ttl{};
        std::optional<bool> first_acquirer{};
        std::optional<uint_t> delivery_count{};

        auto operator<=>(const Header &) const = default;
    };

    // 3.2.2 Delivery Annotations
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-delivery-annotations
    // <type name="delivery-annotations" class="restricted" source="annotations" provides="section">
    //     <descriptor name="amqp:delivery-annotations:map" code="0x00000000:0x00000071"/>
    // </type>
    struct DeliveryAnnotations : wrapper_t<Annotations, DeliveryAnnotations> {
        using wrapper_t<Annotations, DeliveryAnnotations>::wrapper_t;
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:delivery-annotations:map", 0x00, 0x71};

        auto operator<=>(const DeliveryAnnotations &) const = default;
    };

    // 3.2.3 Message Annotations
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-message-annotations
    // <type name="message-annotations" class="restricted" source="annotations" provides="section">
    //     <descriptor name="amqp:message-annotations:map" code="0x00000000:0x00000072"/>
    // </type>
    struct MessageAnnotations : wrapper_t<Annotations, MessageAnnotations> {
        using wrapper_t<Annotations, MessageAnnotations>::wrapper_t;
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:message-annotations:map", 0x00, 0x72};

        auto operator<=>(const MessageAnnotations &) const = default;
    };

    // 3.2.4 Properties
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-properties
    // <type name="properties" class="composite" source="list" provides="section">
    //     <descriptor name="amqp:properties:list" code="0x00000000:0x00000073"/>
    //     <field name="message-id" type="*" requires="message-id"/>
    //     <field name="user-id" type="binary"/>
    //     <field name="to" type="*" requires="address"/>
    //     <field name="subject" type="string"/>
    //     <field name="reply-to" type="*" requires="address"/>
    //     <field name="correlation-id" type="*" requires="message-id"/>
    //     <field name="content-type" type="symbol"/>
    //     <field name="content-encoding" type="symbol"/>
    //     <field name="absolute-expiry-time" type="timestamp"/>
    //     <field name="creation-time" type="timestamp"/>
    //     <field name="group-id" type="string"/>
    //     <field name="group-sequence" type="sequence-no"/>
    //     <field name="reply-to-group-id" type="string"/>
    // </type>
    struct Properties {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:properties:list", 0x00, 0x73};
        static constexpr const char *const elt_names[] = {
            "message-id",
            "user-id",
            "to",
            "subject",
            "reply-to",
            "correlation-id",
            "content-type",
            "content-encoding",
            "absolute-expiry-time",
            "creation-time",
            "group-id",
            "group-sequence",
            "reply-to-group-id",
        };
        std::optional<MessageId> message_id{};
        std::optional<binary_t> user_id{};
        std::optional<Address> to{};
        std::optional<string_t> subject{};
        std::optional<Address> reply_to{};
        std::optional<MessageId> correlation_id{};
        std::optional<symbol_t> content_type{};
        std::optional<symbol_t> content_encoding{};
        std::optional<timestamp_t> absolute_expiry_time{};
        std::optional<timestamp_t> creation_time{};
        std::optional<string_t> group_id{};
        std::optional<SequenceNo> group_sequence{};
        std::optional<string_t> reply_to_group_id{};

        auto operator<=>(const Properties &) const = default;
    };

    // 3.2.5 Application Properties
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-application-properties
    // <type name="application-properties" class="restricted" source="map" provides="section">
    //     <descriptor name="amqp:application-properties:map" code="0x00000000:0x00000074"/>
    // </type>
    struct ApplicationProperties : wrapper_t<map_t<string_t, scalar_t>, ApplicationProperties> {
        using wrapper_t<map_t<string_t, scalar_t>, ApplicationProperties>::wrapper_t;
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:application-properties:map", 0x00, 0x74};
        auto operator<=>(const ApplicationProperties &) const = default;
    };

    // 3.2.6 Data
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-data
    // <type name="data" class="restricted" source="binary" provides="section">
    //   <descriptor name="amqp:data:binary" code="0x00000000:0x00000075"/>
    // </type>
    struct Data : wrapper_t<binary_t, Data> {
        using wrapper_t<binary_t, Data>::wrapper_t;
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:data:binary", 0x00, 0x75};
        auto operator<=>(const Data &) const = default;
    };

    // 3.2.7 Ampq Sequence
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-amqp-sequence
    // <type name="amqp-sequence" class="restricted" source="list" provides="section">
    //     <descriptor name="amqp:amqp-sequence:list" code="0x00000000:0x00000076"/>
    // </type>
    struct AMQPSequence : wrapper_t<any_list_t, AMQPSequence> {
        using wrapper_t<any_list_t, AMQPSequence>::wrapper_t;
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:amqp-sequence:list", 0x00, 0x76};
        auto operator<=>(const AMQPSequence &) const = default;
    };

    // 3.2.8 Ampq Value
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-amqp-value
    // <type name="amqp-value" class="restricted" source="*" provides="section">
    //     <descriptor name="amqp:amqp-value:*" code="0x00000000:0x00000077"/>
    // </type>
    struct AMQPValue : wrapper_t<types::any_t, AMQPValue> {
        using wrapper_t<types::any_t, AMQPValue>::wrapper_t;
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:amqp-value:*", 0x00, 0x77};
        auto operator<=>(const AMQPValue &) const = default;
    };

    // 3.2.9 Footer
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-amqp-value
    // <type name="footer" class="restricted" source="annotations" provides="section">
    //     <descriptor name="amqp:footer:map" code="0x00000000:0x00000078"/>
    // </type>
    struct Footer : wrapper_t<Annotations, Footer> {
        using wrapper_t<Annotations, Footer>::wrapper_t;
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:footer:map", 0x00, 0x78};
        auto operator<=>(const Footer &) const = default;
    };

    using MessagePayload = std::variant<std::vector<Data>, std::vector<AMQPSequence>, AMQPValue>;

    struct Message {
        static constexpr const char *const elt_names[] = {
            "header",
            "delivery-annotations",
            "message-annotations",
            "properties",
            "application-properties",
            "data",
            "footer",
        };
        std::optional<Header> header;
        std::optional<DeliveryAnnotations> delivery_annotations;
        std::optional<MessageAnnotations> message_annotations;
        std::optional<Properties> properties;
        std::optional<ApplicationProperties> application_properties;
        MessagePayload data;
        std::optional<Footer> footer;
        auto operator<=>(const Message &) const = default;

        std::vector<std::byte> as_bytes() {
            std::vector<std::byte> result;
            for (const auto &seg : std::get<std::vector<Data>>(data)) {
                result.insert(result.end(), std::begin(seg.value), std::end(seg.value));
            }
            return result;
        }

        std::string as_string() {
            std::string result;
            for (const auto &seg : std::get<std::vector<Data>>(data)) {
                std::transform(std::begin(seg.value), std::end(seg.value), std::back_inserter(result), [](auto b) {
                    return char(b);
                });
            }
            const auto &bytes = as_bytes();
            return result;
        }

        nlohmann::json as_json() {
            return nlohmann::json::parse(as_string());
        }
    };

} // namespace amqp_asio::messages