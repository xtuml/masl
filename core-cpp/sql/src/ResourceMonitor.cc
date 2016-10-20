//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include <algorithm>
#include <iostream>

#include "swa/Process.hh"
#include "swa/CommandLine.hh"

#include "sql/ResourceMonitor.hh"
#include "sql/ResourceMonitorObserver.hh"

#include "boost/bind.hpp"

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
