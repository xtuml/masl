domain JSON is

  public exception JSONException;

  //! The valid JSON types. A distinction is made between 'real' and 'integer' numbers.
  public type JSONType is enum ( Object, Array, String, Real, Integer, Boolean, Null );

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
    obj: dictionary;
    arr: sequence of string;
    str: string;
    real: real;
    int: integer;
    bool: boolean;
  end structure;

  //! This structure represents a single JSON value. The default value for the
  //! 'kind' field is 'Null', therefore the default value of this structure
  //! represents JSON null. A well-formed value will have the 'kind' field set
  //! and the corresponding field of the 'data' field populated with the value.
  public type JSONElement is structure
    kind: JSONType := Null;
    data: JSONData;
    raw: string;
  end structure;

  //! A 'JSONObject' is a map of string keys to instances of the 'JSONElement' structure.
  public type JSONObject is dictionary of JSONElement;

  //! A 'JSONObject' is an ordered sequence of instances of the 'JSONElement' structure.
  public type JSONArray is sequence of JSONElement;

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
  public service get_string(json_element: in JSONElement) return string;
  public service get_raw_string(json_element: in JSONElement) return string;
  public service get_real(json_element: in JSONElement) return real;
  public service get_integer(json_element: in JSONElement) return integer;
  public service get_boolean(json_element: in JSONElement) return boolean;

  //! These service abstract the process of creating an instance of the
  //! 'JSONElement' structure from 'JSONObject', 'JSONArray', and MASL core
  //! types. For 'timestamp' and 'duration', the value is first converted a MASL
  //! string and then treated as JSON string.
  // Developer Note: for 'JSONObject' and 'JSONArray', 'dump' is being called
  // to convert the nested structures back to a JSON string for storage.
  public service to_json(json_object: in JSONObject) return JSONElement; pragma filename("to_json_object.svc");
  public service to_json(json_array: in JSONArray) return JSONElement; pragma filename("to_json_array.svc");
  public service to_json(val: in string) return JSONElement; pragma filename("to_json_string.svc");
  public service to_json(val: in real) return JSONElement; pragma filename("to_json_real.svc");
  public service to_json(val: in integer) return JSONElement; pragma filename("to_json_integer.svc");
  public service to_json(val: in boolean) return JSONElement; pragma filename("to_json_boolean.svc");
  public service to_json(val: in timestamp) return JSONElement; pragma filename("to_json_timestamp.svc");
  public service to_json(val: in duration) return JSONElement; pragma filename("to_json_duration.svc");

end domain;
pragma service_domain(true);
pragma build_set("UtilityDomains");
