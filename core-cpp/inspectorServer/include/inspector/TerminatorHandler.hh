//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef Inspector_TerminatorHandler_HH
#define Inspector_TerminatorHandler_HH

#include "types.hh"
#include <map>
#include "boost/shared_ptr.hpp"

namespace Inspector
{
  class CommunicationChannel;
  class ActionHandler;

  class TerminatorHandler
  {
    public:
      bool registerServiceHandler ( int serviceId, boost::shared_ptr<ActionHandler> handler );
      ActionHandler& getServiceHandler ( int serviceId );

      bool overrideServiceHandler ( int serviceId, boost::shared_ptr<ActionHandler> handler );

      virtual ~TerminatorHandler();

    private:
      typedef std::map<int,boost::shared_ptr<ActionHandler> > ActionLookup;
      ActionLookup serviceLookup;

  };

}


#endif
