//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "swa/StackFrame.hh"


namespace SWA
{

  StackFrame::StackFrame ( ActionType type, int domainId, int actionId )
    : type(type),
      domainId(domainId),
      objectId(-1),
      actionId(actionId),
      line(1),
      thisPtr(0)
  {
  }

  StackFrame::StackFrame ( ActionType type, int domainId, int objectId, int actionId )
    : type(type),
      domainId(domainId),
      objectId(objectId),
      actionId(actionId),
      line(1),
      thisPtr(0)

  {
  }

  StackFrame::StackFrame ( ActionType type, int domainId, int objectId, int actionId, int line )
    : type(type),
      domainId(domainId),
      objectId(objectId),
      actionId(actionId),
      line(line),
      thisPtr(0)

  {
  }

  bool StackFrame::operator< ( const StackFrame& rhs ) const
  {
    bool res = (line < rhs.line) ||
           ( (line == rhs.line) && 
             ( (actionId < rhs.actionId) ||
               ( (actionId == rhs.actionId) && 
                 ( (objectId < rhs.objectId) ||
                   ( (objectId == rhs.objectId) && 
                     ( (type < rhs.type) ||
                       ( (type == rhs.type) &&
                         (domainId < rhs.domainId) ))))))); 

    return res;
  }



}
