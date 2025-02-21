/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include "utils.hh"
#include "data.hh"
#include <gtest/gtest.h>
#include <amqp_asio/types.hh>
#include "../decoder.hh"
#include <boost/algorithm/string/erase.hpp>

using namespace amqp_asio::types;
using namespace std::literals;


std::vector<CodecTestParams<any_value_t>> decodeOnlyTestData = {
        {"5600", false},
        {"5601", true},

        {"5200", uint_t{0}},
        {"7000000000", uint_t{0}},
        {"7000000001", uint_t{1}},

        {"5300", ulong_t{0}},
        {"800000000000000000", ulong_t{0}},
        {"800000000000000001", ulong_t{1}},

        {"7100000000", int_t{0}},
        {"7100000001", int_t{1}},
        {"71ffffffff", int_t{-1}},

        {"810000000000000000", long_t{0}},
        {"810000000000000001", long_t{1}},
        {"81ffffffffffffffff", long_t{-1}},

        {"b000000000", vector_from_hex("")},
        {"b0000000109023C8737BC94950801CABA183C1F793", vector_from_hex("9023C8737BC94950801CABA183C1F793")},

        {"b100000000", ""s},
        {"b10000000C48656c6c6f20576f726c6421", "Hello World!"s},

        {"b300000000", symbol_t{}},
        {"b30000000C48656c6c6f20576f726c6421", symbol_t{"Hello World!"s}},

        {"c0 01 00", any_list_t{}},
        {"c0 02 01 40", any_list_t{null_t{}}},
        {"c0 05 04 40 40 40 40", any_list_t{null_t{}, null_t{}, null_t{}, null_t{}}},

        {"c0 02 01 41", any_list_t{true}},
        {"c0 03 01 5601", any_list_t{true}},

        {R"(
            c0 4f 12
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
        )",

         any_list_t{
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
                "Hello World!",
                symbol_t("Hello World!"),
        }, "scalarList8"},


        {"d0 00000004 00000000", any_list_t{}},
        {"d0 00000006 00000001 5601", any_list_t{true}},



        {"c1 01 00", any_map_t{}},
        {"c1 03 02 4040", any_map_t{{null_t{}, null_t{}}}},
        {"c1 03 02 4141", any_map_t{{true, true}}},
        {"c1 05 04 40414243", any_map_t{{null_t{}, true}, {false, uint_t{0}}}},
        {"c1 09 04 5001500250035004", any_map_t{{ubyte_t{1}, ubyte_t{2}}, {ubyte_t{3}, ubyte_t{4}}}},
        {"c1 16 06 5001 5002 5003 5004 5005 c1 09 04 5001 5002 5003 5004",
         any_map_t{{ubyte_t{1}, ubyte_t{2}},
                   {ubyte_t{3}, ubyte_t{4}},
                   {ubyte_t{5}, any_map_t{{ubyte_t{1}, ubyte_t{2}}, {ubyte_t{3}, ubyte_t{4}}}}}},

        {"e0 02 00 53", any_array_t{}},
        {"e0 02 01 40", any_array_t{null_t{}}},
        {"e0 06 04 56 01000100", any_array_t{true, false, true, false}},
        {"e0 02 05 41", any_array_t{true, true, true, true, true}},
        {"e0 02 05 42", any_array_t{false, false, false, false, false}},
        {"f0 00000005 00000000 53", any_array_t{}},
        {"f0 00000005 00000001 40", any_array_t{null_t{}}},
        {"f0 00000009 00000004 56 01000100", any_array_t{true, false, true, false}},
        {"f0 00000005 00000005 41", any_array_t{true, true, true, true, true}},
        {"f0 00000005 00000005 42", any_array_t{false, false, false, false, false}},
};

template<typename T>
void check_value(std::string hex, const T &expected) {
    auto buffer = vector_from_hex(std::move(hex));
    auto decoder = Decoder(buffer);
    auto actual = decoder.template decode<T>();
    EXPECT_EQ(actual, expected);
}


class DecodeData : public ::testing::TestWithParam<CodecTestParams<any_value_t>> {

};

TEST_P(DecodeData, decode_data) {
    auto [hex, expected, name, descriptors] = GetParam();

    check_value(hex, any_t{descriptors, expected});
}

TEST_P(DecodeData, decode_explicit) {
    auto [hex, expected, name, descriptors] = GetParam();

    std::visit([h = hex](auto &e) {
        check_value(h, e);
    }, expected);


}

INSTANTIATE_TEST_SUITE_P(normalised, DecodeData, testing::ValuesIn(normalisedTestData), test_name);

INSTANTIATE_TEST_SUITE_P(decodeOnly, DecodeData, testing::ValuesIn(decodeOnlyTestData), test_name);

TEST(DecodeVector, simple) {
    check_value("e0 06 04  54   01020304", array_t<int_t>{1, 2, 3, 4});
}

TEST(DecodeVector, nested) {
    check_value("e0 10 02  e0   06 04 54 01020304   06 04 54 05060708",
                array_t<array_t<int_t>>{{1, 2, 3, 4}, {5, 6, 7, 8}});
}

TEST(DecodeTuple, simple) {
    check_value("c0 11 02  a30C48656c6c6f20576f726c6421 54 04", std::make_tuple(symbol_t("Hello World!"), int_t(4)));
}

TEST(DecodeEnum, boolean) {
    enum class AnEnum : boolean_t {
        zero, one
    };

    check_value("41", AnEnum::one);
}

