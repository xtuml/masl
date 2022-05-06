domain JSON is

  public type JSONObject is dictionary;

  // Parse and serialize
  public service to_string1(obj: in JSONObject) return string; pragma filename("to_string.obj.svc");
  public service to_string2(str: in string) return string; pragma filename("to_string.str.svc");

end domain;
pragma service_domain(true);
pragma build_set("UtilityDomains");
