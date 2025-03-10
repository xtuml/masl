#include "gtest/gtest.h"
#include "JSON_OOA/__JSON_types.hh"
#include "JSON_OOA/__JSON_services.hh"

using namespace masld_JSON;
using namespace std::literals;

TEST(Patch,string_string) {

    auto doc1 = R"(
        {
            "a" : "aaa",
            "b" : "bbb",
            "c" : "ccc"
        }
    )"s;

    auto doc2 = R"(
            {
                "a" : "aaa",
                "b" : "BBB",
                "c" : null,
                "d" : "ddd"
            }
        )"s;

    auto result = masls_overload4_dump(masls_overload1_patch(doc1,doc2)).s_str();

    EXPECT_EQ(result,R"({"a":"aaa","b":"BBB","d":"ddd"})"s);


}

TEST(Patch,element_element) {

    auto doc1 = masls_parse(R"(
            {
                "a" : "aaa",
                "b" : "bbb",
                "c" : "ccc"
            }
        )"s);

    auto doc2 = masls_parse(R"(
                {
                    "a" : "aaa",
                    "b" : "BBB",
                    "c" : null,
                    "d" : "ddd"
                }
            )"s);

    auto result = masls_overload4_dump(masls_patch(doc1,doc2)).s_str();

    EXPECT_EQ(result,R"({"a":"aaa","b":"BBB","d":"ddd"})"s);

}

