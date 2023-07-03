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

#include "swa/Stack.hh"

namespace SWA
{

  Stack& Stack::instance = Stack::getInstanceStartupSafe();

  Stack& Stack::getInstanceStartupSafe()
  {
    // Adaption of a 'Myers Singleton', uses a function static 
    // for the actual storage, but a pointer for all accesses so 
    // that they can be used inline. If the pointer was not used 
    // the getInstance() call could not be declared inline 
    // because there could then be separate statics all over the 
    // place, unless the compiler is very standard compliant and 
    // does clever stuff to eliminate them. I'm not taking the 
    // chance. 
    static Stack singleton;
    return singleton;
  }

}
