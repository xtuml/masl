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

#include "swa/ListenerPriority.hh"
#include <signal.h>

namespace SWA
{

  const ListenerPriority& ListenerPriority::getMinimum()
  {
    static ListenerPriority priority(SIGRTMAX);
    return priority;
  }

  const ListenerPriority& ListenerPriority::getMaximum()
  {
    static ListenerPriority priority(SIGRTMIN);
    return priority;
  }

  const ListenerPriority& ListenerPriority::getNormal()
  {
    static ListenerPriority priority(getMinimum(),getMaximum());
    return priority;
  }

  const ListenerPriority& ListenerPriority::getLow()
  {
    static ListenerPriority priority(getMinimum(),getNormal());
    return priority;
  }

  const ListenerPriority& ListenerPriority::getHigh()
  {
    static ListenerPriority priority(getNormal(),getMaximum());
    return priority;
  }

  ListenerPriority::ListenerPriority ( int priority )
    : priority(priority)
  {
  }

 ListenerPriority::ListenerPriority ( const ListenerPriority& low, const ListenerPriority& high )
    : priority((low.priority + high.priority)/2)
  {
  }

}
