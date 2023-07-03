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
