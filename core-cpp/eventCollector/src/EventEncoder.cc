//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  EventCollector.cc
//
//============================================================================//
#include <algorithm>

#include "EventWriter.hh"
#include "EventEncoder.hh"

#include "boost/bind.hpp"

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
