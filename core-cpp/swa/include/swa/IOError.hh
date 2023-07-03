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

#ifndef SWA_IOError_HH
#define SWA_IOError_HH

#include <string>
#include "boost/tuple/tuple.hpp"

#include "Exception.hh"

namespace SWA
{
  class IOError : public Exception
  {
    public:
       IOError () : Exception(std::string("IO Error :")) {}
       IOError ( const std::string& error ) : Exception(std::string("IO Error :") + error) {}

       template <class T>
       IOError (const T& tuple):Exception(std::string("IO Error :"),tuple) {}
  };

}

#endif
