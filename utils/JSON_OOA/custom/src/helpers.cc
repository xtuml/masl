#include "JSON_OOA/__JSON_types.hh"
#include <nlohmann/json.hpp>

namespace masld_JSON {

    maslt_JSONElement make_masl_element(const nlohmann::json &j) {
        maslt_JSONElement result;
        switch (j.type()) {
            case nlohmann::json::value_t::object:
                result.set_masla_kind() = maslt_JSONType::masle_Object;
                for (const auto &[key, value]: j.items()) {
                    result.set_masla_data().set_masla_obj()[key] = make_masl_element(value);
                }
                break;
            case nlohmann::json::value_t::array:
                result.set_masla_kind() = maslt_JSONType::masle_Array;
                for (const auto &[key, value]: j.items()) {
                    result.set_masla_data().set_masla_arr() += make_masl_element(value);
                }
                break;
            case nlohmann::json::value_t::string:
                result.set_masla_kind() = maslt_JSONType::masle_String;
                j.get_to(result.set_masla_data().set_masla_str());
                break;
            case nlohmann::json::value_t::number_float:
                result.set_masla_kind() = maslt_JSONType::masle_Real;
                j.get_to(result.set_masla_data().set_masla_real());
                break;
            case nlohmann::json::value_t::number_integer:
                [[fallthrough]]
            case nlohmann::json::value_t::number_unsigned:
                result.set_masla_kind() = maslt_JSONType::masle_Integer;
                j.get_to(result.set_masla_data().set_masla_int());
                break;
            case nlohmann::json::value_t::boolean:
                result.set_masla_kind() = maslt_JSONType::masle_Boolean;
                j.get_to(result.set_masla_data().set_masla_bool());
                break;
            case nlohmann::json::value_t::null:
                result.set_masla_kind() = maslt_JSONType::masle_Null;
                break;
        }
        return result;
    }

    nlohmann::json make_json(const maslt_JSONElement &element) {
        switch (element.get_masla_kind().getIndex()) {
            case maslt_JSONType::index_masle_Object: {
                nlohmann::json result;
                for (const auto &[k, v]: element.get_masla_data().get_masla_obj()) {
                    result[k.s_str()] = make_json(v);
                }
                return result;
            }
            case maslt_JSONType::index_masle_Array: {
                nlohmann::json result;
                for (const auto &v: element.get_masla_data().get_masla_arr()) {
                    result.emplace_back(make_json(v));
                }
                return result;
            }
            case maslt_JSONType::index_masle_String:
                return element.get_masla_data().get_masla_str();
            case maslt_JSONType::index_masle_Real:
                return element.get_masla_data().get_masla_real();
            case maslt_JSONType::index_masle_Integer:
                return element.get_masla_data().get_masla_int();
            case maslt_JSONType::index_masle_Boolean:
                return element.get_masla_data().get_masla_bool();
            default:
                return {};
        }
    }

}