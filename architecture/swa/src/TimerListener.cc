/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "swa/TimerListener.hh"
#include "swa/ActivityMonitor.hh"
#include "swa/Duration.hh"
#include "swa/ListenerPriority.hh"
#include "swa/Process.hh"
#include "swa/Timestamp.hh"

#include <errno.h>
#include <iostream>
#include <string.h>

namespace SWA {
class Event;

TimerListener::TimerListener(const ListenerPriority &priority,
                             const Callback &callback)
    : timerId(), callback(callback), expiryTime(), interval(), active(false) {
    sigevent eventSpec = {};
    eventSpec.sigev_notify = SIGEV_SIGNAL;
    eventSpec.sigev_signo = priority.getValue();
    eventSpec.sigev_value.sival_ptr = &timerId;

    if (timer_create(CLOCK_REALTIME, &eventSpec, &timerId)) {
        throw SWA::ProgramError(
            ::boost::make_tuple("Failed to create timer : ", strerror(errno)));
    }

    Process::getInstance().getActivityMonitor().addTimerCallback(
        timerId, [this](int32_t overrun) { timerFired(overrun); });
}

TimerListener::~TimerListener() {
    timer_delete(timerId);
    Process::getInstance().getActivityMonitor().removeTimerCallback(timerId);
}

void TimerListener::schedule(const Timestamp &expiryTime,
                             const Duration &interval) {
    this->expiryTime = expiryTime;
    this->interval = interval;
    this->active = true;
    setTime(expiryTime.getTimespec(), interval.getTimespec());
}

void TimerListener::setTime(const timespec &expiryTime,
                            const timespec &interval) {

    itimerspec timeout = {};
    timeout.it_value = expiryTime;
    timeout.it_interval = interval;

    // A time of zero is interpreted as cancel, and a negative
    // time is an error (as far as timer_settime is concerned).
    // In either case we are in the past, so
    // just set to something small and positive, and let the
    // timer expire straight away.
    if (timeout.it_value.tv_sec < 0 ||
        (timeout.it_value.tv_sec == 0 && timeout.it_value.tv_nsec == 0)) {
        timeout.it_value.tv_sec = 0;
        timeout.it_value.tv_nsec = 1;
    }

    if (timer_settime(timerId, TIMER_ABSTIME, &timeout, 0)) {
        // It appears that on 32 bit platforms the maximum time
        // ahead that a timer can be set is around 24 days
        // (coincidentally, about 2^31 milliseconds!) and on 64 bit
        // is 68 years (conincidentally, about 2^31 seconds) even
        // though the specified time is well within the range of
        // timespec. For safety, set the maximum expiry on the
        // timer to be twenty days and cope with the fact that the
        // expiry time may not have been reached when it fires. In
        // this case it will just get rescheduled.

        timeout.it_value.tv_sec = 3600 * 24 * 20;
        timeout.it_value.tv_nsec = 0;

        if (timer_settime(timerId, 0, &timeout, 0)) {
            throw SWA::ProgramError(::boost::make_tuple(
                "Failed to schedule timer : ", strerror(errno)));
        }
    }
}

void TimerListener::cancel() {
    static const itimerspec cancelTimer = {};

    this->active = false;

    if (timer_settime(timerId, 0, &cancelTimer, 0)) {
        throw SWA::ProgramError(
            ::boost::make_tuple("Failed to cancel timer : ", strerror(errno)));
    }
}

void TimerListener::timerFired(int32_t overrun) {
    if (active) {
        // The timer is still active
        if (expiryTime <= Timestamp::now()) {
            if (interval > Duration::zero()) {
                // Periodic timer, so update the expiry time
                expiryTime += interval * (1 + overrun);
            } else {
                active = false;
            }
            callback(overrun);
        } else {
            // There must have been a pending signal before a reschedule.
            // Reschedule the timer to make sure we don't miss expirations due
            // to race conditions.
            setTime(expiryTime.getTimespec(), interval.getTimespec());
        }
    } else {
        // Do nothing... must have been a pending signal before cancellation.
    }
}

} // namespace SWA
