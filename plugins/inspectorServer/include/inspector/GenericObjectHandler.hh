/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
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

      virtual ~GenericObjectHandler() = default;

    private:
      typedef std::map<int,std::shared_ptr<ActionHandler> > ActionLookup;
      typedef std::map<int,std::shared_ptr<EventHandler> > EventLookup;
      ActionLookup serviceLookup;
      ActionLookup stateLookup;
      EventLookup  eventLookup;

  };

}


#endif
