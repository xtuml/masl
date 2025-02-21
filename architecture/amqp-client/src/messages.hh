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

#include "amqp_asio/enum.hh"
#include "amqp_asio/json.hh"
#include "amqp_asio/types.hh"
#include "amqp_asio/messages.hh"

namespace amqp_asio::messages {
    using amqp_asio::types::to_json;

    using namespace amqp_asio::types;

    using ChannelId = ushort_t;
    using Window = uint_t;

    // ----------------------------------------------
    // 2. Transport
    // ----------------------------------------------

    // 2.8.1 Role
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-role
    // <type name="role" class="restricted" source="boolean">
    //     <choice name="sender" value="false"/>
    //     <choice name="receiver" value="true"/>
    // </type>
    enum class Role : boolean_t { sender, receiver };
    NUMERIC_ENUM(Role, {{Role::sender, "sender"}, {Role::receiver, "receiver"}});

    // 2.8.2 Sender Settle Mode
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-sender-settle-mode
    // <type name="sender-settle-mode" class="restricted" source="ubyte">
    //     <choice name="unsettled" value="0"/>
    //     <choice name="settled" value="1"/>
    //     <choice name="mixed" value="2"/>
    // </type>
    enum class SenderSettleMode : ubyte_t { unsettled, settled, mixed };
    NUMERIC_ENUM(
        SenderSettleMode,
        {{SenderSettleMode::unsettled, "unsettled"},
         {SenderSettleMode::settled, "settled"},
         {SenderSettleMode::mixed, "mixed"}}
    );

    // 2.8.3 Receiver Settle Mode
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-receiver-settle-mode
    // <type name="receiver-settle-mode" class="restricted" source="ubyte">
    //     <choice name="first" value="0"/>
    //     <choice name="second" value="1"/>
    // </type
    enum class ReceiverSettleMode : ubyte_t { first, second };
    NUMERIC_ENUM(ReceiverSettleMode, {{ReceiverSettleMode::first, "first"}, {ReceiverSettleMode::second, "second"}});

    // 2.8.4 Handle
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-handle
    // <type name="handle" class="restricted" source="uint"/>
    using Handle = uint_t;

    // 2.8.5 Seconds
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-seconds
    // <type name="seconds" class="restricted" source="uint"/>
    using Seconds = std::chrono::duration<uint_t>;

    // 2.8.7 Delivery Tag
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-delivery-tag
    // <type name="delivery-tag" class="restricted" source="binary"/>
    using DeliveryTag = binary_t;

    // 2.8.8 Delivery Number
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-delivery-number
    // <type name="delivery-number" class="restricted" source="sequence-no"/>
    using DeliveryNumber = SequenceNo;

    // 2.8.9 Transfer Number
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-transfer-number
    // <type name="transfer-number" class="restricted" source="sequence-no"/>
    using TransferNumber = SequenceNo;

    // 2.8.11 Message Format
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-message-format
    // <type name="message-format" class="restricted" source="uint"/>
    using MessageFormat = uint_t;

    // 2.8.12 Ietf Language Tag
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-ietf-language-tag
    // <type name="ietf-language-tag" class="restricted" source="symbol"/>
    using IetfLanguageTag = symbol_t;

    // ----------------------------------------------
    // 3. Messages
    // ----------------------------------------------

    // 3.4.1 Received
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-received
    // <type name="received" class="composite" source="list" provides="delivery-state">
    //     <descriptor name="amqp:received:list" code="0x00000000:0x00000023"/>
    //     <field name="section-number" type="uint" mandatory="true"/>
    //     <field name="section-offset" type="ulong" mandatory="true"/>
    // </type>
    struct Received {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:received:list", 0x00, 0x23};
        static constexpr const char *const elt_names[] = {
            "section-number",
            "section-offset",
        };
        uint_t section_number{};
        ulong_t section_offset{};
        auto operator<=>(const Received &) const = default;
    };

