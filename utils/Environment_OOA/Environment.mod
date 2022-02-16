//
// UK Crown Copyright (c) 2009. All Rights Reserved
//

domain Environment is

  //! Type to hold the variable names
  type variable_name is string;

  //! Sets the named environment variable to the specified value. If 
  //! name is empty or contains an '=' character, a 
  //! program_error will be thrown 
  service setenv ( name : in variable_name, value : in anonymous string );

  //! Unset the named environment variable. 
  //! If no value was previously set then it is ignored. 
  service unsetenv ( name : in variable_name );

  //! Gets the value contained in the named environment 
  //! variable. If no value has been set, then the empty string 
  //! will be returned. If it is important whether the value is 
  //! unset or set to the empty sring, use isset. 
  function getenv ( name : in variable_name ) return anonymous string;

  //! Returns true if the named environment variable has been 
  //! given a value, false otherwise. 
  function isset ( name : in variable_name ) return anonymous boolean;

end domain; pragma build_set("UtilityDomains");
