/*
* ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Inspector_ProcessHandler_HH
#define Inspector_ProcessHandler_HH

#include <map>
#include <memory>

namespace Inspector
{
  class DomainHandler;
  
  class ProcessHandler
  {

    public:
      static ProcessHandler& getInstance();

      bool registerDomainHandler ( int domainId, std::shared_ptr<DomainHandler> handler );

      DomainHandler& getDomainHandler ( int domainId );

    private:
      typedef std::map<int,std::shared_ptr<DomainHandler> > DomainLookup;
      DomainLookup domainLookup;

  };

}

#endif
