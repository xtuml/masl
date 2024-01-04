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

#ifndef Logging_Logging_HH
#define Logging_Logging_HH

#include "Logger.hh"

namespace Logging
{

  inline void trace                  ( const std::string& log, const std::string& message ) { Logger::getInstance().trace       (log,message); } 
  inline void debug                  ( const std::string& log, const std::string& message ) { Logger::getInstance().debug       (log,message); } 
  inline void info                   ( const std::string& log, const std::string& message ) { Logger::getInstance().information (log,message); } 
  inline void information            ( const std::string& log, const std::string& message ) { Logger::getInstance().information (log,message); } 
  inline void notice                 ( const std::string& log, const std::string& message ) { Logger::getInstance().notice      (log,message); } 
  inline void warning                ( const std::string& log, const std::string& message ) { Logger::getInstance().warning     (log,message); } 
  inline void error                  ( const std::string& log, const std::string& message ) { Logger::getInstance().error       (log,message); } 
  inline void critical               ( const std::string& log, const std::string& message ) { Logger::getInstance().critical    (log,message); } 
  inline void fatal                  ( const std::string& log, const std::string& message ) { Logger::getInstance().fatal       (log,message); } 

  inline void trace                  ( const std::string& message ) { Logger::getInstance().trace       (message); }     
  inline void debug                  ( const std::string& message ) { Logger::getInstance().debug       (message); }     
  inline void info                   ( const std::string& message ) { Logger::getInstance().information (message); }     
  inline void information            ( const std::string& message ) { Logger::getInstance().information (message); }     
  inline void notice                 ( const std::string& message ) { Logger::getInstance().notice      (message); }     
  inline void warning                ( const std::string& message ) { Logger::getInstance().warning     (message); }     
  inline void error                  ( const std::string& message ) { Logger::getInstance().error       (message); }     
  inline void critical               ( const std::string& message ) { Logger::getInstance().critical    (message); }     
  inline void fatal                  ( const std::string& message ) { Logger::getInstance().fatal       (message); }     

  inline bool traceEnabled           ( const std::string& log ) { return Logger::getInstance().traceEnabled(log); }
  inline bool debugEnabled           ( const std::string& log ) { return Logger::getInstance().debugEnabled(log); }
  inline bool infoEnabled            ( const std::string& log ) { return Logger::getInstance().informationEnabled(log); }
  inline bool informationEnabled     ( const std::string& log ) { return Logger::getInstance().informationEnabled(log); }
  inline bool noticeEnabled          ( const std::string& log ) { return Logger::getInstance().noticeEnabled(log); }
  inline bool warningEnabled         ( const std::string& log ) { return Logger::getInstance().warningEnabled(log); }
  inline bool errorEnabled           ( const std::string& log ) { return Logger::getInstance().errorEnabled(log); }
  inline bool criticalEnabled        ( const std::string& log ) { return Logger::getInstance().criticalEnabled(log); }
  inline bool fatalEnabled           ( const std::string& log ) { return Logger::getInstance().fatalEnabled(log); }

  inline bool traceEnabled           () { return Logger::getInstance().traceEnabled(); }
  inline bool debugEnabled           () { return Logger::getInstance().debugEnabled(); }
  inline bool infoEnabled            () { return Logger::getInstance().informationEnabled(); }
  inline bool informationEnabled     () { return Logger::getInstance().informationEnabled(); }
  inline bool noticeEnabled          () { return Logger::getInstance().noticeEnabled(); }
  inline bool warningEnabled         () { return Logger::getInstance().warningEnabled(); }
  inline bool errorEnabled           () { return Logger::getInstance().errorEnabled(); }
  inline bool criticalEnabled        () { return Logger::getInstance().criticalEnabled(); }
  inline bool fatalEnabled           () { return Logger::getInstance().fatalEnabled(); }


}

#endif