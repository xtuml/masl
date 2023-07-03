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

#ifndef SWA_Schedule_HH
#define SWA_Schedule_HH

#include <string>
#include <vector>
#include <boost/function.hpp>

namespace SWA
{
  class Schedule
  {
    public:
      Schedule ( const std::string& name, const std::string& text );
      
      bool isValid() const { return valid; }

      const std::string& getName() const { return name; }

      typedef boost::function<void()> Action;
 
      typedef std::vector<Action> Actions;
      const Actions& getActions() const { return actions; }

    private:
      void reportError( int lineNo, const std::string& error );
      std::string name;
      std::string text;
      bool valid;

      Actions actions;
  };
}

#endif