    // 3.4.2 Accepted
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-accepted
    // <type name="accepted" class="composite" source="list" provides="delivery-state, outcome">
    //     <descriptor name="amqp:accepted:list" code="0x00000000:0x00000024"/>
    // </type>
    struct Accepted {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:accepted:list", 0x00, 0x24};
        static constexpr const char *const elt_names[] = {};
        auto operator<=>(const Accepted &) const = default;
    };

    // 3.4.3 Rejected
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-rejected
    // <type name="rejected" class="composite" source="list" provides="delivery-state, outcome">
    //     <descriptor name="amqp:rejected:list" code="0x00000000:0x00000025"/>
    //     <field name="error" type="error"/>
    // </type>
    struct Rejected {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:rejected:list", 0x00, 0x25};
        static constexpr const char *const elt_names[] = {"error"};
        std::optional<Error> error{};
        auto operator<=>(const Rejected &) const = default;
    };

    // 3.4.4 Released
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-released
    // <type name="released" class="composite" source="list" provides="delivery-state, outcome">
    //     <descriptor name="amqp:released:list" code="0x00000000:0x00000026"/>
    // </type>
    struct Released {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:released:list", 0x00, 0x26};
        static constexpr const char *const elt_names[] = {};
        auto operator<=>(const Released &) const = default;
    };

    // 3.4.5 Modified
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-modified
    // <type name="modified" class="composite" source="list" provides="delivery-state, outcome">
    //     <descriptor name="amqp:modified:list" code="0x00000000:0x00000027"/>
    //     <field name="delivery-failed" type="boolean"/>
    //     <field name="undeliverable-here" type="boolean"/>
    //     <field name="message-annotations" type="fields"/>
    // </type>
    struct Modified {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:modified:list", 0x00, 0x27};
        static constexpr const char *const elt_names[] = {
            "delivery-failed",
            "undeliverable-here",
            "message-annotations",
        };
        std::optional<boolean_t> delivery_failed{};
        std::optional<boolean_t> undeliverable_here{};
        std::optional<Fields> message_annotations{};
        auto operator<=>(const Modified &) const = default;
    };

    // 4.5.4 Transaction Id
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transactions-v1.0-os.html#transaction-id
    // <type name="transaction-id" class="restricted" source="binary" provides="txn-id"/>
    using TransactionId = binary_t;

    // provides = "txn-id"
    using TxnId = std::variant<TransactionId>;

    // 4.5.5 Declared
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transactions-v1.0-os.html#type-declared
    // <type name="declared" class="composite" source="list" provides="delivery-state, outcome">
    //     <descriptor name="amqp:declared:list" code="0x00000000:0x00000033"/>
    //     <field name="txn-id" type="*" requires="txn-id" mandatory="true"/>
    // </type>
    struct Declared {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:declared:list", 0x00, 0x33};
        static constexpr const char *const elt_names[] = {"txn-id"};
        TxnId txn_id{};
        auto operator<=>(const Declared &) const = default;
    };

    // provides="outcome"
    using Outcome = std::variant<Accepted, Rejected, Released, Modified, Declared>;

    // 4.5.6 Transactional State
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transactions-v1.0-os.html#type-transactional-state
    // <type name="transactional-state" class="composite" source="list" provides="delivery-state">
    //     <descriptor name="amqp:transactional-state:list" code="0x00000000:0x00000034"/>
    //     <field name="txn-id" type="*" mandatory="true" requires="txn-id"/>
    //     <field name="outcome" type="*" requires="outcome"/>
    // </type>
    struct TransactionalState {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:transactional-state:list", 0x00, 0x34};
        static constexpr const char *const elt_names[] = {
            "txn-id",
            "outcome",
        };
        TxnId txn_id{};
        std::optional<Outcome> outcome;
        auto operator<=>(const TransactionalState &) const = default;
    };

    // provides="delivery-state"
    using DeliveryState = std::variant<Declared, TransactionalState, Received, Accepted, Rejected, Released, Modified>;

