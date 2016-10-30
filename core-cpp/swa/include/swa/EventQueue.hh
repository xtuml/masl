//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_EventQueue_HH
#define SWA_EventQueue_HH

#include "Event.hh"

#include "boost/shared_ptr.hpp"

#include <deque>
#include <map>
#include <vector>

namespace SWA
{
  class EventQueue
  {
    public:

      void addEvent( const boost::shared_ptr<Event> event );

      int processEvents(); 
      bool empty(); 

      std::vector<boost::shared_ptr<Event> > getEvents() const;

    private:
      typedef std::deque<boost::shared_ptr<Event> > InnerQueueType;
      typedef std::map<int,InnerQueueType> QueueType;

      QueueType queue;
  };

}

#endif
