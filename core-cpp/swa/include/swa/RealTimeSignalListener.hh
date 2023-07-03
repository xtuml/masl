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

#ifndef SWA_RealTimeSignalListener_HH
#define SWA_RealTimeSignalListener_HH

#include "boost/function.hpp"
#include "ListenerPriority.hh" 

namespace SWA
{

  class ActivityMonitor;

  class RealTimeSignalListener
  {
    public:

      // The type for the function to be called on a realtime signal.
      // Parameters are the pid and uid of the process that raised the signal
      typedef boost::function<void(int,int)> Callback;

      RealTimeSignalListener( const Callback& callback, ActivityMonitor& monitor );
      ~RealTimeSignalListener();

      void activate() { active = true; }
      void cancel() { active = false; }
 
      void queueSignal() const;
      void queueSignal( const ListenerPriority& priority ) const;

      void setPriority( const ListenerPriority& priority );
      const ListenerPriority& getPriority() { return priority; }


    private:
      void callCallback(int pid, int uid);

      int id;
      Callback callback;
      ListenerPriority priority;
      bool active;
      ActivityMonitor& monitor;

  };




}

#endif
