//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  EventContext.cc
//
//============================================================================//
#include "EventContext.hh"

#include "metadata/MetaData.hh"

#include "boost/lexical_cast.hpp"

namespace EVENTS {

// ***************************************************************
// ***************************************************************
EventContext::EventContext( int domainId, int srcObjectId, int eventId, int dstObjectId, SWA::IdType instanceId):
    domainId(domainId),
    srcObjectId(srcObjectId),
    eventId(eventId),
    dstObjectId(dstObjectId),
    instanceId(instanceId)
{

}

// ***************************************************************
// ***************************************************************
EventContext::EventContext( int domainId, int srcObjectId, int eventId, int dstObjectId):
    domainId(domainId),
    srcObjectId(srcObjectId),
    eventId(eventId),
    dstObjectId(dstObjectId),
    instanceId(0)
{

}

// ***************************************************************
// ***************************************************************
EventContext::~EventContext( )
{

}

// ***************************************************************
// ***************************************************************
int EventContext::getDomainId () const
{
  return domainId;
}

// ***************************************************************
// ***************************************************************
int EventContext::getEventId () const
{
 return eventId;
}

// ***************************************************************
// ***************************************************************
int EventContext::getSrcObjectId () const
{
  return srcObjectId;
}

// ***************************************************************
// ***************************************************************
int EventContext::getDstObjectId () const
{
  return dstObjectId;
}

// ***************************************************************
// ***************************************************************
int EventContext::getDstInstanceId () const
{
  return instanceId;
}

// ***************************************************************
// ***************************************************************
bool EventContext::isNormalEvent() const
{
  return instanceId > 0;
}

// ***************************************************************
// ***************************************************************
const std::string EventContext::getDomain() const
{
    return SWA::ProcessMetaData::getProcess().getDomain(domainId).getName();
}

// ***************************************************************
// ***************************************************************
const std::string EventContext::getType() const
{
  const SWA::EventMetaData& eventMetaData = SWA::ProcessMetaData::getProcess().
                                                     getDomain(domainId).
                                                     getObject(dstObjectId).
                                                     getEvent(eventId); 
     std::string type("illegal");
     switch (eventMetaData.getType())
     {
       case SWA::EventMetaData::Assigner : type = "assigner"; break;
       case SWA::EventMetaData::Normal   : type = "normal";   break;
       case SWA::EventMetaData::Creation : type = "creation"; break;
       default :  break;    // allow default value to be used.
                 
     }
     return type;
}

// ***************************************************************
// ***************************************************************
const std::string EventContext::getSrcInstanceIdText() const
{
  std::string srcObjId("none"); 
  return srcObjId;
}

// ***************************************************************
// ***************************************************************
const std::string EventContext::getDstInstanceIdText() const
{
  std::string instanceIdText("none"); 
  if (instanceId > 0){
      instanceIdText = boost::lexical_cast<std::string>(instanceId);
  }
  return instanceIdText;
}

// ***************************************************************
// ***************************************************************
const std::string EventContext::getSrcObjectName() const
{
  std::string srcObjName("none");
  if (srcObjectId >= 0){
      srcObjName = SWA::ProcessMetaData::getProcess().getDomain(domainId).getObject(srcObjectId).getName();
  }
  return srcObjName;
}

// ***************************************************************
// ***************************************************************
const std::string EventContext::getDstObjectName() const
{
  std::string dstObjName("none");
  if (dstObjectId >= 0){
      dstObjName = SWA::ProcessMetaData::getProcess().getDomain(domainId).getObject(dstObjectId).getName();
  }
  return dstObjName;

}

// ***************************************************************
// ***************************************************************
const std::string EventContext::getEventName() const
{
  return SWA::ProcessMetaData::getProcess().getDomain(domainId).getObject(dstObjectId).getEvent(eventId).getName();
}




}
