//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "logging/Logger.hh"
#include "XMLExtractor.hh"

#include <iostream>

#include <Poco/Logger.h>
#include <Poco/PatternFormatter.h>
#include <Poco/FormattingChannel.h>
#include <Poco/LoggingRegistry.h>
#include <Poco/ConsoleChannel.h>

namespace Logging
{

  Logger& Logger::getInstance()
  {
    static Logger instance;
    return instance;
  }

  Logger::Logger()
  {
    Poco::Logger::get(Poco::Logger::ROOT).setLevel(Poco::Message::PRIO_INFORMATION);
    Poco::Logger::get(Poco::Logger::ROOT).setChannel(
          new Poco::FormattingChannel(
                new Poco::PatternFormatter("%Y-%m-%dT%H:%M:%S%z %p : %s : %t"),
                new Poco::ConsoleChannel(std::cout)));
  }

  Logger::~Logger()
  {
  }

  void Logger::loadXMLConfiguration ( std::istream& stream, const std::string& cmd, const std::string& name )
  {
    loadXMLConfiguration(stream,cmd,name,std::map<std::string,std::string>());
  }


  void Logger::loadXMLConfiguration ( std::istream& stream, const std::string& cmd, const std::string& name, const std::map<std::string,std::string>& params )
  {
    XMLExtractor(stream,cmd,name,params);
  }

  void Logger::log ( const std::string& log, Priority priority, const std::string& message ) const
  {
    switch ( priority )
    {
      case Fatal       : fatal       (message); break;
      case Critical    : critical    (message); break;
      case Error       : error       (message); break;
      case Warning     : warning     (message); break;
      case Notice      : notice      (message); break;
      case Information : information (message); break;
      case Debug       : debug       (message); break;
      case Trace       : trace       (message); break;
    }
  }

  namespace
  {
    int getPocoLevel ( Logger::Priority priority )
    {
      return Poco::Message::PRIO_FATAL + ( priority - Logger::Fatal );
    }
  }
  void Logger::setLogLevel ( const std::string& log, Priority priority )
  { 
    Poco::Logger::get(log);
    Poco::Logger::setLevel(log, getPocoLevel(priority));
  }

  void Logger::trace       ( const std::string& log, const std::string& message ) const { Poco::Logger::get(log).trace      (message); }
  void Logger::debug       ( const std::string& log, const std::string& message ) const { Poco::Logger::get(log).debug      (message); };
  void Logger::information ( const std::string& log, const std::string& message ) const { Poco::Logger::get(log).information(message); };
  void Logger::notice      ( const std::string& log, const std::string& message ) const { Poco::Logger::get(log).notice     (message); };
  void Logger::warning     ( const std::string& log, const std::string& message ) const { Poco::Logger::get(log).warning    (message); };
  void Logger::error       ( const std::string& log, const std::string& message ) const { Poco::Logger::get(log).error      (message); };
  void Logger::critical    ( const std::string& log, const std::string& message ) const { Poco::Logger::get(log).critical   (message); };
  void Logger::fatal       ( const std::string& log, const std::string& message ) const { Poco::Logger::get(log).fatal      (message); };

  void Logger::printLoggers() const
  {
    std::vector<std::string> names;
    Poco::Logger::names(names);
    for ( std::vector<std::string>::const_iterator it = names.begin(), end = names.end(); it != end; ++it )
    {
      std::cout << *it << " : ";
      switch ( Poco::Logger::get(*it).getLevel() )
      {
        case Poco::Message::PRIO_TRACE      : std::cout << "Trace\n";        break;
        case Poco::Message::PRIO_DEBUG      : std::cout << "Debug\n";        break;
        case Poco::Message::PRIO_INFORMATION: std::cout << "Information\n";  break;
        case Poco::Message::PRIO_NOTICE     : std::cout << "Notice\n";       break;
        case Poco::Message::PRIO_WARNING    : std::cout << "Warning\n";      break;
        case Poco::Message::PRIO_ERROR      : std::cout << "Error\n";        break;
        case Poco::Message::PRIO_CRITICAL   : std::cout << "Critical\n";     break;
        case Poco::Message::PRIO_FATAL      : std::cout << "Fatal\n";        break;
      }
      std::cout << std::flush;
    }
  }

}
