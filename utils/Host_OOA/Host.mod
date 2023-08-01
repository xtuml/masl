//
// UK Crown Copyright (c) 2009. All Rights Reserved
//

domain Host is 
  
  type hostname is string;
  type uid is integer;
  type gid is integer;
  type username is string;
  type groupname is string;


  //! Get current host name
  function get_hostname () return hostname;


  //! Get logged in users username
  function get_login () return username;

  //! Get actual user id of the current process
  function get_real_uid () return uid;

  //! Get effective user id of the current process
  function get_effective_uid () return uid;

  //! Get actual group id of the current process
  function get_real_gid () return gid;

  //! Get effective group id of the current process
  function get_effective_gid () return gid;
  
  //! Get all group ids of the current process
  function get_supplementary_gids () return anonymous set of gid;


  //! Get name of supplied user id
  function get_username ( uid : in uid ) return username;

  //! Get id of supplied user name
  function get_uid ( username : in username ) return uid;

  //! Get name of supplied group id
  function get_groupname ( gid : in gid ) return groupname;

  //! Get id of supplied group name
  function get_gid ( groupname : in groupname ) return gid;

  //! Get default group id of the supplied user id
  function get_user_gid ( uid : in uid ) return gid;

end domain;
pragma service_domain(true);
