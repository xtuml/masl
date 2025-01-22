/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include "data.hh"
#include "utils.hh"
#include <gtest/gtest.h>

#include "../messages.hh"
#include "../decoder.hh"
#include "../encoder.hh"
#include "amqp_asio/types.hh"

using namespace amqp_asio::types;
using namespace amqp_asio::messages;
using namespace std::literals;

template <typename T>
void check_value(const T &value, std::string hex) {
    auto encoded = vector_from_hex(std::move(hex));

    Encoder encoder;
    encoder.encode(value);
    auto encoded_result = encoder.buffer();
    auto decoded_result = decode<T>(encoded);

    EXPECT_EQ(encoded_result, encoded);
    EXPECT_EQ(decoded_result, value);
}

class CodecData : public ::testing::TestWithParam<CodecTestParams<any_value_t>> {};

TEST_P(CodecData, encode_data) {
    auto [hex, value, name, descriptors] = GetParam();

    check_value(any_t(descriptors, value), hex);
}

TEST_P(CodecData, encode_explicit) {
    auto [hex, value, name, descriptors] = GetParam();

    if (descriptors.empty()) {
        std::visit(
            [h = hex](auto &v) {
                check_value(v, h);
            },
            value
        );
    }
}

INSTANTIATE_TEST_SUITE_P(normalised, CodecData, testing::ValuesIn(normalisedTestData), test_name);

TEST(CodecEnum, boolean) {
    enum class AnEnum : boolean_t { zero, one };

    check_value(AnEnum::one, "41");
}

TEST(CodecEnum, uint) {
    enum class AnEnum : uint_t { zero = 0, one = 1 };

    check_value(AnEnum::one, "5201");
}

TEST(CodecEnum, array) {
    enum class AnEnum : uint_t { zero = 0, one = 1 };

    check_value(std::vector<AnEnum>{AnEnum::zero, AnEnum::one, AnEnum::zero}, "f0 00000008 00000003 52 00 01 00");
}

TEST(CodecOptional, unset) {
    check_value(std::optional<uint_t>{}, "40");
}

TEST(CodecOptional, value) {
    check_value(std::optional<uint_t>{1}, "52 01");
}

TEST(CodecAggregate, full) {
    check_value(
        Book{
            .title = "AMQP for & by Dummies",
            .authors = {"Rob J. Godfrey", "Rafael H. Schloming"},
            .isbn = "978-1-56619-909-4"
        },
        "(00 80 00000003 00000002 d0) 0000005b 00000003"
        "   ( a1 15 414d515020666f7220262062792044756d6d696573) "
        "   (f0 00000028 00000002 a1"
        "       (0e 'Rob J. Godfrey') "
        "       (13 'Rafael H. Schloming'))"
        "    ( a1 11 '978-1-56619-909-4')"
    );
}

TEST(CodecAggregate, trailingOptionalOmitted) {
    check_value(
        Book{.title = "AMQP for & by Dummies", .authors = {"Rob J. Godfrey", "Rafael H. Schloming"}},
        "(00 80 00000003 00000002 d0) 00000048 00000002"
        "   ( a1 15 'AMQP for & by Dummies') "
        "   (f0 00000028 00000002 a1"
        "       (0e 'Rob J. Godfrey') "
        "       (13 'Rafael H. Schloming'))"
    );
}

TEST(CodecAggregate, trailingMultipleOmitted) {
    check_value(
        Book{
            .title = "AMQP for & by Dummies",
        },
        "(00 80 00000003 00000002 d0) 0000001b 00000001"
        "   ( a1 15 'AMQP for & by Dummies') "
    );
}

TEST(CodecAggregate, trailingMultipleSingle) {
    check_value(
        Book{.title = "AMQP for & by Dummies", .authors = {"Rob J. Godfrey"}},
        "(00 80 00000003 00000002 d0) 0000002b 00000002"
        "   ( a1 15 'AMQP for & by Dummies') "
        "   ( a1 0e 'Rob J. Godfrey') "
    );
}

TEST(CodecAggregate, empty) {
    check_value(Empty{}, "00 80 00000003 00000004 45");
}

TEST(CodecAggregate, array_empty) {
    check_value(array_t<Empty>{{}, {}}, "f0 0000000f 00000002 00 80 00000003 00000004 45");
}

TEST(CodecAggregate, array) {
    check_value(
        array_t<Simple>{{}, {}},
        "f0 00000027 00000002"
        " 00 80 00000003 00000001 d0 "
        "       00000008 00000002 a3 00 5400"
        "       00000008 00000002 a3 00 5400"
    );
}

TEST(CodecAggregate, noDescriptor) {
    check_value(
        NoDescAggregate{symbol_t("Hello World!"), int_t(4)}, "d0 00000014 00000002  a3 0c 'Hello World!' 54 04"
    );
}

