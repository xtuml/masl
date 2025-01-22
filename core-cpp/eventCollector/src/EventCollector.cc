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

#include <iostream>
#include <algorithm>

#include "swa/Stack.hh"
#include "swa/Timestamp.hh"
#include "swa/Process.hh"
#include "swa/Exception.hh"
#include "swa/StackFrame.hh"
#include "metadata/MetaData.hh"

#include "boost/bind/bind.hpp"
#include "boost/tuple/tuple.hpp"
#include "boost/tuple/tuple_io.hpp"

#include "EventXmlEncoder.hh"
#include "EventFileWriter.hh"
#include "EventConsoleWriter.hh"
#include "EventInspectorWriter.hh"

#include "eventCollector/EventCollector.hh"
#include "EventContext.hh"
#include "StateServiceContext.hh"
#include "EventFinishedContext.hh"
#include "ObjectServiceContext.hh"
#include "DomainServiceContext.hh"
#include "TerminatorServiceContext.hh"

using namespace boost::placeholders;

namespace
{
  bool initialiseCollector()
  {
    static ::EVENTS::EventCollector collector;
    return true;
  }

  struct init
  {
    init()
    {
      SWA::Process::getInstance().registerStartedListener(&initialiseCollector);
    }
  } x;

}

namespace EVENTS {

// ***************************************************************
// ***************************************************************
EventCollector::EventCollector()
{

   ::boost::shared_ptr<EventEncoder> xmlEncoder(new EventXmlEncoder);

   xmlEncoder->addWriter(::boost::shared_ptr<EventWriter>(new EventFileWriter));
   xmlEncoder->addWriter(::boost::shared_ptr<EventWriter>(new EventConsoleWriter));

   try
   {
     xmlEncoder->addWriter(::boost::shared_ptr<EventWriter>(new EventInspectorWriter));
   }
   catch(const SKT::SocketException& se)
   {
     std::cerr << "EventInspectorWriter initialisation failed : " << se.report() << std::endl;
     std::cerr << "EventInspectorWriter has been disabled ...  " << std::endl;
   }
   eventEncoders.push_back(xmlEncoder);

   registerMonitor();
   connectToMonitor();

   SWA::Process::getInstance().registerThreadStartedListener   ( boost::bind(&EventCollector::threadStarted,   boost::ref(*this),_1) );
   SWA::Process::getInstance().registerThreadCompletedListener ( boost::bind(&EventCollector::threadCompleted, boost::ref(*this))    );
   SWA::Process::getInstance().registerThreadAbortedListener   ( boost::bind(&EventCollector::threadAborted,   boost::ref(*this))    );
   SWA::Process::getInstance().registerShutdownListener        ( boost::bind(&EventCollector::shutdown,        boost::ref(*this))    );
}

// ***************************************************************
// ***************************************************************
EventCollector::~EventCollector()
{
}

// ***************************************************************
// ***************************************************************
std::string EventCollector::getName() { return "EventCollector"; }

// ***************************************************************
// ***************************************************************
void EventCollector::threadStarted(const std::string& tag)
{
   std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeThreadStarted,_1,processContext,tag));
}

// ***************************************************************
// ***************************************************************
void EventCollector::threadCompleted()
{
   const EventFinishedContext finishedContext("thread");
   std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeFinishedEvent,_1, processContext,finishedContext));
   std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeThreadFinished,_1,processContext));
}

// ***************************************************************
// ***************************************************************
void EventCollector::threadAborted ()
{
   std::cout << "[ Thread aborted ]" << std::endl;
   std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeThreadAborted,_1,processContext));
}

// ***************************************************************
// ***************************************************************
void EventCollector::startProcessingEventQueue()
{
   //std::cout << "[ Start Processing EventQueue ]" << std::endl;
}

// ***************************************************************
// ***************************************************************
void EventCollector::endProcessingEventQueue()
{
   //std::cout << "[ Finished Processing EventQueue ] " << std::endl;
}


// ***************************************************************
// ***************************************************************
void EventCollector::enteredAction()
{
   SWA::Stack& maslStack = SWA::Stack::getInstance();
   const SWA::StackFrame& currentFrame = maslStack.top();
   const std::size_t frameLevel = maslStack.getStackFrames().size();

   try {

      const SWA::DomainMetaData& actionsDomain = SWA::ProcessMetaData::getProcess().getDomain(currentFrame.getDomainId());
      switch (currentFrame.getType())
      {
         case SWA::StackFrame::DomainService :
         {
            const DomainServiceContext domainServiceContext(currentFrame,frameLevel,actionsDomain);  
            std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeDomainService,_1,processContext,domainServiceContext));
         }
         break;

         case SWA::StackFrame::TerminatorService : 
         {
            const TerminatorServiceContext terminatorServiceContext(currentFrame,frameLevel,actionsDomain);  
            std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeTerminatorService,_1,processContext,terminatorServiceContext));
         }
         break;


         case SWA::StackFrame::ObjectService : 
         {
            const ObjectServiceContext objectServiceContext(currentFrame,frameLevel,actionsDomain,actionsDomain.getObject(currentFrame.getObjectId()));  
            std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeObjectService,_1,processContext,objectServiceContext));
         }
         break;

         case SWA::StackFrame::StateAction   :
         {
            const StateServiceContext stateServiceContext(currentFrame,frameLevel,actionsDomain,actionsDomain.getObject(currentFrame.getObjectId()));  
            std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeStateService,_1,processContext,stateServiceContext));
         }
         break;

         default:
         {
           std::cout << "EventCollector::enteredAction : encoutered invalid action type in switch statement " << currentFrame.getType()  << std::endl;
           return;
         }
         break;
      }
   }
   catch(::SWA::Exception& e){
      std::cout << "EventCollector::enteredAction : meta data lookup failed for action : "  << boost::make_tuple(currentFrame.getDomainId(),currentFrame.getObjectId(),currentFrame.getActionId()) << " : "<< e.what() <<  std::endl;
   }
}

