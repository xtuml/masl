/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_TimerListener_HH
#define SWA_TimerListener_HH

#include "Duration.hh"
#include "Timestamp.hh"
#include <boost/utility.hpp>
#include <functional>

namespace SWA {
    class ListenerPriority;

    class TimerListener : public boost::noncopyable {
      public:
        typedef std::function<void(int)> Callback;

        TimerListener(const ListenerPriority &priority, const Callback &callback);
        ~TimerListener();

        void schedule(const Timestamp &expiryTime, const Duration &interval = Duration::zero());
        void cancel();

      private:
        void timerFired(int32_t overrun);

        void setTime(const timespec &expiryTime, const timespec &interval);

      private:
        timer_t timerId;
        const Callback callback;

        Timestamp expiryTime;
        Duration interval;
        bool active;
    };

} // namespace SWA

#endif
