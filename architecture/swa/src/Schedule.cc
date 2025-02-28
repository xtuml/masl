/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "swa/Schedule.hh"
#include <boost/algorithm/string.hpp>
#include <format>
#include <iostream>
#include <print>
#include <sstream>

#include "swa/Process.hh"
#include "swa/ProcessMonitor.hh"
#include "swa/ProgramError.hh"

namespace SWA {

    class RunServiceAction {
      public:
        RunServiceAction(const std::function<void()> &service, const std::string &input)
            : service(service), input(input) {}

        void operator()() const {
            SWA::Process::getInstance().runService(service, input);
        }

      private:
        std::function<void()> service;
        const std::string input;
    };

    class IdleAction {
      public:
        IdleAction(int timeout)
            : timeout(timeout) {}

        void operator()() const {
            SWA::Process::getInstance().idle(timeout);
        }

      private:
        int timeout;
    };

    class PauseAction {
      public:
        void operator()() const {
            SWA::Process::getInstance().pause();
        }
    };

    class TerminateAction {
      public:
        void operator()() const {
            SWA::Process::getInstance().forceTerminate();
        }
    };

    Schedule::Schedule(const std::string &name, const std::string &text)
        : name(name), text(text), valid(true) {
        std::vector<std::string> lines;
        boost::split(lines, text, boost::is_any_of("\n"));

        int lineNo = 0;
        for (std::vector<std::string>::const_iterator it = lines.begin(); it != lines.end(); ++it) {
            ++lineNo;
            std::istringstream line(*it);
            std::string command;
            line >> command;

            if (line && command[0] != '$' && command[0] != '#') {
                if (command == "RUN") {
                    std::string type;
                    std::string domainName;
                    int number;
                    std::string remainder;
                    std::string input;

                    if (line >> type >> domainName >> number) {
                        if (std::getline(line, remainder)) {
                            std::string::size_type inputStart = remainder.find_first_of('[');
                            std::string::size_type inputEnd = remainder.find_last_of(']');
                            if (inputStart != std::string::npos) {
                                if (inputEnd == std::string::npos) {
                                    reportError(lineNo, "missing ] to end input");
                                } else {
                                    input = remainder.substr(inputStart + 1, inputEnd - inputStart - 1);
                                    boost::replace_all(input, ",", "\n");
                                }
                            }
                        }
                        try {
                            const Domain &domain = Process::getInstance().getDomain(domainName);
                            const std::function<void()> service;

                            if (type == "SCENARIO") {
                                try {
                                    actions.push_back(RunServiceAction(domain.getScenario(number), input));
                                } catch (const ProgramError &e) {
                                    reportError(lineNo, std::format("no scenario {} on domain {}", number, domainName));
                                }
                            } else if (type == "EXTERNAL") {
                                try {
                                    actions.push_back(RunServiceAction(domain.getExternal(number), input));
                                } catch (const ProgramError &e) {
                                    reportError(lineNo, std::format("no external {} on domain {}", number, domainName));
                                }
                            } else {
                                reportError(lineNo, std::format("unrecognised service type: {}", type));
                            }
                        } catch (const ProgramError &e) {
                            reportError(lineNo, std::format("unrecognised domain: ", domainName));
                        }
                    } else
                        reportError(lineNo, "incorrectly formatted RUN command");

                } else if (command == "IDLE") {
                    int timeout;
                    line >> timeout;
                    if (line) {
                        actions.push_back(IdleAction(timeout));
                    } else {
                        reportError(lineNo, "expected timeout value");
                    }
                } else if (command == "PAUSE")
                    actions.push_back(PauseAction());
                else if (command == "TERMINATE")
                    actions.push_back(TerminateAction());
                else
                    reportError(lineNo, std::format("unrecognised command: {}", command));
            }
        }
    }

    void Schedule::reportError(int lineNo, const std::string &error) {
        std::println(stderr, "{}:{}: error: {}", name, lineNo, error);
        valid = false;
    }

} // namespace SWA
