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

#ifndef Events_EventCollector_HH
#define Events_EventCollector_HH

#include <string>

#include "swa/ProcessMonitor.hh"
#include "boost/shared_ptr.hpp"

#include "ProcessContext.hh"

namespace EVENTS {

  class EventStream;

  // ****************************************************************************** 
  //! Define an implementation for the interface provided by the MonitorConnection
  //! class, this can be used to log a variety of event types. i.e. service calls,
  //! event generation and event consumption. These events are then encoded and 
  //! sent to one or more logging destinations (file/inspector socket stream).
  //!
  //! This implementation is built into a shared library that is dynamically loaded 
  //! into a masl process using the -util command line option. Once loaded it will
  //! hook into the masl process and start reporting the monitored set of events.
  // ****************************************************************************** 
  class EventCollector : public SWA::ProcessMonitor::MonitorConnection
  {
    public:
                EventCollector();
       virtual ~EventCollector();

    public:

       // ***************************************************************** 
       //! @return unique name for this EventCollector monitor class.
       // ***************************************************************** 
       virtual std::string getName();

       // ***************************************************************** 
       //! Report that the event queue has started processing 
       // ***************************************************************** 
       virtual void startProcessingEventQueue();

       // ***************************************************************** 
       //! Report that the event queue has finished processing the 
       //! current set of pending events. 
       // ***************************************************************** 
       virtual void endProcessingEventQueue();

       // ***************************************************************** 
       //! Report that the an action has been entered. Can use the StackFrame
       //! class to find details on the action that has been entered. 
       // ***************************************************************** 
       virtual void enteredAction();

       // ***************************************************************** 
       //! Report that the an action has finished and is being unwound from 
       //! the stack. Can use the StackFrame class to find details on the action 
       //! that is being unwound. 
       // ***************************************************************** 
       virtual void leavingAction ();

       // ***************************************************************** 
       //! Report that the an action has been successfully left.
       // ***************************************************************** 
       virtual void leftAction ();

       // ***************************************************************** 
       //! Report that the masl process has raised an exception
       // ***************************************************************** 
       virtual void exceptionRaised ( const std::string& message );

       // ***************************************************************** 
       //! Report that the masl process caught the exception
       // ***************************************************************** 
       virtual void exceptionCaught ();

       // ***************************************************************** 
       //! Report that the masl process has propagated a caught exception
       // ***************************************************************** 
       virtual void exceptionPropagated ();

       // ***************************************************************** 
       //! Report that the masl process has generated an event.
       //! 
       //! @param domainId     the domain the event was generated from
       //! @param objectId     the unique id associated with the src object.
       //! @param eventId      the unique id associated with event.
       //! @param destObjectId the unique id associated with dest object.
       //! @param instanceId   the architecture id of the destination object.
       // ***************************************************************** 
       virtual void generatingEvent ( int domainId, int objectId, int eventId, int destObjectId, SWA::IdType instanceId );

       // ***************************************************************** 
       //! Report that the masl process has processed an event.
       //! 
       //! @param domainId     the domain the event was generated from
       //! @param objectId     the unique id associated with the src object.
       //! @param eventId      the unique id associated with event.
       //! @param destObjectId the unique id associated with dest object.
       //! @param instanceId   the architecture id of the destination object.
       // ***************************************************************** 
       virtual void processingEvent ( int domainId, int objectId, int eventId, int destObjectId, SWA::IdType instanceId );

       // ***************************************************************** 
       //! Report that the masl process has generated a creation or
       //! assigner state event.
       //! 
       //! @param domainId     the domain the event was generated from
       //! @param objectId     the unique id associated with the src object.
       //! @param eventId      the unique id associated with event.
       //! @param destObjectId the unique id associated with dest object.
       // ***************************************************************** 
       virtual void generatingEvent ( int domainId, int objectId, int eventId, int destObjectId );

       // ***************************************************************** 
       //! Report that the masl process has processed a creation or
       //! assigner state event.
       //! 
       //! @param domainId     the domain the event was generated from
       //! @param objectId     the unique id associated with the src object.
       //! @param eventId      the unique id associated with event.
       //! @param destObjectId the unique id associated with dest object.
       // ***************************************************************** 
       virtual void processingEvent ( int domainId, int objectId, int eventId, int destObjectId );

       // ***************************************************************** 
       //! Report that a timer has fired.
       //! 
       //! @param timerId   the id of the timer 
       // ***************************************************************** 
       virtual void firingTimer ( int timerId );

       // ***************************************************************** 
       //! Report that a timer has been created.
       //! 
       //! @param timerId   the id of the timer 
       // ***************************************************************** 
       virtual void creatingTimer ( int timerId );

       // ***************************************************************** 
       //! Report that a timer has been deleted.
       //! 
       //! @param timerId   the id of the timer 
       // ***************************************************************** 
       virtual void deletingTimer ( int timerId );

       // ***************************************************************** 
       //! Report that a timer has been cancelled.
       //! 
       //! @param timerId  the id of the timer 
       // ***************************************************************** 
       virtual void cancellingTimer ( int timerId );

       // ***************************************************************** 
       //! Report that a timer has been set.
       //! 
       //! @param timerId  the id of the timer 
       //! @param timeout  the fire time
       //! @param event    the details of the event
       // ***************************************************************** 
       virtual void settingTimer ( int timerId, const SWA::Timestamp& timeout, const SWA::Duration& period, const boost::shared_ptr< ::SWA::Event >& event);

       // ***************************************************************** 
       //! Report that a thread has been started
       //! 
       //! @param tag name associated with the thread
       // ***************************************************************** 
       virtual void threadStarted (const std::string& tag);

       // ***************************************************************** 
       //! Report that a thread has completed
       // ***************************************************************** 
       virtual void threadCompleted ();

       // ***************************************************************** 
       //! Report that a thread has aborted
       // ***************************************************************** 
       virtual void threadAborted ();

       // ***************************************************************** 
       //! Signal the collector that the process is being shutdown so it can 
       //! undertake required cleanup and flushing.
       // ***************************************************************** 
       void shutdown();

    private:
       ProcessContext   processContext;
       std::list< ::boost::shared_ptr<EventEncoder> > eventEncoders;
  };

} // end EVENTS namespace

#endif