    // 3.5.5 Terminus Durability
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-terminus-durability
    // <type name="terminus-durability" class="restricted" source="uint">
    //     <choice name="none" value="0"/>
    //     <choice name="configuration" value="1"/>
    //     <choice name="unsettled-state" value="2"/>
    // </type>
    enum class TerminusDurablility : uint_t { none, configuration, unsettled_state };
    NUMERIC_ENUM(
        TerminusDurablility,
        {
            {TerminusDurablility::none, "none"},
            {TerminusDurablility::configuration, "configuration"},
            {TerminusDurablility::unsettled_state, "unsettled-state"},
        }
    );

    // 3.5.6 Terminus Expiry Policy
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-terminus-expiry-policy
    // <type name="terminus-expiry-policy" class="restricted" source="symbol">
    //     <choice name="link-detach" value="link-detach"/>
    //     <choice name="session-end" value="session-end"/>
    //     <choice name="connection-close" value="connection-close"/>
    //     <choice name="never" value="never"/>
    // </type>
    enum class TerminusExpiryPolicy { link_detach, session_end, connection_close, never };
    SYMBOLIC_ENUM(
        TerminusExpiryPolicy,
        {
            {TerminusExpiryPolicy::link_detach, "link-detach"},
            {TerminusExpiryPolicy::session_end, "session-end"},
            {TerminusExpiryPolicy::connection_close, "connection-close"},
            {TerminusExpiryPolicy::never, "never"},
        }
    );

    // 3.5.7 Std Dist Mode
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-std-dist-mode
    // <type name="std-dist-mode" class="restricted" source="symbol" provides="distribution-mode">
    //     <choice name="move" value="move"/>
    //     <choice name="copy" value="copy"/>
    // </type>
    enum class StdDistMode { move, copy };
    SYMBOLIC_ENUM(
        StdDistMode,
        {
            {StdDistMode::move, "move"},
            {StdDistMode::copy, "copy"},
        }
    );

    // provides = "distribution-mode"
    using DistributionMode = std::variant<symbol_t, StdDistMode>;

    // 3.5.8 Filter Set
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-filter-set
    // <type name="filter-set" class="restricted" source="map"/>
    using FilterSet = std::vector<std::pair<symbol_t, any_t>>;

    // 3.5.9 Node Properties
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-node-properties
    // <type name="node-properties" class="restricted" source="fields"/>
    using NodeProperties = Fields;

    // 3.5.10 Delete On Close
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-delete-on-close
    // <type name="delete-on-close" class="composite" source="list" provides="lifetime-policy">
    //     <descriptor name="amqp:delete-on-close:list" code="0x00000000:0x0000002b"/>
    // </type>
    struct DeleteOnClose {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:delete-on-close:list", 0x00, 0x2b};
        static constexpr const char *const elt_names[] = {};
        auto operator<=>(const DeleteOnClose &) const = default;
    };

    // 3.5.11 Delete On No Links
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-delete-on-no-links
    // <type name="delete-on-no-links" class="composite" source="list" provides="lifetime-policy">
    //     <descriptor name="amqp:delete-on-no-links:list" code="0x00000000:0x0000002c"/>
    // </type>
    struct DeleteOnNoLinks {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:delete-on-no-links:list", 0x00, 0x2c};
        static constexpr const char *const elt_names[] = {};
        auto operator<=>(const DeleteOnNoLinks &) const = default;
    };

    // 3.5.12 Delete On No Messages
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-delete-on-no-messages
    // <type name="delete-on-no-messages" class="composite" source="list" provides="lifetime-policy">
    //     <descriptor name="amqp:delete-on-no-messages:list" code="0x00000000:0x0000002d"/>
    // </type>
    struct DeleteOnNoMessages {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:delete-on-no-messages:list", 0x00, 0x2d};
        static constexpr const char *const elt_names[] = {};
        auto operator<=>(const DeleteOnNoMessages &) const = default;
    };

    // 3.5.13 Delete On No Link Or Messages
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-delete-on-no-links-or-messages
    // <type name="delete-on-no-links-or-messages" class="composite" source="list" provides="lifetime-policy">
    //     <descriptor name="amqp:delete-on-no-links-or-messages:list" code="0x00000000:0x0000002e"/>
    // </type>
    struct DeleteOnNoLinksOrMessages {
        static constexpr DescriptorDefinition amqp_descriptor = {
            "amqp:delete-on-no-links-or-messages:list", 0x00, 0x2e
        };
        static constexpr const char *const elt_names[] = {};
        auto operator<=>(const DeleteOnNoLinksOrMessages &) const = default;
    };

