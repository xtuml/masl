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
#include "swa/Duration.hh"
#include "swa/EventTimers.hh"
#include "swa/Process.hh"
#include "swa/Timestamp.hh"

#include <errno.h>
#include <print>
#include <string.h>

namespace SWA {
    class Event;

    EventTimer::EventTimer(TimerIdType id)
        : id(id),
          timer(Process::getInstance().getIOContext().get_executor()),
          scheduled(false),
          expired(false),
          expiryTime(),
          event() {}

    const Timestamp &EventTimer::getScheduledAt() const {
        if (scheduled) {
            return expiryTime;
        } else {
            throw SWA::ProgramError("Timer is not scheduled");
        }
    }

    Timestamp EventTimer::getExpiredAt() const {
        if (expired) {
            return scheduled ? expiryTime - period : expiryTime;
        } else
            throw SWA::ProgramError("Timer has not expired");
    }

    const Duration &EventTimer::getPeriod() const {
        return period;
    }

    int EventTimer::getMissed() const {
        return missed;
    }

    const EventTimer::EventDetails &EventTimer::getEvent() const {
        return event;
    }

    const Timestamp &EventTimer::getExpiryTime() const {
        return expiryTime;
    }

    void EventTimer::schedule(const Timestamp &expiryTime, const Duration &period, const EventDetails &event) {
        this->expiryTime = expiryTime;
        this->period = period;
        this->event = event;
        missed = 0;
        scheduled = true;
        expired = false;
        schedule();
        ProcessMonitor::getInstance().settingTimer(id, expiryTime, period, event);
    }

    void EventTimer::schedule() {
        if (!EventTimers::getInstance().isSuspended()) {
            timer.expires_at(this->expiryTime.getChronoTimePoint());
            timer.async_wait(SWA::Process::getInstance().wrapProcessingThread(
                "EventTimer",
                [self = this->shared_from_this()](const asio::error_code &ec) {
                    if (ec == asio::error::operation_aborted) {
                        return;
                    }
                    EventTimers::getInstance().fireTimer(
                        self->id,
                        self->period > Duration::zero()
                            ? (std::chrono::system_clock::now() - self->expiryTime.getChronoTimePoint()) /
                                  self->period.getChronoDuration()
                            : 0
                    );
                }
            ));
        }
    }

    void EventTimer::restore(
        const Timestamp &expiryTime,
        const Duration &period,
        bool scheduled,
        bool expired,
        int missed,
        const EventDetails &event
    ) {
        this->expiryTime = expiryTime;
        this->period = period;
        this->scheduled = scheduled;
        this->expired = expired;
        this->missed = missed;
        this->event = event;

        if (scheduled) {
            if (!EventTimers::getInstance().isSuspended()) {
                schedule();
            }
        }
    }

    void EventTimer::cancel() {
        timer.cancel();
        scheduled = false;
        expired = false;
        missed = 0;
        event.reset();

        ProcessMonitor::getInstance().cancellingTimer(id);
    }

    void EventTimer::fire(int overrun) {
        ::SWA::Process::getInstance().getEventQueue().addEvent(event);

        expired = true;

        if (period > Duration::zero()) {
            expiryTime += period * (1 + overrun);
            missed = overrun;
            schedule();
        } else {
            scheduled = false;
            missed = 0;
            event.reset();
        }

        ProcessMonitor::getInstance().firingTimer(id, overrun);
    }

    void EventTimer::suspend() {
        timer.cancel();
    }

    void EventTimer::resume() {
        if (scheduled) {
            schedule();
        }
    }

    void EventTimer::callback(int overrun) {
        EventTimers::getInstance().fireTimer(id, overrun);
    }

} // namespace SWA
