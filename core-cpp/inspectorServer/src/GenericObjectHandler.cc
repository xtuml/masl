//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "inspector/GenericObjectHandler.hh"
#include "inspector/ActionHandler.hh"
#include "inspector/EventHandler.hh"

namespace Inspector
{
  GenericObjectHandler::~GenericObjectHandler() 
  {
  }


  bool GenericObjectHandler::registerServiceHandler ( int serviceId, boost::shared_ptr<ActionHandler> handler )
  {
    serviceLookup.insert(ActionLookup::value_type(serviceId,handler));
    return true;
  }

  ActionHandler& GenericObjectHandler::getServiceHandler ( int serviceId )
  {
    ActionLookup::iterator it = serviceLookup.find(serviceId);

    return *(it->second);
  }


  bool GenericObjectHandler::registerStateHandler ( int stateId, boost::shared_ptr<ActionHandler> handler )
  {
    stateLookup.insert(ActionLookup::value_type(stateId,handler));
    return true;
  }

  ActionHandler& GenericObjectHandler::getStateHandler ( int stateId )
  {
    ActionLookup::iterator it = stateLookup.find(stateId);

    return *(it->second);
  }



  bool GenericObjectHandler::registerEventHandler ( int eventId, boost::shared_ptr<EventHandler> handler )
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
