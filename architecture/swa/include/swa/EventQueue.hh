/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_EventQueue_HH
#define SWA_EventQueue_HH

#include "Event.hh"

#include <memory>

#include <deque>
#include <map>
#include <vector>

namespace SWA {
class EventQueue {
  public:
    void addEvent(const std::shared_ptr<Event> event);

    int processEvents();
    bool empty();

    std::vector<std::shared_ptr<Event>> getEvents() const;

  private:
    typedef std::deque<std::shared_ptr<Event>> InnerQueueType;
    typedef std::map<int, InnerQueueType> QueueType;

    QueueType queue;
};

} // namespace SWA

#endif
