domain JSON is

  public type JSONObject is dictionary;

  // Parse and serialize
  public service to_string(obj: in JSONObject) return string; pragma filename("to_string.obj.svc");
  public service to_string(str: in string) return string; pragma filename("to_string.str.svc");

end domain; pragma build_set("UtilityDomains");
