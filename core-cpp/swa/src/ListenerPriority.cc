//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "swa/ListenerPriority.hh"
#include <signal.h>

namespace SWA
{

  const ListenerPriority& ListenerPriority::getMinimum()
  {
    static ListenerPriority priority(SIGRTMAX);
    return priority;
  }

  const ListenerPriority& ListenerPriority::getMaximum()
  {
    static ListenerPriority priority(SIGRTMIN);
    return priority;
  }

  const ListenerPriority& ListenerPriority::getNormal()
  {
    static ListenerPriority priority(getMinimum(),getMaximum());
    return priority;
  }

  const ListenerPriority& ListenerPriority::getLow()
  {
    static ListenerPriority priority(getMinimum(),getNormal());
    return priority;
  }

  const ListenerPriority& ListenerPriority::getHigh()
  {
    static ListenerPriority priority(getNormal(),getMaximum());
    return priority;
  }

  ListenerPriority::ListenerPriority ( int priority )
    : priority(priority)
  {
  }

 ListenerPriority::ListenerPriority ( const ListenerPriority& low, const ListenerPriority& high )
    : priority((low.priority + high.priority)/2)
  {
  }

}
