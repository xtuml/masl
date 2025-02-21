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
#include "utils.hh"

using namespace amqp_asio::types;
using namespace std::literals;
namespace {
    std::tm tm{42, 17, 14, 10, 3, 124, 0, 0, 0, 0, nullptr};
    auto blob = R"( b0 1a b1 62 b6 03 8e f4 ed 91 88 18 8e dc 11 49
                    fa 1c ca 6f ab 26 38 88 39 37 83 2e 6b 29 e4 d9
                    4c ec a9 35 1a 2a 1c 39 01 ec 19 dc e1 f5 fc 31
                    79 a4 da 67 9b de 8c 46 b6 aa 01 14 24 51 61 90
                    80 39 ab 41 4a bf f3 74 2a 6c 98 f5 df 03 ca 2a
                    ad a0 25 a8 84 c4 ef 65 01 21 57 f4 db 86 1f 05
                    26 e2 60 e4 06 79 ef 23 e5 40 f9 3e 10 86 11 6b
                    e4 43 0d 63 56 6d 8b 0d 78 e2 de 5b 93 a5 2e 0e
                    cb a2 5a f5 41 4e 29 1a e8 4f e5 55 76 ef 44 33
                    c6 ac fd 5d d0 c8 e4 0a fd a3 2c 5a e2 8c 54 56
                    28 e3 56 94 ae 84 fd cf 98 df a5 33 3a 86 0e d7
                    07 76 fa 9e 39 07 fa 26 c0 97 72 c8 89 9c 63 03
                    3b 00 f3 9f f0 79 4e 8f c4 4f 32 72 a9 43 1c b8
                    44 fc 8a 3c 41 1d a3 ec 8a d9 1f 13 c3 9b 38 0d
                    8a a4 de 42 7e e3 62 ee 36 f4 4a 3b 38 6e 0b 00
                    30 12 1a be f8 ac 84 ae 5e 99 64 ac d6 9c 56 7e )"s;
    auto jaberwocky = R"(
'Twas brillig, and the slithy toves
      Did gyre and gimble in the wabe:
All mimsy were the borogoves,
      And the mome raths outgrabe.

"Beware the Jabberwock, my son!
      The jaws that bite, the claws that catch!
Beware the Jubjub bird, and shun
      The frumious Bandersnatch!"

He took his vorpal sword in hand;
      Long time the manxome foe he sought-
So rested he by the Tumtum tree
      And stood awhile in thought.

And, as in uffish thought he stood,
      The Jabberwock, with eyes of flame,
Came whiffling through the tulgey wood,
      And burbled as it came!

One, two! One, two! And through and through
      The vorpal blade went snicker-snack!
He left it dead, and with its head
      He went galumphing back.

"And hast thou slain the Jabberwock?
      Come to my arms, my beamish boy!
O frabjous day! Callooh! Callay!"
      He chortled in his joy.

'Twas brillig, and the slithy toves
      Did gyre and gimble in the wabe:
All mimsy were the borogoves,
      And the mome raths outgrabe.)"s;

    auto jabberwocky_hex = R"(
