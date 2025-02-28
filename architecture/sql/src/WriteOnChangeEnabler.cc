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
#include <iterator>
#include <sstream>

#include "sql/WriteOnChangeEnabler.hh"

#include "swa/CommandLine.hh"
#include "swa/Process.hh"

namespace {

    // *****************************************************************
    // *****************************************************************
    const char *const wocCommandLineArg = "-woc";
    const char *const wocCommandLineUsage = "do not cache modifications but write SQL directly on change";
    const char *const wocCommandLineDesc = "['ALL':Name:...]";

    // *****************************************************************
    // *****************************************************************
    bool configureCommandLine() {
        SWA::Process::getInstance().getCommandLine().registerOption(
            SWA::NamedOption(wocCommandLineArg, wocCommandLineUsage, false, wocCommandLineDesc, false)
        );
        return true;
    }

    // *****************************************************************
    // *****************************************************************
    bool isWocMonitorEnabled(const std::string &objectName) {
        static bool enabled = SWA::Process::getInstance().getCommandLine().optionPresent(wocCommandLineArg);

        bool enableForName = false;
        if (enabled == true) {
            const std::string wocOptions = SWA::Process::getInstance().getCommandLine().getOption(wocCommandLineArg);
            std::string currentOption;
            std::istringstream lexerStream(wocOptions);
            while (std::getline(lexerStream, currentOption, ':')) {
                if (currentOption == objectName || currentOption == "ALL") {
                    enableForName = true;
                    break;
                }
            }
        }
        return enableForName;
    }

    // *****************************************************************
    // *****************************************************************
    bool initCommandLine = configureCommandLine();

} // namespace

namespace SQL {

    // *****************************************************************
    // *****************************************************************
    WriteOnChangeEnabler::WriteOnChangeEnabler(const std::string &objectName)
        : name(objectName) {}

    // *****************************************************************
    // *****************************************************************
    WriteOnChangeEnabler::~WriteOnChangeEnabler() {}

    // *****************************************************************
    // *****************************************************************
    bool WriteOnChangeEnabler::isEnabled() {
        return isWocMonitorEnabled(name);
    }

} // end namespace SQL
