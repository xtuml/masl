domain IDM is

  public service set_topic_config(topic: in string, param_name: in string, param_value: in string);
  public service set_topic_config(topic: in string, param_name: in string, param_value: in integer);
  public service set_topic_config(topic: in string, param_name: in string, param_value: in boolean);

end domain;
pragma service_domain(true);
