/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_EventTimer_HH
#define SWA_EventTimer_HH

#include "TimerListener.hh"
#include "Timestamp.hh"
#include <boost/utility.hpp>

namespace SWA {
class Event;

class EventTimer : public boost::noncopyable {
  public:
    typedef std::shared_ptr<Event> EventDetails;
    typedef uint32_t TimerIdType;

  public:
    EventTimer(TimerIdType id);
    ~EventTimer();

    TimerIdType getId() const { return id; }

    const Timestamp &getExpiryTime() const;
    const Timestamp &getScheduledAt() const;
    Timestamp getExpiredAt() const;
    const Duration &getPeriod() const;
    int getMissed() const;
    const EventDetails &getEvent() const;

    bool isScheduled() const { return scheduled; }
    bool isExpired() const { return expired; }

    void schedule(const Timestamp &expiryTime, const Duration &period,
                  const EventDetails &eventDetails);
    void restore(const Timestamp &expiryTime, const Duration &period,
                 bool scheduled, bool expired, int missed,
                 const EventDetails &event);
    void cancel();
    void suspend();
    void resume();

    void fire(int overrun);
    void callback(int overrun);

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

} // namespace SWA

#endif
