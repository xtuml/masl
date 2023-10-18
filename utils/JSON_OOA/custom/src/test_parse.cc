#include "gtest/gtest.h"
#include "JSON_OOA/__JSON_types.hh"
#include "JSON_OOA/__JSON_services.hh"

using namespace masld_JSON;

TEST(Parse,string) {

    std::string input = R"(
    "hello"
    )";

    maslt_JSONElement result = masls_parse(input);

    EXPECT_EQ(result.get_masla_kind(),maslt_JSONType::masle_String);
    EXPECT_EQ(result.get_masla_data().get_masla_str(),"hello");

}

TEST(Parse, real) {

    std::string input = R"(
        123.0
        )";

    maslt_JSONElement result = masls_parse(input);

    EXPECT_EQ(result.get_masla_kind(),maslt_JSONType::masle_Real);
    EXPECT_EQ(result.get_masla_data().get_masla_real(),123.0);

}


TEST(Parse, integer) {

    std::string input = R"(
            123
            )";

    maslt_JSONElement result = masls_parse(input);

    EXPECT_EQ(result.get_masla_kind(),maslt_JSONType::masle_Integer);
    EXPECT_EQ(result.get_masla_data().get_masla_int(),123);

}

TEST(Parse, boolean) {

    std::string input = R"(
                true
                )";

    maslt_JSONElement result = masls_parse(input);

    EXPECT_EQ(result.get_masla_kind(),maslt_JSONType::masle_Boolean);
    EXPECT_EQ(result.get_masla_data().get_masla_bool(),true);

}

TEST(Parse, object) {

    std::string input = R"(
                    {
                        "s" : "bbb",
                        "i" : 4
                    }
                    )";

    maslt_JSONElement result = masls_parse(input);

    EXPECT_EQ(result.get_masla_kind(),maslt_JSONType::masle_Object);
    EXPECT_EQ(result.get_masla_data().get_masla_obj()["s"].get_masla_kind(),maslt_JSONType::masle_String);
    EXPECT_EQ(result.get_masla_data().get_masla_obj()["s"].get_masla_data().get_masla_str(),"bbb");
    EXPECT_EQ(result.get_masla_data().get_masla_obj()["i"].get_masla_kind(),maslt_JSONType::masle_Integer);
    EXPECT_EQ(result.get_masla_data().get_masla_obj()["i"].get_masla_data().get_masla_int(),4);

}

TEST(Parse, array) {

    std::string input = R"(
                            [1, "two" ]
                            )";

    maslt_JSONElement result = masls_parse(input);

    EXPECT_EQ(result.get_masla_kind(),maslt_JSONType::masle_Array);
    EXPECT_EQ(result.get_masla_data().get_masla_arr()[0].get_masla_kind(),maslt_JSONType::masle_Integer);
    EXPECT_EQ(result.get_masla_data().get_masla_arr()[0].get_masla_data().get_masla_int(),1);
    EXPECT_EQ(result.get_masla_data().get_masla_arr()[1].get_masla_kind(),maslt_JSONType::masle_String);
    EXPECT_EQ(result.get_masla_data().get_masla_arr()[1].get_masla_data().get_masla_str(),"two");

}

TEST(Parse, deep) {

    std::string input = R"(
                    {
                        "s" : "bbb",
                        "i" : 4,
                        "o" : {
                            "r" : 123.4,
                            "b" : true,
                            "a" : [ "one", 2, 3.0 ]
                        }
                     }
                    )";

    maslt_JSONElement result = masls_parse(input);

    EXPECT_EQ(result.get_masla_kind(),maslt_JSONType::masle_Object);
    auto s = result.get_masla_data().get_masla_obj()["s"];
    auto i = result.get_masla_data().get_masla_obj()["i"];
    auto o = result.get_masla_data().get_masla_obj()["o"];
    auto o_r =  o.get_masla_data().get_masla_obj()["r"];
    auto o_b =  o.get_masla_data().get_masla_obj()["b"];
    auto o_a =  o.get_masla_data().get_masla_obj()["a"];
    auto o_ax = o_a.get_masla_data().get_masla_arr();

    EXPECT_EQ(s.get_masla_kind(),maslt_JSONType::masle_String);
    EXPECT_EQ(s.get_masla_data().get_masla_str(),"bbb");

    EXPECT_EQ(i.get_masla_kind(),maslt_JSONType::masle_Integer);
    EXPECT_EQ(i.get_masla_data().get_masla_int(),4);

    EXPECT_EQ(o.get_masla_kind(),maslt_JSONType::masle_Object);
    EXPECT_EQ(o_r.get_masla_kind(),maslt_JSONType::masle_Real);
    EXPECT_EQ(o_r.get_masla_data().get_masla_real(),123.4);

    EXPECT_EQ(o_b.get_masla_kind(),maslt_JSONType::masle_Boolean);
    EXPECT_EQ(o_b.get_masla_data().get_masla_bool(),true);

    EXPECT_EQ(o_a.get_masla_kind(),maslt_JSONType::masle_Array);

    EXPECT_EQ(o_ax[0].get_masla_kind(),maslt_JSONType::masle_String);
    EXPECT_EQ(o_ax[0].get_masla_data().get_masla_str(),"one");
    EXPECT_EQ(o_ax[1].get_masla_kind(),maslt_JSONType::masle_Integer);
    EXPECT_EQ(o_ax[1].get_masla_data().get_masla_int(),2);
    EXPECT_EQ(o_ax[2].get_masla_kind(),maslt_JSONType::masle_Real);
    EXPECT_EQ(o_ax[2].get_masla_data().get_masla_real(),3.0);

}
