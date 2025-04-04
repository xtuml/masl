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

#include "Trace.hh"
#include "metadata/MetaData.hh"
#include "swa/CommandLine.hh"
#include "swa/Duration.hh"
#include "swa/NameFormatter.hh"
#include "swa/PluginRegistry.hh"
#include "swa/Process.hh"
#include "swa/Stack.hh"
#include "swa/String.hh"
#include <iostream>

namespace {
    const char *const TraceLinesOption = "-trace-lines";
    const char *const TraceEventsOption = "-trace-events";
    const char *const TraceActionsOption = "-trace-actions";
    const char *const TraceAllOption = "-trace-all";
    const char *const TraceDomainOption = "-trace-domains";

    bool started() {
        Trace::Trace::getInstance().initialise();
        return true;
    }

    bool initialise() {
        SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(TraceEventsOption, "Trace Events", false));
        SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(TraceActionsOption, "Trace Actions", false));
        SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(TraceLinesOption, "Trace Lines", false));
        SWA::CommandLine::getInstance().registerOption(
            SWA::NamedOption(TraceAllOption, "Trace Events, Actions & Lines", false)
        );
        SWA::CommandLine::getInstance().registerOption(
            SWA::NamedOption(TraceDomainOption, "Domains to Trace", false, "domain", true, true)
        );

        SWA::Process::getInstance().registerStartedListener(&started);
        return true;
    }

    bool init = initialise();

} // namespace

namespace Trace {

    Trace::Trace()
        : traceEvents(
              SWA::CommandLine::getInstance().optionPresent(TraceEventsOption) ||
              SWA::CommandLine::getInstance().optionPresent(TraceAllOption)
          ),
          traceActions(
              SWA::CommandLine::getInstance().optionPresent(TraceActionsOption) ||
              SWA::CommandLine::getInstance().optionPresent(TraceAllOption)
          ),
          traceLines(
              SWA::CommandLine::getInstance().optionPresent(TraceLinesOption) ||
              SWA::CommandLine::getInstance().optionPresent(TraceAllOption)
          ),
          processStack(SWA::Stack::getInstance()) {
        const std::vector<std::string> &domainNames = SWA::CommandLine::getInstance().getMultiOption(TraceDomainOption);
        for (std::vector<std::string>::const_iterator it = domainNames.begin(), end = domainNames.end(); it != end;
             ++it) {
            try {
                domains.insert(SWA::Process::getInstance().getDomain(*it).getId());
            } catch (...) {
                std::cerr << "ERROR: Trace: Could not find domain " << *it << std::endl;
            }
        }
    }

    Trace &Trace::getInstance() {
        static Trace singleton;
        return singleton;
    }

    bool Trace::initialise() {

        SWA::PluginRegistry::getInstance().registerFlagSetter(getName(), "Events", [this](bool flag) {
            setTraceEvents(flag);
        });
        SWA::PluginRegistry::getInstance().registerFlagSetter(getName(), "Actions", [this](bool flag) {
            setTraceActions(flag);
        });
        SWA::PluginRegistry::getInstance().registerFlagSetter(getName(), "Lines", [this](bool flag) {
            setTraceLines(flag);
        });

        SWA::PluginRegistry::getInstance().registerFlagGetter(getName(), "Events", [this]() {
            return isTraceEvents();
        });
        SWA::PluginRegistry::getInstance().registerFlagGetter(getName(), "Actions", [this]() {
            return isTraceActions();
        });
        SWA::PluginRegistry::getInstance().registerFlagGetter(getName(), "Lines", [this]() {
            return isTraceLines();
        });

        registerMonitor();
        checkConnect();
        return true;
    }

    void Trace::checkConnect() {
        if (traceLines | traceEvents | traceActions) {
            connectToMonitor();
        } else {
            disconnectFromMonitor();
        }
    }

    void Trace::enteredAction() {
        if (!traceActions)
            return;
        if (domains.size() > 0 && !domains.count(processStack.top().getDomainId()))
            return;

        std::cout << std::string(((processStack.getStackFrames().size() - 1) * 2), ' ') << "-> "
                  << SWA::NameFormatter::formatStackFrame(processStack.top(), false) << std::endl;
    }

    void Trace::leavingAction() {
        if (!traceActions)
            return;
        if (domains.size() > 0 && !domains.count(processStack.top().getDomainId()))
            return;

        std::cout << std::string(((processStack.getStackFrames().size() - 1) * 2), ' ') << "<- "
                  << SWA::NameFormatter::formatStackFrame(processStack.top(), false) << std::endl;
    }

    void Trace::startStatement() {
        if (!traceLines)
            return;
        if (domains.size() > 0 && !domains.count(processStack.top().getDomainId()))
            return;

        std::cout << std::string(((processStack.getStackFrames().size() - 1) * 2), ' ') << " . "
                  << SWA::NameFormatter::formatStackFrame(processStack.top(), true) << std::endl;
    }

    void Trace::processingEvent(const std::shared_ptr<SWA::Event> &event) {
        if (!traceEvents)
            return;

        if (domains.size() > 0 && !domains.count(event->getDomainId()))
            return;

        std::cout << "* "
                  << SWA::NameFormatter::formatEventName(
                         event->getDomainId(), event->getObjectId(), event->getEventId()
                     )
                  << std::endl;
    }

    Trace::~Trace() {}

} // namespace Trace