0a2754776173206272696c6c69672c20616e642074686520736c6974687920746f7665730a202020
202020446964206779726520616e642067696d626c6520696e2074686520776162653a0a416c6c20
6d696d737920776572652074686520626f726f676f7665732c0a202020202020416e642074686520
6d6f6d65207261746873206f757467726162652e0a0a2242657761726520746865204a6162626572
776f636b2c206d7920736f6e210a202020202020546865206a617773207468617420626974652c20
74686520636c6177732074686174206361746368210a42657761726520746865204a75626a756220
626972642c20616e64207368756e0a202020202020546865206672756d696f75732042616e646572
736e6174636821220a0a486520746f6f6b2068697320766f7270616c2073776f726420696e206861
6e643b0a2020202020204c6f6e672074696d6520746865206d616e786f6d6520666f652068652073
6f756768742d0a536f20726573746564206865206279207468652054756d74756d20747265650a20
2020202020416e642073746f6f6420617768696c6520696e2074686f756768742e0a0a416e642c20
617320696e207566666973682074686f756768742068652073746f6f642c0a202020202020546865
204a6162626572776f636b2c20776974682065796573206f6620666c616d652c0a43616d65207768
6966666c696e67207468726f756768207468652074756c67657920776f6f642c0a20202020202041
6e6420627572626c65642061732069742063616d65210a0a4f6e652c2074776f21204f6e652c2074
776f2120416e64207468726f75676820616e64207468726f7567680a20202020202054686520766f
7270616c20626c6164652077656e7420736e69636b65722d736e61636b210a4865206c6566742069
7420646561642c20616e6420776974682069747320686561640a20202020202048652077656e7420
67616c756d7068696e67206261636b2e0a0a22416e6420686173742074686f7520736c61696e2074
6865204a6162626572776f636b3f0a202020202020436f6d6520746f206d792061726d732c206d79
206265616d69736820626f79210a4f20667261626a6f757320646179212043616c6c6f6f68212043
616c6c617921220a20202020202048652063686f72746c656420696e20686973206a6f792e0a0a27
54776173206272696c6c69672c20616e642074686520736c6974687920746f7665730a2020202020
20446964206779726520616e642067696d626c6520696e2074686520776162653a0a416c6c206d69
6d737920776572652074686520626f726f676f7665732c0a202020202020416e6420746865206d6f
6d65207261746873206f757467726162652e
)"s;

    std::vector<CodecTestParams<any_value_t>> normalisedTestData = {
            {"40", null_t{}},

            {"41", true},
            {"42", false},

            {"5000", ubyte_t{0}},
            {"5001", ubyte_t{1}},
            {"507f", ubyte_t{std::numeric_limits<int8_t>::max()}},
            {"5080", ubyte_t{std::numeric_limits<int8_t>::max() + 1}},
            {"50ff", ubyte_t{std::numeric_limits<uint8_t>::max()}},

            {"600000", ushort_t{0}},
            {"600001", ushort_t{1}},
            {"607fff", ushort_t{std::numeric_limits<int16_t>::max()}},
            {"608000", ushort_t{std::numeric_limits<int16_t>::max() + 1}},
            {"60ffff", ushort_t{std::numeric_limits<uint16_t>::max()}},

            {"43", uint_t{0}},
            {"5201", uint_t{1}},
            {"527f", uint_t{127}},
            {"5280", uint_t{128}},
            {"52ff", uint_t{255}},
            {"707fffffff", uint_t{std::numeric_limits<int32_t>::max()}},
            {"7080000000", uint_t{uint32_t(std::numeric_limits<int32_t>::max()) + 1}},
            {"70ffffffff", uint_t{std::numeric_limits<uint32_t>::max()}},

            {"44", ulong_t{0}},
            {"5301", ulong_t{1}},
            {"537f", ulong_t{127}},
            {"5380", ulong_t{128}},
            {"53ff", ulong_t{255}},
            {"807fffffffffffffff", ulong_t{std::numeric_limits<int64_t>::max()}},
            {"808000000000000000", ulong_t{uint64_t(std::numeric_limits<int64_t>::max()) + 1}},
            {"80ffffffffffffffff", ulong_t{std::numeric_limits<uint64_t>::max()}},


            {"5100", byte_t{0}},
            {"5101", byte_t{1}},
            {"517f", byte_t{std::numeric_limits<int8_t>::max()}},
            {"5180", byte_t{std::numeric_limits<int8_t>::min()}},
            {"51ff", byte_t{-1}},

            {"610000", short_t{0}},
            {"610001", short_t{1}},
            {"617fff", short_t{std::numeric_limits<int16_t>::max()}},
            {"618000", short_t{std::numeric_limits<int16_t>::min()}},
            {"61ffff", short_t{-1}},

            {"5400", int_t{0}},
            {"5401", int_t{1}},
            {"547f", int_t{127}},
            {"5480", int_t{-128}},
            {"54ff", int_t{-1}},
            {"717fffffff", int_t{std::numeric_limits<int32_t>::max()}},
            {"7180000000", int_t{std::numeric_limits<int32_t>::min()}},

            {"5500", long_t{0}},
            {"5501", long_t{1}},
            {"557f", long_t{127}},
            {"5580", long_t{-128}},
            {"55ff", long_t{-1}},
            {"817fffffffffffffff", long_t{std::numeric_limits<int64_t>::max()}},
            {"818000000000000000", long_t{std::numeric_limits<int64_t>::min()}},

            {"7200000000", float_t{0.0F}},
            {"723f800000", float_t{1.0F}},
            {"72c996b43f", float_t{-1234567.875}},
            {"727f800000", float_t{std::numeric_limits<float>::infinity()}},
            {"72ff800000", float_t{-std::numeric_limits<float>::infinity()}},
            {"820000000000000000", double_t{0.0F}},
            {"823ff0000000000000", double_t{1.0F}},
            {"82c132d687e3df2180", double_t{-1.23456789012345671653747558594E6}},
            {"827ff0000000000000", double_t{std::numeric_limits<double>::infinity()}},
            {"82fff0000000000000", double_t{-std::numeric_limits<double>::infinity()}},
            {"7400000000", decimal32_t{}},
            {"840000000000000000", decimal64_t{}},
            {"9400000000000000000000000000000000", decimal128_t{}},
            {"7300000020", char_t{U' '}},
            {"730001f34c", char_t{U'🍌'}},
            {"830000000000000000", timestamp_t{}},
            {"830000018EC85F2370", timestamp_t{std::chrono::seconds(mktime(&tm))}},
            {"989023C8737BC94950801CABA183C1F793",
             amqp_asio::types::uuid_t{array_from_hex<16>("9023C8737BC94950801CABA183C1F793")}},

            {"a000", vector_from_hex("")},
            {"a0109023C8737BC94950801CABA183C1F793", vector_from_hex("9023C8737BC94950801CABA183C1F793")},
            {"b000000100" + blob, vector_from_hex(blob), "blob"},

            {"a100", ""s},
            {"a10C48656c6c6f20576f726c6421", "Hello World!"s},

            {"b1000003fa"s + jabberwocky_hex, jaberwocky, "jaberwocky"},

            {"a300", symbol_t{}},
            {"a30c48656c6c6f20576f726c6421", symbol_t{"Hello World!"s}},
            {"b3000003fa"s + jabberwocky_hex, symbol_t{jaberwocky}, "jaberwocky"},


            {"45", any_list_t{}},
            {"d0 00000005 00000001 40", any_list_t{null_t{}}},
            {"d0 00000008 00000004 40 40 40 40", any_list_t{null_t{}, null_t{}, null_t{}, null_t{}}},
            {"d0 00000005 00000001 41", any_list_t{true}},

            {R"(
            d0 00000052 00000012
            40
            41
            50 01
            60 0002
            43
            52 03
            70 00000100
            44
            53 04
            80 0000000000000100
            51 01
            61 0002
            54 03
            71 00000100
            55 04
            81 0000000000000100
            a1 0c 48656c6c6f20576f726c6421
            a3 0c 48656c6c6f20576f726c6421
            )", any_list_t{
                    null_t{},
                    true,
                    ubyte_t{1},
                    ushort_t{2},
                    uint_t{0}, uint_t{3}, uint_t{256},
                    ulong_t{0}, ulong_t{4}, ulong_t{256},
                    byte_t{1},
                    short_t{2},
                    int_t{3}, int_t{256},
                    long_t{4}, long_t{256},
                    string_t{"Hello World!"},
                    symbol_t("Hello World!"),
            }, "scalarList32"},

            {"d0 00000008 00000004 41 41 40 40", any_list_t{true, true, null_t{}, null_t{}}},
            {"d0 00000008 00000004 41 40 40 41", any_list_t{true, null_t{}, null_t{}, true}},

            {"d1 00000006 00000002 4040", any_map_t{{null_t{}, null_t{}}}},
            {"d1 00000006 00000002 4141", any_map_t{{true, true}}},
            {"d1 00000008 00000004 40414243", any_map_t{{null_t{}, true}, {false, uint_t{0}}}},
            {"d1 0000000c 00000004 5001 5002 5003 5004", any_map_t{{ubyte_t{1}, ubyte_t{2}}, {ubyte_t{3}, ubyte_t{4}}}},
            {"d1 0000001f 00000006 5001 5002 5003 5004 5005 d1 0000000c 00000004 5001 5002 5003 5004",
             any_map_t{{ubyte_t{1}, ubyte_t{2}},
                       {ubyte_t{3}, ubyte_t{4}},
                       {ubyte_t{5}, any_map_t{{ubyte_t{1}, ubyte_t{2}}, {ubyte_t{3}, ubyte_t{4}}}}}},

            {"f0 00000005 00000000 40", any_array_t{}},

            {"f0 00000005 00000004 40", any_array_t{null_t{}, null_t{}, null_t{}, null_t{}}},

            {"f0 00000005 00000004 41", any_array_t{true, true, true, true}},
            {"f0 00000005 00000004 42", any_array_t{false, false, false, false}},

            {"f0 00000009 00000004 50 00 00 00 00", any_array_t{ubyte_t{0}, ubyte_t{0}, ubyte_t{0}, ubyte_t{0}}},
            {"f0 00000009 00000004 50 01 02 03 04", any_array_t{ubyte_t{1}, ubyte_t{2}, ubyte_t{3}, ubyte_t{4}}},

            {"f0 0000000d 00000004 60 0000 0000 0000 0000",
             any_array_t{ushort_t{0}, ushort_t{0}, ushort_t{0}, ushort_t{0}}},
            {"f0 0000000d 00000004 60 0001 0002 0003 0004",
             any_array_t{ushort_t{1}, ushort_t{2}, ushort_t{3}, ushort_t{4}}},

            {"f0 00000005 00000004 43", any_array_t{uint_t{0}, uint_t{0}, uint_t{0}, uint_t{0}}},
            {"f0 00000009 00000004 52 00 01 02 03", any_array_t{uint_t{0}, uint_t{1}, uint_t{2}, uint_t{3}}},
            {"f0 00000009 00000004 52 01 02 03 00", any_array_t{uint_t{1}, uint_t{2}, uint_t{3}, uint_t{0}}},
            {"f0 00000015 00000004 70 00000001 00000002 00000003 00000100",
             any_array_t{uint_t{1}, uint_t{2}, uint_t{3}, uint_t{256}}},
            {"f0 00000015 00000004 70 00000100 00000001 00000002 00000003",
             any_array_t{uint_t{256}, uint_t{1}, uint_t{2}, uint_t{3}}},

            {"f0 00000005 00000004 44", any_array_t{ulong_t{0}, ulong_t{0}, ulong_t{0}, ulong_t{0}}},
            {"f0 00000009 00000004 53 00 01 02 03", any_array_t{ulong_t{0}, ulong_t{1}, ulong_t{2}, ulong_t{3}}},
            {"f0 00000009 00000004 53 01 02 03 00", any_array_t{ulong_t{1}, ulong_t{2}, ulong_t{3}, ulong_t{0}}},
            {"f0 00000025 00000004 80 0000000000000001 0000000000000002 0000000000000003 0000000000000100",
             any_array_t{ulong_t{1}, ulong_t{2}, ulong_t{3}, ulong_t{256}}},
            {"f0 00000025 00000004 80 0000000000000100 0000000000000001 0000000000000002 0000000000000003",
             any_array_t{ulong_t{256}, ulong_t{1}, ulong_t{2}, ulong_t{3}}},

            {"f0 00000009 00000004 51 00 00 00 00", any_array_t{byte_t{0}, byte_t{0}, byte_t{0}, byte_t{0}}},
            {"f0 00000009 00000004 51 01 02 03 04", any_array_t{byte_t{1}, byte_t{2}, byte_t{3}, byte_t{4}}},

            {"f0 0000000d 00000004 61 0000 0000 0000 0000",
             any_array_t{short_t{0}, short_t{0}, short_t{0}, short_t{0}}},
            {"f0 0000000d 00000004 61 0001 0002 0003 0004",
             any_array_t{short_t{1}, short_t{2}, short_t{3}, short_t{4}}},

            {"f0 00000009 00000004 54 00 00 00 00", any_array_t{int_t{0}, int_t{0}, int_t{0}, int_t{0}}},
            {"f0 00000009 00000004 54 00 01 02 03", any_array_t{int_t{0}, int_t{1}, int_t{2}, int_t{3}}},
            {"f0 00000009 00000004 54 01 02 03 00", any_array_t{int_t{1}, int_t{2}, int_t{3}, int_t{0}}},
            {"f0 00000015 00000004 71 00000001 00000002 00000003 00000100",
             any_array_t{int_t{1}, int_t{2}, int_t{3}, int_t{256}}},
            {"f0 00000015 00000004 71 00000100 00000001 00000002 00000003",
             any_array_t{int_t{256}, int_t{1}, int_t{2}, int_t{3}}},

            {"f0 00000009 00000004 55 00 00 00 00", any_array_t{long_t{0}, long_t{0}, long_t{0}, long_t{0}}},
            {"f0 00000009 00000004 55 00 01 02 03", any_array_t{long_t{0}, long_t{1}, long_t{2}, long_t{3}}},
            {"f0 00000009 00000004 55 01 02 03 00", any_array_t{long_t{1}, long_t{2}, long_t{3}, long_t{0}}},
            {"f0 00000025 00000004 81 0000000000000001 0000000000000002 0000000000000003 0000000000000100",
             any_array_t{long_t{1}, long_t{2}, long_t{3}, long_t{256}}},
            {"f0 00000025 00000004 81 0000000000000100 0000000000000001 0000000000000002 0000000000000003",
             any_array_t{long_t{256}, long_t{1}, long_t{2}, long_t{3}}},

            {"f0 00000011 00000004 a0 00 0401010101 0101 03030303",
             any_array_t{binary_t{}, vector_from_hex("01010101"), vector_from_hex("01"), vector_from_hex("030303")}},
            {"f0 0000011d 00000004 b0 00000100 " + blob + " 00000004 01010101 00000001 01 00000003 030303",
             any_array_t{vector_from_hex(blob),
                         vector_from_hex("01010101"),
                         vector_from_hex("01"),
                         vector_from_hex("030303")}},

            {"f0 0000000f 00000004 a1 00 01 20 022020 03202020",
             any_array_t{string_t{}, string_t{" "}, string_t{"  "}, string_t{"   "}}},
            {"f0 00000415 00000004 b1 000003fa" + jabberwocky_hex + " 00000001 20 00000002 2020 00000003 202020",
             any_array_t{jaberwocky, string_t{" "}, string_t{"  "}, string_t{"   "}}},

            {"f0 0000000f 00000004 a3 00 01 20 022020 03202020",
             any_array_t{symbol_t{}, symbol_t{" "}, symbol_t{"  "}, symbol_t{"   "}}},
            {"f0 00000415 00000004 b3 000003fa" + jabberwocky_hex + " 00000001 20 00000002 2020 00000003 202020",
             any_array_t{symbol_t{jaberwocky}, symbol_t{" "}, symbol_t{"  "}, symbol_t{"   "}}},

            {"00 80 00003039 00010932 40", null_t{}, "descriptor_numeric", {NumericDescriptor{12345, 67890}}},
            {"00 80 00003039 00010932 00 80 00006068 0000350b 40",
             null_t{},
             "descriptor_multi",
             {NumericDescriptor{12345, 67890}, NumericDescriptor{24680, 13579}}},
            {"00a3126578616d706c653a64657363726970746f7240",
             null_t{},
             "descriptor_symbolic",
             {SymbolicDescriptor{"example:descriptor"}}}

    };



    struct NoDescAggregate {
        symbol_t sym;
        int_t i;

        auto operator<=>(const NoDescAggregate &) const = default;

    };

    struct Simple {
        static constexpr DescriptorDefinition amqp_descriptor = {"example:agg:list", 0x03, 0x01};

        symbol_t sym;
        int_t i;

        auto operator<=>(const Simple &) const = default;
    };


    struct Book {
        static constexpr DescriptorDefinition amqp_descriptor = {"example:book:list", 0x03, 0x02};

        std::string title;
        std::vector<std::string> authors;
        std::optional<std::string> isbn;

        auto operator<=>(const Book &) const = default;
    };


    struct WrappedMap : wrapper_t<map_t<uint_t, uint_t>, WrappedMap> {
        static constexpr DescriptorDefinition amqp_descriptor = {"example:wrapped-map:map", 0x03, 0x03};

        using wrapper_t<map_t<uint_t, uint_t>, WrappedMap>::wrapper_t;

        auto operator<=>(const WrappedMap &) const = default;
    };

    struct Empty {
        static constexpr DescriptorDefinition amqp_descriptor = {"example:empty:list", 0x03, 0x04};

        auto operator<=>(const Empty &) const = default;
    };

    using Variant = std::variant<Book, Simple, WrappedMap, Empty, string_t>;




}

