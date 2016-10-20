//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef TRANSIENT_EventTimers_HH
#define TRANSIENT_EventTimers_HH

#include "swa/EventTimers.hh"

namespace transient
{
  class EventTimers : public ::SWA::EventTimers
  {
    public:
      static EventTimers& getInstance();

    private:
      EventTimers() : nextId(1) {}

      virtual TimerIdType createTimerInner () { return nextId++; }
      virtual void deleteTimerInner ( const TimerIdType id ) {}
      virtual void updateTimerInner ( const SWA::EventTimer& eventTimer ) {}

      TimerIdType nextId;
  };
}

#endif
