#ifndef masld_JSON_HELPERS_HH
#define masld_JSON_HELPERS_HH


namespace masld_JSON {

    maslt_JSONElement make_masl_element(const nlohmann::json &j);
    nlohmann::json make_json(const maslt_JSONElement &element);

}


#endif
