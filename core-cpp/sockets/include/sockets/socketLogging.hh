//============================================================================//
// UK Crown Copyright (c) 2005. All rights reserved.
//
// File:   logger.hh
//
// Description:             
//    Logger is a singleton, that provides a single place for all application
// logging to go be forwarded through.
//
//    Due to the vagaries of the C++ standard and the different interpretations by 
// platform vendors. The design of the logger has had to change from its orginal
// Linux implementation, to handle a problem on the DEC Alpha platform running 
// Tru64, to do with the static initialisation order of user defined objects and
// static runtime system objects, such as std::cout.
// The standard only guarantees that std::cout will be initialised on entry to main
// or after a call to std::basic_ios<char>::Init (para 27.3 of the standard). This
// causes a problem on DEC because the static initialisation of objects that subsequently
// use the logger (or std::cout directly) will SEGV as the runtime has not initialised
// std::cout before its invocation. The fix would appear to be a call to 'std::basic_ios<char>::Init'
// before the logger uses std::cout, but this will cause another SEGV deep inside the std::basic_ios<char>::Init
// implementation, this is non standard behaviour and has been reported as a bug to COMPAQ.
//    The solution has been to force the initialisation of the logger from within side main(...) and
// store any logger messages prior to this time in a local repository of log messages. These logs are
// output (if any have been created) as part of the logger initialisation sequence.
//    For platforms that support the correct behaviour (Linux), the local log repository is not required,
// and an optimisation has been added to exclude its use. 
// 
//============================================================================//
#ifndef SocketLogger
#define SocketLogger

#include <string>
#include <vector>
#include <iterator>
#include <iostream>
#include <exception>

#include <sys/types.h>
#include <unistd.h>
#include <stdlib.h>

#include "sockets/socketCommon.hh"

namespace SKT {

class Logger {
  
  public:
      static Logger& Instance()
      {
        static Logger instance;
        return instance;
      }

     ~Logger()
      {
        if (lout_ != NULL){
            lout_->flush();
        }
      }
      
      // Only call this method from within side main.
      void initialise ()
      {
        if (initialised_ == false) {
            init();
            // For platforms that do not support the use of the logger for objects created
            // (static initialisation) before entry to main, the logger implementation will
            // store log messages in a repository. The application using the logger will need 
            // to call Logger::Instance()::initialise() within side main; 
            if (enabledTrace_ == true){
                std::copy(uninitLogRepository_.begin(), uninitLogRepository_.end(),std::ostream_iterator<std::string>(*lout_,"\n"));
            }
            uninitLogRepository_.clear();
        }
      }

  private:
      Logger    ():
         initialised_ (false),
         enabledTrace_(false)
      {
      
         #ifndef __osf__
              // The C++ standard guarantees that 'cout' will be initialised on entry
              // into main(...). As this logger might be used by objects being statically
              // initialised (prior to main(...) entry) then need to make sure that
              // cout is initialised before its use. The C++ standard (para 27.3)
              // guarantees cout will be initialised after the call to 'std::basic_ios<char>::Init'.
              // Therefore force the Init call to get cout initialised before the logger is used.
              // As usual this works fine on Linux but not on DEC!!.
              std::basic_ios<char>::Init dummy;
              init();
         #endif
      }

      void init ()
      {
        initialised_ = true;
        lout_ = &std::cout;
  
        if (getenv("SKT_TRACE") != NULL){
           enabledTrace_ = true;
        }
      }
 
  public:
     static void trace (const std::string& location)
     {
       Logger& log = Logger::Instance();
       log.ltrace(location);
     }

     static void trace (const std::string& location, const std::string& message)
     {  
       Logger& log = Logger::Instance();
       log.ltrace(location,message);
     }  
     
     static void trace (const std::string& location, const std::string& message, const std::string& value)
     {
        if (Logger::Instance().enabledTrace() == true){
	   std::string report(message);
	   report += " = ";
	   report += value;
	   trace(location,report);
	}
     }
     
     template<class T>
     static void trace (const std::string& location, const std::string& message, const T& value)
     {
	 // Check if trace is enabled before undertaking valueToString
	 // conversion as this is very expensive on the DEC platform.
	 if (Logger::Instance().enabledTrace() == true){
	     std::string report(message);
	     report += " = ";
	     report += valueToString<T>(value);
	     trace(location,report);
	 }    
     }

  private:
       inline const bool enabledTrace() const { return enabledTrace_; }
              
       void ltrace (const std::string& location)
       {
         if (initialised_ == true){
            if (enabledTrace_ == true){
                *lout_ << "=== " << getpid() << " === Data Cache :: trace - " << location << std::endl;
            }
         }
         else{
           // As the logger has not be initialised, do not know whether
           // trace has been enabled. Therefore store message in uninit 
           // repository. 
           std::string lmessage("Data Cache :: trace - ");
           lmessage += location;
           uninitLogRepository_.push_back(lmessage);
         }   
       }

       // *************************************************************************************************
       // *************************************************************************************************
       // *************************************************************************************************
       void ltrace (const std::string& location, const std::string& message)
       {
         if (initialised_ == true){
            if (enabledTrace_ == true){
               *lout_ << "=== " << getpid() << " === Data Cache :: trace - " << location << " >> " << message << std::endl;
            }
         }
         else{
           // As the logger has not be initialised, do not know whether
           // trace has been enabled. Therefore store message in uninit 
           // repository. 
           std::string lmessage("Data Cache :: trace - ");
           lmessage += location;
           lmessage += " >> ";
           lmessage += message;
           uninitLogRepository_.push_back(lmessage);
         }   
       }
       
  private:
     bool initialised_;
     bool enabledTrace_;
     std::ostream* lout_; 
     std::vector<std::string> uninitLogRepository_;

};

} // end namespace SKT


#endif
