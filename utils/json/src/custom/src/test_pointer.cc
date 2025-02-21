#include "gtest/gtest.h"
#include "JSON_OOA/__JSON_types.hh"
#include "JSON_OOA/__JSON_services.hh"

using namespace masld_JSON;
using namespace std::literals;

TEST(Pointer,string) {

    auto doc = R"(
        {
            "a" : "aaa",
            "b" : {
                "b" : "bbb",
                "c" : "ccc"
            }
        }
    )"s;

    auto result = masls_overload4_dump(masls_overload1_pointer(doc,"/b/c")).s_str();

    EXPECT_EQ(result,R"("ccc")"s);

}

TEST(Pointer,element) {

    auto doc = masls_parse(R"(
            {
                "a" : "aaa",
                "b" : {
                    "b" : "bbb",
                    "c" : "ccc"
                }
            }
        )"s);

    auto result = masls_overload4_dump(masls_pointer(doc,"/b/c")).s_str();

    EXPECT_EQ(result,R"("ccc")"s);
}

