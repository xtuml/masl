#include "JSON_OOA/__JSON__JSONException.hh"
#include "JSON_OOA/__JSON_services.hh"
#include "JSON_OOA/__JSON_types.hh"
#include "swa/String.hh"
#include "swa/parse.hh"
#include "swa/console.hh"
#include "swa/Device.hh"
#include <nlohmann/json.hpp>
#include <fstream>
#include "DocumentStore.hh"

#include "helpers.hh"
#include "schema.hh"

namespace masld_JSON {

    DocumentStore &store() {
        static DocumentStore instance;
        return instance;

    }

    ValidatorStore &validators() {
        static ValidatorStore instance{store()};
        return instance;
    }

    namespace {
        nlohmann::json get_document(const maslt_URI &key) {
            return store().get_document(key.s_str());
        }

        nlohmann::json get_document(const maslt_URI &key, const SWA::String &pointer) {
            return store().get_document(key.s_str())[nlohmann::json::json_pointer(pointer.s_str())];
        }

        void add_document(const maslt_URI &key, nlohmann::json doc) {
            store().register_document(key.s_str(), std::move(doc));
        }

        SWA::String add_document(nlohmann::json doc) {
            return store().register_document(std::move(doc));
        }

        void remove_document(const maslt_URI &key) {
            store().deregister_document(key.s_str());
        }

    }

