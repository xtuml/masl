domain Assertions is

  public exception AssertionFailure;

  public service assertTrue(axiom: in anonymous boolean, message: in anonymous string);

end domain; pragma build_set("UtilityDomains");