    // provides = "lifetime-policy"
    using LifetimePolicy = std::variant<DeleteOnClose, DeleteOnNoLinks, DeleteOnNoMessages, DeleteOnNoLinksOrMessages>;

    // 3.5.3 Source
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-source
    // <type name="source" class="composite" source="list" provides="source">
    //     <descriptor name="amqp:source:list" code="0x00000000:0x00000028"/>
    //     <field name="address" type="*" requires="address"/>
    //     <field name="durable" type="terminus-durability" default="none"/>
    //     <field name="expiry-policy" type="terminus-expiry-policy" default="session-end"/>
    //     <field name="timeout" type="seconds" default="0"/>
    //     <field name="dynamic" type="boolean" default="false"/>
    //     <field name="dynamic-node-properties" type="node-properties"/>
    //     <field name="distribution-mode" type="symbol" requires="distribution-mode"/>
    //     <field name="filter" type="filter-set"/>
    //     <field name="default-outcome" type="*" requires="outcome"/>
    //     <field name="outcomes" type="symbol" multiple="true"/>
    //     <field name="capabilities" type="symbol" multiple="true"/>
    // </type>
    struct Source {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:source:list", 0x00, 0x28};
        static constexpr const char *const elt_names[] = {
            "address",
            "durable",
            "expiry-policy",
            "timeout",
            "dynamic",
            "dynamic-node-properties",
            "distribution-mode",
            "filter",
            "default-outcome",
            "outcomes",
            "capabilities"
        };
        std::optional<Address> address;
        std::optional<TerminusDurablility> durable;
        std::optional<TerminusExpiryPolicy> expiry_policy;
        std::optional<Seconds> timeout;
        std::optional<boolean_t> dynamic;
        std::optional<NodeProperties> dynamic_node_properties;
        std::optional<DistributionMode> distribution_mode;
        std::optional<FilterSet> filter;
        std::optional<Outcome> default_outcome;
        std::vector<symbol_t> outcomes;
        std::vector<symbol_t> capabilities;
        auto operator<=>(const Source &) const = default;
    };

    // 3.5.4 Target
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-target
    // <type name="target" class="composite" source="list" provides="target">
    //     <descriptor name="amqp:target:list" code="0x00000000:0x00000029"/>
    //     <field name="address" type="*" requires="address"/>
    //     <field name="durable" type="terminus-durability" default="none"/>
    //     <field name="expiry-policy" type="terminus-expiry-policy" default="session-end"/>
    //     <field name="timeout" type="seconds" default="0"/>
    //     <field name="dynamic" type="boolean" default="false"/>
    //     <field name="dynamic-node-properties" type="node-properties"/>
    //     <field name="capabilities" type="symbol" multiple="true"/>
    // </type>
    struct Target {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:target:list", 0x00, 0x29};
        static constexpr const char *const elt_names[] = {
            "address", "durable", "expiry-policy", "timeout", "dynamic", "dynamic-node-properties", "capabilities"
        };

        std::optional<Address> address;
        std::optional<TerminusDurablility> durable;
        std::optional<TerminusExpiryPolicy> expiry_policy;
        std::optional<Seconds> timeout;
        std::optional<boolean_t> dynamic;
        std::optional<NodeProperties> dynamic_node_properties;
        std::vector<symbol_t> capabilities;
        auto operator<=>(const Target &) const = default;
    };


    // 3.2 Message
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#section-message-format
    //                                                      Bare Message
    //                                                            |
    //                                      .---------------------+--------------------.
    //                                      |                                          |
    // +--------+-------------+-------------+------------+--------------+--------------+--------+
    // | header | delivery-   | message-    | properties | application- | application- | footer |
    // |        | annotations | annotations |            | properties   | data         |        |
    // +--------+-------------+-------------+------------+--------------+--------------+--------+
    // |                                                                                        |
    // '-------------------------------------------+--------------------------------------------'
    //                                             |
    //                                     Annotated Message
    using MessageSection = std::variant<
        Header,
        DeliveryAnnotations,
        MessageAnnotations,
        Properties,
        ApplicationProperties,
        Data,
        AMQPSequence,
        AMQPValue,
        Footer>;