TEST(CodecDescriptorWrapped, map) {
    check_value(
        WrappedMap(map_t<uint_t, uint_t>{{{1, 2}, {3, 4}}}),
        R"(
                (00 80 00000003 00000003 d1) 0000000c 00000004 5201 5202 5203 5204
           )"
    );
}

TEST(CodecDescriptorWrapped, array_map) {
    check_value(
        array_t<WrappedMap>{
            WrappedMap(map_t<uint_t, uint_t>{{{1, 2}, {3, 4}}}), WrappedMap(map_t<uint_t, uint_t>{{{1, 2}, {3, 4}}})
        },
        R"(
                f0 0000002f 00000002
                (00 80 00000003 00000003 d1)
                        0000000c 00000004 5201 5202 5203 5204
                        0000000c 00000004 5201 5202 5203 5204
           )"
    );
}

TEST(CodecDescriptorWrapped, typedMap) {
    check_value(
        map_t<symbol_t, any_t>{{symbol_t("hello"), uint_t(2)}, {symbol_t("world"), uint_t(4)}},
        R"(
                d1 00000016 00000004 (a3 05 'hello') 5202 (a3 05 'world') 5204
           )"
    );
}

/*
<type name="open" class="composite" source="list" provides="frame">
    <descriptor name="amqp:open:list" code="0x00000000:0x00000010"/>
    <field name="container-id" type="string" mandatory="true"/>
    <field name="hostname" type="string"/>
    <field name="max-frame-size" type="uint" default="4294967295"/>
    <field name="channel-max" type="ushort" default="65535"/>
    <field name="idle-time-out" type="milliseconds"/>
    <field name="outgoing-locales" type="ietf-language-tag" multiple="true"/>
    <field name="incoming-locales" type="ietf-language-tag" multiple="true"/>
    <field name="offered-capabilities" type="symbol" multiple="true"/>
    <field name="desired-capabilities" type="symbol" multiple="true"/>
    <field name="properties" type="fields"/>
</type>
*/
TEST(CodecTransport, openEmpty) {
    check_value(Open{}, "005310 d0 00000006 00000001 a100");
}

TEST(CodecTransport, openFull) {
    check_value(
        Open{
            .container_id = "container id",
            .hostname = "hostname",
            .max_frame_size = 1,
            .channel_max = 2,
            .idle_time_out = 3ms,
            .outgoing_locales = {symbol_t{"loc 1"}, symbol_t{"loc 2"}},
            .incoming_locales = {symbol_t{"loc 3"}, symbol_t{"loc 4"}},
            .offered_capabilities = {symbol_t{"cap 1"}, symbol_t{"cap 2"}},
            .desired_capabilities = {symbol_t{"cap 3"}, symbol_t{"cap 4"}},
            .properties = Fields{{symbol_t{"key 1"}, string_t{"value 1"}}, {symbol_t{"key 2"}, uint_t{3}}}

        },
        R"(
        005310 d0 0000009d 0000000a
        a1 0c 'container id'
        a1 08 'hostname'
        52 01
        60 00 02
        52 03
        f0 00000011 00000002 a3
            05 'loc 1'
            05 'loc 2'
        f0 00000011 00000002 a3
            05 'loc 3'
            05 'loc 4'
        f0 00000011 00000002 a3
            05 'cap 1'
            05 'cap 2'
        f0 00000011 00000002 a3
            05 'cap 3'
            05 'cap 4'
        d1 0000001d 00000004
            a3 05 'key 1'
            a1 07 'value 1'
            a3 05 'key 2'
            52 03
        )"
    );
}

/*
<type name="begin" class="composite" source="list" provides="frame">
    <descriptor name="amqp:begin:list" code="0x00000000:0x00000011"/>
    <field name="remote-channel" type="ushort"/>
    <field name="next-outgoing-id" type="transfer-number" mandatory="true"/>
    <field name="incoming-window" type="uint" mandatory="true"/>
    <field name="outgoing-window" type="uint" mandatory="true"/>
    <field name="handle-max" type="handle" default="4294967295"/>
    <field name="offered-capabilities" type="symbol" multiple="true"/>
    <field name="desired-capabilities" type="symbol" multiple="true"/>
    <field name="properties" type="fields"/>
</type>
*/
TEST(CodecTransport, beginEmpty) {
    check_value(Begin{}, "005311 d0 00000008 00000004 40 43 43 43");
}

