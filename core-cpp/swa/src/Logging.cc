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

#include "logging/Logger.hh"
#include "swa/Process.hh"
#include "swa/CommandLine.hh"
#include <fstream>

namespace SWA
{
  namespace 
  {

    const char* const LogConfigOption = "-log-config";
    const char* const LogParamOption = "-log-param";

    const char* const LogLevelOption = "-log-level";

    Logging::Logger::Priority parseLevel ( const std::string& level )
    {
      if ( !level.size() ) return Logging::Logger::Information;

      switch ( std::tolower(level[0]) )
      {
        case 't': return Logging::Logger::Trace;
        case 'd': return Logging::Logger::Debug;
        case 'i': return Logging::Logger::Information;
        case 'n': return Logging::Logger::Notice;
        case 'w': return Logging::Logger::Warning;
        case 'e': return Logging::Logger::Error;
        case 'c': return Logging::Logger::Critical;
        case 'f': return Logging::Logger::Fatal;
        default:  return Logging::Logger::Information;
      }
    }

    void startup()
    {
      if ( CommandLine::getInstance().optionPresent(LogConfigOption) )
      {
        std::ifstream configFile(CommandLine::getInstance().getOption(LogConfigOption).c_str());

        std::map<std::string,std::string> params;

        const std::vector<std::string>& rawParams = CommandLine::getInstance().getMultiOption(LogParamOption);
        for ( std::vector<std::string>::const_iterator it = rawParams.begin(), end = rawParams.end(); it != end; ++it )
        {
          std::string::size_type equalPos = it->find('=');
          if ( equalPos != std::string::npos )
          {
            params[it->substr(0,equalPos)] = it->substr(equalPos+1);
          }          
        }

        Logging::Logger::getInstance().loadXMLConfiguration ( configFile, CommandLine::getInstance().getCommand(), Process::getInstance().getName(), params );
      }
      
      if ( CommandLine::getInstance().optionPresent(LogLevelOption) )
      {
        const std::vector<std::string>& levels = CommandLine::getInstance().getMultiOption(LogLevelOption);
        for ( std::vector<std::string>::const_iterator it = levels.begin(), end = levels.end(); it != end; ++it )
        {
          std::string::size_type equalPos = it->find('=');
          if ( equalPos != std::string::npos )
          {
            Logging::Logger::getInstance().setLogLevel(it->substr(0,equalPos),parseLevel(it->substr(equalPos+1)));
          }
          else
          {
            Logging::Logger::getInstance().setLogLevel(parseLevel(*it));
          }
        }

      }

    }

    bool init()
    {
      SWA::Process::getInstance().registerStartedListener(&startup);

      SWA::CommandLine::getInstance().registerOption (SWA::NamedOption(LogConfigOption,  "Logging configuration file",false, "file", true, false));
      SWA::CommandLine::getInstance().registerOption (SWA::NamedOption(LogParamOption,  "Logging parameter referenced in config",false, "name=value", true, true));
      SWA::CommandLine::getInstance().registerOption (SWA::NamedOption(LogLevelOption,  
            "Logging level (t[race], d[ebug], i[nformation], n[otice], w[arning], e[rror], c[ritical], f[atal])",false, "[logname=]level", true, true));
      return true;
    }

    bool initialised = init();

  }

}


