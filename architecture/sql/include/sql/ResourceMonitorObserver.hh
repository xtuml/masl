/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_ResourceMonitorObserver_HH
#define Sql_ResourceMonitorObserver_HH

#include <iostream>

namespace SQL {

// *************************************************
// *************************************************
class ResourceMonitorContext {
  public:
    ResourceMonitorContext() : reportStream_(&std::cout) {}
    ResourceMonitorContext(std::ostream *str) : reportStream_(str) {}
    ~ResourceMonitorContext() {}

    std::ostream *getReportStream() { return reportStream_; }

  private:
    std::ostream *reportStream_;
};

// *************************************************
// *************************************************
class ResourceMonitorObserver {
  public:
    virtual void
    report(ResourceMonitorContext &context) = 0; // report on resource usage.
    virtual void compact(ResourceMonitorContext
                             &context) = 0; // shrink resource usage to minimum.
    virtual void
    release(ResourceMonitorContext &context) = 0; // release all used resources.

  protected:
    ResourceMonitorObserver() {};
    virtual ~ResourceMonitorObserver() {}
};

} // namespace SQL

#endif
