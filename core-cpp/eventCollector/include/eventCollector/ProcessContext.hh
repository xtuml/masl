//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  ProcessContext.hh
//
//============================================================================//
#ifndef Events_ProcessContext_HH
#define Events_ProcessContext_HH

#include <string>
#include <unistd.h>

namespace EVENTS {

class ProcessContext
{
    public:
       ProcessContext();
      ~ProcessContext();

      pid_t  getPid()  const;
      const std::string   getName() const;
      const std::string   getTime() const;
      const std::string   getDomainName(int domainId) const;
      const std::string   getFrameLevel() const;
};

}

#endif
