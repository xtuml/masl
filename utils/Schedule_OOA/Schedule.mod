domain Schedule is

  public type ServiceType is enum ( SCENARIO, EXTERNAL );

  public service run_service(service_type: in ServiceType, domain_name: in string, number: in integer);
  public service run_service(service_type: in ServiceType, domain_name: in string, number: in integer, input: in string);
  public service idle(timeout: in integer);
  public service pause();
  public service terminate();

end domain;
pragma service_domain(true);
pragma build_set("UtilityDomains");
