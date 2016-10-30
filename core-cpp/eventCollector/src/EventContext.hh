//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  EventContext.hh
//
//============================================================================//
#ifndef Events_EventContext_HH
#define Events_EventContext_HH

#include <string>
#include <vector>

#include "swa/types.hh"

namespace SWA {
class StackFrame;
class DomainMetaData;
class ObjectMetaData;
class StateMetaData;
}

namespace EVENTS {

class EventContext
{
   public:
      EventContext( int domainId, int srcObjectId, int eventId, int destObjectId, SWA::IdType instanceId);
      EventContext( int domainId, int srcObjectId, int eventId, int destObjectId);
     ~EventContext( );

      int getDomainId      () const;
      int getEventId       () const;
      int getSrcObjectId   () const;
      int getDstObjectId   () const;
      int getDstInstanceId () const;

      bool isNormalEvent() const;
      
      const std::string getType()       const;
      const std::string getDomain()     const;
      const std::string getEventName()  const;

      const std::string getSrcInstanceIdText() const;
      const std::string getDstInstanceIdText() const;

      const std::string getSrcObjectName() const;
      const std::string getDstObjectName() const;

   private:
       int domainId;
       int srcObjectId;
       int eventId;
       int dstObjectId;
       int instanceId;

};

} // end EVENTS namespace

#endif
