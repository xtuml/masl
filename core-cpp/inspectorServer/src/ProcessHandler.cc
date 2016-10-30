//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "inspector/ProcessHandler.hh"
#include "inspector/DomainHandler.hh"
#include "swa/Process.hh"
#include "swa/ProgramError.hh"
#include <iostream>
namespace Inspector
{

  namespace 
  {
    // Load up the domain handlers once the process has initialised and domains are known.
    bool loadLibs()
    {
      SWA::Process::getInstance().loadDynamicLibraries ("inspector", "_if",true);
      return true;
    }

    bool initialise()
    {
      SWA::Process::getInstance().registerInitialisedListener(&loadLibs);
      return true;
    }

    bool initialised = initialise();
  }


  ProcessHandler& ProcessHandler::getInstance()
  {
    static ProcessHandler instance;
    return instance;
  }

  bool ProcessHandler::registerDomainHandler ( int domainId, boost::shared_ptr<DomainHandler> handler )
  {
    domainLookup.insert(DomainLookup::value_type(domainId,handler));
    return true;
  }

  DomainHandler& ProcessHandler::getDomainHandler ( int domainId )
  {
    DomainLookup::iterator it = domainLookup.find(domainId);

    if ( it == domainLookup.end() )
    {
      throw SWA::ProgramError("No Inspector Data present for " + SWA::Process::getInstance().getDomain(domainId).getName() + "." );
    }

    return *(it->second);
  }

}
