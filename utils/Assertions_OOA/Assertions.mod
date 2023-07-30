domain Assertions is

  public exception AssertionFailure;

  public service assertTrue(axiom: in anonymous boolean, message: in anonymous string);

  public service assertEquals(expected: in anonymous string, actual: in anonymous string); pragma filename("assertEquals.string.svc");
  public service assertEquals(expected: in anonymous boolean, actual: in anonymous boolean); pragma filename("assertEquals.boolean.svc");
  public service assertEquals(expected: in anonymous real, actual: in anonymous real); pragma filename("assertEquals.real.svc");
  public service assertEquals(expected: in anonymous integer, actual: in anonymous integer); pragma filename("assertEquals.integer.svc");

end domain;
pragma service_domain(true);
pragma build_set("UtilityDomains");
