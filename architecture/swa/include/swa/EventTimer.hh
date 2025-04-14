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

#include "Timestamp.hh"
#include "Duration.hh"
#include <asio/system_timer.hpp>

namespace SWA {
    class Event;


    class EventTimer : public std::enable_shared_from_this<EventTimer> {
      public:
        using EventDetails = std::shared_ptr<Event>;
        using TimerIdType = std::uint32_t;

      public:
        EventTimer(TimerIdType id);
        ~EventTimer() = default;

        EventTimer(const EventTimer &) = delete;
        EventTimer &operator=(const EventTimer &) = delete;
        EventTimer(EventTimer &&) = delete;
        EventTimer &operator=(EventTimer &&) = delete;

        TimerIdType getId() const {
            return id;
        }

        const Timestamp &getExpiryTime() const;
        const Timestamp &getScheduledAt() const;
        Timestamp getExpiredAt() const;
        const Duration &getPeriod() const;
        int getMissed() const;
        const EventDetails &getEvent() const;

        bool isScheduled() const {
            return scheduled;
        }
        bool isExpired() const {
            return expired;
        }

        std::shared_ptr<EventTimer> ptr() { return shared_from_this(); }

        void schedule(const Timestamp &expiryTime, const Duration &period, const EventDetails &eventDetails);
        void restore(
            const Timestamp &expiryTime,
            const Duration &period,
            bool scheduled,
            bool expired,
            int missed,
            const EventDetails &event
        );
        void cancel();
        void suspend();
        void resume();

        void fire(int overrun);
        void callback(int overrun);

      private:
        void schedule();


        const TimerIdType id;
        asio::system_timer timer;
        bool scheduled;
        bool expired;
        Timestamp expiryTime;
        Duration period;
        int missed;
        EventDetails event;
    };

} // namespace SWA

#endif
