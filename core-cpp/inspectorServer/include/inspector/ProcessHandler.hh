//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef Inspector_ProcessHandler_HH
#define Inspector_ProcessHandler_HH

#include <map>
#include "boost/shared_ptr.hpp"

namespace Inspector
{
  class DomainHandler;
  
  class ProcessHandler
  {

    public:
      static ProcessHandler& getInstance();

      bool registerDomainHandler ( int domainId, boost::shared_ptr<DomainHandler> handler );

      DomainHandler& getDomainHandler ( int domainId );

    private:
      typedef std::map<int,boost::shared_ptr<DomainHandler> > DomainLookup;
      DomainLookup domainLookup;

  };

}

#endif
