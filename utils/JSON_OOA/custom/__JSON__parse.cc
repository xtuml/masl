//
// File: __JSON__parse.cc
//
#include "JSON_OOA/__JSON__JSONException.hh"
#include "JSON_OOA/__JSON_services.hh"
#include "JSON_OOA/__JSON_types.hh"
#include "swa/String.hh"
#include "swa/parse.hh"
#include "swa/console.hh"

#include <string.h>
#include <stdio.h>
#include "jsmn.h"

#define jsmn_get_text(STR, TOK) std::string(STR, TOK.start, TOK.end - TOK.start)

namespace masld_JSON
{

  // utility function to get the total number of tokens until the next sibling
  int get_total_size(const int i, const std::vector<jsmntok_t> tokens) {
    int total_size = 1;
    for (int j = 0; j < tokens[i].size; j++) {
      total_size += get_total_size(i + total_size, tokens);
    }
    return total_size;
  }

  // process a single token from a stream of tokens and return a JSONElement
  maslt_JSONElement parse_token(const int index, const std::vector<jsmntok_t> tokens, const std::string json_string) {

    maslt_JSONElement json_element;
    jsmntok_t token = tokens[index];
    std::string token_text = jsmn_get_text(json_string, token);

    switch (token.type) {
      case JSMN_OBJECT:
        json_element.set_masla_kind() = maslt_JSONType::masle_Object;
        // loop through all subtokens. do not navigate grandchildren.
        for (int i = index + 1, j = 0; j < token.size; j++) {
          std::string key = jsmn_get_text(json_string, tokens[i]);
          std::string val = jsmn_get_text(json_string, tokens[i+1]);
          if (tokens[i+1].type == JSMN_STRING) {
            val = "\"" + val + "\"";  // add double quotes around literal strings
          }
          json_element.set_masla_data().set_masla_obj().setValue(key) = val;
          i += get_total_size(i, tokens);
        }
        break;
      case JSMN_ARRAY:
        json_element.set_masla_kind() = maslt_JSONType::masle_Array;
        // loop through all subtokens. do not navigate grandchildren.
        for (int i = index + 1, j = 0; j < token.size; j++) {
          std::string val = jsmn_get_text(json_string, tokens[i]);
          if (tokens[i].type == JSMN_STRING) {
            val = "\"" + val + "\"";  // add double quotes around literal strings
          }
          json_element.set_masla_data().set_masla_arr() += val;
          i += get_total_size(i, tokens);
        }
        break;
      case JSMN_STRING:
        json_element.set_masla_kind() = maslt_JSONType::masle_String;
        json_element.set_masla_data().set_masla_str() = token_text;
        break;
      case JSMN_PRIMITIVE:
        if (token_text == "true" || token_text == "false") {
          json_element.set_masla_kind() = maslt_JSONType::masle_Boolean;
          json_element.set_masla_data().set_masla_bool() = token_text == "true";
        } else if (token_text == "null") {
          json_element.set_masla_kind() = maslt_JSONType::masle_Null;
        } else {
          // try to parse integer
          try {
            int32_t ival = ::SWA::parse(token_text, &ival);
            json_element.set_masla_kind() = maslt_JSONType::masle_Integer;
            json_element.set_masla_data().set_masla_int() = ival;
          } catch (const ::SWA::Exception&) {
            // try to parse real
            try {
              double rval = ::SWA::parse(token_text, &rval);
              json_element.set_masla_kind() = maslt_JSONType::masle_Real;
              json_element.set_masla_data().set_masla_real() = rval;
            } catch (const ::SWA::Exception&) {
              throw maslex_JSONException("Invalid JSON primitive: " + token_text);
            }
          }
        }
        break;
      default:
        throw maslex_JSONException("Unrecognized JSON token: " + boost::lexical_cast<std::string>(token.type));
        break;
    }

    return json_element;

  }

  maslt_JSONElement masls_parse(const ::SWA::String& maslp_json_string) {

    // initialise parser
    jsmn_parser p;
    jsmn_init(&p);
    std::vector<jsmntok_t> tokens(256);

    // parse the JSON
    long n = jsmn_parse(&p, maslp_json_string.c_str(), maslp_json_string.size(), tokens.data(), tokens.size());
    while (n == JSMN_ERROR_NOMEM) {
      tokens.resize(tokens.size() * 2);
      n = jsmn_parse(&p, maslp_json_string.c_str(), maslp_json_string.size(), tokens.data(), tokens.size());
    }

    if (n < 0) {
      throw maslex_JSONException("Could not parse invalid JSON string");
    } else if (n == 0) {
      throw maslex_JSONException("Nothing to parse");
    } else {

      // DEBUG
      //for (int i = 0; i < n; i++) {
      //  jsmntok_t token = tokens[i];
      //  ::SWA::console() << token.type << "  " << jsmn_get_text(maslp_json_string, token) << "\n";
      //}

      // process the first token
      return parse_token(0, tokens, maslp_json_string);

    }

  }

}
