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

#ifndef SWA_Domain_HH
#define SWA_Domain_HH

#include "boost/function.hpp"

#include <stdint.h>
#include <string>
#include <map>

#include "types.hh"

namespace SWA
{

  class Domain
  {
    public:
      Domain () : id(0), name(""), interface(true) {}
      Domain ( int id, const std::string& name, bool interface ) : id(id), name(name), interface(interface) { }

      int getId() const { return id; }

      const std::string& getName() const { return name; }

      bool isInterface() const { return interface; }
      void setInterface( bool interface ) { this->interface = interface; }


      typedef boost::function<void()> Scenario;
      typedef boost::function<void()> External;

      void addExternal ( int id, const Scenario& external );
      void addScenario ( int id, const External& scenario );

      const boost::function<void()>& getScenario(int id) const;
      const boost::function<void()>& getExternal(int id) const;
  
    private:
      typedef std::map<int,Scenario>            ScenarioLookup;
      typedef std::map<int,External>            ExternalLookup;

      int id;
      std::string name;
      bool interface;

      ScenarioLookup            scenarios;
      ExternalLookup            externals;

  };
}

#endif
