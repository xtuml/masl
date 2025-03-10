/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include <algorithm>
#include <iostream>

#include "swa/CommandLine.hh"
#include "swa/Process.hh"

#include "sql/ResourceMonitor.hh"
#include "sql/ResourceMonitorObserver.hh"

namespace {

    const std::string CACHE_REPORTING("-sqlcache-reporting");

    bool registerCommandLine() {
        SWA::CommandLine::getInstance().registerOption(
            SWA::NamedOption(CACHE_REPORTING, "report on the cache usage for the sql implementation", false)
        );
        return true;
    }
    bool registerCmdLine = registerCommandLine();

} // namespace

namespace SQL {

    // *************************************************
    // *************************************************
    ResourceMonitor &ResourceMonitor::singleton() {
        static ResourceMonitor instance;
        return instance;
    }

    // *************************************************
    // *************************************************
    ResourceMonitor::ResourceMonitor()
        : reportingEnabled(SWA::CommandLine::getInstance().optionPresent(CACHE_REPORTING)) {}

    // *************************************************
    // *************************************************
    ResourceMonitor::~ResourceMonitor() {
        observerList_.clear();
    }

    // *************************************************
    // *************************************************
    void ResourceMonitor::committed() {
        if (reportingEnabled == true) {
            reportOnResources();
        }
        compactResources();
    }

    // *************************************************
    // *************************************************
    void ResourceMonitor::registerActiveResource(ResourceMonitorObserver *observer) {
        observerList_.insert(observer);
    }

    // *************************************************
    // *************************************************
    void ResourceMonitor::deregisterActiveResource(ResourceMonitorObserver *observer) {
        // Be careful when calling this method because this object is created
        // statically and is therefore prone to static de-initialisation ordering
        // problems when being called from within destructors.
        observerList_.erase(observer);
    }

    // *************************************************
    // *************************************************
    void ResourceMonitor::reportOnResources() {
        ResourceMonitorContext context;
        std::for_each(observerList_.begin(), observerList_.end(), [&](ResourceMonitorObserver *obs) {
            obs->report(context);
        });
    }

    // *************************************************
    // *************************************************
    void ResourceMonitor::compactResources() {
        ResourceMonitorContext context;
        std::for_each(observerList_.begin(), observerList_.end(), [&](ResourceMonitorObserver *obs) {
            obs->compact(context);
        });
    }

    // *************************************************
    // *************************************************
    void ResourceMonitor::releaseResources() {
        ResourceMonitorContext context;
        std::for_each(observerList_.begin(), observerList_.end(), [&](ResourceMonitorObserver *obs) {
            obs->release(context);
        });
    }
} // namespace SQL
