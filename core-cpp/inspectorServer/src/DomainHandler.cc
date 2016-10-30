//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
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
