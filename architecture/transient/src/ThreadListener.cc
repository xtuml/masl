/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "transient/ThreadListener.hh"
#include <swa/Process.hh>

namespace transient {
    bool init = ThreadListener::initialise();

    bool ThreadListener::initialise() {
        getInstance();
        return true;
    }

    ThreadListener::ThreadListener() {
        SWA::Process::getInstance().registerThreadCompletedListener([this]() {
            performCleanup();
        });
        SWA::Process::getInstance().registerThreadAbortedListener([this]() {
            performCleanup();
        });
    }

    ThreadListener &ThreadListener::getInstance() {
        static ThreadListener instance;
        return instance;
    }

    void ThreadListener::addCleanup(const std::function<void()> function) {
        cleanupRoutines.push_back(function);
    }

    void ThreadListener::performCleanup() {
        for (std::vector<std::function<void()>>::const_iterator it = cleanupRoutines.begin(),
                                                                end = cleanupRoutines.end();
             it != end;
             ++it) {
            (*it)();
        }
        cleanupRoutines.clear();
    }

} // namespace transient