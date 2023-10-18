#ifndef UTILS_SCHEMA_HH
#define UTILS_SCHEMA_HH

#include <unordered_map>
#include <unordered_set>
#include <nlohmann/json-schema.hpp>
#include <string>
#include "DocumentStore.hh"

using namespace std::literals;

namespace masld_JSON {
    using Uri = nlohmann::json_uri;

    class ValidationResult : public nlohmann::json_schema::basic_error_handler {
    public:
        using ErrorReport = std::vector<std::pair<nlohmann::json::json_pointer,std::string>>;

        void error(const nlohmann::json::json_pointer &pointer,
                   const nlohmann::json &instance,
                   const std::string &message) override;

        bool valid() const;

        const ErrorReport& errors() const;

        const nlohmann::json& patch() const;

        void set_patch(nlohmann::json patch);

    private:
        nlohmann::json patch_;
        ErrorReport errors_;
    };

    class SchemaValidator {
    public:

        SchemaValidator(DocumentStore store);

        ValidationResult set_schema(const std::string &uri);

        ValidationResult validate(const nlohmann::json &document);

    private:
        DocumentStore document_store_;
        nlohmann::json_schema::json_validator validator_;
    };


    class ValidatorStore {
    public:
        ValidatorStore(DocumentStore document_store);

        bool has_validator(const std::string& uri);

        const SchemaValidator& get_validator(const std::string& uri);

        void deregister_schema(const std::string& uri );

        ValidationResult register_schema(const std::string& uri);

    private:
        std::unordered_map<std::string,SchemaValidator> validators_;
        DocumentStore document_store_;
    };

}
#endif
