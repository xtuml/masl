/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */


#include "inspector/TerminatorHandler.hh"
#include "inspector/ActionHandler.hh"

namespace Inspector
{
  bool TerminatorHandler::registerServiceHandler ( int serviceId, std::shared_ptr<ActionHandler> handler )
  {
    serviceLookup.insert(ActionLookup::value_type(serviceId,handler));
    return true;
  }

  ActionHandler& TerminatorHandler::getServiceHandler ( int serviceId )
  {
    ActionLookup::iterator it = serviceLookup.find(serviceId);

    return *(it->second);
  }

  bool TerminatorHandler::overrideServiceHandler ( int serviceId, std::shared_ptr<ActionHandler> handler )
  {
    serviceLookup[serviceId] = handler;
    return true;
  }

}
