#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include "JSON_OOA/__JSON_types.hh"
#include "JSON_OOA/__JSON_services.hh"

using namespace masld_JSON;
using namespace std::literals;
namespace {
    std::string valid_schema = R"({
            "$id": "https://example.com/person.schema.json",
            "$schema": "https://json-schema.org/draft/2020-12/schema",
            "title": "Person",
            "type": "object",
            "properties": {
                "firstName": {
                    "type": "string",
                    "description": "The person's first name."
                },
                "lastName": {
                    "type": "string",
                    "description": "The person's last name."
                },
                "age": {
                    "description": "Age in years which must be equal to or greater than zero.",
                    "type": "integer",
                    "minimum": 0,
                    "default": 18
                }
            }
    }

    )"s;
}

TEST(Schema,addValidSchema ) {
    auto schema = masls_overload2_add_document(valid_schema);

    auto result = masls_register_schema(schema);

    EXPECT_TRUE(result.get_masla_valid());
    EXPECT_EQ(result.get_masla_errors().size(),0);

}
TEST(Schema,addInvalidSchema ) {
    auto schema = masls_overload2_add_document(R"(
            {
              "$id": "https://example.com/person.schema.json",
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "title": [ "An", "Unexpected", "Array", 2]
            }

        )"s);

    auto result = masls_register_schema(schema);

    EXPECT_FALSE(result.get_masla_valid());
    EXPECT_EQ(result.get_masla_errors().size(),1);
    EXPECT_EQ(result.get_masla_errors()[0].get_masla_location().s_str(),"/title");

}


TEST(Schema,validateDocOK) {
    auto schema = masls_overload2_add_document(valid_schema);

    masls_register_schema(schema);

    auto doc = masls_overload2_add_document(R"(
        {
          "firstName": "John",
          "lastName": "Doe",
          "age": 21
        }
    )");

    auto result = masls_validate(doc,schema,false);
    EXPECT_TRUE(result.get_masla_valid());
    EXPECT_EQ(result.get_masla_errors().size(),0);

    auto validated_doc = masls_dump(doc).s_str();

    EXPECT_EQ(validated_doc,R"({"age":21,"firstName":"John","lastName":"Doe"})"s);
}

TEST(Schema,validateDocFail) {
    auto schema = masls_overload2_add_document(valid_schema);

    masls_register_schema(schema);

    auto doc = masls_overload2_add_document(R"(
            {
              "firstName": "John",
              "lastName": "Doe",
              "age": "twenty-one"
            }
        )");

    auto result = masls_validate(doc,schema,false);
    EXPECT_FALSE(result.get_masla_valid());
    EXPECT_EQ(result.get_masla_errors().size(),1);
    EXPECT_EQ(result.get_masla_errors()[0].get_masla_location().s_str(),"/age");

    auto validated_doc = masls_dump(doc).s_str();
    EXPECT_EQ(validated_doc,R"({"age":"twenty-one","firstName":"John","lastName":"Doe"})"s);
}

TEST(Schema,validateDocOKNoPatch) {
    auto schema = masls_overload2_add_document(valid_schema);

    masls_register_schema(schema);

    auto doc = masls_overload2_add_document(R"(
                {
                  "firstName": "John",
                  "lastName": "Doe"
                }
            )");

    auto result = masls_validate(doc,schema,false);
    EXPECT_TRUE(result.get_masla_valid());
    EXPECT_EQ(result.get_masla_errors().size(),0);

    auto validated_doc = masls_dump(doc).s_str();

    EXPECT_EQ(validated_doc,R"({"firstName":"John","lastName":"Doe"})"s);
}

TEST(Schema,validateDocOKPatch) {
    auto schema = masls_overload2_add_document(valid_schema);

    masls_register_schema(schema);

    auto doc = masls_overload2_add_document(R"(
            {
              "firstName": "John",
              "lastName": "Doe"
            }
        )");

    auto result = masls_validate(doc,schema,true);
    EXPECT_TRUE(result.get_masla_valid());
    EXPECT_EQ(result.get_masla_errors().size(),0);

    auto validated_doc = masls_dump(doc).s_str();

    EXPECT_EQ(validated_doc,R"({"age":18,"firstName":"John","lastName":"Doe"})"s);
}

TEST(Schema,validateDocFailPatch) {
    auto schema = masls_overload2_add_document(valid_schema);

    masls_register_schema(schema);

    auto doc = masls_overload2_add_document(R"(
                {
                  "firstName": 123,
                  "lastName": "Doe"
                }
            )");

    auto result = masls_validate(doc,schema,true);
    EXPECT_FALSE(result.get_masla_valid());
    EXPECT_EQ(result.get_masla_errors().size(),1);
    EXPECT_EQ(result.get_masla_errors()[0].get_masla_location().s_str(),"/firstName");

    auto validated_doc = masls_dump(doc).s_str();

    EXPECT_EQ(validated_doc,R"({"firstName":123,"lastName":"Doe"})"s);
}


TEST(Schema,validateDocResolveRefs) {
    auto schema = masls_overload2_add_document(R"({
            "$id": "https://example.com/person.schema.json",
            "$schema": "https://json-schema.org/draft/2020-12/schema",
            "title": "Person",
            "type": "object",
            "properties": {
                "firstName": {
                    "type": "string",
                    "description": "The person's first name."
                },
                "lastName": {
                    "type": "string",
                    "description": "The person's last name."
                },
                "age": {
                    "$ref" : "https://example.com/age.schema.json#/definitions/age"
                }
            }
    }

    )"s);
    auto ref_schema = masls_overload2_add_document(R"({
            "$id": "https://example.com/age.schema.json",
            "$schema": "https://json-schema.org/draft/2020-12/schema",
            "definitions": {
                "age": {
                    "description": "Age in years which must be equal to or greater than zero.",
                    "type": "integer",
                    "minimum": 0,
                    "default": 18
                }
            }
        }
    )"s);

    masls_register_schema(schema);

    auto doc = masls_overload2_add_document(R"(
            {
              "firstName": "John",
              "lastName": "Doe",
              "age": 21
            }
        )");

    auto result = masls_validate(doc,schema,false);
    EXPECT_TRUE(result.get_masla_valid());
    EXPECT_EQ(result.get_masla_errors().size(),0);

    auto validated_doc = masls_dump(doc).s_str();

    EXPECT_EQ(validated_doc,R"({"age":21,"firstName":"John","lastName":"Doe"})"s);
}
