//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "swa/NameFormatter.hh"
#include "swa/Process.hh"

#include "boost/lexical_cast.hpp"
#include <iostream>

namespace SWA
{
  boost::shared_ptr<NameFormatter>& getOverride()
  {
    static boost::shared_ptr<NameFormatter> formatter;
    return formatter;
  }

  NameFormatter& NameFormatter::getInstance()
  {
    static NameFormatter instance;
    return getOverride()?*getOverride().get():instance;
  }


  bool NameFormatter::overrideFormatter ( const boost::shared_ptr<NameFormatter> newFormatter )
  {
    getOverride() = newFormatter;
    return true;
  }

  std::string NameFormatter::formatStackFrame ( const ExceptionStackFrame& frame, bool showLineNo )
  {
    return formatStackFrame(frame.getType(),frame.getDomainId(),frame.getObjectId(),frame.getActionId())
     + (showLineNo?(":"+boost::lexical_cast<std::string>(frame.getLine())):"");
  }

  std::string NameFormatter::formatStackFrame ( const StackFrame& frame, bool showLineNo )
  {
    return formatStackFrame(frame.getType(),frame.getDomainId(),frame.getObjectId(),frame.getActionId())
     + (showLineNo?(":"+boost::lexical_cast<std::string>(frame.getLine())):"");
  }

  std::string NameFormatter::formatStackFrame ( StackFrame::ActionType type, int domainId, int objectId, int actionId )
  {
    std::string name;
    switch ( type )
    {
      case StackFrame::DomainService:     return formatDomainServiceName(domainId,actionId);
      case StackFrame::TerminatorService: return formatTerminatorServiceName(domainId,objectId,actionId);
      case StackFrame::ObjectService:     return formatObjectServiceName(domainId,objectId,actionId);
      case StackFrame::StateAction:       return formatStateName(domainId,objectId,actionId);
      default:                            return "UNKNOWN";
    }
  }

  std::string NameFormatter::getDomainName ( int domainId )
  {
    return Process::getInstance().getDomain(domainId).getName();
  }

  std::string NameFormatter::getTerminatorName ( int domainId, int terminatorId ) const
  {
    return "Terminator_" + boost::lexical_cast<std::string>(terminatorId);
  }

  std::string NameFormatter::getTerminatorServiceName ( int domainId, int terminatorId, int serviceId ) const
  {
    return "Service_" + boost::lexical_cast<std::string>(serviceId);
  }

  std::string NameFormatter::getDomainServiceName ( int domainId, int serviceId ) const
  {
    return "Service_" + boost::lexical_cast<std::string>(serviceId);
  }

  std::string NameFormatter::getObjectName ( int domainId, int objectId ) const
  {
    return "Object_" + boost::lexical_cast<std::string>(objectId);
  }

  std::string NameFormatter::getObjectServiceName ( int domainId, int objectId, int serviceId ) const
  {
    return "Service_" + boost::lexical_cast<std::string>(serviceId);
  }

  std::string NameFormatter::getStateName ( int domainId, int objectId, int stateId ) const
  {
    return "State_" + boost::lexical_cast<std::string>(stateId);
  }

  std::string NameFormatter::getEventName ( int domainId, int objectId, int eventId ) const
  {
    return "Event_" + boost::lexical_cast<std::string>(eventId);
  }

  int NameFormatter::getEventParentObjectId ( int domainId, int objectId, int eventId ) const
  {
    return objectId;
  }

  std::string NameFormatter::formatTerminatorName ( int domainId, int terminatorId )
  {
    return getDomainName(domainId) + "::" + getInstance().getTerminatorName(domainId,terminatorId);
  }

  std::string NameFormatter::formatDomainServiceName ( int domainId, int serviceId )
  {
    return getDomainName(domainId) + "::" + getInstance().getDomainServiceName(domainId,serviceId);
  }

  std::string NameFormatter::formatObjectName ( int domainId, int objectId )
  {
    return getDomainName(domainId) + "::" + getInstance().getObjectName(domainId,objectId);
  }

  std::string NameFormatter::formatTerminatorServiceName ( int domainId, int terminatorId, int serviceId )
  {
    return formatTerminatorName(domainId,terminatorId) + "~>" + getInstance().getTerminatorServiceName(domainId,terminatorId,serviceId);
  }

  std::string NameFormatter::formatObjectServiceName ( int domainId, int objectId, int serviceId )
  {
    return formatObjectName(domainId,objectId) + "." + getInstance().getObjectServiceName(domainId,objectId,serviceId);
  }

  std::string NameFormatter::formatStateName ( int domainId, int objectId, int stateId )
  {
    return formatObjectName(domainId,objectId) + "." + getInstance().getStateName(domainId,objectId,stateId);
  }

  std::string NameFormatter::formatEventName ( int domainId, int objectId, int eventId )
  {
    return formatObjectName(domainId,getInstance().getEventParentObjectId(domainId,objectId,eventId)) + "." + getInstance().getEventName(domainId,objectId,eventId);
  }

}

