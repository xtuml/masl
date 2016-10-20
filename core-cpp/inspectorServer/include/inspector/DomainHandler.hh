//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
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
