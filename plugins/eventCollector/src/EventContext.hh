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

#ifndef Events_EventContext_HH
#define Events_EventContext_HH

#include <string>
#include <vector>

#include "swa/types.hh"

namespace SWA {
class StackFrame;
class DomainMetaData;
class ObjectMetaData;
class StateMetaData;
}

namespace EVENTS {

class EventContext
{
   public:
      EventContext( int domainId, int srcObjectId, int eventId, int destObjectId, SWA::IdType instanceId);
      EventContext( int domainId, int srcObjectId, int eventId, int destObjectId);
     ~EventContext( );

      int getDomainId      () const;
      int getEventId       () const;
      int getSrcObjectId   () const;
      int getDstObjectId   () const;
      int getDstInstanceId () const;

      bool isNormalEvent() const;
      
      const std::string getType()       const;
      const std::string getDomain()     const;
      const std::string getEventName()  const;

      const std::string getSrcInstanceIdText() const;
      const std::string getDstInstanceIdText() const;

      const std::string getSrcObjectName() const;
      const std::string getDstObjectName() const;

   private:
       int domainId;
       int srcObjectId;
       int eventId;
       int dstObjectId;
       int instanceId;

};

} // end EVENTS namespace

#endif
