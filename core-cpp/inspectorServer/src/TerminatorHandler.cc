//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "inspector/TerminatorHandler.hh"
#include "inspector/ActionHandler.hh"

namespace Inspector
{
  TerminatorHandler::~TerminatorHandler() 
  {
  }

  bool TerminatorHandler::registerServiceHandler ( int serviceId, boost::shared_ptr<ActionHandler> handler )
  {
    serviceLookup.insert(ActionLookup::value_type(serviceId,handler));
    return true;
  }

  ActionHandler& TerminatorHandler::getServiceHandler ( int serviceId )
  {
    ActionLookup::iterator it = serviceLookup.find(serviceId);

    return *(it->second);
  }

  bool TerminatorHandler::overrideServiceHandler ( int serviceId, boost::shared_ptr<ActionHandler> handler )
  {
    serviceLookup[serviceId] = handler;
    return true;
  }

}
