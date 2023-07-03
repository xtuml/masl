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

#ifndef Inspector_DomainHandler_HH
#define Inspector_DomainHandler_HH

#include <map>
#include "boost/shared_ptr.hpp"
#include "boost/function.hpp"

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
      bool registerObjectHandler ( int objectId, boost::shared_ptr<GenericObjectHandler> handler );
      GenericObjectHandler& getGenericObjectHandler ( int objectId );

      bool registerTerminatorHandler ( int terminatorId, boost::shared_ptr<TerminatorHandler> handler );
      TerminatorHandler& getTerminatorHandler ( int terminatorId );

      template<class Object>
      ObjectHandler<Object>& getObjectHandler ( int objectId ) { return dynamic_cast<ObjectHandler<Object>& >(getGenericObjectHandler(objectId)); }

      bool registerServiceHandler ( int serviceId, boost::shared_ptr<ActionHandler> handler );
      ActionHandler& getServiceHandler ( int objectId );

      virtual void createRelationship ( CommunicationChannel& channel, int relationshipId ) = 0;

      virtual ~DomainHandler();

    private:
      typedef std::map<int,boost::shared_ptr<GenericObjectHandler> > ObjectLookup;
      ObjectLookup objectLookup;

      typedef std::map<int,boost::shared_ptr<TerminatorHandler> > TerminatorLookup;
      TerminatorLookup terminatorLookup;

      typedef std::map<int,boost::shared_ptr<ActionHandler> > ServiceLookup;
      ServiceLookup serviceLookup;
  };

}

#endif
