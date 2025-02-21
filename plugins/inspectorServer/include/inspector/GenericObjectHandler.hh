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

#ifndef Inspector_GenericObjectHandler_HH
#define Inspector_GenericObjectHandler_HH

#include "swa/types.hh"
#include "types.hh"
#include <vector>
#include <map>
#include <memory>

namespace Inspector
{
  class CommunicationChannel;
  class EventHandler;
  class ActionHandler;

  class GenericObjectHandler
  {
    public:
      virtual void writePopulation ( CommunicationChannel& channel ) const = 0;
      virtual void writeRelatedInstances ( CommunicationChannel& channel, SWA::IdType id, int relId ) const = 0;
      virtual void writeSelectedInstances ( CommunicationChannel& channel, const std::vector<SWA::IdType>& ids ) const = 0;
      virtual void writeInstance ( CommunicationChannel& channel, SWA::IdType id ) const = 0;

      virtual void createInstance ( CommunicationChannel& channel ) const = 0;
      virtual void deleteInstance ( CommunicationChannel& channel, SWA::IdType id ) const = 0;
 
      virtual int getCardinality() const = 0;

      bool registerServiceHandler ( int serviceId, std::shared_ptr<ActionHandler> handler );
      ActionHandler& getServiceHandler ( int serviceId );

      bool registerStateHandler ( int serviceId, std::shared_ptr<ActionHandler> handler );
      ActionHandler& getStateHandler ( int stateId );

      bool registerEventHandler ( int eventId, std::shared_ptr<EventHandler> handler );
      EventHandler& getEventHandler ( int eventId );

      virtual std::string getIdentifierText ( SWA::IdType id ) const = 0;

      virtual ~GenericObjectHandler();

    private:
      typedef std::map<int,std::shared_ptr<ActionHandler> > ActionLookup;
      typedef std::map<int,std::shared_ptr<EventHandler> > EventLookup;
      ActionLookup serviceLookup;
      ActionLookup stateLookup;
      EventLookup  eventLookup;

  };

}


#endif
