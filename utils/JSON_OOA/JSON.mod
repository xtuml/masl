domain JSON is

  public exception JSONException;

  //! The valid JSON types. A distinction is made between 'real' and 'integer' numbers.
  public type JSONType is enum ( Object, Array, String, Real, Integer, Boolean, Null );

  public type JSONElement;

  //! A 'JSONObject' is a map of string keys to instances of the 'JSONElement' structure.
  public type JSONObject is dictionary of JSONElement;

  //! A 'JSONObject' is an ordered sequence of instances of the 'JSONElement' structure.
  public type JSONArray is sequence of JSONElement;

  //! This structure represents the data within a single JSON value. It is
  //! intended to be used as a 'union' -- that is only one of the 6 fields
  //! should be set for any given value. The value of the other 5 fields has no
  //! meaning. For JSON null values, none of fields should be accessed.
  //! Developer Note: It would be ideal for 'obj' to be of type 'JSONObject' and
  //! 'arr' to be of type 'JSONArray'. This was not possible with the code
  //! generator at the time of this writing, so each contains string-typed
  //! values. The values are JSON encoded strings which must be parsed before
  //! they can be used.
  public type JSONData is structure
    obj: JSONObject;
    arr: JSONArray;
    str: anonymous string;
    real: anonymous real;
    int: anonymous long_integer;
    bool: anonymous boolean;
  end structure;

  //! This structure represents a single JSON value. The default value for the
  //! 'kind' field is 'Null', therefore the default value of this structure
  //! represents JSON null. A well-formed value will have the 'kind' field set
  //! and the corresponding field of the 'data' field populated with the value.
  public type JSONElement is structure
    kind: JSONType := Null;
    data: JSONData;
  end structure;

  public type URI is string;

  public type JSONPointer is string;

  public type SchemaValidationError is structure
        location: JSONPointer;
        message: string;
  end structure;

  public type SchemaValidationResult is structure
    valid: boolean;
    errors: sequence of SchemaValidationError;
  end structure;

  public service add_document(id : in URI, document : in anonymous string );
  public service add_document(id : in URI, document : in JSONElement );

  public service add_document(document : in anonymous string ) return URI;
  public service add_document(document : in JSONElement ) return URI;

  public service add_subdocument(id : in URI, parent : in URI, pointer: in JSONPointer );
  public service add_subdocument(parent : in URI, pointer: in JSONPointer ) return URI;

  public service load_document(id : in URI, path : in anonymous string );
  public service load_document(path : in anonymous string ) return URI;
  public service load_document(id : in URI, input : in anonymous device );
  public service load_document(input : in anonymous device ) return URI;

  public service has_document(id : in URI ) return anonymous boolean;
  public service get_document(id : in URI ) return JSONElement;
  public service list_documents() return sequence of URI;

  public service remove_document(id : in URI );

  public service register_schema(id: in URI) return SchemaValidationResult;
  public service deregister_schema(id: in URI);

  //! Validate the source document against the supplied schema.
  //! The source document may optionally be updated with any default values supplied by the schema.
  public service validate(document: in URI, schema: in URI, patch_defaults: in anonymous boolean ) return SchemaValidationResult;

  //! Run an RFC6902 patch on the target document, adding the patched document to the store
  public service add_patch(target: in URI, patch : in URI ) return URI;

  //! Run an RFC6902 patch on the target document, updating it in the store.
  public service patch(target: in URI, patch : in URI );

  //! Run an RFC7386 merge patch on the target document, adding the patched document to the store
  public service add_merge_patch(target: in URI, patch : in URI ) return URI;

  //! Run an RFC7386 merge patch on the target document, updating it in the store.
  public service merge_patch(target: in URI, patch : in URI );

  //! Dump the supplied document to a string.
  public service dump(document: in URI) return anonymous string; pragma filename("dump_uri.svc");
  public service dump(document: in URI, pretty : in anonymous boolean) return anonymous string;

  //! Return the JSON element pointed to by the supplied JSON Pointer
  public service pointer(document: in URI, json_pointer : in JSONPointer ) return JSONElement;
  public service pointer_string(document: in URI, json_pointer : in JSONPointer ) return anonymous string;
  public service pointer_real(document: in URI, json_pointer : in JSONPointer ) return anonymous real;
  public service pointer_integer(document: in URI, json_pointer : in JSONPointer ) return anonymous long_integer;
  public service pointer_boolean(document: in URI, json_pointer : in JSONPointer ) return anonymous boolean;

  //! This service parses a JSON string and returns a single instance of
  //! 'JSONElement' representing the top-level element of the JSON string. If
  //! the string contains multiple top-level elements, all following the first
  //! will be discarded. Invalid JSON results in a 'JSONException' being raised.
  public service parse(json_string: in anonymous string) return JSONElement;

  //! These services dump an instance of 'JSONObject', 'JSONArray', or 'JSONElement' to a JSON string.
  public service dump(json_object: in JSONObject) return anonymous string; pragma filename("dump_object.svc");
  public service dump(json_object: in JSONObject, pretty: in anonymous boolean) return anonymous string; pragma filename("dump_object_pretty.svc");
  public service dump(json_array: in JSONArray) return anonymous string; pragma filename("dump_array.svc");
  public service dump(json_array: in JSONArray, pretty: in anonymous boolean) return anonymous string; pragma filename("dump_array_pretty.svc");
  public service dump(json_element: in JSONElement) return anonymous string; pragma filename("dump_element.svc");
  public service dump(json_element: in JSONElement, pretty: in anonymous boolean) return anonymous string; pragma filename("dump_element_pretty.svc");

  //! These services abstract the process of extracting a specific type from an
  //! instance of 'JSONElement'. If the 'kind' field of the given element does
  //! not match the return type of the service, a 'JSONException' is raised.
  //! These services can be thought of as "casting" the generic 'JSONElement'
  //! type to the desired MASL type.
  //! Developer Note: within the 'get_object' and 'get_array' services, 'parse'
  //! is being called a second time to convert from serialized JSON strings to
  //! 'JSONObject' and 'JSONArray'.
  public service get_object(json_element: in JSONElement) return JSONObject;
  public service get_array(json_element: in JSONElement) return JSONArray;
  public service get_string(json_element: in JSONElement) return anonymous string;
  public service get_real(json_element: in JSONElement) return anonymous real;
  public service get_integer(json_element: in JSONElement) return anonymous integer;
  public service get_boolean(json_element: in JSONElement) return anonymous boolean;

  //! These service abstract the process of creating an instance of the
  //! 'JSONElement' structure from 'JSONObject', 'JSONArray', and MASL core
  //! types. For 'timestamp' and 'duration', the value is first converted a MASL
  //! string and then treated as JSON string.
  // Developer Note: for 'JSONObject' and 'JSONArray', 'dump' is being called
  // to convert the nested structures back to a JSON string for storage.
  public service to_json(json_object: in JSONObject) return JSONElement; pragma filename("to_json_object.svc");
  public service to_json(json_array: in JSONArray) return JSONElement; pragma filename("to_json_array.svc");
  public service to_json(val: in anonymous string) return JSONElement; pragma filename("to_json_string.svc");
  public service to_json(val: in anonymous real) return JSONElement; pragma filename("to_json_real.svc");
  public service to_json(val: in anonymous integer) return JSONElement; pragma filename("to_json_integer.svc");
  public service to_json(val: in anonymous boolean) return JSONElement; pragma filename("to_json_boolean.svc");
  public service to_json(val: in anonymous timestamp) return JSONElement; pragma filename("to_json_timestamp.svc");
  public service to_json(val: in anonymous duration) return JSONElement; pragma filename("to_json_duration.svc");
  public service to_json(val: in anonymous sequence of anonymous string) return JSONElement; pragma filename("to_json_seq_of_string.svc");
  public service to_json(val: in anonymous dictionary of anonymous string) return JSONElement; pragma filename("to_json_dict_of_string.svc");

end domain;
pragma service_domain(true);
