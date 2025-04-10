/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Inspector_EventHandler_HH
#define Inspector_EventHandler_HH

#include "CommunicationChannel.hh"
#include "swa/Event.hh"
#include <memory>

namespace Inspector
{
  class EventHandler
  {
    public:
      virtual std::shared_ptr<SWA::Event> getEvent ( CommunicationChannel& channel ) const = 0;

      virtual void writeParameters ( const SWA::Event& event, BufferedOutputStream& stream ) const = 0;

      virtual ~EventHandler() = default;
  };
}

#endif
