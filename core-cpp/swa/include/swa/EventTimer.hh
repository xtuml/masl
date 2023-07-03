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

#ifndef SWA_EventTimer_HH
#define SWA_EventTimer_HH

#include "Timestamp.hh"
#include "TimerListener.hh"
#include <boost/utility.hpp>

namespace SWA
{
  class Event;

  class EventTimer : public boost::noncopyable
  {
    public:
      typedef boost::shared_ptr<Event> EventDetails;
      typedef uint32_t TimerIdType;
    

    public:
      EventTimer ( TimerIdType id );
      ~EventTimer ();

      TimerIdType getId() const { return id; }

      const Timestamp& getExpiryTime() const;
      const Timestamp& getScheduledAt() const;
      Timestamp getExpiredAt() const;
      const Duration& getPeriod() const;
      int getMissed() const;
      const EventDetails& getEvent() const;

      bool isScheduled() const { return scheduled; }
      bool isExpired() const { return expired; }

      void schedule ( const Timestamp& expiryTime, const Duration& period, const EventDetails& eventDetails );
      void restore ( const Timestamp& expiryTime, const Duration& period, bool scheduled, bool expired, int missed, const EventDetails& event );
      void cancel();
      void suspend();
      void resume();

      void fire( int overrun );
      void callback ( int overrun );


    private:
      TimerListener listener;
      const TimerIdType id;

      bool scheduled;
      bool expired;
      Timestamp expiryTime;
      Duration period;
      int missed;
      EventDetails event;

  };

}


#endif