TEST(DecodeEnum, uint) {
    enum class AnEnum : uint_t {
        zero = 0, one = 1
    };

    check_value("5201", AnEnum::one);
}
TEST(DecodeEnum, array) {
    enum class AnEnum : uint_t {
        zero = 0, one = 1
    };

    check_value("f0 00000008 00000003 52 00 01 00", std::vector<AnEnum>{ AnEnum::zero, AnEnum::one, AnEnum::zero});
}

TEST(DecodeOptional, unset) {
    check_value("40", std::optional<uint_t>{});
}

TEST(DecodeOptional, value) {
    check_value("52 01", std::optional<uint_t>{1});
}


TEST(DecodeAggregate, noDescriptor) {
    check_value("c0 11 02  a30C48656c6c6f20576f726c6421 54 04", NoDescAggregate{symbol_t("Hello World!"), int_t(4)});
}


TEST(DecodeAggregate, empty) {
    check_value("00 80 00000003 00000004 45", Empty{});
}

TEST(DecodeAggregate, amqpDocExample) {
    check_value(
            "(00 a3 11 6578616d706c653a626f6f6b3a6c697374 c0) 40 03"
            "   ( a1 15 414d515020666f7220262062792044756d6d696573) "
            "   (e0 25 02 a1"
            "       (0e 526f62204a2e20476f6466726579) "
            "       (13 52616661656c20482e205363686c6f6d696e67))"
            "   40",
            Book{
                    .title = "AMQP for & by Dummies",
                    .authors = {"Rob J. Godfrey", "Rafael H. Schloming"}
            });
}

TEST(DecodeAggregate, trailingOptionalOmitted) {
    check_value(
            "(00 a3 11 6578616d706c653a626f6f6b3a6c697374 c0) 3f 02"
            "   ( a1 15 414d515020666f7220262062792044756d6d696573) "
            "   (e0 25 02 a1"
            "       (0e 526f62204a2e20476f6466726579) "
            "       (13 52616661656c20482e205363686c6f6d696e67))",
            Book{
                    .title = "AMQP for & by Dummies",
                    .authors = {"Rob J. Godfrey", "Rafael H. Schloming"}
            });
}

TEST(DecodeAggregate, trailingMultipleOmitted) {
    check_value(
            "(00 a3 11 6578616d706c653a626f6f6b3a6c697374 c0) 18 01"
            "   ( a1 15 414d515020666f7220262062792044756d6d696573) ",
            Book{
                    .title = "AMQP for & by Dummies",
            });
}

TEST(DecodeAggregate, singleMultiple) {
    check_value(
            "(00 a3 11 6578616d706c653a626f6f6b3a6c697374 c0) 29 03"
            "   ( a1 15 414d515020666f7220262062792044756d6d696573) "
            "   ( a1 0e 526f62204a2e20476f6466726579)"
            "   40",
            Book{
                    .title = "AMQP for & by Dummies",
                    .authors = {"Rob J. Godfrey"}
            });
}

TEST(DecodeAggregate, nullMultiple) {
    check_value(
            "(00 a3 11 6578616d706c653a626f6f6b3a6c697374 c0) 1a 03"
            "   ( a1 15 414d515020666f7220262062792044756d6d696573) "
            "   40"
            "   40",
            Book{
                    .title = "AMQP for & by Dummies",
                    .authors = {}
            });
}

TEST(DecodeAggregate, numericDescriptor) {
    check_value(
            R"(
            (00 80 00000003 00000002 c0) 40 03
               ( a1 15 414d515020666f7220262062792044756d6d696573)
               (e0 25 02 a1
                   (0e 526f62204a2e20476f6466726579)
                   (13 52616661656c20482e205363686c6f6d696e67))
               40
               )",
            Book{
                    .title = "AMQP for & by Dummies",
                    .authors = {"Rob J. Godfrey", "Rafael H. Schloming"}
            });
}


TEST(DecodeDescriptorWrapped, map) {
    check_value(R"(
           (00 80 00000003 00000003 c1) 09 04 5201 5202 5203 5204
           )",
                WrappedMap(map_t<uint_t, uint_t>{{{1, 2}, {3, 4}}}));

}

TEST(Variant, name) {
    check_value(
            "(00 a3 11 6578616d706c653a626f6f6b3a6c697374 c0) 40 03"
            "   ( a1 15 414d515020666f7220262062792044756d6d696573) "
            "   (e0 25 02 a1"
            "       (0e 526f62204a2e20476f6466726579) "
            "       (13 52616661656c20482e205363686c6f6d696e67))"
            "   40",
            Variant{Book{
                    .title = "AMQP for & by Dummies",
                    .authors = {"Rob J. Godfrey", "Rafael H. Schloming"}
            }});
}

TEST(Variant, numeric) {
    check_value(
            R"(
           (00 80 00000003 00000003 c1) 09 04 5201 5202 5203 5204
           )",
            Variant{WrappedMap(map_t<uint_t, uint_t>{{{1, 2}, {3, 4}}})});
}

TEST(Variant, nonDescriptor) {
    check_value(
            R"(
           a10C48656c6c6f20576f726c6421
           )",
            Variant{string_t{"Hello World!"}});

}

TEST(Variant, scalar) {
    check_value(
            R"(
           a10C48656c6c6f20576f726c6421
           )",
            scalar_t{string_t{"Hello World!"}});
}