TEST(CodecTransport, beginFull) {
    check_value(
        Begin{
            .remote_channel = 1,
            .next_outgoing_id = 2,
            .incoming_window = 3,
            .outgoing_window = 4,
            .handle_max = 5,
            .offered_capabilities = {symbol_t{"cap 1"}, symbol_t{"cap 2"}},
            .desired_capabilities = {symbol_t{"cap 3"}, symbol_t{"cap 4"}},
            .properties = Fields{{symbol_t{"key 1"}, string_t{"value 1"}}, {symbol_t{"key 2"}, uint_t{3}}}
        },
        R"(
        005311 d0 0000005d 00000008
        60 00 01
        52 02
        52 03
        52 04
        52 05
        f0 00000011 00000002 a3
            05 'cap 1'
            05 'cap 2'
        f0 00000011 00000002 a3
            05 'cap 3'
            05 'cap 4'
        d1 0000001d 00000004
            a3 05 'key 1'
            a1 07 'value 1'
            a3 05 'key 2'
            52 03
        )"
    );
}

/*
<type name="attach" class="composite" source="list" provides="frame">
    <descriptor name="amqp:attach:list" code="0x00000000:0x00000012"/>
    <field name="name" type="string" mandatory="true"/>
    <field name="handle" type="handle" mandatory="true"/>
    <field name="role" type="role" mandatory="true"/>
    <field name="snd-settle-mode" type="sender-settle-mode" default="mixed"/>
    <field name="rcv-settle-mode" type="receiver-settle-mode" default="first"/>
    <field name="source" type="*" requires="source"/>
    <field name="target" type="*" requires="target"/>
    <field name="unsettled" type="map"/>
    <field name="incomplete-unsettled" type="boolean" default="false"/>
    <field name="initial-delivery-count" type="sequence-no"/>
    <field name="max-message-size" type="ulong"/>
    <field name="offered-capabilities" type="symbol" multiple="true"/>
    <field name="desired-capabilities" type="symbol" multiple="true"/>
    <field name="properties" type="fields"/>
</type>
*/
TEST(CodecTransport, attachEmpty) {
    check_value(Attach{}, "005312 d0 00000008 00000003 a1 00 43 42");
}

TEST(CodecTransport, attachFull) {
    check_value(
        Attach{
            .name = "name",
            .handle = 1,
            .role = Role::receiver,
            .snd_settle_mode = SenderSettleMode::mixed,
            .rcv_settle_mode = ReceiverSettleMode::second,
            .source = Source{},
            .target = Target{},
            .unsettled =
                map_t<DeliveryTag, DeliveryState>{
                    {DeliveryTag{std::byte{0}, std::byte{1}, std::byte{2}, std::byte{3}}, Declared{}}
                },
            .incomplete_unsettled = true,
            .initial_delivery_count = 2,
            .max_message_size = 3,
            .offered_capabilities = {symbol_t{"cap 1"}, symbol_t{"cap 2"}},
            .desired_capabilities = {symbol_t{"cap 3"}, symbol_t{"cap 4"}},
            .properties = Fields{{symbol_t{"key 1"}, string_t{"value 1"}}, {symbol_t{"key 2"}, uint_t{3}}}

        },
        R"(
        005312 d0 00000099 0000000e
        a1 04 'name'
        52 01
        41
        50 02
        50 01
        005328 d0 00000004 00000000 
        005329 d0 00000004 00000000 
        d1 00000018 00000002
            a0 04 00010203
            005333 d0 00000006 00000001 a0 00 
        41
        52 02
        53 03
        f0 00000011 00000002 a3
            05 'cap 1'
            05 'cap 2'
        f0 00000011 00000002 a3
            05 'cap 3'
            05 'cap 4'
        d1 0000001d 00000004
            a3 05 'key 1'
            a1 07 'value 1'
            a3 05 'key 2'
            52 03

        )"
    );
}

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
TEST(CodecTransport, flowEmpty) {
    check_value(Flow{}, "005313 d0 00000008 00000004 40 43 43 43");
}

