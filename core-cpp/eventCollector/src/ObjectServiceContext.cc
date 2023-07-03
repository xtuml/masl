/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 * Classification: UK OFFICIAL
 * ----------------------------------------------------------------------------
 */

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
