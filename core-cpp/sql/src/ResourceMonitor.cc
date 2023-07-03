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

#include <algorithm>
#include <iostream>

#include "swa/Process.hh"
#include "swa/CommandLine.hh"

#include "sql/ResourceMonitor.hh"
#include "sql/ResourceMonitorObserver.hh"

#include "boost/bind/bind.hpp"
using namespace boost::placeholders;


namespace {

const std::string CACHE_REPORTING  ("-sqlcache-reporting");

bool registerCommandLine()
{       
    SWA::CommandLine::getInstance().registerOption (SWA::NamedOption(CACHE_REPORTING,  "report on the cache usage for the sql implementation",false));
    return true;
}
bool registerCmdLine = registerCommandLine();

}

namespace SQL {

// *************************************************
// *************************************************
ResourceMonitor& ResourceMonitor::singleton()
{
    static ResourceMonitor instance;
    return instance;
}

// *************************************************
// *************************************************
ResourceMonitor::ResourceMonitor():
  reportingEnabled(SWA::CommandLine::getInstance().optionPresent(CACHE_REPORTING))
{

}

// *************************************************
// *************************************************
ResourceMonitor::~ResourceMonitor()
{
   observerList_.clear();
}

// *************************************************
// *************************************************
void ResourceMonitor::committed()
{
  if(reportingEnabled == true){
     reportOnResources();
  }
  compactResources();
}

// *************************************************
// *************************************************
void ResourceMonitor::registerActiveResource(ResourceMonitorObserver* observer)
{
   observerList_.insert(observer);
}

// *************************************************
// *************************************************
void ResourceMonitor::deregisterActiveResource(ResourceMonitorObserver* observer)
{
   // Be careful when calling this method because this object is created statically 
   // and is therefore prone to static de-initialisation ordering problems when
   // being called from within destructors.
   observerList_.erase(observer);
}

// *************************************************
// *************************************************
void ResourceMonitor::reportOnResources ()
{
   ResourceMonitorContext context;
   std::for_each(observerList_.begin(),observerList_.end(),boost::bind(&ResourceMonitorObserver::report,_1,context));
}

// *************************************************
// *************************************************
void ResourceMonitor::compactResources  ()
{
   ResourceMonitorContext context;
   std::for_each(observerList_.begin(),observerList_.end(),boost::bind(&ResourceMonitorObserver::compact,_1,context));
}

// *************************************************
// *************************************************
void ResourceMonitor::releaseResources  ()
{
   ResourceMonitorContext context;
   std::for_each(observerList_.begin(),observerList_.end(),boost::bind(&ResourceMonitorObserver::release,_1,context));
}
} // end namepsace SQL
