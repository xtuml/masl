//
// UK Crown Copyright (c) 2009. All Rights Reserved
//
#include "Environment_OOA/__Environment_services.hh"
#include "swa/String.hh"
#include "swa/ProgramError.hh"
#include <stdlib.h>
#include <errno.h>

namespace masld_Environment
{
  const bool localServiceRegistration_masls_setenv = interceptor_masls_setenv::instance().registerLocal( &masls_setenv );
  const bool localServiceRegistration_masls_unsetenv = interceptor_masls_unsetenv::instance().registerLocal( &masls_unsetenv );

  void masls_setenv ( const maslt_variable_name& maslp_name,
                      const ::SWA::String&       maslp_value )
  {
    if ( setenv(maslp_name.c_str(), maslp_value.c_str(), true) ) throw SWA::ProgramError(::std::strerror(errno));
  }

  void masls_unsetenv ( const maslt_variable_name& maslp_name )
  {
    if ( unsetenv(maslp_name.c_str()) ) throw SWA::ProgramError(::std::strerror(errno));
  }

  ::SWA::String masls_getenv ( const maslt_variable_name& maslp_name )
  {
    const char* val = getenv(maslp_name.c_str());
    if ( val ) return val;
    else return "";
  }

  bool masls_isset ( const maslt_variable_name& maslp_name )
  {
    return getenv(maslp_name.c_str());
  }

}
