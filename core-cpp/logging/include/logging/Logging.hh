//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
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

}

#endif