TEST(CodecTransport, flowFull) {
    check_value(
        Flow{
            .next_incoming_id = 1,
            .incoming_window = 2,
            .next_outgoing_id = 3,
            .outgoing_window = 4,
            .handle = 5,
            .delivery_count = 6,
            .link_credit = 7,
            .available = 8,
            .drain = true,
            .echo = true,
            .properties = Fields{{symbol_t{"key 1"}, string_t{"value 1"}}, {symbol_t{"key 2"}, uint_t{3}}}
        },
        R"(005313 d0 00000038 0000000b
            52 01
            52 02
            52 03
            52 04
            52 05
            52 06
            52 07
            52 08
            41
            41
            d1 0000001d 00000004
                a3 05 'key 1'
                a1 07 'value 1'
                a3 05 'key 2'
                52 03
            )"
    );
}

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
TEST(CodecTransport, transferEmpty) {
    check_value(Transfer{}, "005314 d0 00000005 00000001 43");
}
TEST(CodecTransport, transferFull) {
    check_value(
        Transfer{
            .handle = 1,
            .delivery_id = 2,
            .delivery_tag = DeliveryTag{std::byte{0}, std::byte{1}, std::byte{2}, std::byte{3}},
            .message_format = 3,
            .settled = true,
            .more = true,
            .rcv_settle_mode = ReceiverSettleMode::second,
            .state = Declared{},
            .resume = true,
            .aborted = true,
            .batchable = true
        },
        R"(005314 d0 00000025 0000000b
        52 01
        52 02
        a0 04 00010203
        52 03
        41
        41
        50 01
        005333 d0 00000006 00000001 a0 00
        41
        41
        41
        )"
    );
}
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
TEST(CodecTransport, dispositionEmpty) {
    check_value(Disposition{}, "005315 d0 00000006 00000002 42 43");
}
TEST(CodecTransport, dispositionFull) {
    check_value(
        Disposition{
            .role = Role::receiver,
            .first = 2,
            .last = 3,
            .settled = true,
            .state = Declared{},
            .batchable = true,
        },
        R"(005315 d0 00000019 00000006
        41
        52 02
        52 03
        41
        005333 d0 00000006 00000001 a0 00
        41
    )"
    );
}

// 2.7.7 Detach
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-detach
// <type name="detach" class="composite" source="list" provides="frame">
//     <descriptor name="amqp:detach:list" code="0x00000000:0x00000016"/>
//     <field name="handle" type="handle" mandatory="true"/>
//     <field name="closed" type="boolean" default="false"/>
//     <field name="error" type="error"/>
// </type>
TEST(CodecTransport, detachEmpty) {
    check_value(Detach{}, "005316 d0 00000005 00000001 43");
}
TEST(CodecTransport, detachFull) {
    check_value(
        Detach{
            .handle = 1,
            .closed = true,
            .error = Error{},
        },
        R"(005316 d0 00000015 00000003
            52 01
            41
            00531d d0 00000006 00000001
                a3 00
        )"
    );
}
// 2.7.8 End
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-end
// <type name="end" class="composite" source="list" provides="frame">
//     <descriptor name="amqp:end:list" code="0x00000000:0x00000017"/>
//     <field name="error" type="error"/>
// </type>
TEST(CodecTransport, endEmpty) {
    check_value(End{}, "005317 d0 00000004 00000000");
}
TEST(CodecTransport, endFull) {
    check_value(
        End{.error = Error{}},
        R"(005317 d0 00000012 00000001
            00531d d0 00000006 00000001
                a3 00
    )"
    );
}
// 2.7.9 Close
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-close
// <type name="close" class="composite" source="list" provides="frame">
//     <descriptor name="amqp:close:list" code="0x00000000:0x00000018"/>
//     <field name="error" type="error"/>
// </type>
TEST(CodecTransport, closeEmpty) {
    check_value(Close{}, "005318 d0 00000004 00000000");
}
TEST(CodecTransport, closeFull) {
    check_value(
        Close{
            .error = Error{},
        },
        R"(005318 d0 00000012 00000001
            00531d d0 00000006 00000001
                a3 00
    )"
    );
}

// 2.8.14 Error
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#type-error
// <type name="error" class="composite" source="list">
//     <descriptor name="amqp:error:list" code="0x00000000:0x0000001d"/>
//     <field name="condition" type="symbol" requires="error-condition" mandatory="true"/>
//     <field name="description" type="string"/>
//     <field name="info" type="fields"/>
// </type>
TEST(CodecTransport, errorEmpty) {
    check_value(Error{}, "00531d d0 00000006 00000001 a3 00");
}
TEST(CodecTransport, errorFull) {
    check_value(
        Error{
            .condition = AmqpError::internal_error,
            .description = "desc",
            .info = Fields{{symbol_t{"key 1"}, string_t{"value 1"}}, {symbol_t{"key 2"}, uint_t{3}}},
        },
        R"(00531d d0 00000041 00000003
            a3 13 'amqp:internal-error'
            a1 04 'desc'
            d1 0000001d 00000004
                a3 05 'key 1'
                a1 07 'value 1'
                a3 05 'key 2'
                52 03
    )"
    );
}

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
TEST(CodecMessage, headerEmpty) {
    check_value(Header{}, "005370 d0 00000004 00000000");
}

TEST(CodecMessage, headerFull) {
    check_value(
        Header{
            .durable = true,
            .priority = 2,
            .ttl = 3ms,
            .first_acquirer = true,
            .delivery_count = 4,
        },
        R"(005370 d0 0000000c 00000005 
            41       
            50 02
            52 03
            41
            52 04
    )"
    );
}

