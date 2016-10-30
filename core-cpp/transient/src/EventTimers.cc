//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "EventTimers.hh"

namespace transient
{

  EventTimers& EventTimers::getInstance()
  {
    static EventTimers instance;
    return instance;
  }

  bool registered = EventTimers::registerSingleton( &EventTimers::getInstance );

}
