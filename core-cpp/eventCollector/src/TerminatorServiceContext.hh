//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  TerminatorServiceContext.hh
//
//============================================================================//
#ifndef Events_TerminatorServiceContext_HH
#define Events_TerminatorServiceContext_HH

#include <string>
#include <vector>

namespace SWA {
class StackFrame;
class DomainMetaData;
}

namespace EVENTS {

class ParameterContext;
class TerminatorServiceContext
{
   public:
      TerminatorServiceContext(const ::SWA::StackFrame& frame, const std::size_t frameLevel, const ::SWA::DomainMetaData& domain);
     ~TerminatorServiceContext( );

      int getDomainId   () const ;
      int getServiceId  () const ;

      const bool        isMulti()            const;
      const std::string getDomainName()      const;
      const std::string getKeyLetters()      const;

      const std::string getServiceName()     const;
      const std::string getStackFrameLevel() const;

   private:
      const SWA::StackFrame&     frame;
      const std::size_t          frameLevel;
      const SWA::DomainMetaData& domain;
};

} // end EVENTS namespace

#endif
