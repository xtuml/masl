//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  DomainServiceContext.hh
//
//============================================================================//
#ifndef Events_DomainServiceContext_HH
#define Events_DomainServiceContext_HH

#include <string>
#include <vector>

namespace SWA {
class StackFrame;
class DomainMetaData;
}

namespace EVENTS {

class ParameterContext;
class DomainServiceContext
{
   public:
      DomainServiceContext(const ::SWA::StackFrame& frame, const std::size_t frameLevel, const ::SWA::DomainMetaData& domain);
     ~DomainServiceContext( );

      int getDomainId   () const ;
      int getServiceId  () const ;
      int getServiceType() const ;

      const std::string getDomainName()      const;
      const std::string getServiceName()     const;
      const std::string getServiceTypeName() const;
      const std::string getStackFrameLevel() const;

   private:
      const SWA::StackFrame&     frame;
      const std::size_t          frameLevel;
      const SWA::DomainMetaData& domain;
};

} // end EVENTS namespace

#endif
