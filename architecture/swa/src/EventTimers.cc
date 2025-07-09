/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "swa/EventTimers.hh"
#include "swa/CommandLine.hh"
#include "swa/Duration.hh"
#include "swa/Process.hh"
#include <print>
#include <format>

#include <execinfo.h>
#include <signal.h>
#include <unistd.h>
#include <cstdlib>
#include <iostream>

void print_stacktrace() {
    const int max_frames = 64;
    void* frames[max_frames];

    int num_frames = backtrace(frames, max_frames);
    char** symbols = backtrace_symbols(frames, num_frames);

    std::cerr << "Stack trace:\n";
    for (int i = 0; i < num_frames; ++i) {
        std::cerr << symbols[i] << '\n';
    }

    free(symbols);
}

namespace SWA {
    namespace {
        const char *const DisableTimersOption = "-timers-disable";
        bool init() {
            SWA::CommandLine::getInstance().registerOption(
                SWA::NamedOption(DisableTimersOption, "Disable Timers", false, "", false)
            );
            return true;
        }

        bool initialised = init();

        bool timersDisabled() {
            static bool disabled = Process::getInstance().getCommandLine().optionPresent(DisableTimersOption);
            return disabled;
        }
    } // namespace

    EventTimers &EventTimers::getInstance() {
        return getSingleton();
    }

    EventTimers::EventTimers()
        : suspended(false) {}

    EventTimers::~EventTimers() {}

    EventTimer &EventTimers::getTimer(const TimerIdType id) {
        TimerStore::iterator it = timers.find(id);
        if (it == timers.end()) {
            print_stacktrace();
            throw std::runtime_error(std::format("Levi1: Timer {} not found.", id));
        }
        return *it->second;
    }

    const EventTimer &EventTimers::getTimer(const TimerIdType id) const {
        TimerStore::const_iterator it = timers.find(id);
        if (it == timers.end()) {
            print_stacktrace();
            throw std::runtime_error(std::format("Levi2: Timer {} not found.", id));
        }
        return *it->second;
    }

    EventTimers::TimerIdType EventTimers::createTimer() {
        TimerIdType id = createTimerInner();
        addTimer(id);
        return id;
    }

    EventTimer &EventTimers::addTimer(const TimerIdType id) {
        EventTimer &result =
            *timers.emplace(id, std::make_shared<EventTimer>(id)).first->second;
        return result;
    }

    void EventTimers::deleteTimer(const TimerIdType id) {
        EventTimer &timer = getTimer(id);
        timer.cancel();
        timers.erase(id);
        deleteTimerInner(id);
        std::cout << "Levi3: deleted timer: " << id << std::endl;
    }

    void EventTimers::cancelTimer(const TimerIdType id) {
        EventTimer &timer = getTimer(id);
        timer.cancel();
        updateTimerInner(timer);
    }

    void EventTimers::fireTimer(const TimerIdType id, int overrun) {
        if (timersDisabled())
            return;

        // Check for race condition where timers are suspended after signal has been generated
        if (!suspended) {
            signalTimerFired();
            EventTimer &timer = getTimer(id);
            timer.fire(overrun);
            updateTimerInner(timer);
        }
    }

    void EventTimers::scheduleTimer(
        const TimerIdType id, const Timestamp &expiryTime, const Duration &period, const std::shared_ptr<Event> &event
    ) {
        EventTimer &timer = getTimer(id);
        timer.schedule(expiryTime, period, event);
        updateTimerInner(timer);
    }

    void
    EventTimers::scheduleTimer(const TimerIdType id, const Timestamp &expiryTime, const std::shared_ptr<Event> &event) {
        scheduleTimer(id, expiryTime, Duration::zero(), event);
    }

    bool EventTimers::isScheduled(const TimerIdType id) const {
        return getTimer(id).isScheduled();
    }

    Timestamp EventTimers::getScheduledAt(const TimerIdType id) const {
        return getTimer(id).getScheduledAt();
    }

    bool EventTimers::isExpired(const TimerIdType id) const {
        return getTimer(id).isExpired();
    }

    Timestamp EventTimers::getExpiredAt(const TimerIdType id) const {
        return getTimer(id).getExpiredAt();
    }

    Duration EventTimers::getPeriod(const TimerIdType id) const {
        return getTimer(id).getPeriod();
    }

    Duration EventTimers::getTimeRemaining(const TimerIdType id) const {
        return getTimer(id).getExpiryTime() - Timestamp::now();
    }

    int EventTimers::getMissed(const TimerIdType id) const {
        return getTimer(id).getMissed();
    }

    std::shared_ptr<Event> EventTimers::getEvent(const TimerIdType id) const {
        return getTimer(id).getEvent();
    }

    struct OrderTimestamp {
        bool
        operator()(const EventTimers::QueuedEvents::value_type &lhs, const EventTimers::QueuedEvents::value_type &rhs) {
            return lhs->getExpiryTime() < rhs->getExpiryTime();
        }
    };

    EventTimers::QueuedEvents EventTimers::getQueuedEvents() const {
        QueuedEvents result;
        for (TimerStore::const_iterator it = timers.begin(), end = timers.end(); it != end; ++it) {
            if (it->second->isScheduled()) {
                result.push_back(it->second);
            }
        }
        std::sort(result.begin(), result.end(), OrderTimestamp());
        return result;
    }

    void EventTimers::suspendTimers() {
        if (!suspended) {
            suspended = true;
            for (TimerStore::iterator it = timers.begin(), end = timers.end(); it != end; ++it) {
                it->second->suspend();
            }
        }
    }

    void EventTimers::resumeTimers() {
        if (suspended) {
            suspended = false;
            for (TimerStore::iterator it = timers.begin(), end = timers.end(); it != end; ++it) {
                it->second->resume();
            }
        }
    }

    boost::signals2::connection EventTimers::registerTimerFiredListener(const std::function<void()> &function) {
        return signalTimerFired.connect(function);
    }

} // namespace SWA
