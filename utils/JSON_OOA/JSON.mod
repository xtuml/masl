domain JSON is

  public type JSONType is enum ( Object, Array, String, Real, Integer, Boolean, Null );

  public type JSONData is structure
    obj: dictionary;
    arr: sequence of string;
    str: string;
    real: real;
    int: integer;
    bool: boolean;
  end structure;

  public type JSONElement is structure
    kind: JSONType := Null;
    data: JSONData;
  end structure;

  public type JSONObject is dictionary of JSONElement;
  public type JSONArray is sequence of JSONElement;

  public exception JSONException;

  // parse and dump
  public service parse(json_string: in anonymous string) return JSONElement;
  public service dump(json_element: in JSONElement) return anonymous string; pragma filename("dump_element.svc");
  public service dump(json_object: in JSONObject) return anonymous string; pragma filename("dump_object.svc");
  public service dump(json_array: in JSONArray) return anonymous string; pragma filename("dump_array.svc");

  // access functions
  public service get_object(json_element: in JSONElement) return JSONObject;
  public service get_array(json_element: in JSONElement) return JSONArray;
  public service get_string(json_element: in JSONElement) return string;
  public service get_real(json_element: in JSONElement) return real;
  public service get_integer(json_element: in JSONElement) return integer;
  public service get_boolean(json_element: in JSONElement) return boolean;

  // type cast functions
  public service to_json(json_object: in JSONObject) return JSONElement; pragma filename("to_json_object.svc");
  public service to_json(json_array: in JSONArray) return JSONElement; pragma filename("to_json_array.svc");
  public service to_json(val: in string) return JSONElement; pragma filename("to_json_string.svc");
  public service to_json(val: in real) return JSONElement; pragma filename("to_json_real.svc");
  public service to_json(val: in integer) return JSONElement; pragma filename("to_json_integer.svc");
  public service to_json(val: in boolean) return JSONElement; pragma filename("to_json_boolean.svc");
  public service to_json(val: in timestamp) return JSONElement; pragma filename("to_json_timestamp.svc");
  public service to_json(val: in duration) return JSONElement; pragma filename("to_json_duration.svc");

end domain; pragma build_set("UtilityDomains");
