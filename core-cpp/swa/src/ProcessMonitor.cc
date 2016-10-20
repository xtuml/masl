// 
// Filename : pm_process_monitor.cc
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//

#include "swa/EventQueue.hh"
#include "swa/ProcessMonitor.hh"

#include "boost/bind.hpp"

#include <sstream>
#include <iostream>

namespace SWA
{

  ProcessMonitor& ProcessMonitor::instance = ProcessMonitor::getInstanceStartupSafe();

  ProcessMonitor& ProcessMonitor::getInstanceStartupSafe()
  {
    // Adaption of a 'Myers Singleton', uses a function static 
    // for the actual storage, but a pointer for all accesses so 
    // that they can be used inline. If the pointer was not used 
    // the getInstance() call could not be declared inline 
    // because there could then be separate statics all over the 
    // place, unless the compiler is very standard compliant and 
    // does clever stuff to eliminate them. I'm not taking the 
    // chance. 
    static ProcessMonitor singleton;
    return singleton;
  }


  template<typename InputIter, typename Function>
  Function
  erase_safe_for_each(InputIter first, InputIter last, Function f)
  {
    while ( first != last )
      f(*first++);
    return f;
  }


  ProcessMonitor::ProcessMonitor() 
   : ignoreExceptions(false)
  {
  }

  void ProcessMonitor::registerConnection ( MonitorConnection* connection )
  {
    if ( std::find(pendingConnections.begin(),pendingConnections.end(),connection ) == pendingConnections.end() )
    {
      for ( std::vector<MonitorConnection*>::const_iterator it = connection->getPrerequisites().begin();
            it != connection->getPrerequisites().end(); ++it )
      {
        registerConnection(*it);
      }

      pendingConnections.push_back(connection);
    }
  }

  void ProcessMonitor::activateConnection ( MonitorConnection* connection )
  {
    // Insert the required connection into the active list, but 
    // maintain the same order as the pending collection list. 
    // Make sure that any iterators into the active list are not 
    // invalidated. 

    std::vector<MonitorConnection*>::const_iterator allConnIt = pendingConnections.begin();
    std::list<MonitorConnection*>::iterator activeConnIt = activeConnections.begin();

    while ( allConnIt != pendingConnections.end() )
    {
      if ( *allConnIt == connection && (activeConnIt == activeConnections.end() || *activeConnIt != connection ) )
      {
        activeConnIt = activeConnections.insert(activeConnIt,connection);
      }
      if ( activeConnIt != activeConnections.end() && *allConnIt == *activeConnIt )
      {
        ++activeConnIt;
      }
      ++allConnIt;
    }
  }

  void ProcessMonitor::deactivateConnection ( MonitorConnection* connection )
  {
    activeConnections.remove(connection);
  }

  void ProcessMonitor::startMainLoop()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::startMainLoop,_1) );
  }

  void ProcessMonitor::endMainLoop()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::endMainLoop,_1) );
  }

  void ProcessMonitor::startProcessingEventQueue()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::startProcessingEventQueue,_1) );
  }

  void ProcessMonitor::endProcessingEventQueue()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::endProcessingEventQueue,_1) );
  }

  void ProcessMonitor::processRunning()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::processRunning,_1) );
  }

  void ProcessMonitor::processIdle()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::processIdle,_1) );
  }

  void ProcessMonitor::threadStarted()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::threadStarted,_1) );
  }

  void ProcessMonitor::threadAborted()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::threadAborted,_1) );
  }

  void ProcessMonitor::threadCompleting()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::threadCompleting,_1) );
  }


  void ProcessMonitor::threadCompleted()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::threadCompleted,_1) );
  }


  void ProcessMonitor::startStatement ()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::startStatement,_1) );
  }

  void ProcessMonitor::endStatement()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::endStatement,_1) );
  }

  void ProcessMonitor::enteredAction()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::enteredAction,_1) );
  }

  void ProcessMonitor::leavingAction()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::leavingAction,_1) );
  }

  void ProcessMonitor::enteredCatch ()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::enteredCatch,_1) );
  }

  void ProcessMonitor::leavingCatch ()
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::leavingCatch,_1) );
  }


  void ProcessMonitor::exceptionRaised ( const std::string& message )
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::exceptionRaised,_1,message) );
  }

  void ProcessMonitor::transitioningState ( int domainId, int objectId, int instanceId, int oldState, int newState )
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::transitioningState,_1,domainId,objectId,instanceId,oldState,newState) );
  }

  void ProcessMonitor::transitioningAssignerState ( int domainId, int objectId, int oldState, int newState )
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::transitioningAssignerState,_1,domainId,objectId,oldState,newState) );
  }

  void ProcessMonitor::generatingEvent (const boost::shared_ptr<Event>& event)
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::generatingEvent,_1,boost::ref(event)) );
  }

  void ProcessMonitor::processingEvent (const boost::shared_ptr<Event>& event )
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::processingEvent,_1,boost::ref(event)) );
  }

  void ProcessMonitor::firingTimer ( int timerId, int overrun )
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::firingTimer,_1,timerId,overrun) );
  }

  void ProcessMonitor::creatingTimer (int timerId )
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::creatingTimer,_1,timerId) );
  } 

  void ProcessMonitor::deletingTimer (int timerId )
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::deletingTimer,_1,timerId) );
  } 

  void ProcessMonitor::cancellingTimer (int timerId )
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::cancellingTimer,_1,timerId) );
  } 

  void ProcessMonitor::settingTimer (int timerId, const Timestamp& timeout, const Duration& period, const boost::shared_ptr<Event>& event)
  {
    erase_safe_for_each ( activeConnections.begin(),
                          activeConnections.end(),
                          boost::bind(&MonitorConnection::settingTimer,_1,timerId,boost::ref(timeout),boost::ref(period),boost::ref(event)));
  } 

  void ProcessMonitor::pauseRequested ()
  {
    erase_safe_for_each ( pendingConnections.begin(),
                          pendingConnections.end(),
                          boost::bind(&MonitorConnection::pauseRequested,_1) );
  }


}
