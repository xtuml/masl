/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "swa/EventTimer.hh"
#include "swa/ActivityMonitor.hh"
#include "swa/Duration.hh"
#include "swa/EventTimers.hh"
#include "swa/ListenerPriority.hh"
#include "swa/Process.hh"
#include "swa/Timestamp.hh"

#include <errno.h>
#include <string.h>

namespace SWA {
class Event;

EventTimer::EventTimer(TimerIdType id)
    : listener(ListenerPriority::getNormal(),
               [this](int overrun) { callback(overrun); }),
      id(id), scheduled(false), expired(false), expiryTime(), event() {}

EventTimer::~EventTimer() {}

const Timestamp &EventTimer::getScheduledAt() const {
    if (scheduled)
        return expiryTime;
    else
        throw SWA::ProgramError("Timer is not scheduled");
}

Timestamp EventTimer::getExpiredAt() const {
    if (expired) {
        return scheduled ? expiryTime - period : expiryTime;
    } else
        throw SWA::ProgramError("Timer has not expired");
}

const Duration &EventTimer::getPeriod() const { return period; }

int EventTimer::getMissed() const { return missed; }

const EventTimer::EventDetails &EventTimer::getEvent() const { return event; }

const Timestamp &EventTimer::getExpiryTime() const { return expiryTime; }

void EventTimer::schedule(const Timestamp &expiryTime, const Duration &period,
                          const EventDetails &event) {
    this->expiryTime = expiryTime;
    this->period = period;
    this->event = event;
    missed = 0;
    scheduled = true;
    expired = false;

    if (!EventTimers::getInstance().isSuspended()) {
        listener.schedule(expiryTime, period);
    }
    ProcessMonitor::getInstance().settingTimer(id, expiryTime, period, event);
}

void EventTimer::restore(const Timestamp &expiryTime, const Duration &period,
                         bool scheduled, bool expired, int missed,
                         const EventDetails &event) {
    this->expiryTime = expiryTime;
    this->period = period;
    this->scheduled = scheduled;
    this->expired = expired;
    this->missed = missed;
    this->event = event;

    if (scheduled) {
        if (!EventTimers::getInstance().isSuspended()) {
            listener.schedule(expiryTime, period);
        }
    }
}

void EventTimer::cancel() {
    scheduled = false;
    expired = false;
    missed = 0;
    event.reset();

    listener.cancel();
    ProcessMonitor::getInstance().cancellingTimer(id);
}

void EventTimer::fire(int overrun) {
    ::SWA::Process::getInstance().getEventQueue().addEvent(event);

    expired = true;

    if (period > Duration::zero()) {
        expiryTime += period * (1 + overrun);
        missed = overrun;
    } else {
        scheduled = false;
        missed = 0;
        event.reset();
        listener.cancel();
    }

    ProcessMonitor::getInstance().firingTimer(id, overrun);
}

void EventTimer::suspend() { listener.cancel(); }

void EventTimer::resume() {
    if (scheduled) {
        listener.schedule(expiryTime, period);
    }
}

void EventTimer::callback(int overrun) {
    EventTimers::getInstance().fireTimer(id, overrun);
}

} // namespace SWA
