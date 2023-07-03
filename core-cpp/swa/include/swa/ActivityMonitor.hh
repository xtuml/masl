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

#ifndef SWA_ActivityMonitor_HH
#define SWA_ActivityMonitor_HH

#include <map>
#include <signal.h>
#include <stdint.h>

#include "boost/function.hpp"

namespace SWA
{
  class FileDescriptorListener;

  class ActivityMonitor
  {
    public:
      ActivityMonitor();

      // The type for the function to be called on a fd signal
      typedef boost::function<void(int)> FdCallback;
      typedef boost::function<void(int,int)> FdExtendedCallback;

      // The type for the function to be called on a POSIX.1b timer signal
      // Parameter is the overrun.
      typedef boost::function<void(int)> TimerCallback;

      // The type for the function to be called on kernel signal.
      // Parameters are the signal number, and the pid and uid of the sending process.
      typedef boost::function<void (int,int)> SignalCallback;

      // The type for the function to be called on any other signal.
      // Parameters are the pid and uid of the sending process.
      typedef boost::function<void (int,int)> NormalCallback;

      void initialise();

      void addFdCallback ( int fd, const FdCallback& callback );
      void removeFdCallback ( int fd );

      void addFdExtCallback    ( int fd, const FdExtendedCallback& callback );
      void removeFdExtCallback ( int fd );

      void addTimerCallback    (timer_t id, const TimerCallback& callback );
      void removeTimerCallback (timer_t id);

      uint64_t addSignalCallback (int signal, const SignalCallback& callback );
      void removeSignalCallback ( uint64_t id );

      int addNormalCallback     ( const NormalCallback& callback );
      void removeNormalCallback (int id);

      // Looks to see if there is any activity to process and 
      // processes it. Returns true if activity was processed, 
      // false otherwise. Timeout value gives maximum time to 
      // wait for activity in milliseconds.
      bool pollActivity( int timeout_ms = 0 ) const;

      // Waits for some activity to process and processes it
      void waitOnActivity() const;

    private:
      void processActivity( const siginfo_t& info ) const;
      const FdCallback&          getFdCallback(int fd) const;
      const FdExtendedCallback&  getFdExtCallback(int fd) const;

      const NormalCallback& getNormalCallback(int id) const;
      const void processSignalCallbacks(int signal, int pid, int uid) const;
      const TimerCallback&  getTimerCallback(timer_t id) const;


    private:
      typedef std::map<int,FdCallback>          FdCallbackTable;
      typedef std::map<int,FdExtendedCallback>  FdExtCallbackTable;
      typedef std::map<int,NormalCallback>  NormalCallbackTable;
      typedef std::multimap<int,std::pair<uint64_t,SignalCallback> >  SignalCallbackTable;
      typedef std::map<timer_t,TimerCallback>   TimerCallbackTable;
      typedef std::map<int32_t, struct sigaction> SignalHandlerType;  

      FdCallbackTable      fdCallbacks;
      FdExtCallbackTable   fdExtCallbacks;
      NormalCallbackTable  normalCallbacks;
      SignalCallbackTable  signalCallbacks;
      TimerCallbackTable   timerCallbacks;
      SignalHandlerType    orgSignalHandlers;

      sigset_t signalMask;

      bool finished;
      bool initialised;

  };


} // end namespace SWA

#endif
