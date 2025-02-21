/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_EventTimers_HH
#define SWA_EventTimers_HH

#include "DynamicSingleton.hh"
#include "EventTimer.hh"
#include "EventTimers.hh"
#include "boost/signals2.hpp"
#include <unordered_map>
#include <vector>

namespace SWA {

class TimerImpl;

class EventTimers : public DynamicSingleton<EventTimers> {
  public:
    typedef EventTimer::TimerIdType TimerIdType;

  public:
    static EventTimers &getInstance();

    TimerIdType createTimer();
    void deleteTimer(const TimerIdType id);
    void cancelTimer(const TimerIdType id);
    void scheduleTimer(const TimerIdType id, const Timestamp &expiryTime,
                       const Duration &period,
                       const std::shared_ptr<Event> &event);
    void scheduleTimer(const TimerIdType id, const Timestamp &expiryTime,
                       const std::shared_ptr<Event> &event);
    void fireTimer(const TimerIdType id, int overrun);

    bool isScheduled(const TimerIdType id) const;
    bool isExpired(const TimerIdType id) const;
    Timestamp getScheduledAt(const TimerIdType id) const;
    Timestamp getExpiredAt(const TimerIdType id) const;
    Duration getPeriod(const TimerIdType id) const;
    Timestamp getExpiry(const TimerIdType id) const;
    Duration getTimeRemaining(const TimerIdType id) const;
    int getMissed(const TimerIdType id) const;
    std::shared_ptr<Event> getEvent(const TimerIdType id) const;

    typedef std::vector<std::shared_ptr<EventTimer>> QueuedEvents;

    QueuedEvents getQueuedEvents() const;

    void suspendTimers();
    void resumeTimers();
    bool isSuspended() const { return suspended; }

    boost::signals2::connection
    registerTimerFiredListener(const std::function<void()> &function);

    EventTimer &getTimer(const TimerIdType id);
    const EventTimer &getTimer(const TimerIdType id) const;

  protected:
    EventTimers();
    virtual ~EventTimers();

    EventTimer &addTimer(const TimerIdType id);

  private:
    virtual TimerIdType createTimerInner() = 0;
    virtual void deleteTimerInner(const TimerIdType id) = 0;
    virtual void updateTimerInner(const EventTimer &eventTimer) = 0;

  private:
    typedef std::unordered_map<TimerIdType, std::shared_ptr<EventTimer>>
        TimerStore;
    TimerStore timers;
    bool suspended;

  private:
    typedef boost::signals2::signal_type<
        void(), boost::signals2::keywords::mutex_type<
                    boost::signals2::dummy_mutex>>::type VoidSignal;
    VoidSignal signalTimerFired;
};

} // namespace SWA

#endif
