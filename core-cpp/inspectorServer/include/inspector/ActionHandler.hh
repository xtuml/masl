//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef Inspector_ActionHandler_HH
#define Inspector_ActionHandler_HH

#include "CommunicationChannel.hh"
#include <iostream>
#include <vector>
#include "swa/Set.hh"
#include "swa/Stack.hh"
#include "types.hh"

namespace Inspector
{
  class ActionHandler
  {
    public:
      virtual Callable getInvoker ( CommunicationChannel& channel ) const { return Callable(); }
      virtual void writeLocalVars ( CommunicationChannel& channel, const SWA::StackFrame& frame  ) const = 0;

      virtual ~ActionHandler();
  };
}

#endif
