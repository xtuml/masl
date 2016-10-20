//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  TerminatorServiceContext.cc
//
//============================================================================//
#include <vector>
#include <string>

#include "TerminatorServiceContext.hh"

#include "swa/Stack.hh"
#include "swa/StackFrame.hh"
#include "swa/ProgramError.hh"

#include "metadata/MetaData.hh"

#include "boost/tuple/tuple.hpp"
#include "boost/lexical_cast.hpp"

namespace EVENTS {     

// ***************************************************************
// ***************************************************************
TerminatorServiceContext::TerminatorServiceContext(const SWA::StackFrame& frame, const std::size_t frameLevel, const SWA::DomainMetaData& domain):
        frame(frame),
        frameLevel(frameLevel),
        domain(domain)
{
   if (frame.getType() != SWA::StackFrame::TerminatorService)
   {
      throw SWA::ProgramError("Failed to create TerminatorServiceContext : not a  based terminator service ");
   }
}

// ***************************************************************
// ***************************************************************
TerminatorServiceContext::~TerminatorServiceContext( )
{

}

// ***************************************************************
// ***************************************************************
int TerminatorServiceContext::getDomainId () const
{
  return domain.getId();
} 

// ***************************************************************
// ***************************************************************
int TerminatorServiceContext::getServiceId  () const 
{
  return frame.getActionId();
}

// ***************************************************************
// ***************************************************************
const std::string TerminatorServiceContext::getKeyLetters() const
{
  return domain.getTerminator(frame.getObjectId()).getKeyLetters();
} 

// ***************************************************************
// ***************************************************************
const bool TerminatorServiceContext::isMulti() const
{
   return domain.getTerminator(frame.getObjectId()).getService(frame.getActionId()).getType() == SWA::ServiceMetaData::ProjectTerminator;
}

// ***************************************************************
// ***************************************************************
const std::string TerminatorServiceContext::getDomainName()  const
{
   return domain.getName();
}

// ***************************************************************
// ***************************************************************
const std::string TerminatorServiceContext::getServiceName() const
{
   return domain.getTerminator(frame.getObjectId()).getService(frame.getActionId()).getName();
}

// ***************************************************************
// ***************************************************************
const std::string TerminatorServiceContext::getStackFrameLevel() const
{
   return boost::lexical_cast<std::string>(frameLevel);
}


} // end EVENTS namespace
