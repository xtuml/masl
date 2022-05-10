domain JSON is

  public type JSONObject is dictionary;

  // Parse and serialize
  public service to_string(obj: in JSONObject) return anonymous string; pragma filename("to_string.obj.svc");
  public service to_string(str: in anonymous string) return anonymous string; pragma filename("to_string.str.svc");
  public service to_string(arr: in anonymous sequence of JSONObject) return anonymous string; pragma filename("to_string.arr.svc");

end domain; pragma build_set("UtilityDomains");
