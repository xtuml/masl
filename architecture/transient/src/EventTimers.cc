/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "EventTimers.hh"

namespace transient {

EventTimers &EventTimers::getInstance() {
    static EventTimers instance;
    return instance;
}

bool registered = EventTimers::registerSingleton(&EventTimers::getInstance);

} // namespace transient
