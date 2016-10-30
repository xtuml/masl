//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
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

    private:
      Logger();
      ~Logger();

    private:
      std::string defaultLog;

  };

}

#endif
