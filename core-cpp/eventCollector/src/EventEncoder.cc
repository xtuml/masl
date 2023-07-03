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
#include "EventWriter.hh"
#include "EventEncoder.hh"

#include "boost/bind/bind.hpp"
using namespace boost::placeholders;

namespace EVENTS {

// ***************************************************************
// ***************************************************************
EventEncoder::EventEncoder()
{

}

// ***************************************************************
// ***************************************************************
EventEncoder::~EventEncoder()
{

}

// ***************************************************************
// ***************************************************************
void EventEncoder::addWriter(const ::boost::shared_ptr<EventWriter>& writer)
{
   writers.push_back(writer);
}

// ***************************************************************
// ***************************************************************
void EventEncoder::writeThreadStarted  (const ProcessContext& processContext, const std::string& tag)
{
   if (aWriterIsEnabled() == true){
       doEncodeThreadStarted(processContext,tag);
       doWriteEncoded(writers);
   }
}

// ***************************************************************
// ***************************************************************
void EventEncoder::writeThreadFinished (const ProcessContext& processContext)
{
   if (aWriterIsEnabled() == true){
       doEncodeThreadFinished(processContext);
       doWriteEncoded(writers);
   }
}

// ***************************************************************
// ***************************************************************
void EventEncoder::writeThreadAborted  (const ProcessContext& processContext)
{
   if (aWriterIsEnabled() == true){
       doEncodeThreadAborted(processContext);
       doWriteEncoded(writers);
   }
}

// ***************************************************************
// ***************************************************************
void EventEncoder::writeDomainService (const ProcessContext& processContext, const DomainServiceContext& context)
{
   if (aWriterIsEnabled() == true){
       doEncodeDomainService(processContext,context);
       doWriteEncoded(writers);
   }
}

// ***************************************************************
// ***************************************************************
void EventEncoder::writeTerminatorService (const ProcessContext& processContext, const TerminatorServiceContext& context)
{
   if (aWriterIsEnabled() == true){
       doEncodeTerminatorService(processContext,context);
       doWriteEncoded(writers);
   }
}

// ***************************************************************
// ***************************************************************
void EventEncoder::writeObjectService (const ProcessContext& processContext, const ObjectServiceContext& context)
{
   if (aWriterIsEnabled() == true){
       doEncodeObjectService(processContext,context);
       doWriteEncoded(writers);
   }
}

// ***************************************************************
// ***************************************************************
void EventEncoder::writeStateService  (const ProcessContext& processContext, const StateServiceContext&  context)
{
   if (aWriterIsEnabled() == true){
       doEncodeStateService(processContext,context);
       doWriteEncoded(writers);
   }
}

// ***************************************************************
// ***************************************************************
void EventEncoder::writeGeneratingEvent (const ProcessContext& processContext, const EventContext& eventContext)
{
   if (aWriterIsEnabled() == true){
       doEncodeGeneratingEvent(processContext,eventContext);
       doWriteEncoded(writers);
   }
}

// ***************************************************************
// ***************************************************************
void EventEncoder::writeProcessingEvent (const ProcessContext& processContext, const EventContext& eventContext)
{
   if (aWriterIsEnabled() == true){
       doEncodeProcessingEvent(processContext,eventContext);
       doWriteEncoded(writers);
   }
}

// ***************************************************************
// ***************************************************************
void EventEncoder::writeTimerCreated (const ProcessContext& processContext, const uint32_t timerId)
{
   if (aWriterIsEnabled() == true){
       doEncodeTimerCreated(processContext,timerId);
       doWriteEncoded(writers);
   }
}

// ***************************************************************
// ***************************************************************
void EventEncoder::writeTimerDeleted (const ProcessContext& processContext, const uint32_t timerId)
{
   if (aWriterIsEnabled() == true){
       doEncodeTimerDeleted(processContext,timerId);
       doWriteEncoded(writers);
   }
}

// ***************************************************************
// ***************************************************************
void EventEncoder::writeTimerFired (const ProcessContext& processContext, const uint32_t timerId)
{
   if (aWriterIsEnabled() == true){
       doEncodeTimerFired(processContext,timerId);
       doWriteEncoded(writers);
   }
}

// ***************************************************************
// ***************************************************************
void EventEncoder::writeTimerCancelled (const ProcessContext& processContext, const uint32_t timerId)
{
   if (aWriterIsEnabled() == true){
       doEncodeTimerCancelled(processContext,timerId);
       doWriteEncoded(writers);
   }
}

// ***************************************************************
// ***************************************************************
void EventEncoder::writeTimerSet (const ProcessContext& processContext, const uint32_t timerId)
{
   if (aWriterIsEnabled() == true){
       doEncodeTimerSet(processContext,timerId);
       doWriteEncoded(writers);
   }
}

// ***************************************************************
// ***************************************************************
void EventEncoder::writeFinishedEvent (const ProcessContext& processContext, const EventFinishedContext& context)
{
   if (aWriterIsEnabled() == true){
       doEncodeFinishedEvent(processContext,context);
       doWriteEncoded(writers);
   }
}

// ***************************************************************
// ***************************************************************
void EventEncoder::shutdown ()
{
   std::for_each (writers.begin(),writers.end(),boost::bind(&EventWriter::shutdown,_1));
}

// ***************************************************************
// ***************************************************************
bool EventEncoder::aWriterIsEnabled()
{
   std::size_t enabledCount = std::count_if (writers.begin(),writers.end(),boost::bind(&EventWriter::isEnabled,_1));
   return enabledCount;
}

} // end EVENTS namespace
