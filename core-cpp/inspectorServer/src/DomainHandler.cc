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

#include "inspector/DomainHandler.hh"
#include "inspector/ObjectHandler.hh"
#include "inspector/GenericObjectHandler.hh"
#include "inspector/TerminatorHandler.hh"

namespace Inspector
{
  DomainHandler::~DomainHandler() 
  {
  }

  bool DomainHandler::registerObjectHandler ( int objectId, boost::shared_ptr<GenericObjectHandler> handler )
  {
    objectLookup.insert(ObjectLookup::value_type(objectId,handler));
    return true;
  }

  GenericObjectHandler& DomainHandler::getGenericObjectHandler ( int objectId )
  {
    ObjectLookup::iterator it = objectLookup.find(objectId);

    return *(it->second);
  }

  bool DomainHandler::registerTerminatorHandler ( int terminatorId, boost::shared_ptr<TerminatorHandler> handler )
  {
    terminatorLookup.insert(TerminatorLookup::value_type(terminatorId,handler));
    return true;
  }

  TerminatorHandler& DomainHandler::getTerminatorHandler ( int terminatorId )
  {
    TerminatorLookup::iterator it = terminatorLookup.find(terminatorId);

    return *(it->second);
  }

  bool DomainHandler::registerServiceHandler ( int serviceId, boost::shared_ptr<ActionHandler> handler )
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
