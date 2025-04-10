/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Inspector_TerminatorHandler_HH
#define Inspector_TerminatorHandler_HH

#include "types.hh"
#include <map>
#include <memory>

namespace Inspector
{
  class CommunicationChannel;
  class ActionHandler;

  class TerminatorHandler
  {
    public:
      bool registerServiceHandler ( int serviceId, std::shared_ptr<ActionHandler> handler );
      ActionHandler& getServiceHandler ( int serviceId );

      bool overrideServiceHandler ( int serviceId, std::shared_ptr<ActionHandler> handler );

      virtual ~TerminatorHandler() = default;

    private:
      typedef std::map<int,std::shared_ptr<ActionHandler> > ActionLookup;
      ActionLookup serviceLookup;

  };

}


#endif
