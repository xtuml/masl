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

#include "swa/EventTimers.hh"
#include "swa/Process.hh"
#include "swa/Duration.hh"
#include "swa/CommandLine.hh"

#include <boost/lexical_cast.hpp>

namespace SWA
{
  namespace
  {
    const char* const DisableTimersOption = "-timers-disable";
    bool init()
    {
      SWA::CommandLine::getInstance().registerOption (SWA::NamedOption(DisableTimersOption,  "Disable Timers",false,"",false));
      return true;
    }

    bool initialised = init();

    bool timersDisabled()
    {
      static bool disabled = Process::getInstance().getCommandLine().optionPresent(DisableTimersOption);
      return disabled;
    }
  }

  EventTimers& EventTimers::getInstance()
  {
    return getSingleton();
  }

  EventTimers::EventTimers ()
    : suspended(false)
  {
  }

  EventTimers::~EventTimers ()
  {
  }

  EventTimer& EventTimers::getTimer ( const TimerIdType id )
  {
    TimerStore::iterator it = timers.find(id);
    if ( it == timers.end() )
    {
      throw ProgramError("Timer " + boost::lexical_cast<std::string>(id) + " not found." );
    }
    return *it->second;
  }

  const EventTimer& EventTimers::getTimer ( const TimerIdType id ) const
  {
    TimerStore::const_iterator it = timers.find(id);
    if ( it == timers.end() )
    {
      throw ProgramError("Timer " + boost::lexical_cast<std::string>(id) + " not found." );
    }
    return *it->second;
  }
 
 
  EventTimers::TimerIdType EventTimers::createTimer()
  {
    TimerIdType id = createTimerInner();
    addTimer(id);
    return id;
  }

  EventTimer& EventTimers::addTimer( const TimerIdType id )
  {
    EventTimer& result = *timers.insert(TimerStore::value_type(id, boost::shared_ptr<EventTimer>(new EventTimer(id)))).first->second;
    return result;
  }

  void  EventTimers::deleteTimer (const TimerIdType id)
  {
    timers.erase(id);
    deleteTimerInner(id);
  }

  void  EventTimers::cancelTimer (const TimerIdType id)
  {
    EventTimer& timer = getTimer(id);
    timer.cancel();
    updateTimerInner(timer);
  }

  void  EventTimers::fireTimer (const TimerIdType id, int overrun )
  {
    if ( timersDisabled() ) return;

    // Check for race condition where timers are suspended after signal has been generated
    if ( !suspended )
    {
      signalTimerFired();
      EventTimer& timer = getTimer(id);
      timer.fire(overrun);
      updateTimerInner(timer);
    }
  }

  void EventTimers::scheduleTimer (const TimerIdType id, const Timestamp& expiryTime, const Duration& period, const boost::shared_ptr<Event>& event)
  {
    EventTimer& timer = getTimer(id);
    timer.schedule(expiryTime,period,event);
    updateTimerInner(timer);
  }

  void EventTimers::scheduleTimer (const TimerIdType id, const Timestamp& expiryTime, const boost::shared_ptr<Event>& event)
  {
    scheduleTimer(id,expiryTime,Duration::zero(),event);
  }

  bool EventTimers::isScheduled (const TimerIdType id) const
  {
    return getTimer(id).isScheduled();
  }

  Timestamp EventTimers::getScheduledAt (const TimerIdType id) const
  {
    return getTimer(id).getScheduledAt();
  }

  bool EventTimers::isExpired (const TimerIdType id) const
  {
    return getTimer(id).isExpired();
  }

  Timestamp EventTimers::getExpiredAt (const TimerIdType id) const
  {
    return getTimer(id).getExpiredAt();
  }

  Duration EventTimers::getPeriod (const TimerIdType id) const
  {
    return getTimer(id).getPeriod();
  }

  Duration EventTimers::getTimeRemaining (const TimerIdType id) const
  {
    return getTimer(id).getExpiryTime() - Timestamp::now();
  }

  int EventTimers::getMissed (const TimerIdType id) const
  {
    return getTimer(id).getMissed();
  }

  boost::shared_ptr<Event> EventTimers::getEvent (const TimerIdType id) const
  {
    return getTimer(id).getEvent();
  }

  struct OrderTimestamp
  {
    bool operator() ( const EventTimers::QueuedEvents::value_type& lhs, const EventTimers::QueuedEvents::value_type& rhs )
    {
      return lhs->getExpiryTime() < rhs->getExpiryTime();
    }
  };

  EventTimers::QueuedEvents EventTimers::getQueuedEvents() const
  {
    QueuedEvents result;
    for ( TimerStore::const_iterator it = timers.begin(), end = timers.end(); it != end; ++it )
    {
      if ( it->second->isScheduled() )
      {
        result.push_back(it->second);
      }
    }
    std::sort(result.begin(),result.end(),OrderTimestamp());
    return result;
  }

  void EventTimers::suspendTimers()
  {
    if ( !suspended )
    {
      suspended = true;
      for ( TimerStore::iterator it = timers.begin(), end = timers.end(); it != end; ++it )
      {
        it->second->suspend();
      }
    }
  }

  void EventTimers::resumeTimers()
  {
    if ( suspended )
    {
      suspended = false;
      for ( TimerStore::iterator it = timers.begin(), end = timers.end(); it != end; ++it )
      {
        it->second->resume();
      }
    }
  }

  boost::signals2::connection EventTimers::registerTimerFiredListener ( const boost::function<void()>& function )
  {
    return signalTimerFired.connect(function);
  }

}
