//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  EventFinishedContext.hh
//
//============================================================================//
#ifndef Events_EventFinishedContext_HH
#define Events_EventFinishedContext_HH

#include <string>

namespace SWA {
class StateMetaData;
class ObjectMetaData;
class DomainMetaData;
}

namespace EVENTS {

class EventFinishedContext
{
   public:
      EventFinishedContext(const std::string& type);
     ~EventFinishedContext( );

      const std::string getType()       const;

   private:
      const std::string          type;
};

} // end EVENTS namespace

#endif
