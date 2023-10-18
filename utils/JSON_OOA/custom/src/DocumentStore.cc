#include "DocumentStore.hh"
#include <ranges>
#include <uuid/uuid.h>
#include <nlohmann/json.hpp>
#include <iostream>
#include "JSON_OOA/__JSON__JSONException.hh"

namespace masld_JSON {

    using namespace std::literals;

    DocumentStore::DocumentStore()
            : aliases{std::make_shared < std::unordered_map < std::string, std::string >> ()},
              documents{std::make_shared < std::unordered_map < std::string, nlohmann::json >> ()} {}

    std::string DocumentStore::register_document(nlohmann::json document) {
        auto uri = get_id(document);
        if (uri.empty()) {
            uri = make_uri();
        } else {
            deregister_document(uri);
        }

        documents->emplace(uri, std::move(document));
        return uri;
    }

    std::vector <std::string> DocumentStore::list_documents() {
        std::vector <std::string> result;
        result.reserve(documents->size() + aliases->size());

        std::ranges::copy(*documents | std::views::keys, std::back_inserter(result));
        std::ranges::copy(*aliases | std::views::keys, std::back_inserter(result));

        return result;
    }

    void DocumentStore::register_document(std::string uri, nlohmann::json document) {
        deregister_document(uri);
        if (auto id = get_id(document); !id.empty() && id != uri) {
            aliases->emplace(id, uri);
        }
        documents->emplace(std::move(uri), std::move(document));
    }

    void DocumentStore::deregister_document(const std::string &uri) {
        if (aliases->contains(uri)) {
            documents->erase(aliases->at(uri));
            aliases->erase(uri);
        } else if (documents->contains(uri)) {
            if (auto id = get_id(documents->at(uri));
                    !id.empty() && aliases->contains(id) && aliases->at(id) == uri) {
                aliases->erase(id);
            }
            documents->erase(uri);
        }
    }

    bool DocumentStore::has_document(const std::string &uri) const {
        return aliases->contains(uri) || documents->contains(uri);
    }

    const nlohmann::json &DocumentStore::get_document(const std::string &uri) const {
        try {
            if (aliases->contains(uri)) {
                return documents->at(aliases->at(uri));
            } else {
                return documents->at(uri);
            }
        } catch (const std::out_of_range &e) {
            throw maslex_JSONException("Document not found: [" + uri + "]");
        }

    }

    std::string DocumentStore::get_id(const nlohmann::json &document) {
        if (document.contains("$id")) {
            return document["$id"].get<std::string>();
        } else {
            return "";
        }
    }

    std::string DocumentStore::make_uri() {
        uuid_t uuid;
        uuid_generate(uuid);

        char formatted[37];
        uuid_unparse(uuid, formatted);

        return "urn:uuid:"s + formatted;
    }

}