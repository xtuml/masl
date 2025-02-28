/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef TRANSIENT_EventTimers_HH
#define TRANSIENT_EventTimers_HH

#include <swa/EventTimers.hh>

namespace transient {
    class EventTimers : public ::SWA::EventTimers {
      public:
        static EventTimers &getInstance();

      private:
        EventTimers()
            : nextId(1) {}

        virtual TimerIdType createTimerInner() {
            return nextId++;
        }
        virtual void deleteTimerInner(const TimerIdType id) {}
        virtual void updateTimerInner(const SWA::EventTimer &eventTimer) {}

        TimerIdType nextId;
    };
} // namespace transient

#endif
