/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 * Classification: UK OFFICIAL
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

      virtual ~EventHandler();
  };
}

#endif
