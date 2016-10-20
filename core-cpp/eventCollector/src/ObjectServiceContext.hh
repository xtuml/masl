//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  ObjectServiceContext.hh
//
//============================================================================//
#ifndef Events_ObjectServiceContext_HH
#define Events_ObjectServiceContext_HH

#include <string>
#include <vector>

namespace SWA {
class StackFrame;
class DomainMetaData;
class ObjectMetaData;
}

namespace EVENTS {

class ObjectServiceContext
{
   public:
      ObjectServiceContext(const ::SWA::StackFrame& frame, const std::size_t frameLevel, const SWA::DomainMetaData& domain, const ::SWA::ObjectMetaData& object);
     ~ObjectServiceContext( );

      int getDomainId   () const;
      int getObjectId   () const;
      int getServiceId  () const;

      const std::string getDomainName()      const;
      const std::string getObjectName()      const;
      const std::string getServiceName()     const;
      const std::string getServiceType()     const;
      const std::string getStackFrameLevel() const;

   private:
      const SWA::StackFrame&     frame;
      const std::size_t          frameLevel;
      const SWA::DomainMetaData& domain;
      const SWA::ObjectMetaData& object;
};

} // end EVENTS namespace

#endif
