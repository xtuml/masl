//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  DomainServiceContext.cc
//
//============================================================================//
#include "EventFinishedContext.hh"

#include "boost/lexical_cast.hpp"

namespace EVENTS {     

// ***************************************************************
// ***************************************************************
EventFinishedContext::EventFinishedContext(const std::string& type):
     type(type)
{

}

// ***************************************************************
// ***************************************************************
EventFinishedContext::~EventFinishedContext()
{

}

// ***************************************************************
// ***************************************************************
const std::string EventFinishedContext::getType() const
{
   return type;
}

} // end EVENTS namespace
