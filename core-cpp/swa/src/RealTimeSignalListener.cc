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

#include "swa/RealTimeSignalListener.hh"
#include "swa/ActivityMonitor.hh"
#include "swa/ProgramError.hh"
#include <fcntl.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <signal.h>
#include "boost/bind/bind.hpp"
using namespace boost::placeholders;

namespace SWA
{


  RealTimeSignalListener::RealTimeSignalListener( const Callback& callback, ActivityMonitor& monitor )
    : id(monitor.addNormalCallback(boost::bind(&RealTimeSignalListener::callCallback,this,_1,_2))),
      callback(callback),
      priority(ListenerPriority::getNormal()),
      active(true),
      monitor(monitor)
  {
  }

  RealTimeSignalListener::~RealTimeSignalListener()
  {
    monitor.removeNormalCallback(id);
  }


  void RealTimeSignalListener::setPriority( const ListenerPriority& priority )
  {
    this->priority = priority;
  }
  
  void RealTimeSignalListener::queueSignal() const
  {
    sigval data;
    data.sival_int = id;
    sigqueue(getpid(),priority.getValue(),data);
  }

 void RealTimeSignalListener::queueSignal( const ListenerPriority& priority ) const
  {
    sigval data;
    data.sival_int = id;
    sigqueue(getpid(),priority.getValue(),data);
  }

  void RealTimeSignalListener::callCallback( int pid, int uid )
  {
    if ( active ) callback ( pid, uid );
  }
}