// 3.2.2 Delivery Annotations
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-delivery-annotations
// <type name="delivery-annotations" class="restricted" source="annotations" provides="section">
//     <descriptor name="amqp:delivery-annotations:map" code="0x00000000:0x00000071"/>
// </type>
TEST(CodecMessage, deliveryAnnotationsEmpty) {
    check_value(DeliveryAnnotations{}, "005371 d1 00000004 00000000");
}

TEST(CodecMessage, deliveryAnnotationsFull) {
    check_value(
        DeliveryAnnotations{{
            {ulong_t{1}, uint_t{2}},
            {symbol_t{"hello"}, string_t{"world"}},
        }},
        R"(005371 d1 00000016 00000004
            53 01
            52 02
            a3 05 'hello'
            a1 05 'world'
        )"
    );
}

// 3.2.3 Message Annotations
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-message-annotations
// <type name="message-annotations" class="restricted" source="annotations" provides="section">
//     <descriptor name="amqp:message-annotations:map" code="0x00000000:0x00000072"/>
// </type>
TEST(CodecMessage, messageAnnotationsEmpty) {
    check_value(MessageAnnotations{}, "005372 d1 00000004 00000000");
}

TEST(CodecMessage, messageAnnotationsFull) {
    check_value(
        MessageAnnotations{{
            {ulong_t{1}, uint_t{2}},
            {symbol_t{"hello"}, string_t{"world"}},
        }},
        R"(005372 d1 00000016 00000004
            53 01
            52 02
            a3 05 'hello'
            a1 05 'world'
        )"
    );
}

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
TEST(CodecMessage, propertiesEmpty) {
    check_value(Properties{}, "005373 d0 00000004 00000000");
}

TEST(CodecMessage, propertiesFull) {
    check_value(
        Properties{
            .message_id = ulong_t{1},
            .user_id = binary_t{std::byte{0}, std::byte{1}, std::byte{2}, std::byte{3}},
            .to = "to",
            .subject = "subject",
            .reply_to = "reply to",
            .correlation_id = ulong_t{2},
            .content_type = symbol_t{"content type"},
            .content_encoding = symbol_t{"content encoding"},
            .absolute_expiry_time = timestamp_t{3ms},
            .creation_time = timestamp_t{4ms},
            .group_id = "group",
            .group_sequence = 5,
            .reply_to_group_id = "rtgi",

        },
        R"(005373 d0 00000066 0000000d
            53 01
            a0 04 00010203
            a1 02 'to'
            a1 07 'subject'
            a1 08 'reply to'
            53 02
            a3 0c 'content type'
            a3 10 'content encoding'
            83 0000000000000003
            83 0000000000000004
            a1 05 'group'
            52 05
            a1 04 'rtgi'
        )"
    );
}

// 3.2.5 Application Properties
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-application-properties
// <type name="application-properties" class="restricted" source="map" provides="section">
//     <descriptor name="amqp:application-properties:map" code="0x00000000:0x00000074"/>
// </type>
TEST(CodecMessage, applicationPropertiesEmpty) {
    check_value(ApplicationProperties{}, "005374 d1 00000004 00000000");
}

TEST(CodecMessage, applicationPropertiesFull) {
    check_value(
        ApplicationProperties{{
            {"aaa", uint_t{2}},
            {"bbb", string_t{"ccc"}},
        }},
        R"(005374 d1 00000015 00000004
            a1 03 'aaa'
            52 02
            a1 03 'bbb'
            a1 03 'ccc'
        )"
    );
}

// 3.2.6 Data
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-data
// <type name="data" class="restricted" source="binary" provides="section">
//   <descriptor name="amqp:data:binary" code="0x00000000:0x00000075"/>
// </type>
TEST(CodecMessage, data) {
    check_value(Data{std::byte{0}, std::byte{1}, std::byte{2}, std::byte{3}}, R"(005375 a0 04 00010203)");
}

// 3.2.7 Ampq Sequence
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-amqp-sequence
// <type name="amqp-sequence" class="restricted" source="list" provides="section">
//     <descriptor name="amqp:amqp-sequence:list" code="0x00000000:0x00000076"/>
// </type>
TEST(CodecMessage, AMQPSequence) {
    check_value(AMQPSequence{true, uint_t{1}}, R"(005376 d0 00000007 00000002 41 52 01)");
}

// 3.2.7 Ampq Value
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-amqp-value
// <type name="amqp-value" class="restricted" source="*" provides="section">
//     <descriptor name="amqp:amqp-value:*" code="0x00000000:0x00000077"/>
// </type>
TEST(CodecMessage, AMQPValue) {
    check_value(AMQPValue{true}, R"(005377 41)");
}

