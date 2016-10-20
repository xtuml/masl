//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  StateServiceContext.cc
//
//============================================================================//
#include <vector>
#include <string>

#include "StateServiceContext.hh"

#include "swa/Stack.hh"
#include "swa/StackFrame.hh"
#include "swa/ProgramError.hh"

#include "metadata/MetaData.hh"

#include "boost/tuple/tuple.hpp"
#include "boost/lexical_cast.hpp"

namespace EVENTS {     

// ***************************************************************
// ***************************************************************
StateServiceContext::StateServiceContext(const SWA::StackFrame& frame, const std::size_t frameLevel, const SWA::DomainMetaData& domain, const SWA::ObjectMetaData& object):
        frame(frame),
        frameLevel(frameLevel),
        domain(domain),
        object(object)
{
   if (frame.getType() != SWA::StackFrame::StateAction)
   {
      throw SWA::ProgramError("Failed to create  StateServiceContext : not a state action ");
   }
}

// ***************************************************************
// ***************************************************************
StateServiceContext::~StateServiceContext( )
{

}

// ***************************************************************
// ***************************************************************
int StateServiceContext::getDomainId () const
{
   return domain.getId();
} 

// ***************************************************************
// ***************************************************************
int StateServiceContext::getObjectId () const
{
  return object.getId();
} 

// ***************************************************************
// ***************************************************************
int StateServiceContext::getStateTypeId () const
{
  return object.getState(frame.getActionId()).getType();
} 

// ***************************************************************
// ***************************************************************
int StateServiceContext::getServiceId  () const 
{
  return object.getService(frame.getActionId()).getId();
}

// ***************************************************************
// ***************************************************************
const std::string StateServiceContext::getDomainName()  const
{
   return domain.getName();
}

// ***************************************************************
// ***************************************************************
const std::string StateServiceContext::getObjectName() const
{
  return object.getName();
}

// ***************************************************************
// ***************************************************************
const std::string StateServiceContext::getStateName() const
{
   return object.getState(frame.getActionId()).getName();
}

// ***************************************************************
// ***************************************************************
const std::string StateServiceContext::getStateType() const
{
   std::string stateType("unknown");
   switch (object.getState(frame.getActionId()).getType())
   {
      case SWA::StateMetaData::Assigner : stateType = "assigner"; break;
      case SWA::StateMetaData::Start    : stateType = "start";    break;
      case SWA::StateMetaData::Normal   : stateType = "normal";   break;
      case SWA::StateMetaData::Creation : stateType = "creation"; break;
      case SWA::StateMetaData::Terminal : stateType = "terminal"; break;
      default       : // let the default unknown value drop through 
                      break;
   };
   return stateType;
}

// ***************************************************************
// ***************************************************************
const std::string StateServiceContext::getStackFrameLevel() const
{
   return boost::lexical_cast<std::string>(frameLevel);
}

} // end EVENTS namespace