    // 4.5.7 Txn Capability
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transactions-v1.0-os.html#type-txn-capability
    // <type name="txn-capability" class="restricted" source="symbol" provides="txn-capability">
    //     <choice name="local-transactions" value="amqp:local-transactions"/>
    //     <choice name="distributed-transactions" value="amqp:distributed-transactions"/>
    //     <choice name="promotable-transactions" value="amqp:promotable-transactions"/>
    //     <choice name="multi-txns-per-ssn" value="amqp:multi-txns-per-ssn"/>
    //     <choice name="multi-ssns-per-txn" value="amqp:multi-ssns-per-txn"/>
    // </type>
    enum class TxnCapability {
        local_transactions,
        distributed_transactions,
        promotable_transactions,
        multi_txns_per_ssn,
        multi_ssns_per_txn,
    };
    SYMBOLIC_ENUM(
        TxnCapability,
        {
            {TxnCapability::local_transactions, "amqp:local-transactions"},
            {TxnCapability::distributed_transactions, "amqp:distributed-transactions"},
            {TxnCapability::promotable_transactions, "amqp:promotable-transactions"},
            {TxnCapability::multi_txns_per_ssn, "amqp:multi-txns-per-ssn"},
            {TxnCapability::multi_ssns_per_txn, "amqp:multi-ssns-per-txn"},
        }
    );

    // 4.5.1 Coordinator
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transactions-v1.0-os.html#type-coordinator
    // <type name="coordinator" class="composite" source="list" provides="target">
    //     <descriptor name="amqp:coordinator:list" code="0x00000000:0x00000030"/>
    //     <field name="capabilities" type="symbol" requires="txn-capability" multiple="true"/>
    // </type>
    struct Coordinator {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:coordinator:list", 0x00, 0x30};
        static constexpr const char *const elt_names[] = {"capabilities"};

        std::vector<TxnCapability> capabilities{};
        auto operator<=>(const Coordinator &) const = default;
    };

    using GlobalTxId = any_t;

    // 4.5.2 Declare
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transactions-v1.0-os.html#type-declare
    // <type name="declare" class="composite" source="list">
    //     <descriptor name="amqp:declare:list" code="0x00000000:0x00000031"/>
    //     <field name="global-id" type="*" requires="global-tx-id"/>
    // </type>
    struct Declare {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:declare:list", 0x00, 0x31};
        static constexpr const char *const elt_names[] = {"global-id"};
        std::optional<GlobalTxId> global_id{};
        auto operator<=>(const Declare &) const = default;
    };

    // 4.5.3 Discharge
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transactions-v1.0-os.html#type-discharge
    // <type name="discharge" class="composite" source="list">
    //     <descriptor name="amqp:discharge:list" code="0x00000000:0x00000032"/>
    //     <field name="txn-id" type="*" requires="txn-id" mandatory="true"/>
    //     <field name="fail" type="boolean"/>
    // </type>
    struct Discharge {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:discharge:list", 0x00, 0x32};
        static constexpr const char *const elt_names[] = {"txn-id", "fail"};
        TxnId txn_id{};
        std::optional<boolean_t> fail{};
        auto operator<=>(const Discharge &) const = default;
    };

    using Sources = std::variant<Source>;
    using Targets = std::variant<Target, Coordinator>;

