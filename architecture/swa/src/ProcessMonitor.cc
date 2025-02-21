/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "swa/ProcessMonitor.hh"
#include "swa/EventQueue.hh"

#include <iostream>
#include <sstream>

namespace SWA {

ProcessMonitor &ProcessMonitor::instance =
    ProcessMonitor::getInstanceStartupSafe();

ProcessMonitor &ProcessMonitor::getInstanceStartupSafe() {
    // Adaption of a 'Myers Singleton', uses a function static
    // for the actual storage, but a pointer for all accesses so
    // that they can be used inline. If the pointer was not used
    // the getInstance() call could not be declared inline
    // because there could then be separate statics all over the
    // place, unless the compiler is very standard compliant and
    // does clever stuff to eliminate them. I'm not taking the
    // chance.
    static ProcessMonitor singleton;
    return singleton;
}

template <typename InputIter, typename Function>
Function erase_safe_for_each(InputIter first, InputIter last, Function f) {
    while (first != last)
        f(*first++);
    return f;
}

ProcessMonitor::ProcessMonitor() : ignoreExceptions(false) {}

void ProcessMonitor::registerConnection(MonitorConnection *connection) {
    if (std::find(pendingConnections.begin(), pendingConnections.end(),
                  connection) == pendingConnections.end()) {
        for (std::vector<MonitorConnection *>::const_iterator it =
                 connection->getPrerequisites().begin();
             it != connection->getPrerequisites().end(); ++it) {
            registerConnection(*it);
        }

        pendingConnections.push_back(connection);
    }
}

void ProcessMonitor::activateConnection(MonitorConnection *connection) {
    // Insert the required connection into the active list, but
    // maintain the same order as the pending collection list.
    // Make sure that any iterators into the active list are not
    // invalidated.

    std::vector<MonitorConnection *>::const_iterator allConnIt =
        pendingConnections.begin();
    std::list<MonitorConnection *>::iterator activeConnIt =
        activeConnections.begin();

    while (allConnIt != pendingConnections.end()) {
        if (*allConnIt == connection &&
            (activeConnIt == activeConnections.end() ||
             *activeConnIt != connection)) {
            activeConnIt = activeConnections.insert(activeConnIt, connection);
        }
        if (activeConnIt != activeConnections.end() &&
            *allConnIt == *activeConnIt) {
            ++activeConnIt;
        }
        ++allConnIt;
    }
}

void ProcessMonitor::deactivateConnection(MonitorConnection *connection) {
    activeConnections.remove(connection);
}

void ProcessMonitor::startMainLoop() {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [](MonitorConnection *monitor) { monitor->startMainLoop(); });
}

void ProcessMonitor::endMainLoop() {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [](MonitorConnection *monitor) { monitor->endMainLoop(); });
}

void ProcessMonitor::startProcessingEventQueue() {
    erase_safe_for_each(activeConnections.begin(), activeConnections.end(),
                        [](MonitorConnection *monitor) {
                            monitor->startProcessingEventQueue();
                        });
}

void ProcessMonitor::endProcessingEventQueue() {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [](MonitorConnection *monitor) { monitor->endProcessingEventQueue(); });
}

void ProcessMonitor::processRunning() {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [](MonitorConnection *monitor) { monitor->processRunning(); });
}

void ProcessMonitor::processIdle() {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [](MonitorConnection *monitor) { monitor->processIdle(); });
}

void ProcessMonitor::threadStarted() {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [](MonitorConnection *monitor) { monitor->threadStarted(); });
}

void ProcessMonitor::threadAborted() {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [](MonitorConnection *monitor) { monitor->threadAborted(); });
}

void ProcessMonitor::threadCompleting() {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [](MonitorConnection *monitor) { monitor->threadCompleting(); });
}

void ProcessMonitor::threadCompleted() {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [](MonitorConnection *monitor) { monitor->threadCompleted(); });
}

void ProcessMonitor::startStatement() {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [](MonitorConnection *monitor) { monitor->startStatement(); });
}

void ProcessMonitor::endStatement() {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [](MonitorConnection *monitor) { monitor->endStatement(); });
}

void ProcessMonitor::enteredAction() {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [](MonitorConnection *monitor) { monitor->enteredAction(); });
}

void ProcessMonitor::leavingAction() {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [](MonitorConnection *monitor) { monitor->leavingAction(); });
}

void ProcessMonitor::enteredCatch() {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [](MonitorConnection *monitor) { monitor->enteredCatch(); });
}

void ProcessMonitor::leavingCatch() {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [](MonitorConnection *monitor) { monitor->leavingCatch(); });
}

void ProcessMonitor::exceptionRaised(const std::string &message) {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [&](MonitorConnection *monitor) { monitor->exceptionRaised(message); });
}

void ProcessMonitor::transitioningState(int domainId, int objectId,
                                        int instanceId, int oldState,
                                        int newState) {
    erase_safe_for_each(activeConnections.begin(), activeConnections.end(),
                        [&](MonitorConnection *monitor) {
                            monitor->transitioningState(domainId, objectId,
                                                        instanceId, oldState,
                                                        newState);
                        });
}

void ProcessMonitor::transitioningAssignerState(int domainId, int objectId,
                                                int oldState, int newState) {
    erase_safe_for_each(activeConnections.begin(), activeConnections.end(),
                        [&](MonitorConnection *monitor) {
                            monitor->transitioningAssignerState(
                                domainId, objectId, oldState, newState);
                        });
}

void ProcessMonitor::generatingEvent(const std::shared_ptr<Event> &event) {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [&](MonitorConnection *monitor) { monitor->generatingEvent(event); });
}

void ProcessMonitor::processingEvent(const std::shared_ptr<Event> &event) {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [&](MonitorConnection *monitor) { monitor->processingEvent(event); });
}

void ProcessMonitor::firingTimer(int timerId, int overrun) {
    erase_safe_for_each(activeConnections.begin(), activeConnections.end(),
                        [&](MonitorConnection *monitor) {
                            monitor->firingTimer(timerId, overrun);
                        });
}

void ProcessMonitor::creatingTimer(int timerId) {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [&](MonitorConnection *monitor) { monitor->creatingTimer(timerId); });
}

void ProcessMonitor::deletingTimer(int timerId) {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [&](MonitorConnection *monitor) { monitor->deletingTimer(timerId); });
}

void ProcessMonitor::cancellingTimer(int timerId) {
    erase_safe_for_each(
        activeConnections.begin(), activeConnections.end(),
        [&](MonitorConnection *monitor) { monitor->cancellingTimer(timerId); });
}

void ProcessMonitor::settingTimer(int timerId, const Timestamp &timeout,
                                  const Duration &period,
                                  const std::shared_ptr<Event> &event) {
    erase_safe_for_each(activeConnections.begin(), activeConnections.end(),
                        [&](MonitorConnection *monitor) {
                            monitor->settingTimer(timerId, timeout, period,
                                                  event);
                        });
}

void ProcessMonitor::pauseRequested() {
    erase_safe_for_each(
        pendingConnections.begin(), pendingConnections.end(),
        [](MonitorConnection *monitor) { monitor->pauseRequested(); });
}

} // namespace SWA