// 3.2.9 Footer
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-amqp-value
// <type name="footer" class="restricted" source="annotations" provides="section">
//     <descriptor name="amqp:footer:map" code="0x00000000:0x00000078"/>
// </type>
TEST(CodecMessage, footerEmpty) {
    check_value(Footer{}, "005378 d1 00000004 00000000");
}

TEST(CodecMessage, footerFull) {
    check_value(
        Footer{{
            {ulong_t{1}, uint_t{2}},
            {symbol_t{"hello"}, string_t{"world"}},
        }},
        R"(005378 d1 00000016 00000004
            53 01
            52 02
            a3 05 'hello'
            a1 05 'world'
        )"
    );
}

TEST(CodecMessage, messageDataPayload) {
    check_value(
        Message{
            .header = {},
            .delivery_annotations = {},
            .message_annotations = {},
            .properties = {},
            .application_properties = {},
            .data = std::vector<Data>{Data{std::byte{0}, std::byte{1}, std::byte{2}, std::byte{3}}},
            .footer = {}
        },
        R"(
            005375 a0 04 00010203
        )"
    );
}
TEST(CodecMessage, messageDataMultiPayload) {
    check_value(
        Message{
            .header = {},
            .delivery_annotations = {},
            .message_annotations = {},
            .properties = {},
            .application_properties = {},
            .data =
                std::vector<Data>{
                    Data{std::byte{0}, std::byte{1}, std::byte{2}, std::byte{3}},
                    Data{std::byte{4}, std::byte{5}, std::byte{6}, std::byte{7}},
                },
            .footer = {}
        },
        R"(
            005375 a0 04 00010203
            005375 a0 04 04050607
        )"
    );
}

TEST(CodecMessage, messageSequencePayload) {
    check_value(
        Message{
            .header = {},
            .delivery_annotations = {},
            .message_annotations = {},
            .properties = {},
            .application_properties = {},
            .data =
                std::vector<AMQPSequence>{
                    AMQPSequence{true, uint_t{1}},
                },
            .footer = {}
        },
        R"(
            005376 d0 00000007 00000002 41 52 01
        )"
    );
}
TEST(CodecMessage, messageSequenceMultiPayload) {
    check_value(
        Message{
            .header = {},
            .delivery_annotations = {},
            .message_annotations = {},
            .properties = {},
            .application_properties = {},
            .data =
                std::vector<AMQPSequence>{
                    AMQPSequence{true, uint_t{1}},
                    AMQPSequence{false, uint_t{2}},
                },
            .footer = {}
        },
        R"(
            005376 d0 00000007 00000002 41 52 01
            005376 d0 00000007 00000002 42 52 02
        )"
    );
}
TEST(CodecMessage, messageValuePayload) {
    check_value(
        Message{
            .header = {},
            .delivery_annotations = {},
            .message_annotations = {},
            .properties = {},
            .application_properties = {},
            .data = AMQPValue{true},
            .footer = {}
        },
        R"(
            005377 41
        )"
    );
}
TEST(CodecMessage, messageFull) {
    check_value(
        Message{
            .header = Header{},
            .delivery_annotations = DeliveryAnnotations{},
            .message_annotations = MessageAnnotations{},
            .properties = Properties{},
            .application_properties = ApplicationProperties{},
            .data = AMQPValue{true},
            .footer = Footer{}
        },
        R"(
            005370 d0 00000004 00000000
            005371 d1 00000004 00000000
            005372 d1 00000004 00000000
            005373 d0 00000004 00000000
            005374 d1 00000004 00000000
            005377 41
            005378 d1 00000004 00000000
        )"
    );
}

// 3.4.1 Received
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-received
// <type name="received" class="composite" source="list" provides="delivery-state">
//     <descriptor name="amqp:received:list" code="0x00000000:0x00000023"/>
//     <field name="section-number" type="uint" mandatory="true"/>
//     <field name="section-offset" type="ulong" mandatory="true"/>
// </type>
TEST(CodecMessage, receivedEmpty) {
    check_value(Received{}, "005323 d0 00000006 00000002 43 44");
}

TEST(CodecMessage, receivedFull) {
    check_value(
        Received{
            .section_number = 1,
            .section_offset = 2,
        },
        "005323 d0 00000008 00000002 52 01 53 02"
    );
}

// 3.4.2 Accepted
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-rejected
// <type name="accepted" class="composite" source="list" provides="delivery-state, outcome">
//     <descriptor name="amqp:accepted:list" code="0x00000000:0x00000024"/>
// </type>
TEST(CodecMessage, accepted) {
    check_value(Accepted{}, "005324 45");
}

