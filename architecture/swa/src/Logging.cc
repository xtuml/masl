/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "logging/log.hh"
#include "swa/Process.hh"
#include "swa/CommandLine.hh"
#include <fstream>

namespace SWA
{
  namespace 
  {

    const char* const LogConfigOption = "-log-config";

    void startup()
    {
      if ( CommandLine::getInstance().optionPresent(LogConfigOption) ) {
          xtuml::logging::Logger::load_config(CommandLine::getInstance().getOption(LogConfigOption));
      }
    }

    bool init()
    {
      SWA::Process::getInstance().registerStartedListener(&startup);

      SWA::CommandLine::getInstance().registerOption (SWA::NamedOption(LogConfigOption,  "Logging configuration file",false, "file", true, false));
      return true;
    }

    bool initialised = init();

  }

}


