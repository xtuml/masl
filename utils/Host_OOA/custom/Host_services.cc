//
// UK Crown Copyright (c) 2009. All Rights Reserved
//
#include <stdint.h>
#include "Host_OOA/__Host_services.hh"
#include "Host_OOA/__Host_types.hh"
#include "swa/Sequence.hh"
#include "swa/String.hh"
#include "swa/ProgramError.hh"

#include <unistd.h>
#include <errno.h>
#include <pwd.h>
#include <grp.h>
#include <sys/types.h>
#include <sys/param.h>

namespace masld_Host
{
  maslt_hostname masls_get_hostname ( )
  {
    char result[MAXHOSTNAMELEN+1];
    if ( gethostname(result, MAXHOSTNAMELEN+1) ) throw SWA::ProgramError(strerror(errno));
    return result;
  }

  maslt_uid masls_get_real_uid ( )
  {
    return getuid();
  }

  maslt_uid masls_get_effective_uid ( )
  {
    return geteuid();
  }

  ::SWA::String masls_get_login ( )
  {
    errno = 0;
    char* login = getlogin();
std::cerr << errno << ": " << strerror(errno) << std::endl;
    if ( !login ) throw SWA::ProgramError(strerror(errno));
    return login;
  }

  maslt_username masls_get_username ( maslt_uid maslp_uid )
  {
    errno = 0;
    passwd* result = getpwuid ( maslp_uid );
    if ( !result )
    {
      if ( errno == 0 ) throw SWA::ProgramError("uid not found");
      else throw SWA::ProgramError(strerror(errno));
    }
    else
    {
      return result->pw_name;
    } 
  }

  maslt_uid masls_get_uid ( const maslt_username& maslp_username )
  {
    errno = 0;
    passwd* result = getpwnam ( maslp_username.c_str() );
    if ( !result )
    {
      if ( errno == 0 ) throw SWA::ProgramError("username not found");
      else throw SWA::ProgramError(strerror(errno));
    }
    else
    {
      return result->pw_uid;
    } 
  }

  maslt_gid masls_get_user_gid ( maslt_uid maslp_uid )
  {
    errno = 0;
    passwd* result = getpwuid ( maslp_uid );
    if ( !result )
    {
      if ( errno == 0 ) throw SWA::ProgramError("username not found");
      else throw SWA::ProgramError(strerror(errno));
    }
    else
    {
      return result->pw_gid;
    } 
  }

  ::SWA::Set<maslt_gid> masls_get_supplementary_gids ( )
  {
    int size = getgroups(0,0);
    std::vector<gid_t> gids(size);
    getgroups(size,&gids[0]);
    return ::SWA::Set<maslt_gid>(gids);
  }


  maslt_gid masls_get_real_gid ( )
  {
    return getgid();
  }

  maslt_gid masls_get_effective_gid ( )
  {
    return getegid();
  }

  maslt_groupname masls_get_groupname ( maslt_gid maslp_gid )
  {
    errno = 0;
    group* result = getgrgid ( maslp_gid );
    if ( !result )
    {
      if ( errno == 0 ) throw SWA::ProgramError("gid not found");
      else throw SWA::ProgramError(strerror(errno));
    }
    else
    {
      return result->gr_name;
    } 
  }

  maslt_gid masls_get_gid ( const maslt_groupname& maslp_groupname )
  {
    errno = 0;
    group* result = getgrnam ( maslp_groupname.c_str() );
    if ( !result )
    {
      if ( errno == 0 ) throw SWA::ProgramError("username not found");
      else throw SWA::ProgramError(strerror(errno));
    }
    else
    {
      return result->gr_gid;
    } 
  }

}
