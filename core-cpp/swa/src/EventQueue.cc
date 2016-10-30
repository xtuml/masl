//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "swa/EventQueue.hh"
#include "swa/ProcessMonitor.hh"
#include <iostream>

namespace SWA
{

  enum EventQueueId { LOCAL, MAIN };

  void EventQueue::addEvent( const boost::shared_ptr<Event> event )
  { 
     ProcessMonitor::getInstance().generatingEvent(event);
     if ( event->getHasDest() && event->getHasSource() && 
          event->getObjectId() == event->getSourceObjectId() &&
          event->getDestInstanceId() == event->getSourceInstanceId() )
     {
       queue[LOCAL].push_back(event);
     }
     else
     {  
       queue[MAIN].push_back(event);
     }
  }

  bool EventQueue::empty()
  {
    for ( QueueType::const_iterator it = queue.begin(); it != queue.end(); ++it )
    {
      if ( !it->second.empty() ) return false;
    }
    return true;
  }

  std::vector<boost::shared_ptr<Event> > EventQueue::getEvents() const
  {
    std::vector<boost::shared_ptr<Event> > result;
    for ( QueueType::const_iterator it = queue.begin(); it != queue.end(); ++it )
    {
      for ( InnerQueueType::const_iterator it2 = it->second.begin(), end = it->second.end(); it2 != end; ++it2 )
      {
        result.push_back(*it2);
      }
    }
    return result;
  }


  int EventQueue::processEvents()
  {
    int processed = 0;
 
    if ( ! empty() )
    {
      ProcessMonitor::ProcessingEventQueue raii_dummy;

    QueueType::iterator it = queue.begin();
    while ( it != queue.end() )
    {
      if ( it->second.empty() )
      {
        // Nothing at this priority, so move to next queue
        ++it;
      }
      else
      {
        // Remove the event from the queue and process it
        boost::shared_ptr<Event> event = it->second.front();
        it->second.pop_front();

        ProcessMonitor::getInstance().processingEvent(event);
        event->invoke();
        ++processed;

        // The event may have added higher priority events, so 
        // start again from the begining 
        it = queue.begin();
        }
      }
    }
    return processed;
  } 

}
