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
      SWA::Process::getInstance().loadDynamicProjectLibrary ("inspector");
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
