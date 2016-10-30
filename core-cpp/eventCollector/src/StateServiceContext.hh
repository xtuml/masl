//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  StateServiceContext.hh
//
//============================================================================//
#ifndef Events_StateServiceContext_HH
#define Events_StateServiceContext_HH

#include <string>
#include <vector>

namespace SWA {
class StackFrame;
class DomainMetaData;
class ObjectMetaData;
class StateMetaData;
}

namespace EVENTS {

class StateServiceContext
{
   public:
      StateServiceContext(const ::SWA::StackFrame& frame, const std::size_t frameLevel, const SWA::DomainMetaData& domain, const SWA::ObjectMetaData& object);
     ~StateServiceContext( );

      int getDomainId    () const;
      int getObjectId    () const;
      int getServiceId   () const;
      int getStateTypeId () const;

      const std::string getDomainName()      const;
      const std::string getObjectName()      const;
      const std::string getStateName()       const;
      const std::string getStackFrameLevel() const;
      const std::string getStateType      () const;

   private:
      const SWA::StackFrame&     frame;
      const std::size_t          frameLevel;
      const SWA::DomainMetaData& domain;
      const SWA::ObjectMetaData& object;
};

} // end EVENTS namespace

#endif