// 3.4.3 Rejected
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-accepted
// <type name="rejected" class="composite" source="list" provides="delivery-state, outcome">
//     <descriptor name="amqp:rejected:list" code="0x00000000:0x00000025"/>
//     <field name="error" type="error"/>
// </type>
TEST(CodecMessage, rejectedEmpty) {
    check_value(Rejected{}, "005325 d0 00000004 00000000");
}
TEST(CodecMessage, rejectedFull) {
    check_value(Rejected{.error = Error{}},
     R"(005325 d0 00000012 00000001 00531d d0 00000006 00000001 a3 00)");
}

// 3.4.4 Released
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-released
// <type name="released" class="composite" source="list" provides="delivery-state, outcome">
//     <descriptor name="amqp:released:list" code="0x00000000:0x00000026"/>
// </type>
TEST(CodecMessage, released) {
    check_value(Released{}, "005326 45");
}

// 3.4.5 Modified
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-modified
// <type name="modified" class="composite" source="list" provides="delivery-state, outcome">
//     <descriptor name="amqp:modified:list" code="0x00000000:0x00000027"/>
//     <field name="delivery-failed" type="boolean"/>
//     <field name="undeliverable-here" type="boolean"/>
//     <field name="message-annotations" type="fields"/>
// </type>
TEST(CodecMessage, modifiedEmpty) {
    check_value(Modified{}, "005327 d0 00000004 00000000");
}
TEST(CodecMessage, modifiedFull) {
    check_value(
        Modified{
            .delivery_failed = true,
            .undeliverable_here = false,
            .message_annotations = Fields{{symbol_t{"key 1"}, string_t{"value 1"}}, {symbol_t{"key 2"}, uint_t{3}}},
        },
        R"(005327 d0 00000028 00000003
        41
        42
        d1 0000001d 00000004
            a3 05 'key 1'
            a1 07 'value 1'
            a3 05 'key 2'
            52 03
    )"
    );
}

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
TEST(CodecMessage, sourceEmpty) {
    check_value(Source{}, R"(005328 d0 00000004 00000000)");
}

TEST(CodecMessage, sourceFull) {
    check_value(
        Source{
            .address = "address",
            .durable = TerminusDurablility::unsettled_state,
            .expiry_policy = TerminusExpiryPolicy::link_detach,
            .timeout = 2s,
            .dynamic = true,
            .dynamic_node_properties = Fields{},
            .distribution_mode = StdDistMode::move,
            .filter = FilterSet{},
            .default_outcome = Accepted{},
            .outcomes = {symbol_t{"outcome1"}, symbol_t{"outcome2"}},
            .capabilities = {symbol_t("cap1"), symbol_t{"cap2"}}
        },
        R"(005328 d0 0000006b 0000000b
            a1 07 'address'
            52 02
            a3 0b 'link-detach'
            52 02
            41
            d1 00000004 00000000
            a3 04 'move'
            d1 00000004 00000000
            005324 45
            f0 00000017 00000002 a3
                08 'outcome1'
                08 'outcome2'
            f0 0000000f 00000002 a3
                04 'cap1'
                04 'cap2'        
    )"
    );
}
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
TEST(CodecMessage, targetEmpty) {
    check_value(Target{}, R"(005329 d0 00000004 00000000)");
}

TEST(CodecMessage, targetFull) {
    check_value(
        Target{
            .address = "address",
            .durable = TerminusDurablility::unsettled_state,
            .expiry_policy = TerminusExpiryPolicy::link_detach,
            .timeout = 2s,
            .dynamic = true,
            .dynamic_node_properties = Fields{},
            .capabilities = {symbol_t("cap1"), symbol_t{"cap2"}}
        },
        R"(005329 d0 0000003c 00000007
        a1 07 'address'
        52 02
        a3 0b 'link-detach'
        52 02
        41
        d1 00000004 00000000
        f0 0000000f 00000002 a3
            04 'cap1'
            04 'cap2'            
    )"
    );
}

// 3.5.10 Delete On Close
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-delete-on-close
// <type name="delete-on-close" class="composite" source="list" provides="lifetime-policy">
//     <descriptor name="amqp:delete-on-close:list" code="0x00000000:0x0000002b"/>
// </type>
TEST(CodecMessage, deleteOnClose) {
    check_value(DeleteOnClose{}, "00532b 45");
}

// 3.5.11 Delete On No Links
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-delete-on-no-links
// <type name="delete-on-no-links" class="composite" source="list" provides="lifetime-policy">
//     <descriptor name="amqp:delete-on-no-links:list" code="0x00000000:0x0000002c"/>
// </type>
TEST(CodecMessage, deleteOnNoLinks) {
    check_value(DeleteOnNoLinks{}, "00532c 45");
}

// 3.5.12 Delete On No Messages
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-delete-on-no-messages
// <type name="delete-on-no-messages" class="composite" source="list" provides="lifetime-policy">
//     <descriptor name="amqp:delete-on-no-messages:list" code="0x00000000:0x0000002d"/>
// </type>
TEST(CodecMessage, deleteOnNoMessages) {
    check_value(DeleteOnNoMessages{}, "00532d 45");
}

