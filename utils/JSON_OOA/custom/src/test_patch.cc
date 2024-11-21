#include "gtest/gtest.h"
#include "JSON_OOA/__JSON_types.hh"
#include "JSON_OOA/__JSON_services.hh"

using namespace masld_JSON;
using namespace std::literals;

TEST(MergePatch,inplace) {

    auto doc = masls_overload2_add_document(R"(
        {
            "a" : "aaa",
            "b" : "bbb",
            "c" : "ccc"
        }
    )"s);

    auto patch = masls_overload2_add_document(R"(
            {
                "b" : "BBB",
                "c" : null,
                "d" : "ddd"
            }
        )"s);

    masls_merge_patch(doc,patch);

    auto result = masls_dump(doc).s_str();

    EXPECT_EQ(result,R"({"a":"aaa","b":"BBB","d":"ddd"})"s);


}

TEST(MergePatch,add_new) {

    auto doc = masls_overload2_add_document(R"(
            {
                "a" : "aaa",
                "b" : "bbb",
                "c" : "ccc"
            }
        )"s);

    auto patch = masls_overload2_add_document(R"(
                {
                    "b" : "BBB",
                    "c" : null,
                    "d" : "ddd"
                }
            )"s);


    auto patched = masls_add_merge_patch(doc,patch).s_str();
    auto result = masls_dump(patched).s_str();

    EXPECT_EQ(result,R"({"a":"aaa","b":"BBB","d":"ddd"})"s);

}


TEST(Patch,inplace) {

    auto doc = masls_overload2_add_document(R"(
            {
                "a" : "aaa",
                "b" : "bbb",
                "c" : "ccc"
            }
        )"s);

    auto patch = masls_overload2_add_document(R"(
                    [
                        {"op": "replace", "path": "/b", "value": "BBB"},
                        {"op": "remove", "path": "/c"},
                        {"op": "add", "path": "/d", "value": "ddd"}
                    ]
                )"s);

    masls_patch(doc,patch);

    auto result = masls_dump(doc).s_str();

    EXPECT_EQ(result,R"({"a":"aaa","b":"BBB","d":"ddd"})"s);


}

TEST(Patch,add_new) {

    auto doc = masls_overload2_add_document(R"(
                {
                    "a" : "aaa",
                    "b" : "bbb",
                    "c" : "ccc"
                }
            )"s);

    auto patch = masls_overload2_add_document(R"(
                    [
                        {"op": "replace", "path": "/b", "value": "BBB"},
                        {"op": "remove", "path": "/c"},
                        {"op": "add", "path": "/d", "value": "ddd"}
                    ]
                )"s);


    auto patched = masls_add_patch(doc,patch).s_str();
    auto result = masls_dump(patched).s_str();

    EXPECT_EQ(result,R"({"a":"aaa","b":"BBB","d":"ddd"})"s);

}

