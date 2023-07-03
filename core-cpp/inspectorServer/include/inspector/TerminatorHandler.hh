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
