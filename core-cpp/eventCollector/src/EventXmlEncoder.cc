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

#include <algorithm>
#include "boost/bind/bind.hpp"

#include "eventCollector/ProcessContext.hh"
#include "EventWriter.hh"
#include "EventXmlEncoder.hh"

#include "EventContext.hh"
#include "EventFinishedContext.hh"
#include "StateServiceContext.hh"
#include "ObjectServiceContext.hh"
#include "DomainServiceContext.hh"
#include "TerminatorServiceContext.hh"

#include "boost/lexical_cast.hpp"

using namespace boost::placeholders;

namespace EVENTS {

// ***************************************************************
// ***************************************************************
EventXmlEncoder::EventXmlEncoder()
{

}

// ***************************************************************
// ***************************************************************
EventXmlEncoder::~EventXmlEncoder()
{

}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doWriteEncoded(const std::list< ::boost::shared_ptr<EventWriter> >& writers) const
{
   std::for_each(writers.begin(),writers.end(),::boost::bind(&EventWriter::write,_1,xmlBuffer));
   xmlBuffer.clear(); 
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doEncodeThreadStarted (const ProcessContext& processContext, const std::string& tag)
{
  xmlBuffer += "< thread ";
  xmlBuffer += "action=\"started\" ";
  xmlBuffer += "process=\"" + processContext.getName() + "\" ";
  formTimeAttribute(processContext,xmlBuffer);
  xmlBuffer += std::string(" value=\"") + tag + "\">\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doEncodeThreadFinished (const ProcessContext& processContext)
{
  // time stamp for event finishing is done by a separate 
  // call to doEncodeFinishedEvent
  xmlBuffer += "</thread>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doEncodeThreadAborted  (const ProcessContext& processContext)
{
  formEventTag  (processContext,"thread", "aborted", processContext.getFrameLevel(),isASingleLine,xmlBuffer);
  xmlBuffer += "</thread>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doEncodeDomainService (const ProcessContext& processContext, const DomainServiceContext& serviceContext)
{
   formEventTag  (processContext,"service", "called", processContext.getFrameLevel(),notASingleLine,xmlBuffer);
   formServiceTag(processContext,serviceContext.getDomainName(),serviceContext.getServiceName(),serviceContext.getServiceTypeName(),xmlBuffer);
   xmlBuffer += "</event>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doEncodeTerminatorService (const ProcessContext& processContext, const TerminatorServiceContext& context)
{
   formEventTag     (processContext,"service", "called", processContext.getFrameLevel(),notASingleLine,xmlBuffer);
   formTerminatorTag(processContext,context.getDomainName(),context.getKeyLetters(),context.getServiceName(),context.isMulti(),xmlBuffer);
   xmlBuffer += "</event>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doEncodeObjectService (const ProcessContext& processContext, const ObjectServiceContext& objectContext)
{
   formEventTag  (processContext,"service", "called", processContext.getFrameLevel(),notASingleLine, xmlBuffer);
   formObjectServiceTag (processContext,objectContext.getDomainName(),objectContext.getObjectName(),objectContext.getServiceName(),objectContext.getServiceType(),xmlBuffer);
   xmlBuffer += "</event>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doEncodeStateService (const ProcessContext& processContext, const StateServiceContext& stateContext)
{
   formEventTag  (processContext,"state", "entered",processContext.getFrameLevel(), notASingleLine,xmlBuffer);
   formStateServiceTag (processContext,stateContext.getDomainName(),stateContext.getObjectName(),stateContext.getStateName(),stateContext.getStateType(),xmlBuffer);
   xmlBuffer += "</event>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doEncodeGeneratingEvent (const ProcessContext& processContext, const EventContext&  context)
{
   formEventTag  (processContext,"event", "generating", processContext.getFrameLevel(),notASingleLine,xmlBuffer);
   formSignalTag(processContext,context,xmlBuffer);
   xmlBuffer += "</event>\n";

}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doEncodeProcessingEvent (const ProcessContext& processContext, const EventContext&  context)
{
   formEventTag (processContext,"event", "processing", processContext.getFrameLevel(),notASingleLine,xmlBuffer);
   formSignalTag(processContext,context,xmlBuffer);
   xmlBuffer += "</event>\n";

}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doEncodeTimerCreated (const ProcessContext& processContext, const uint32_t timerId)
{
  formEventTag     (processContext,"timer", "created", processContext.getFrameLevel(),notASingleLine, xmlBuffer);
  formTimerTag     (processContext, boost::lexical_cast<std::string>(timerId),xmlBuffer);
  xmlBuffer += "</event>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doEncodeTimerDeleted (const ProcessContext& processContext, const uint32_t timerId)
{
  formEventTag  (processContext,"timer", "deleted", processContext.getFrameLevel(),notASingleLine,xmlBuffer);
  formTimerTag  (processContext, boost::lexical_cast<std::string>(timerId),xmlBuffer);
  xmlBuffer += "</event>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doEncodeTimerFired (const ProcessContext& processContext, const uint32_t timerId)
{
  formEventTag  (processContext,"timer", "fired", processContext.getFrameLevel(),notASingleLine,xmlBuffer);
  formTimerTag  (processContext, boost::lexical_cast<std::string>(timerId),xmlBuffer);
  xmlBuffer += "</event>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doEncodeTimerCancelled (const ProcessContext& processContext, const uint32_t timerId)
{
  formEventTag  (processContext,"timer", "cancelled", processContext.getFrameLevel(),notASingleLine,xmlBuffer);
  formTimerTag  (processContext, boost::lexical_cast<std::string>(timerId),xmlBuffer);
  xmlBuffer += "</event>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doEncodeTimerSet (const ProcessContext& processContext, const uint32_t timerId)
{
  formEventTag  (processContext,"timer", "set", processContext.getFrameLevel(),notASingleLine, xmlBuffer);
  formTimerTag  (processContext, boost::lexical_cast<std::string>(timerId),xmlBuffer);
  xmlBuffer += "</event>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::doEncodeFinishedEvent  (const ProcessContext& processContext, const EventFinishedContext& context)
{
  formEventTag  (processContext,context.getType(), "finished", processContext.getFrameLevel(), isASingleLine,xmlBuffer);
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::formEventTag(const ProcessContext& processContext, const std::string& type, const std::string& action, const std::string& frame, const bool isSingleLine, std::string& buffer)
{
   buffer += "<event ";
   buffer += "type=\""   + type   + "\" ";
   buffer += "action=\"" + action + "\" ";
   buffer += "frame=\""  + frame  + "\" ";
   formTimeAttribute(processContext, buffer);
   if(isSingleLine) { buffer += "/"; }
   buffer += ">\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::formServiceTag(const ProcessContext& processContext, const std::string& domain, const std::string& name, const std::string& type, std::string& buffer)
{
   buffer += "<service ";
   buffer += "domain=\"" + domain + "\" ";
   buffer += "name=\""   + name   + "\" ";
   buffer += "type=\""   + type   + "\" ";
   buffer += "/>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::formTerminatorTag (const ProcessContext& processContext, const std::string& domain, const std::string& keyletters, const std::string& name, const bool isMulti, std::string& buffer)
{
   buffer += "<terminator ";
   buffer += "domain=\""      + domain      + "\" ";
   buffer += "keyletters=\""  + keyletters  + "\" ";
   buffer += "name=\""        + name        + "\" ";
   buffer += "ismulti=\""     + ::boost::lexical_cast<std::string>(isMulti) + "\" ";
   buffer += "/>\n";
}
// ***************************************************************
// ***************************************************************
void EventXmlEncoder::formObjectServiceTag (const ProcessContext& processContext,const std::string& domain,  const std::string& object, const std::string& name, const std::string& type,  std::string& buffer)
{
   buffer += "<service ";
   buffer += "domain=\"" + domain + "\" ";
   buffer += "object=\"" + object + "\" ";
   buffer += "name=\""   + name   + "\" ";
   buffer += "type=\""   + type   + "\" ";
   buffer += "/>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::formStateServiceTag (const ProcessContext& processContext, const std::string& domain, const std::string& object, const std::string& state, const std::string& type, std::string& buffer)
{
   buffer += "<state ";
   buffer += "domain=\"" + domain + "\" ";
   buffer += "object=\"" + object + "\" ";
   buffer += "state=\""  + state  + "\" ";
   buffer += "type=\""   + type  + "\" ";
   buffer += "/>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::formSignalTag(const ProcessContext& processContext, const EventContext& context, std::string& buffer)
{
   buffer += "<signal ";
   buffer += "domain=\"" + context.getDomain()    + "\" ";
   buffer += "name=\""   + context.getEventName() + "\" ";
   buffer += "type=\""   + context.getType()      + "\" ";
   buffer += ">\n";
   formSourceTag(processContext,context,buffer);
   formDestinationTag(processContext,context,buffer);
   buffer += "</signal>\n";

}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::formSourceTag(const ProcessContext& processContext, const EventContext& context, std::string& buffer)
{
   buffer += "<source ";
   buffer += "object=\""   + context.getSrcObjectName()     + "\" ";
   buffer += "instance=\"" + context.getSrcInstanceIdText() + "\" ";
   buffer += "/>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::formDestinationTag(const ProcessContext& processContext, const EventContext& context, std::string& buffer)
{
   buffer += "<destination ";
   buffer += "object=\""   + context.getDstObjectName()   + "\" ";
   buffer += "instance=\"" + context.getDstInstanceIdText() + "\" ";
   buffer += "/>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::formParameterTag  (const ProcessContext& processContext, const std::string& name,  const std::string& value, std::string& buffer)
{
   buffer += "<parameter ";
   buffer += "name=\""   + name   + "\" ";
   buffer += "value=\""  + value  + "\" ";
   buffer += "/>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::formTimerTag  (const ProcessContext& processContext, const std::string& value, std::string& buffer)
{
   buffer += "<timer ";
   buffer += "name=\"timerId\" ";
   buffer += "value=\""  + value  + "\" ";
   buffer += "/>\n";
}

// ***************************************************************
// ***************************************************************
void EventXmlEncoder::formTimeAttribute(const ProcessContext& processContext, std::string& buffer)
{
    buffer += "time=\"" + processContext.getTime() + "\" ";
}


} // end EVENTS namespace
