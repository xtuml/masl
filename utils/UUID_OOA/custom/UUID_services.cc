//
// File: NativeStubs.cc
//
// UK Crown Copyright (c) 2009. All Rights Reserved
//
#include "UUID_OOA/__UUID_services.hh"
#include "swa/String.hh"
#include <uuid/uuid.h>

namespace masld_UUID
{
  typedef char formatted_uuid[37];

  maslt_formatted_uuid masls_generate_formatted ( )
  {
    uuid_t uuid;
    uuid_generate(uuid);

    formatted_uuid formatted;
    uuid_unparse(uuid,formatted);

    return formatted;
  }

  maslt_raw_uuid masls_generate_raw ( )
  {
    uuid_t uuid;
    uuid_generate(uuid);
    return maslt_raw_uuid(uuid, uuid + sizeof(uuid));
  }

  maslt_formatted_uuid masls_format_raw ( const maslt_raw_uuid& maslp_raw )
  {
    if ( maslp_raw.size() != sizeof(uuid_t) )
    {
      throw SWA::ProgramError("UUID must be 16 bytes");
    }

    formatted_uuid formatted;
    uuid_unparse(&maslp_raw[0],formatted);
    return formatted;
  }

  maslt_raw_uuid masls_extract_raw ( const maslt_formatted_uuid& maslp_formatted )
  {
    uuid_t uuid;
    if ( uuid_parse ( maslp_formatted.c_str(), uuid ) )
    {
      throw SWA::ProgramError("Error extracting UUID");
    }
    return maslt_raw_uuid(uuid, uuid + sizeof(uuid));
  }

}
