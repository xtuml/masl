/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Inspector_DomainHandler_HH
#define Inspector_DomainHandler_HH

#include <map>
#include <memory>
#include <functional>

#include "types.hh"

namespace Inspector
{

  class CommunicationChannel;
  class ActionHandler;
  template<class Obj> class ObjectHandler;
  class GenericObjectHandler;
  class TerminatorHandler;

  class DomainHandler
  {
    public:
      bool registerObjectHandler ( int objectId, std::shared_ptr<GenericObjectHandler> handler );
      GenericObjectHandler& getGenericObjectHandler ( int objectId );

      bool registerTerminatorHandler ( int terminatorId, std::shared_ptr<TerminatorHandler> handler );
      TerminatorHandler& getTerminatorHandler ( int terminatorId );

      template<class Object>
      ObjectHandler<Object>& getObjectHandler ( int objectId ) { return dynamic_cast<ObjectHandler<Object>& >(getGenericObjectHandler(objectId)); }

      bool registerServiceHandler ( int serviceId, std::shared_ptr<ActionHandler> handler );
      ActionHandler& getServiceHandler ( int objectId );

      virtual void createRelationship ( CommunicationChannel& channel, int relationshipId ) = 0;

      virtual ~DomainHandler() = default;

    private:
      typedef std::map<int,std::shared_ptr<GenericObjectHandler> > ObjectLookup;
      ObjectLookup objectLookup;

      typedef std::map<int,std::shared_ptr<TerminatorHandler> > TerminatorLookup;
      TerminatorLookup terminatorLookup;

      typedef std::map<int,std::shared_ptr<ActionHandler> > ServiceLookup;
      ServiceLookup serviceLookup;
  };

}

#endif