// ***************************************************************
// ***************************************************************
void EventCollector::leavingAction()
{

}

// ***************************************************************
// ***************************************************************
void EventCollector::leftAction()
{
   SWA::Stack& maslStack = SWA::Stack::getInstance();
   const SWA::StackFrame& currentFrame = maslStack.top();

   try {

      switch (currentFrame.getType())
      {
         case SWA::StackFrame::DomainService :
         {
             const EventFinishedContext finishedContext("service");  
             std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeFinishedEvent,_1,processContext,finishedContext));
         }
         break;

         case SWA::StackFrame::ObjectService : 
         {
             const EventFinishedContext finishedContext("service");  
             std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeFinishedEvent,_1,processContext,finishedContext));
         }
         break;

         case SWA::StackFrame::StateAction  :
         {
              const EventFinishedContext finishedContext("state");  
              std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeFinishedEvent,_1,processContext,finishedContext));
         }
         break;

         default:
         {
           std::cout << "EventCollector::leftAction : encoutered invalid action type in switch statement " << currentFrame.getType()  << std::endl;
           return;
         }
         break;
      }
   }
   catch(::SWA::Exception& e){
      std::cout << "EventCollector::leftAction : meta data lookup failed for action : "  << boost::make_tuple(currentFrame.getDomainId(),currentFrame.getObjectId(),currentFrame.getActionId()) << std::endl;
   }
}

// ***************************************************************
// ***************************************************************
void EventCollector::exceptionRaised ( const std::string& message )
{

}

// ***************************************************************
// ***************************************************************
void EventCollector::exceptionCaught()
{

}

// ***************************************************************
// ***************************************************************
void EventCollector::exceptionPropagated()
{

}

// ***************************************************************
// ***************************************************************
void EventCollector::generatingEvent ( int domainId, int objectId, int eventId, int destObjectId, SWA::IdType instanceId )
{
   EventContext eventContext(domainId,objectId,eventId,destObjectId,instanceId);
   std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeGeneratingEvent,_1,processContext,eventContext));   
}

// ***************************************************************
// ***************************************************************
void EventCollector::processingEvent ( int domainId, int objectId, int eventId, int destObjectId, SWA::IdType instanceId )
{
   EventContext eventContext(domainId,objectId,eventId,destObjectId,instanceId);
   std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeProcessingEvent,_1,processContext,eventContext));   
}

// ***************************************************************
// ***************************************************************
void EventCollector::generatingEvent ( int domainId, int objectId, int eventId, int destObjectId )
{
   EventContext eventContext(domainId,objectId,eventId,destObjectId);
   std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeGeneratingEvent,_1,processContext,eventContext));     
}

// ***************************************************************
// ***************************************************************
void EventCollector::processingEvent ( int domainId, int objectId, int eventId, int destObjectId )
{
   EventContext eventContext(domainId,objectId,eventId,destObjectId);
   std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeProcessingEvent,_1,processContext,eventContext));   
}

// ***************************************************************
// ***************************************************************
void EventCollector::firingTimer ( int timerId )
{
   std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeTimerFired,_1,processContext,timerId));
}

// ***************************************************************
// ***************************************************************
void EventCollector::creatingTimer ( int timerId )
{
   std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeTimerCreated,_1,processContext,timerId));
}

// ***************************************************************
// ***************************************************************
void EventCollector::deletingTimer ( int timerId )
{
   std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeTimerDeleted,_1,processContext,timerId));
}

// ***************************************************************
// ***************************************************************
void EventCollector::cancellingTimer ( int timerId )
{
   std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeTimerCancelled,_1,processContext,timerId));
}

// ***************************************************************
// ***************************************************************
void EventCollector::settingTimer ( int timerId, const SWA::Timestamp& timeout, const SWA::Duration& period, const boost::shared_ptr< ::SWA::Event >& event)
{
   std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::writeTimerSet,_1,processContext,timerId));
}

// ***************************************************************
// ***************************************************************
void EventCollector::shutdown()
{
  std::for_each(eventEncoders.begin(),eventEncoders.end(),::boost::bind(&EventEncoder::shutdown,_1));   
}

} // end EVENTS namespace
