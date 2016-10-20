//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_EventTimers_HH
#define SWA_EventTimers_HH

#include "EventTimer.hh"
#include "EventTimers.hh"
#include "DynamicSingleton.hh"
#include "boost/unordered_map.hpp"
#include "boost/signals2.hpp"
#include <vector>

namespace SWA
{

  class TimerImpl;

  class EventTimers : public DynamicSingleton<EventTimers>
  {
    public:
      typedef EventTimer::TimerIdType TimerIdType;
      
    public:
      static EventTimers& getInstance();

      TimerIdType createTimer();
      void deleteTimer (const TimerIdType id);
      void cancelTimer (const TimerIdType id);
      void scheduleTimer (const TimerIdType id, const Timestamp& expiryTime, const Duration& period, const boost::shared_ptr<Event>& event);
      void scheduleTimer (const TimerIdType id, const Timestamp& expiryTime, const boost::shared_ptr<Event>& event);
      void fireTimer (const TimerIdType id, int overrun);


      bool isScheduled (const TimerIdType id) const;
      bool isExpired (const TimerIdType id) const;
      Timestamp getScheduledAt (const TimerIdType id) const;
      Timestamp getExpiredAt (const TimerIdType id) const;
      Duration getPeriod (const TimerIdType id) const;
      Timestamp getExpiry (const TimerIdType id) const;
      Duration getTimeRemaining (const TimerIdType id) const;
      int getMissed (const TimerIdType id) const;
      boost::shared_ptr<Event> getEvent (const TimerIdType id) const;

      typedef std::vector<boost::shared_ptr<EventTimer> > QueuedEvents;

      QueuedEvents getQueuedEvents() const;

      void suspendTimers();
      void resumeTimers();
      bool isSuspended() const { return suspended; }

      boost::signals2::connection registerTimerFiredListener          ( const boost::function<void()>& function );

      EventTimer& getTimer ( const TimerIdType id );
      const EventTimer& getTimer ( const TimerIdType id ) const;

    protected:
      EventTimers();
      virtual ~EventTimers();

      EventTimer& addTimer( const TimerIdType id );

    private:
      virtual TimerIdType createTimerInner () = 0;
      virtual void deleteTimerInner ( const TimerIdType id ) = 0;
      virtual void updateTimerInner ( const EventTimer& eventTimer ) = 0;

    private:
      typedef boost::unordered_map<TimerIdType,boost::shared_ptr<EventTimer> > TimerStore;
      TimerStore timers;
      bool suspended;

    private:
      typedef boost::signals2::signal_type<void(),boost::signals2::keywords::mutex_type<boost::signals2::dummy_mutex> >::type VoidSignal;
      VoidSignal signalTimerFired;

  };
  


}


#endif
