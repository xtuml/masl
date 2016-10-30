//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  DomainServiceContext.cc
//
//============================================================================//
#include <vector>
#include <string>

#include "DomainServiceContext.hh"

#include "swa/Stack.hh"
#include "swa/StackFrame.hh"
#include "swa/ProgramError.hh"

#include "metadata/MetaData.hh"

#include "boost/tuple/tuple.hpp"
#include "boost/lexical_cast.hpp"

namespace EVENTS {     

// ***************************************************************
// ***************************************************************
DomainServiceContext::DomainServiceContext(const SWA::StackFrame& frame, const std::size_t frameLevel, const SWA::DomainMetaData& domain):
        frame(frame),
        frameLevel(frameLevel),
        domain(domain)
{
   if (frame.getType() != SWA::StackFrame::DomainService)
   {
      throw SWA::ProgramError("Failed to create DomainServiceContext : not a domain based service ");
   }
}

// ***************************************************************
// ***************************************************************
DomainServiceContext::~DomainServiceContext( )
{

}

// ***************************************************************
// ***************************************************************
int DomainServiceContext::getDomainId () const
{
  return domain.getId();
} 

// ***************************************************************
// ***************************************************************
int DomainServiceContext::getServiceId  () const 
{
  return frame.getActionId();
}

// ***************************************************************
// ***************************************************************
int DomainServiceContext::getServiceType() const
{
   return domain.getService(frame.getActionId()).getType();
} 

// ***************************************************************
// ***************************************************************
const std::string DomainServiceContext::getDomainName()  const
{
   return domain.getName();
}

// ***************************************************************
// ***************************************************************
const std::string DomainServiceContext::getServiceName() const
{
   return domain.getService(frame.getActionId()).getName();
}

// ***************************************************************
// ***************************************************************
const std::string DomainServiceContext::getServiceTypeName() const
{
   std::string serviceType("illegal");
   const SWA::ServiceMetaData& service = domain.getService(frame.getActionId());

   switch ( service.getType() )
   {
       case SWA::ServiceMetaData::Scenario : serviceType = "scenario"; break;
       case SWA::ServiceMetaData::External : serviceType = "external"; break;
       case SWA::ServiceMetaData::Domain   : serviceType = "domain";   break;

       case SWA::ServiceMetaData::Object   :  // allow fall through
       case SWA::ServiceMetaData::Instance :  // allow fall through
       default :  break; // use default value;
   }
   return serviceType;
}

// ***************************************************************
// ***************************************************************
const std::string DomainServiceContext::getStackFrameLevel() const
{
   return boost::lexical_cast<std::string>(frameLevel);
}


} // end EVENTS namespace
