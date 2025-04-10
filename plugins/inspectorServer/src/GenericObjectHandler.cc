/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "inspector/GenericObjectHandler.hh"
#include "inspector/ActionHandler.hh"
#include "inspector/EventHandler.hh"

namespace Inspector
{
  bool GenericObjectHandler::registerServiceHandler ( int serviceId, std::shared_ptr<ActionHandler> handler )
  {
    serviceLookup.insert(ActionLookup::value_type(serviceId,handler));
    return true;
  }

  ActionHandler& GenericObjectHandler::getServiceHandler ( int serviceId )
  {
    ActionLookup::iterator it = serviceLookup.find(serviceId);

    return *(it->second);
  }


  bool GenericObjectHandler::registerStateHandler ( int stateId, std::shared_ptr<ActionHandler> handler )
  {
    stateLookup.insert(ActionLookup::value_type(stateId,handler));
    return true;
  }

  ActionHandler& GenericObjectHandler::getStateHandler ( int stateId )
  {
    ActionLookup::iterator it = stateLookup.find(stateId);

    return *(it->second);
  }



  bool GenericObjectHandler::registerEventHandler ( int eventId, std::shared_ptr<EventHandler> handler )
  {
    eventLookup.insert(EventLookup::value_type(eventId,handler));
    return true;
  }

  EventHandler& GenericObjectHandler::getEventHandler ( int eventId )
  {
    EventLookup::iterator it = eventLookup.find(eventId);

    return *(it->second);
  }


}
