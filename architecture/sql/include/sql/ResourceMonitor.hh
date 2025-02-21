/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_ResourceMonitor__
#define Sql_ResourceMonitor__

#include <iosfwd>
#include <set>

namespace SQL {

class ResourceMonitorObserver;
class ResourceMonitor {
  public:
    static ResourceMonitor &singleton();

    void registerActiveResource(ResourceMonitorObserver *observer);
    void deregisterActiveResource(ResourceMonitorObserver *observer);

    void reportOnResources();
    void compactResources(); // shrink resource usage to minimum.
    void releaseResources(); // release all used resources.

    void committed();

  private:
    ResourceMonitor();
    ~ResourceMonitor();

    ResourceMonitor(ResourceMonitor &rhs);
    ResourceMonitor &operator=(ResourceMonitor &rhs);

  private:
    std::set<ResourceMonitorObserver *> observerList_;
    bool reportingEnabled;
};

} // namespace SQL
#endif
