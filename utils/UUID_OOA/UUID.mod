//
// UK Crown Copyright (c) 2009. All Rights Reserved
//

domain UUID is

  type formatted_uuid is string;

  type raw_uuid is sequence of byte;

  function generate_formatted () return formatted_uuid;

  function generate_raw () return raw_uuid;

  function format_raw ( raw : in raw_uuid ) return formatted_uuid;

  function extract_raw ( formatted : in formatted_uuid ) return raw_uuid;

end domain;
pragma service_domain(true);
pragma build_set("UtilityDomains");
