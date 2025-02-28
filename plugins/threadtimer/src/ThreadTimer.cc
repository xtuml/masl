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

#include "ThreadTimer.hh"
#include "metadata/MetaData.hh"
#include "swa/CommandLine.hh"
#include "swa/Duration.hh"
#include "swa/NameFormatter.hh"
#include "swa/PluginRegistry.hh"
#include "swa/Process.hh"
#include "swa/Stack.hh"
#include "swa/String.hh"
#include <format>
#include <iostream>
#include <print>

namespace {
    const char *const ReportThresholdOption = "-tt-threshold";
    const char *const DetailedReportOff = "-tt-detailed-off";
    const char *const ReportOff = "-tt-off";

    bool started() {
        ThreadTimer::ThreadTimer::getInstance().initialise();
        return true;
    }

    bool initialise() {
        SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(
            ReportThresholdOption, "Thread timing report threshold (ms)", false, "threshold", true, false
        ));
        SWA::CommandLine::getInstance().registerOption(
            SWA::NamedOption(DetailedReportOff, "Thread timing detailed report", false)
        );
        SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(ReportOff, "Thread timing reporting off", false)
        );

        SWA::Process::getInstance().registerStartedListener(&started);
        return true;
    }

    bool init = initialise();

} // namespace

namespace ThreadTimer {

    ThreadTimer::ThreadTimer()
        : active(!SWA::CommandLine::getInstance().optionPresent(ReportOff)),
          timeActions(!SWA::CommandLine::getInstance().optionPresent(DetailedReportOff)),
          timingThread(false),
          timingAction(false),
          processStack(SWA::Stack::getInstance()),
          threshold(SWA::Duration::fromMillis(SWA::CommandLine::getInstance().getIntOption(ReportThresholdOption, 0))) {
    }

    ThreadTimer &ThreadTimer::getInstance() {
        static ThreadTimer singleton;
        return singleton;
    }

    bool ThreadTimer::initialise() {

        SWA::PluginRegistry::getInstance().registerFlagSetter(getName(), "Active", [this](bool flag) {
            setActive(flag);
        });
        SWA::PluginRegistry::getInstance().registerFlagGetter(getName(), "Active", [this]() {
            return isActive();
        });
        SWA::PluginRegistry::getInstance().registerFlagSetter(getName(), "Show Detail", [this](bool flag) {
            setTimeActions(flag);
        });
        SWA::PluginRegistry::getInstance().registerFlagGetter(getName(), "Show Detail", [this]() {
            return isTimeActions();
        });
        SWA::PluginRegistry::getInstance()
            .registerPropertySetter(getName(), "Threshold (ms)", [this](const std::string &millis) {
                setThreshold(millis);
            });
        SWA::PluginRegistry::getInstance().registerPropertyGetter(getName(), "Threshold (ms)", [this]() {
            return getThreshold();
        });

        registerMonitor();
        if (active) {
            connectToMonitor();
        }
        return true;
    }

    void ThreadTimer::setActive(bool flag) {
        active = flag;
        if (active) {
            connectToMonitor();
        } else {
            disconnectFromMonitor();
        }
    }

    void ThreadTimer::enteredAction() {
        if (!active)
            return;

        if (processStack.getStackFrames().size() == 1) {
            if (!timingThread) {
                timingThread = true;
                timingAction = timeActions;
                report.str("");
                threadStartReal = SWA::Duration::real();
                threadStartUser = SWA::Duration::user();
                threadStartSystem = SWA::Duration::system();
                actionName = SWA::NameFormatter::formatStackFrame(processStack.top(), false);
            }

            if (timingAction) {
                actionStartReal = SWA::Duration::real();
                actionStartUser = SWA::Duration::user();
                actionStartSystem = SWA::Duration::system();
            }
        }
    }

    void ThreadTimer::formatLine(
        std::ostream &stream,
        const std::string &name,
        const SWA::Duration &startReal,
        const SWA::Duration &startUser,
        const SWA::Duration &startSystem
    ) {
        stream << std::format(
            "{:60} Real : {:5}ms User : {:5}ms Sys : {:5}ms",
            name,
            (SWA::Duration::real() - startReal).millis(),
            (SWA::Duration::user() - startUser).millis(),
            (SWA::Duration::system() - startSystem).millis()
        );
    }

    void ThreadTimer::leavingAction() {
        if (timingAction && processStack.getStackFrames().size() == 1) {
            formatLine(
                report,
                "  " + SWA::NameFormatter::formatStackFrame(processStack.top(), false),
                actionStartReal,
                actionStartUser,
                actionStartSystem
            );
        }
    }

    void ThreadTimer::threadCompleting() {
        if (timingAction) {
            actionStartReal = SWA::Duration::real();
            actionStartUser = SWA::Duration::user();
            actionStartSystem = SWA::Duration::system();
        }
    }

    void ThreadTimer::threadCompleted() {
        if (!timingThread)
            return;

        SWA::Duration elapsedReal = SWA::Duration::real() - threadStartReal;

        if (elapsedReal >= threshold) {
            formatLine(std::cout, actionName, threadStartReal, threadStartUser, threadStartSystem);
            if (timingAction) {
                std::cout << report.str();
                formatLine(std::cout, "  Commit", actionStartReal, actionStartUser, actionStartSystem);
            }
            std::cout << std::flush;
        }
        timingThread = false;
        timingAction = false;
    }

    std::string ThreadTimer::getThreshold() const {
        return std::format("{}", threshold.millis());
    }

    void ThreadTimer::setThreshold(const std::string &millis) {
        threshold = SWA::Duration::fromMillis(std::stoi(millis));
        std::println("Report threshold = {}", threshold);
    }

    ThreadTimer::~ThreadTimer() {}

} // namespace ThreadTimer
