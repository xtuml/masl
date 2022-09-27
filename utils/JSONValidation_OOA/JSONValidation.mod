domain JSONValidation is

  //! This service validates an input JSON element against a given JSON schema.
  //! The result is a JSONObject representing the result of validation. The
  //! result follows the "detailed" output structure defined here:
  //! https://json-schema.org/draft/2020-12/json-schema-core.html#name-basic.
  public service validate(input: in JSON::JSONElement, schema: in JSON::JSONElement) return JSON::JSONObject;
  public service validate(input: in JSON::JSONElement, schema_path: in anonymous string) return JSON::JSONObject; pragma filename("validate_load_schema.svc");

  //! Private domain services to support validation
  private service validate(input: in JSON::JSONElement, schema_element: in JSON::JSONElement, metaschema: in JSON::JSONElement, instance_path: in anonymous string, schema_path: in anonymous string, defs: in JSON::JSONObject) return JSON::JSONObject; pragma filename("validate_internal.svc");
  private service create_validation_error(keyword_location: in anonymous string, instance_location: in anonymous string, error_message: in anonymous string) return JSON::JSONElement;
  private service create_validation_error(keyword_location: in anonymous string, instance_location: in anonymous string, error_message: in anonymous string, suberrors: in JSON::JSONArray) return JSON::JSONElement; pragma filename("create_validation_error_with_suberrors.svc");
  private service create_validation_annotation(schema_location: in anonymous string, instance_location: in anonymous string, keyword: in anonymous string, result: in JSON::JSONElement) return JSON::JSONElement;

end domain; pragma build_set("UtilityDomains");
