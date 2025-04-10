/*
* ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "inspector/DomainHandler.hh"
#include "inspector/ObjectHandler.hh"
#include "inspector/GenericObjectHandler.hh"
#include "inspector/TerminatorHandler.hh"

namespace Inspector
{
  bool DomainHandler::registerObjectHandler ( int objectId, std::shared_ptr<GenericObjectHandler> handler )
  {
    objectLookup.insert(ObjectLookup::value_type(objectId,handler));
    return true;
  }

  GenericObjectHandler& DomainHandler::getGenericObjectHandler ( int objectId )
  {
    ObjectLookup::iterator it = objectLookup.find(objectId);

    return *(it->second);
  }

  bool DomainHandler::registerTerminatorHandler ( int terminatorId, std::shared_ptr<TerminatorHandler> handler )
  {
    terminatorLookup.insert(TerminatorLookup::value_type(terminatorId,handler));
    return true;
  }

  TerminatorHandler& DomainHandler::getTerminatorHandler ( int terminatorId )
  {
    TerminatorLookup::iterator it = terminatorLookup.find(terminatorId);

    return *(it->second);
  }

  bool DomainHandler::registerServiceHandler ( int serviceId, std::shared_ptr<ActionHandler> handler )
  {
    serviceLookup.insert(ServiceLookup::value_type(serviceId,handler));
    return true;
  }

  ActionHandler& DomainHandler::getServiceHandler ( int serviceId )
  {
    ServiceLookup::iterator it = serviceLookup.find(serviceId);

    return *(it->second);
  }



}