// 3.5.13 Delete On No Link Or Messages
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#type-delete-on-no-links-or-messasges
// <type name="delete-on-no-links-or-messages" class="composite" source="list" provides="lifetime-policy">
//     <descriptor name="amqp:delete-on-no-links-or-messages:list" code="0x00000000:0x0000002e"/>
// </type>
TEST(CodecMessage, deleteOnNoLinksOrMessages) {
    check_value(DeleteOnNoLinksOrMessages{}, "00532e 45");
}

// 4.5.1 Coordinator
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transactions-v1.0-os.html#type-coordinator
// <type name="coordinator" class="composite" source="list" provides="target">
//     <descriptor name="amqp:coordinator:list" code="0x00000000:0x00000030"/>
//     <field name="capabilities" type="symbol" requires="txn-capability" multiple="true"/>
// </type>
TEST(CodecTransactions, coordinatorEmpty) {
    check_value(Coordinator{}, "005330 d0 00000004 00000000");
}

TEST(CodecTransactions, coordinatorFull) {
    check_value(
        Coordinator{
            .capabilities = {TxnCapability::local_transactions, TxnCapability::distributed_transactions},
        },
        R"(005330 d0 00000044 00000001
            f0 0000003b 00000002 a3
                17 'amqp:local-transactions'
                1d 'amqp:distributed-transactions'
        )"
    );
}

// 4.5.2 Declare
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transactions-v1.0-os.html#type-declare
// <type name="declare" class="composite" source="list">
//     <descriptor name="amqp:declare:list" code="0x00000000:0x00000031"/>
//     <field name="global-id" type="*" requires="global-tx-id"/>
// </type>
TEST(CodecTransactions, declareEmpty) {
    check_value(Declare{}, "005331 d0 00000004 00000000");
}

TEST(CodecTransactions, declareFull) {
    check_value(
        Declare{
            .global_id = uint_t{1},
        },
        R"(005331 d0 00000006 00000001
            52 01
        )"
    );
}

// 4.5.3 Discharge
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transactions-v1.0-os.html#type-discharge
// <type name="discharge" class="composite" source="list">
//     <descriptor name="amqp:discharge:list" code="0x00000000:0x00000032"/>
//     <field name="txn-id" type="*" requires="txn-id" mandatory="true"/>
//     <field name="fail" type="boolean"/>
// </type>
TEST(CodecTransactions, dischargeEmpty) {
    check_value(Discharge{}, "005332 d0 00000006 00000001 a0 00");
}

TEST(CodecTransactions, dischargeFull) {
    check_value(
        Discharge{
            .txn_id = TransactionId{std::byte{0}, std::byte{1}, std::byte{2}, std::byte{3}},
            .fail = true,
        },
        R"(005332 d0 0000000b 00000002
            a0 04 00010203
            41
        )"
    );
}

// 4.5.5 Declared
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transactions-v1.0-os.html#type-declared
// <type name="declared" class="composite" source="list" provides="delivery-state, outcome">
//     <descriptor name="amqp:declared:list" code="0x00000000:0x00000033"/>
//     <field name="txn-id" type="*" requires="txn-id" mandatory="true"/>
// </type>

TEST(CodecTransactions, declaredEmpty) {
    check_value(Declared{}, "005333 d0 00000006 00000001 a0 00");
}

TEST(CodecTransactions, declaredFull) {
    check_value(
        Declared{
            .txn_id = TransactionId{std::byte{0}, std::byte{1}, std::byte{2}, std::byte{3}},
        },
        R"(005333 d0 0000000a 00000001
            a0 04 00010203
        )"
    );
}

// 4.5.6 Transactional State
// http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transactions-v1.0-os.html#type-transactional-state
// <type name="transactional-state" class="composite" source="list" provides="delivery-state">
//     <descriptor name="amqp:transactional-state:list" code="0x00000000:0x00000034"/>
//     <field name="txn-id" type="*" mandatory="true" requires="txn-id"/>
//     <field name="outcome" type="*" requires="outcome"/>
// </type>
TEST(CodecTransactions, transactionalStateEmpty) {
    check_value(TransactionalState{}, "005334 d0 00000006 00000001 a0 00");
}

TEST(CodecTransactions, transactionalStateFull) {
    check_value(
        TransactionalState{
            .txn_id = TransactionId{std::byte{0}, std::byte{1}, std::byte{2}, std::byte{3}},
            .outcome = Accepted{},
        },
        R"(005334 d0 0000000e 00000002
            a0 04 00010203
            005324 45
        )"
    );
}
