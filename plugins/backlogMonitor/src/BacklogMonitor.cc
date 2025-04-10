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

#include "swa/CommandLine.hh"
#include "swa/PluginRegistry.hh"
#include "swa/Process.hh"
#include <string>
#include <asio/system_timer.hpp>
#include <chrono>
#include <print>


namespace BacklogMonitor {
    const char *const PollIntervalOption = "-backlog-poll-interval";
    const char *const ReportThresholdOption = "-backlog-report-threshold";

    class BacklogMonitor {
      public:
        static BacklogMonitor &getInstance();

        std::string getName() {
            return "Backlog Monitor";
        }

        bool initialise();
        void startup();

        void setActive(bool active);
        bool isActive() const {
            return active;
        }

        void setPollInterval(const std::string &interval);
        void setReportThreshold(const std::string &threshold);
        std::string getPollInterval() const {
            return std::format("{}", pollInterval.count());
        }
        std::string getReportThreshold() const {
            return std::format("{}", reportThreshold.count());
        }

        BacklogMonitor();

      private:
        bool active;
        std::chrono::seconds pollInterval;
        std::chrono::seconds reportThreshold;
        std::chrono::nanoseconds lastBacklog;

        asio::system_timer timer;

        void ping();
        void report(const std::chrono::nanoseconds &backlog) const;
    };

    BacklogMonitor::BacklogMonitor()
        : active(true),
          pollInterval(),
          reportThreshold(),
          lastBacklog(),
          timer(SWA::Process::getInstance().getIOContext().get_executor()) {}

    BacklogMonitor &BacklogMonitor::getInstance() {
        static BacklogMonitor singleton;
        return singleton;
    }

    bool BacklogMonitor::initialise() {
        SWA::PluginRegistry::getInstance()
            .registerPropertySetter(getName(), "Poll interval (secs)", [this](auto &&interval) {
                setPollInterval(interval);
            });
        SWA::PluginRegistry::getInstance()
            .registerPropertySetter(getName(), "Report Threshold (secs)", [this](auto &&threshold) {
                setReportThreshold(threshold);
            });
        SWA::PluginRegistry::getInstance().registerPropertyGetter(getName(), "Poll interval (secs)", [this]() {
            return getPollInterval();
        });
        SWA::PluginRegistry::getInstance().registerPropertyGetter(getName(), "Report Threshold (secs)", [this]() {
            return getReportThreshold();
        });
        SWA::PluginRegistry::getInstance().registerFlagSetter(getName(), "Active", [this](auto &&active) {
            setActive(active);
        });
        SWA::PluginRegistry::getInstance().registerFlagGetter(getName(), "Active", [this]() {
            return isActive();
        });

        SWA::Process::getInstance().registerStartedListener([this]() {
            startup();
        });

        SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(
            PollIntervalOption, "Poll interval for backlog reporting (0 = disabled)", false, "interval", true, false
        ));
        SWA::CommandLine::getInstance().registerOption(
            SWA::NamedOption(ReportThresholdOption, "Threshold for backlog reporting ", false, "threshold", true, false)
        );

        return true;
    }

    void BacklogMonitor::setActive(bool active) {
        this->active = active;
        if (active && pollInterval > std::chrono::seconds(0)) {
            ping();
        } else {
            timer.cancel();
        }
    }

    void BacklogMonitor::ping() {
        timer.expires_after(pollInterval);
        timer.async_wait([this](const asio::error_code &ec) {
            if (ec == asio::error::operation_aborted) {
                return;
            }
            std::chrono::nanoseconds backlog = std::chrono::system_clock::now() - timer.expires_at();
            if (backlog > reportThreshold) {
                report(backlog);
                lastBacklog = backlog;
            } else if (backlog < lastBacklog) {
                report(backlog);
                lastBacklog = std::chrono::seconds(0);
            }
            ping();
        });
    }

    void BacklogMonitor::setPollInterval(const std::string &interval) {
        pollInterval = std::chrono::seconds(std::stoi(interval));
        setActive(active);
    }

    void BacklogMonitor::setReportThreshold(const std::string &threshold) {
        reportThreshold = std::chrono::seconds(std::stoi(threshold));
    }

    void BacklogMonitor::startup() {
        setPollInterval(SWA::CommandLine::getInstance().getOption(PollIntervalOption, "60"));
        setReportThreshold(SWA::CommandLine::getInstance().getOption(ReportThresholdOption, "10"));
        setActive(active);
    }

    void BacklogMonitor::report(const std::chrono::nanoseconds &backlog) const {
        std::println("Backlog : {}",std::chrono::duration_cast<std::chrono::milliseconds>(backlog));
    }

} // namespace BacklogMonitor

namespace {
    bool registered = BacklogMonitor::BacklogMonitor::getInstance().initialise();
}
