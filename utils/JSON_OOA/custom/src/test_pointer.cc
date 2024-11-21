#include "gtest/gtest.h"
#include "JSON_OOA/__JSON_types.hh"
#include "JSON_OOA/__JSON_services.hh"

using namespace masld_JSON;
using namespace std::literals;

TEST(Pointer,element) {

    auto doc = masls_overload2_add_document(R"(
        {
            "a" : "aaa",
            "b" : {
                "b" : "bbb",
                "c" : "ccc"
            }
        }
    )"s);

    auto result = masls_overload6_dump(masls_pointer(doc,"/b/c")).s_str();

    EXPECT_EQ(result,R"("ccc")"s);

}

TEST(Pointer,string) {

    auto doc = masls_overload2_add_document(R"(
            {
                "a" : "aaa",
                "b" : {
                    "b" : "bbb",
                    "c" : "ccc"
                }
            }
        )"s);

    auto result = masls_pointer_string(doc,"/b/c").s_str();

    EXPECT_EQ(result,"ccc"s);
}
TEST(Pointer,real) {

    auto doc = masls_overload2_add_document(R"(
                    {
                        "a" : "aaa",
                        "b" : {
                            "b" : "bbb",
                            "c" : 123.0
                        }
                    }
                )"s);

    auto result = masls_pointer_real(doc,"/b/c");

    EXPECT_EQ(result,123);
}

TEST(Pointer,integer) {

    auto doc = masls_overload2_add_document(R"(
                {
                    "a" : "aaa",
                    "b" : {
                        "b" : "bbb",
                        "c" : 123
                    }
                }
            )"s);

    auto result = masls_pointer_integer(doc,"/b/c");

    EXPECT_EQ(result,123);
}


TEST(Pointer,boolean) {

    auto doc = masls_overload2_add_document(R"(
                    {
                        "a" : "aaa",
                        "b" : {
                            "b" : "bbb",
                            "c" : true
                        }
                    }
                )"s);

    auto result = masls_pointer_boolean(doc,"/b/c");

    EXPECT_EQ(result,true);
}