    // 2.7.1 Open
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-open
    // <type name="open" class="composite" source="list" provides="frame">
    //     <descriptor name="amqp:open:list" code="0x00000000:0x00000010"/>
    //     <field name="container-id" type="string" mandatory="true"/>
    //     <field name="hostname" type="string"/>
    //     <field name="max-frame-size" type="uint" default="4294967295"/>
    //     <field name="channel-max" type="ushort" default="65535"/>
    //     <field name="idle-time-out" type="milliseconds"/>
    //     <field name="outgoing-locales" type="ietf-language-tag" multiple="true"/>
    //     <field name="incoming-locales" type="ietf-language-tag" multiple="true"/>
    //     <field name="offered-capabilities" type="symbol" multiple="true"/>
    //     <field name="desired-capabilities" type="symbol" multiple="true"/>
    //     <field name="properties" type="fields"/>
    // </type>
    struct Open {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:open:list", 0x00, 0x10};
        static constexpr const char *const elt_names[] = {
            "container-id",
            "hostname",
            "max-frame-size",
            "channel-max",
            "idle-time-out",
            "outgoing_locales",
            "incoming-locales",
            "offered-capabilities",
            "desired-capabilities",
            "properties",
        };
        std::string container_id{};
        std::optional<std::string> hostname{};
        std::optional<uint_t> max_frame_size;
        std::optional<ushort_t> channel_max;
        std::optional<Milliseconds> idle_time_out{};
        std::vector<IetfLanguageTag> outgoing_locales{};
        std::vector<IetfLanguageTag> incoming_locales{};
        std::vector<symbol_t> offered_capabilities{};
        std::vector<symbol_t> desired_capabilities{};
        std::optional<Fields> properties{};
        auto operator<=>(const Open &) const = default;
    };

    // 2.7.2 Begin
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-begin
    // <type name="begin" class="composite" source="list" provides="frame">
    //     <descriptor name="amqp:begin:list" code="0x00000000:0x00000011"/>
    //     <field name="remote-channel" type="ushort"/>
    //     <field name="next-outgoing-id" type="transfer-number" mandatory="true"/>
    //     <field name="incoming-window" type="uint" mandatory="true"/>
    //     <field name="outgoing-window" type="uint" mandatory="true"/>
    //     <field name="handle-max" type="handle" default="4294967295"/>
    //     <field name="offered-capabilities" type="symbol" multiple="true"/>
    //     <field name="desired-capabilities" type="symbol" multiple="true"/>
    //     <field name="properties" type="fields"/>
    // </type>
    struct Begin {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:begin:list", 0x00, 0x11};
        static constexpr const char *const elt_names[] = {
            "remote-channel",
            "next-outgoing-id",
            "incoming-window",
            "outgoing-window",
            "handle-max",
            "offered-capbilities",
            "desired-capabilities",
            "properties",
        };
        std::optional<ChannelId> remote_channel{};
        TransferNumber next_outgoing_id{};
        Window incoming_window{};
        Window outgoing_window{};
        std::optional<Handle> handle_max;
        std::vector<symbol_t> offered_capabilities{};
        std::vector<symbol_t> desired_capabilities{};
        std::optional<Fields> properties{};
        auto operator<=>(const Begin &) const = default;
    };

    // 2.7.3 Attach
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-attach
    // <type name="attach" class="composite" source="list" provides="frame">
    //     <descriptor name="amqp:attach:list" code="0x00000000:0x00000012"/>
    //     <field name="name" type="string" mandatory="true"/>
    //     <field name="handle" type="handle" mandatory="true"/>
    //     <field name="role" type="role" mandatory="true"/>
    //     <field name="snd-settle-mode" type="sender-settle-mode" default="mixed"/>
    //     <field name="rcv-settle-mode" type="receiver-settle-mode" default="first"/>
    //     <field name="source" type="*" requires="source"/>
    //     <field name="target" type="*" requires="target"/>
    //     <field name="unsettled" type="map"/>
    //     <field name="incomplete-unsettled" type="boolean" default="false"/>
    //     <field name="initial-delivery-count" type="sequence-no"/>
    //     <field name="max-message-size" type="ulong"/>
    //     <field name="offered-capabilities" type="symbol" multiple="true"/>
    //     <field name="desired-capabilities" type="symbol" multiple="true"/>
    //     <field name="properties" type="fields"/>
    // </type>
    struct Attach {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:attach:list", 0x00, 0x12};
        static constexpr const char *const elt_names[] = {
            "name",
            "handle",
            "role",
            "snd-settle-mode",
            "rcv-settle-mode",
            "source",
            "target",
            "unsettled",
            "incomplete-unsettled",
            "initial-delivery-count",
            "max-message-size",
            "offered-capabilities",
            "desired-capabilities",
            "properties",
        };
        string_t name{};
        Handle handle{};
        Role role{};
        std::optional<SenderSettleMode> snd_settle_mode{};
        std::optional<ReceiverSettleMode> rcv_settle_mode{};
        std::optional<Sources> source{};
        std::optional<Targets> target{};
        std::optional<map_t<DeliveryTag, DeliveryState>> unsettled{};
        std::optional<boolean_t> incomplete_unsettled{};
        std::optional<SequenceNo> initial_delivery_count{};
        std::optional<ulong_t> max_message_size{};
        std::vector<symbol_t> offered_capabilities{};
        std::vector<symbol_t> desired_capabilities{};
        std::optional<Fields> properties{};
        auto operator<=>(const Attach &) const = default;
    };