    void masls_add_document(const maslt_URI &key,
                            const ::SWA::String &document) {
        try {
            add_document(key, nlohmann::json::parse(document.s_str()));
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    const bool localServiceRegistration_masls_add_document = interceptor_masls_add_document::instance().registerLocal(
            &masls_add_document);

    void masls_overload1_add_document(const maslt_URI &key,
                                      const maslt_JSONElement &document) {
        try {
            add_document(key, make_json(document));
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    const bool localServiceRegistration_masls_overload1_add_document = interceptor_masls_overload1_add_document::instance().registerLocal(
            &masls_overload1_add_document);

    maslt_URI masls_overload2_add_document(const ::SWA::String &document) {
        try {
            return add_document(nlohmann::json::parse(document.s_str()));
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    maslt_URI masls_overload3_add_document(const maslt_JSONElement &document) {
        try {
            return add_document(make_json(document));
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }


    void masls_add_subdocument(const maslt_URI &key,
                               const maslt_URI &document,
                               const maslt_JSONPointer &pointer) {
        try {
            add_document(key, get_document(document, pointer));
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    const bool localServiceRegistration_masls_add_subdocument = interceptor_masls_add_subdocument::instance().registerLocal(
            &masls_add_subdocument);

    maslt_URI masls_overload1_add_subdocument(const maslt_URI &document,
                                              const maslt_JSONPointer &pointer) {
        try {
            return add_document(get_document(document, pointer));
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    void masls_load_document(const maslt_URI &key,
                             const ::SWA::String &path) {
        try {
            store().register_document(key.s_str(), nlohmann::json::parse(std::ifstream(path.s_str())));
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    const bool localServiceRegistration_masls_load_document = interceptor_masls_load_document::instance().registerLocal(
            &masls_load_document);


    maslt_URI masls_overload1_load_document(const ::SWA::String &path) {
        try {
            return store().register_document(nlohmann::json::parse(std::ifstream(path.s_str())));
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    void masls_overload2_load_document(const maslt_URI &key,
                                       const ::SWA::Device &input) {
        try {
            if (!input.getInputStream()) {
                throw SWA::IOError("Input Device Invalid");
            }
            store().register_document(key.s_str(), nlohmann::json::parse(*input.getInputStream()));
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    const bool localServiceRegistration_masls_overload2_load_document = interceptor_masls_overload2_load_document::instance().registerLocal(
            &masls_overload2_load_document);

    maslt_URI masls_overload3_load_document(const ::SWA::Device &input) {
        try {
            if (!input.getInputStream()) {
                throw SWA::IOError("Input Device Invalid");
            }
            return store().register_document(nlohmann::json::parse(*input.getInputStream()));
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    bool masls_has_document(const maslt_URI &key) {
        return store().has_document(key.s_str());
    }

    maslt_JSONElement masls_get_document(const maslt_URI &key) {
        return make_masl_element(get_document(key));
    }

    ::SWA::Sequence <maslt_URI> masls_list_documents(const maslt_URI &key) {
        return SWA::Sequence<maslt_URI>(store().list_documents());
    }

    void masls_remove_document(const maslt_URI &key) {
        remove_document(key);
    }


    const bool localServiceRegistration_masls_remove_document = interceptor_masls_remove_document::instance().registerLocal(
            &masls_remove_document);

    maslt_SchemaValidationResult masls_register_schema(const maslt_URI &id) {
        try {
            maslt_SchemaValidationResult masl_result;
            auto result = validators().register_schema(id);
            masl_result.set_masla_valid() = result.valid();
            std::transform(std::begin(result.errors()), std::end(result.errors()), std::back_inserter(masl_result.set_masla_errors()),
                           [](auto err) { return maslt_SchemaValidationError{err.first.to_string(), err.second}; });
            return masl_result;
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }

    }

    void masls_deregister_schema(const maslt_URI &id) {
        validators().deregister_schema(id);
    }

    const bool localServiceRegistration_masls_deregister_schema = interceptor_masls_deregister_schema::instance().registerLocal(
            &masls_deregister_schema);


    maslt_SchemaValidationResult masls_validate(const maslt_URI &document,
                                                const maslt_URI &schema,
                                                bool patch_defaults) {
        try {
            const auto& validator = validators().get_validator(schema);
            const auto& doc = store().get_document(document);
            const auto& result = validator.validate(doc);
            maslt_SchemaValidationResult masl_result;
            masl_result.set_masla_valid() = result.valid();
            std::transform(std::begin(result.errors()), std::end(result.errors()), std::back_inserter(masl_result.set_masla_errors()),
                           [](auto err) { return maslt_SchemaValidationError{err.first.to_string(), err.second}; });
            if ( result.valid()) {
                if ( patch_defaults ) {
                    add_document(document,doc.patch(result.patch()));
                }
            }
            return masl_result;
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }

    }

    maslt_JSONElement masls_parse(const ::SWA::String &json_string) {
        try {
            nlohmann::json j = nlohmann::json::parse(json_string.s_str());
            return make_masl_element(j);
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    ::SWA::String masls_overload1_dump(const maslt_URI &document, bool pretty) {
        try {
            nlohmann::json j = store().get_document(document.s_str());
            return j.dump(pretty ? 2 : -1);
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    maslt_JSONElement masls_pointer(const maslt_URI &document, const ::SWA::String &json_pointer) {
        try {
            return make_masl_element(store().get_document(document.s_str())[nlohmann::json::json_pointer(
                    json_pointer.s_str())]);
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    SWA::String masls_pointer_string(const maslt_URI &document, const ::SWA::String &json_pointer) {
        try {
            return store().get_document(document.s_str())[nlohmann::json::json_pointer(
                    json_pointer.s_str())].get<SWA::String>();
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    double masls_pointer_real(const maslt_URI &document, const ::SWA::String &json_pointer) {
        try {
            return store().get_document(document.s_str())[nlohmann::json::json_pointer(
                    json_pointer.s_str())].get<double>();
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    std::int64_t masls_pointer_integer(const maslt_URI &document, const ::SWA::String &json_pointer) {
        try {
            return store().get_document(document.s_str())[nlohmann::json::json_pointer(
                    json_pointer.s_str())].get<std::int64_t>();
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    bool masls_pointer_boolean(const maslt_URI &document, const ::SWA::String &json_pointer) {
        try {
            return store().get_document(document.s_str())[nlohmann::json::json_pointer(
                    json_pointer.s_str())].get<bool>();
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    maslt_JSONElement
    masls_overload1_pointer(const SWA::String &json_string, const ::SWA::String &json_pointer) {
        try {
            return make_masl_element(nlohmann::json::parse(json_string.s_str())[nlohmann::json::json_pointer(
                    json_pointer.s_str())]);
        } catch (const nlohmann::json::exception &e) {
            throw maslex_JSONException(e.what());
        }
    }

    maslt_URI masls_add_patch(const maslt_URI &target,
                              const maslt_URI &patch) {
        auto doc = get_document(target);
        doc.patch_inplace(get_document(patch));
        return add_document(doc);
    }

    void masls_patch(const maslt_URI &target,
                     const maslt_URI &patch) {
        auto doc = get_document(target);
        doc.patch_inplace(get_document(patch));
        add_document(target, doc);
    }

    const bool localServiceRegistration_masls_patch = interceptor_masls_patch::instance().registerLocal(
            &masls_patch);

    maslt_URI masls_add_merge_patch(const maslt_URI &target,
                                    const maslt_URI &patch) {
        auto doc = get_document(target);
        doc.merge_patch(get_document(patch));
        return add_document(doc);
    }

    void masls_merge_patch(const maslt_URI &target,
                           const maslt_URI &patch) {
        auto doc = get_document(target);
        doc.merge_patch(get_document(patch));
        add_document(target, doc);
    }

    const bool localServiceRegistration_masls_merge_patch = interceptor_masls_merge_patch::instance().registerLocal(
            &masls_merge_patch);


}
