//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  ObjectServiceContext.cc
//
//============================================================================//
#include <vector>
#include <string>

#include "ObjectServiceContext.hh"

#include "swa/Stack.hh"
#include "swa/StackFrame.hh"
#include "swa/ProgramError.hh"

#include "metadata/MetaData.hh"

#include "boost/tuple/tuple.hpp"
#include "boost/lexical_cast.hpp"

namespace EVENTS {     

// ***************************************************************
// ***************************************************************
ObjectServiceContext::ObjectServiceContext(const SWA::StackFrame& frame, const std::size_t frameLevel, const SWA::DomainMetaData& domain, const SWA::ObjectMetaData& object):
        frame(frame),
        frameLevel(frameLevel),
        domain(domain),
        object(object)
{
   if (frame.getType() != SWA::StackFrame::ObjectService)
   {
      throw SWA::ProgramError("Failed to create ObjectServiceContext : not an object service ");
   }
}

// ***************************************************************
// ***************************************************************
ObjectServiceContext::~ObjectServiceContext( )
{

}

// ***************************************************************
// ***************************************************************
int ObjectServiceContext::getDomainId () const
{
   return domain.getId();
} 

// ***************************************************************
// ***************************************************************
int ObjectServiceContext::getObjectId () const
{
  return object.getId();
} 

// ***************************************************************
// ***************************************************************
int ObjectServiceContext::getServiceId  () const 
{
  return object.getService(frame.getActionId()).getId();
}

// ***************************************************************
// ***************************************************************
const std::string ObjectServiceContext::getDomainName()  const
{
   return domain.getName();
}

// ***************************************************************
// ***************************************************************
const std::string ObjectServiceContext::getObjectName() const
{
  return object.getName();
}

// ***************************************************************
// ***************************************************************
const std::string ObjectServiceContext::getServiceName() const
{
   return object.getService(frame.getActionId()).getName();
}

// ***************************************************************
// ***************************************************************
const std::string ObjectServiceContext::getServiceType() const
{
   std::string serviceType("illegal");
   switch(object.getService(frame.getActionId()).getType())
   {
       case SWA::ServiceMetaData::Object   : serviceType = "object";   break;
       case SWA::ServiceMetaData::Instance : serviceType = "instance"; break;

       case SWA::ServiceMetaData::Scenario : // fall through
       case SWA::ServiceMetaData::External : // fall through
       case SWA::ServiceMetaData::Domain   : // fall through
       default : break;                      // use default value
   }
   return serviceType;
}

// ***************************************************************
// ***************************************************************
const std::string ObjectServiceContext::getStackFrameLevel() const
{
   return boost::lexical_cast<std::string>(frameLevel);
}

} // end EVENTS namespace
