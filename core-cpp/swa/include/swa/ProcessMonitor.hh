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

#ifndef SWA_ProcessMonitor_HH
#define SWA_ProcessMonitor_HH

#include <list>
#include <vector>

#include "types.hh"

#include "boost/shared_ptr.hpp"

namespace SWA {
class Timestamp;
class Duration;
class Event;
class ProcessMonitor {

  public:
  
    class MonitorConnection
    {
      public:
        MonitorConnection() : connected(false) {}

        virtual std::string getName() = 0;
        virtual void startMainLoop() {}
        virtual void endMainLoop() {}

        virtual void startProcessingEventQueue() {}
        virtual void endProcessingEventQueue() {}

        virtual void processRunning() {}
        virtual void processIdle() {}

        virtual void threadStarted()    {}
        virtual void threadAborted()    {}
        virtual void threadCompleting() {}
        virtual void threadCompleted()  {}

        virtual void startStatement() {}
        virtual void endStatement() {}

        virtual void enteredAction() {}
        virtual void leavingAction() {}
        virtual void leftAction() {}

        virtual void enteredCatch() {}
        virtual void leavingCatch() {}

        virtual void exceptionRaised    ( const std::string& message ) {}

        virtual void transitioningState ( int domainId, int objectId, int instanceId, int oldState, int newState ) {}
        virtual void transitioningAssignerState ( int domainId, int objectId, int oldState, int newState ) {}
        virtual void generatingEvent ( const boost::shared_ptr<Event>& event ) {}
        virtual void processingEvent ( const boost::shared_ptr<Event>& event ) {}

        virtual void firingTimer     ( int timerId, int overrun ) {}
        virtual void creatingTimer   ( int timerId ) {}
        virtual void deletingTimer   ( int timerId ) {}
        virtual void cancellingTimer ( int timerId ) {}
        virtual void settingTimer    ( int timerId, const Timestamp& timeout, const Duration& period, const boost::shared_ptr<Event>& event) {}

        virtual void pauseRequested() {}

        virtual ~MonitorConnection() {}

        virtual const std::vector<MonitorConnection*> getPrerequisites() const { return std::vector<MonitorConnection*>(); }

        bool registerMonitor() { getInstanceStartupSafe().registerConnection(this); return true; }

      protected:
        void connectToMonitor()           { if ( !connected) { getInstanceStartupSafe().activateConnection(this);   connected = true;  } }
        void disconnectFromMonitor()      { if ( connected)  { getInstanceStartupSafe().deactivateConnection(this); connected = false; } }

      private:
        bool connected;

    };


    static ProcessMonitor& getInstance() { return instance; }
    static ProcessMonitor& getInstanceStartupSafe();

    void startMainLoop();
    void endMainLoop();

    void startProcessingEventQueue();
    void endProcessingEventQueue();

    void processRunning();
    void processIdle();

    void threadStarted();
    void threadAborted();
    void threadCompleting();
    void threadCompleted();

    void startStatement();
    void endStatement();
  
    void enteredAction();
    void leavingAction();

    void enteredCatch();
    void leavingCatch();

    void transitioningState ( int domainId, int objectId, int instanceId, int oldState, int newState );
    void transitioningAssignerState ( int domainId, int objectId, int oldState, int newState );
    void generatingEvent (const boost::shared_ptr<Event>& event );
    void processingEvent (const boost::shared_ptr<Event>& event );

    void exceptionRaised     ( const std::string& message );

    class ProcessingEventQueue
    {
      public:
        ProcessingEventQueue()
        {
          getInstance().startProcessingEventQueue();
        }
        ~ProcessingEventQueue() { getInstance().endProcessingEventQueue(); }
    };

    void firingTimer     ( int timerId, int overrun );
    void creatingTimer   ( int timerId );
    void deletingTimer   ( int timerId );
    void cancellingTimer ( int timerId );
    void settingTimer    ( int timerId, const Timestamp& timeout, const Duration& period, const boost::shared_ptr<Event>& event);
    
    void registerConnection   ( MonitorConnection* connection );
    void activateConnection   ( MonitorConnection* connection );
    void deactivateConnection ( MonitorConnection* connection );

    void pauseRequested();

    void setIgnoreExceptions(bool ignore);

  private:
    static ProcessMonitor& instance;
    ProcessMonitor();
   
    std::vector<MonitorConnection*> pendingConnections;

    // Must use a list, as we require a defined order (same as 
    // for pending connctions, but with elements missing), but 
    // need iterators to remain valid on inserts and erases. 
    std::list<MonitorConnection*> activeConnections;

    bool ignoreExceptions;
};

} // end namespace SWA

#endif
