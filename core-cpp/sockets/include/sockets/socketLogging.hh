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
