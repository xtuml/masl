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

#ifndef Sql_ResourceMonitorObserver_HH
#define Sql_ResourceMonitorObserver_HH

#include <iostream>

namespace SQL {

// *************************************************
// *************************************************
class ResourceMonitorContext
{
   public:
      ResourceMonitorContext():reportStream_(&std::cout)          {}
      ResourceMonitorContext(std::ostream* str):reportStream_(str){}
     ~ResourceMonitorContext(){}

      std::ostream* getReportStream() { return reportStream_; }

   private:
       std::ostream* reportStream_;
};


// *************************************************
// *************************************************
class ResourceMonitorObserver
{
  public:

    virtual void report  (ResourceMonitorContext& context) = 0;  // report on resource usage.
    virtual void compact (ResourceMonitorContext& context) = 0;  // shrink resource usage to minimum.
    virtual void release (ResourceMonitorContext& context) = 0;  // release all used resources.

   protected:
     ResourceMonitorObserver(){};
     virtual ~ResourceMonitorObserver(){}
};

} // end namepsace SQL

#endif
