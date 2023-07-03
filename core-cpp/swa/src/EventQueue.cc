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
