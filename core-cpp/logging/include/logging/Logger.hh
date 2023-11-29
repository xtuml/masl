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

#ifndef Logging_Logger_HH
#define Logging_Logger_HH

#include <string>
#include <map>

namespace Logging
{

  class Logger
  {
    public:
      enum Priority { Fatal, Critical, Error, Warning, Notice, Information, Debug, Trace };

      static Logger& getInstance();

      void loadXMLConfiguration ( std::istream& stream, const std::string& cmd, const std::string& name );

      void loadXMLConfiguration ( std::istream& stream, const std::string& cmd, const std::string& name, const std::map<std::string,std::string>& params );

      void setDefaultLog ( const std::string& name ) { defaultLog = name; }

      void setLogLevel ( const std::string& log, Priority priority );

      void setLogLevel ( Priority priority ) { setLogLevel(defaultLog,priority); }

      void printLoggers() const;

      void log         ( const std::string& log, Priority priority, const std::string& message ) const;
      void trace       ( const std::string& log, const std::string& message ) const;
      void debug       ( const std::string& log, const std::string& message ) const;
      void information ( const std::string& log, const std::string& message ) const;
      void notice      ( const std::string& log, const std::string& message ) const;
      void warning     ( const std::string& log, const std::string& message ) const;
      void error       ( const std::string& log, const std::string& message ) const;
      void critical    ( const std::string& log, const std::string& message ) const;
      void fatal       ( const std::string& log, const std::string& message ) const;

      void log         ( Priority priority, const std::string& message ) const { log(defaultLog,priority,message); }
      void trace       ( const std::string& message ) const { trace      (defaultLog,message); }
      void debug       ( const std::string& message ) const { debug      (defaultLog,message); }
      void information ( const std::string& message ) const { information(defaultLog,message); }
      void notice      ( const std::string& message ) const { notice     (defaultLog,message); }
      void warning     ( const std::string& message ) const { warning    (defaultLog,message); }
      void error       ( const std::string& message ) const { error      (defaultLog,message); }
      void critical    ( const std::string& message ) const { critical   (defaultLog,message); }
      void fatal       ( const std::string& message ) const { fatal      (defaultLog,message); }

      bool enabled             (const std::string& log, Priority priority) const;

      bool traceEnabled       (const std::string& log) const { return enabled(log,Trace); }
      bool debugEnabled       (const std::string& log) const { return enabled(log,Debug); }
      bool informationEnabled (const std::string& log) const { return enabled(log,Information); }
      bool noticeEnabled      (const std::string& log) const { return enabled(log,Notice); }
      bool warningEnabled     (const std::string& log) const { return enabled(log,Warning); }
      bool errorEnabled       (const std::string& log) const { return enabled(log,Error); }
      bool criticalEnabled    (const std::string& log) const { return enabled(log,Critical); }
      bool fatalEnabled       (const std::string& log) const { return enabled(log,Fatal); }

      bool enabled            (Priority priority) const { return enabled (defaultLog,priority); }
      bool traceEnabled       () const { return traceEnabled      (defaultLog); }
      bool debugEnabled       () const { return debugEnabled      (defaultLog); }
      bool informationEnabled () const { return informationEnabled(defaultLog); }
      bool noticeEnabled      () const { return noticeEnabled     (defaultLog); }
      bool warningEnabled     () const { return warningEnabled    (defaultLog); }
      bool errorEnabled       () const { return errorEnabled      (defaultLog); }
      bool criticalEnabled    () const { return criticalEnabled   (defaultLog); }
      bool fatalEnabled       () const { return fatalEnabled      (defaultLog); }

    private:
      Logger();
      ~Logger();

    private:
      std::string defaultLog;

  };

}

#endif
