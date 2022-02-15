//
// UK Crown Copyright (c) 2009. All Rights Reserved
//

domain CommandLine is
  
  type Conditionality  is enum ( Optional, Required );
  type Multiplicity    is enum ( Single, Multiple );

  //! Registers a command line option with the process
  //! option      - the option as it appears on the command line (eg "-d"). Options must begin with a "-" character.
  //! usage_text  - the text to display with usage information
  service register_flag ( option      : in anonymous string, 
                          usage_text  : in anonymous string );

  //! Registers a command line option that can take a value with the process
  //! option      - the option as it appears on the command line (eg "-d"). Options must begin with a "-" character.
  //! usage_text  - the text to display with usage information
  //! option_type - whether this option is required or not
  //! value_name  - the name of the value to print in usage information
  //! value_type  - whether the value is required or not
  //! multiplicity - whether or not the option can occurr multiple times
  service register_value ( option         : in anonymous string,
                           usage_text     : in anonymous string, 
                           option_type    : in Conditionality, 
                           value_name     : in anonymous string,
                           value_type     : in Conditionality, 
                           multiplicity   : in Multiplicity );


  //! Returns whether or not the named option was present on the command line
  function option_present ( option : in anonymous string ) return anonymous boolean;

  //! Returns the value given to the requested option, or empty string if it was not present
  function get_option_value ( option : in anonymous string ) return anonymous string;

  //! Returns the value given to the requested option, or default if it was not present
  function get_option_value ( option : in anonymous string, default : in anonymous string ) return anonymous string;

  //! Returns the list of values given for the requested option, or an empty sequence if it was not present
  function get_option_values ( option : in anonymous string ) return anonymous sequence of anonymous string;

  //! Returns the command that was used to invoke the current process
  function get_command () return anonymous string;

end domain;
