#include <unordered_map>
#include <nlohmann/json-schema.hpp>
#include "schema.hh"
#include <uuid/uuid.h>
#include <ranges>
#include "JSON_OOA/__JSON__JSONException.hh"

namespace masld_JSON {

    void ValidationResult::error(const nlohmann::json::json_pointer &pointer,
                                 const nlohmann::json &instance,
                                 const std::string &message) {
        nlohmann::json_schema::basic_error_handler::error(pointer, instance, message);
        errors_.emplace_back(pointer, message);
    }

    bool ValidationResult::valid() const {
        return errors_.empty();
    }

    const ValidationResult::ErrorReport &ValidationResult::errors() const {
        return errors_;
    }

    const nlohmann::json &ValidationResult::patch() const {
        return patch_;
    }

    void ValidationResult::set_patch(nlohmann::json patch) {
        patch_ = std::move(patch);
    }

    void string_format_check(const std::string &format, const std::string &value) {
        static std::unordered_set <std::string> default_formats{
                "date-time",
                "date",
                "time",
                "uri",
                "email",
                "idn-email",
                "hostname",
                "ipv4",
                "ipv6",
                "uuid",
                "regex"
        };
        if (default_formats.contains(format)) {
            nlohmann::json_schema::default_string_format_check(format, value);
        }
        // Allow any unsupported formats to pass
    }

    SchemaValidator::SchemaValidator(DocumentStore store)
            : document_store_{std::move(store)},
              validator_{
                      [this](const nlohmann::json_uri &uri, nlohmann::json &schema) {
                          schema = document_store_.get_document(uri.location());
                      },
                      string_format_check
              } {
    }

    ValidationResult SchemaValidator::set_schema(const std::string &uri) {
        validator_.set_root_schema(nlohmann::json_schema::draft7_schema_builtin);
        ValidationResult result;
        auto schema = document_store_.get_document(uri);
        result.set_patch(validator_.validate(schema, result));
        if (result.valid()) {
            validator_.set_root_schema(schema);
        }
        return result;
    }

    ValidationResult SchemaValidator::validate(const nlohmann::json &document) {
        ValidationResult result;
        result.set_patch(validator_.validate(document, result));
        return result;
    }


    ValidatorStore::ValidatorStore(DocumentStore document_store)
            : document_store_(std::move(document_store)) {

    }

    bool ValidatorStore::has_validator(const std::string &uri) {
        return validators_.contains(uri);
    }

    const SchemaValidator &ValidatorStore::get_validator(const std::string &uri) {
        try {
            return validators_.at(uri);
        } catch (const std::out_of_range &e) {
            throw maslex_JSONException("Document not found: " + uri);
        }
    }

    void ValidatorStore::deregister_schema(const std::string &uri) {
        validators_.erase(uri);
    }


    ValidationResult ValidatorStore::register_schema(const std::string &uri) {

        SchemaValidator validator{document_store_};
        auto result = validator.set_schema(uri);

        if (result.valid()) {
            validators_.emplace(uri, std::move(validator));
            return {};
        } else {
            return result;
        }
    }

}