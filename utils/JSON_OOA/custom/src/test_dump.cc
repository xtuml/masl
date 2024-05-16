#include "gtest/gtest.h"
#include "JSON_OOA/__JSON_types.hh"
#include "JSON_OOA/__JSON_services.hh"

#include <string>

using namespace masld_JSON;
using namespace std::literals;

TEST(Dump,string) {
    maslt_JSONElement input;
    input.set_masla_kind() = maslt_JSONType::masle_String;
    input.set_masla_data().set_masla_str() = "hello";

    auto result  = masls_overload6_dump(input).s_str();
    auto pretty_result  = masls_overload7_dump(input,true).s_str();
    auto expected = R"("hello")"s;

    EXPECT_EQ(result,expected);
    EXPECT_EQ(pretty_result,expected);
}


TEST(Dump, real) {
    maslt_JSONElement input;
    input.set_masla_kind() = maslt_JSONType::masle_Real;
    input.set_masla_data().set_masla_real() = 123.0;

    auto result  = masls_overload6_dump(input).s_str();
    auto pretty_result  = masls_overload7_dump(input,true).s_str();
    auto expected = R"(123.0)"s;

    EXPECT_EQ(result,expected);
    EXPECT_EQ(pretty_result,expected);
}


TEST(Dump, integer) {

    maslt_JSONElement input;
    input.set_masla_kind() = maslt_JSONType::masle_Integer;
    input.set_masla_data().set_masla_int() = 123;

    auto result  = masls_overload6_dump(input).s_str();
    auto pretty_result  = masls_overload7_dump(input,true).s_str();
    auto expected = R"(123)"s;

    EXPECT_EQ(result,expected);
    EXPECT_EQ(pretty_result,expected);
}

TEST(Dump, boolean) {

    maslt_JSONElement input;
    input.set_masla_kind() = maslt_JSONType::masle_Boolean;
    input.set_masla_data().set_masla_bool() = true;

    auto result  = masls_overload6_dump(input).s_str();
    auto pretty_result  = masls_overload7_dump(input,true).s_str();
    auto expected = R"(true)"s;

    EXPECT_EQ(result,expected);
    EXPECT_EQ(pretty_result,expected);
}

TEST(Dump, object) {

    maslt_JSONElement input;
    input.set_masla_kind() = maslt_JSONType::masle_Object;
    input.set_masla_data().set_masla_obj()["s"].set_masla_kind() = maslt_JSONType::masle_String;
    input.set_masla_data().set_masla_obj()["s"].set_masla_data().set_masla_str() = "bbb";
    input.set_masla_data().set_masla_obj()["i"].set_masla_kind() = maslt_JSONType::masle_Integer;
    input.set_masla_data().set_masla_obj()["i"].set_masla_data().set_masla_int() = 4;

    auto result  = masls_overload6_dump(input).s_str();
    auto pretty_result  = masls_overload7_dump(input,true).s_str();
    auto expected = R"({"i":4,"s":"bbb"})"s;
    auto pretty_expected = R"({
  "i": 4,
  "s": "bbb"
})"s;
    EXPECT_EQ(result,expected);
    EXPECT_EQ(pretty_result,pretty_expected);
}

TEST(Dump, array) {

    maslt_JSONElement input;
    input.set_masla_kind() = maslt_JSONType::masle_Array;
    input.set_masla_data().set_masla_arr().accessExtend(1).set_masla_kind() = maslt_JSONType::masle_Integer;
    input.set_masla_data().set_masla_arr().accessExtend(1).set_masla_data().set_masla_int() = 1;
    input.set_masla_data().set_masla_arr().accessExtend(2).set_masla_kind() = maslt_JSONType::masle_String;
    input.set_masla_data().set_masla_arr().accessExtend(2).set_masla_data().set_masla_str() = "two";

    auto result  = masls_overload6_dump(input).s_str();
    auto pretty_result  = masls_overload7_dump(input,true).s_str();
    auto expected = R"([1,"two"])"s;
    auto pretty_expected = R"([
  1,
  "two"
])"s;
    EXPECT_EQ(result,expected);
    EXPECT_EQ(pretty_result,pretty_expected);

}

TEST(Dump, deep) {

    maslt_JSONElement input;
    input.set_masla_kind() = maslt_JSONType::masle_Object;
    auto& s = input.set_masla_data().set_masla_obj()["s"];
    s.set_masla_kind() = maslt_JSONType::masle_String;
    s.set_masla_data().set_masla_str() = "bbb";
    auto& i = input.set_masla_data().set_masla_obj()["i"];
    i.set_masla_kind() = maslt_JSONType::masle_Integer;
    i.set_masla_data().set_masla_int() = 4;
    auto& o = input.set_masla_data().set_masla_obj()["o"];
    o.set_masla_kind() = maslt_JSONType::masle_Object;
    auto& o_r =  o.set_masla_data().set_masla_obj()["r"];
    o_r.set_masla_kind() = maslt_JSONType::masle_Real;
    o_r.set_masla_data().set_masla_real() = 123.4;
    auto& o_b =  o.set_masla_data().set_masla_obj()["b"];
    o_b.set_masla_kind() = maslt_JSONType::masle_Boolean;
    o_b.set_masla_data().set_masla_bool() = true;
    auto& o_a =  o.set_masla_data().set_masla_obj()["a"];
    o_a.set_masla_kind() = maslt_JSONType::masle_Array;
    auto& o_ax = o_a.set_masla_data().set_masla_arr();
    o_ax.accessExtend(1).set_masla_kind() = maslt_JSONType::masle_String;
    o_ax.accessExtend(1).set_masla_data().set_masla_str() = "one";
    o_ax.accessExtend(2).set_masla_kind() = maslt_JSONType::masle_Integer;
    o_ax.accessExtend(2).set_masla_data().set_masla_int() = 2;
    o_ax.accessExtend(3).set_masla_kind() = maslt_JSONType::masle_Real;
    o_ax.accessExtend(3).set_masla_data().set_masla_real() = 3.0;

    auto result  = masls_overload6_dump(input).s_str();
    auto pretty_result  = masls_overload7_dump(input,true).s_str();
    auto expected = R"({"i":4,"o":{"a":["one",2,3.0],"b":true,"r":123.4},"s":"bbb"})"s;
    auto pretty_expected = R"({
  "i": 4,
  "o": {
    "a": [
      "one",
      2,
      3.0
    ],
    "b": true,
    "r": 123.4
  },
  "s": "bbb"
})"s;
    EXPECT_EQ(result,expected);
    EXPECT_EQ(pretty_result,pretty_expected);





}

