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

#ifndef Sql_ResourceMonitor__
#define Sql_ResourceMonitor__

#include <set>
#include <iosfwd>

namespace SQL {

class ResourceMonitorObserver;
class ResourceMonitor
{
  public:
     static ResourceMonitor& singleton();

     void registerActiveResource   (ResourceMonitorObserver* observer);
     void deregisterActiveResource (ResourceMonitorObserver* observer);

     void reportOnResources ();
     void compactResources  ();    // shrink resource usage to minimum.
     void releaseResources  ();    // release all used resources.

     void committed();

  private:
      ResourceMonitor();
     ~ResourceMonitor();

      ResourceMonitor(ResourceMonitor& rhs);
      ResourceMonitor& operator=(ResourceMonitor& rhs);

  private:
      std::set<ResourceMonitorObserver*> observerList_;
      bool                               reportingEnabled;
};


} // end namepsace POSTGRES
#endif