    // 2.7.4 Flow
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-flow
    // <type name="flow" class="composite" source="list" provides="frame">
    //     <descriptor name="amqp:flow:list" code="0x00000000:0x00000013"/>
    //     <field name="next-incoming-id" type="transfer-number"/>
    //     <field name="incoming-window" type="uint" mandatory="true"/>
    //     <field name="next-outgoing-id" type="transfer-number" mandatory="true"/>
    //     <field name="outgoing-window" type="uint" mandatory="true"/>
    //     <field name="handle" type="handle"/>
    //     <field name="delivery-count" type="sequence-no"/>
    //     <field name="link-credit" type="uint"/>
    //     <field name="available" type="uint"/>
    //     <field name="drain" type="boolean" default="false"/>
    //     <field name="echo" type="boolean" default="false"/>
    //     <field name="properties" type="fields"/>
    // </type>
    struct Flow {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:flow:list", 0x00, 0x13};
        static constexpr const char *const elt_names[] = {
            "next-incoming-id",
            "incoming-window",
            "next-outgoing-id",
            "outgoing-window",
            "handle",
            "delivery-count",
            "link-credit",
            "available",
            "drain",
            "echo",
            "properties",
        };
        std::optional<TransferNumber> next_incoming_id{};
        Window incoming_window{};
        TransferNumber next_outgoing_id{};
        Window outgoing_window{};
        std::optional<Handle> handle{};
        std::optional<SequenceNo> delivery_count{};
        std::optional<uint_t> link_credit{};
        std::optional<uint_t> available{};
        std::optional<boolean_t> drain{};
        std::optional<boolean_t> echo{};
        std::optional<Fields> properties{};
        auto operator<=>(const Flow &) const = default;
    };

    // 2.7.5 Transfer
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-transfer
    // <type name="transfer" class="composite" source="list" provides="frame">
    //     <descriptor name="amqp:transfer:list" code="0x00000000:0x00000014"/>
    //     <field name="handle" type="handle" mandatory="true"/>
    //     <field name="delivery-id" type="delivery-number"/>
    //     <field name="delivery-tag" type="delivery-tag"/>
    //     <field name="message-format" type="message-format"/>
    //     <field name="settled" type="boolean"/>
    //     <field name="more" type="boolean" default="false"/>
    //     <field name="rcv-settle-mode" type="receiver-settle-mode"/>
    //     <field name="state" type="*" requires="delivery-state"/>
    //     <field name="resume" type="boolean" default="false"/>
    //     <field name="aborted" type="boolean" default="false"/>
    //     <field name="batchable" type="boolean" default="false"/>
    // </type>

    struct Transfer {

        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:transfer:list", 0x00, 0x14};
        static constexpr const char *const elt_names[] = {
            "handle",
            "delivery-id",
            "delivery-tag",
            "message-format",
            "settled",
            "more",
            "rcv-settle-mode",
            "state",
            "resume",
            "aborted",
            "batchable",
        };

