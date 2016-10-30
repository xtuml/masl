//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
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
