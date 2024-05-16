#ifndef masld_JSON_DocumentStore_HH
#define masld_JSON_DocumentStore_HH

#include <string>
#include <vector>
#include <unordered_map>
#include <nlohmann/json.hpp>

namespace masld_JSON {
    class DocumentStore {
    public:
        DocumentStore();

        std::string register_document(nlohmann::json document);

        std::vector<std::string> list_documents();

        void register_document(std::string uri, nlohmann::json document);

        void deregister_document(const std::string& uri);

        bool has_document(const std::string& uri) const;

        const nlohmann::json& get_document(const std::string& uri) const;

    private:
        static std::string get_id(const nlohmann::json& document);

        std::string make_uri();

    private:
        std::shared_ptr<std::unordered_map<std::string, std::string>> aliases;
        std::shared_ptr<std::unordered_map<std::string, nlohmann::json>> documents;
    };
}

#endif