        Handle handle{};
        std::optional<DeliveryNumber> delivery_id{};
        std::optional<DeliveryTag> delivery_tag{};
        std::optional<MessageFormat> message_format{};
        std::optional<boolean_t> settled{};
        std::optional<boolean_t> more{};
        std::optional<ReceiverSettleMode> rcv_settle_mode{};
        std::optional<DeliveryState> state{};
        std::optional<boolean_t> resume{};
        std::optional<boolean_t> aborted{};
        std::optional<boolean_t> batchable{};
        auto operator<=>(const Transfer &) const = default;
    };

    // 2.7.6 Disposition
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-disposition
    // <type name="disposition" class="composite" source="list" provides="frame">
    //     <descriptor name="amqp:disposition:list" code="0x00000000:0x00000015"/>
    //     <field name="role" type="role" mandatory="true"/>
    //     <field name="first" type="delivery-number" mandatory="true"/>
    //     <field name="last" type="delivery-number"/>
    //     <field name="settled" type="boolean" default="false"/>
    //     <field name="state" type="*" requires="delivery-state"/>
    //     <field name="batchable" type="boolean" default="false"/>
    // </type>
    struct Disposition {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:disposition:list", 0x00, 0x15};
        static constexpr const char *const elt_names[] = {
            "role",
            "first",
            "last",
            "settled",
            "state",
            "batchable",
        };
        Role role{};
        DeliveryNumber first{};
        std::optional<DeliveryNumber> last{};
        std::optional<boolean_t> settled{};
        std::optional<DeliveryState> state{};
        std::optional<boolean_t> batchable{};
        auto operator<=>(const Disposition &) const = default;
    };

    // 2.7.7 Detach
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-detach
    // <type name="detach" class="composite" source="list" provides="frame">
    //     <descriptor name="amqp:detach:list" code="0x00000000:0x00000016"/>
    //     <field name="handle" type="handle" mandatory="true"/>
    //     <field name="closed" type="boolean" default="false"/>
    //     <field name="error" type="error"/>
    // </type>
    struct Detach {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:detatch:list", 0x00, 0x16};
        static constexpr const char *const elt_names[] = {
            "handle",
            "closed",
            "error",
        };
        Handle handle{};
        std::optional<boolean_t> closed{};
        std::optional<Error> error{};
        auto operator<=>(const Detach &) const = default;
    };

    // 2.7.8 End
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-end
    // <type name="end" class="composite" source="list" provides="frame">
    //     <descriptor name="amqp:end:list" code="0x00000000:0x00000017"/>
    //     <field name="error" type="error"/>
    // </type>
    struct End {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:end:list", 0x00, 0x17};
        static constexpr const char *const elt_names[] = {"error"};
        std::optional<Error> error{};
        auto operator<=>(const End &) const = default;
    };

    // 2.7.9 Close
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-close
    // <type name="close" class="composite" source="list" provides="frame">
    //     <descriptor name="amqp:close:list" code="0x00000000:0x00000018"/>
    //     <field name="error" type="error"/>
    // </type>
    struct Close {
        static constexpr DescriptorDefinition amqp_descriptor = {"amqp:close:list", 0x00, 0x18};
        static constexpr const char *const elt_names[] = {"error"};
        std::optional<Error> error{};
        auto operator<=>(const Close &) const = default;
    };

    // AMQP Frame
    // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#doc-idp134416
    using Performative = std::variant<Open, Begin, Attach, Flow, Transfer, Disposition, Detach, End, Close>;
    using AmqpPayload = std::vector<std::byte>;
    struct AmqpMessage {
        ChannelId channel;
        std::optional<Performative> performative;
        AmqpPayload payload;

        friend void to_json(nlohmann::json &j, const AmqpMessage &msg) {
            j["channel"] = msg.channel;
            if (msg.performative) {
                std::visit(
                    [&j](auto &&p) {
                        j["performative"] = p;
                    },
                    msg.performative.value()
                );
            }
            if (!msg.payload.empty()) {
                j["payload-full"] = msg.payload;
                j["payload"] = fmt::format("{} bytes", msg.payload.size());
            }
        }
    };

} // namespace amqp_asio::messages
