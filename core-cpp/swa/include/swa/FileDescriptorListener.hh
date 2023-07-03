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

#ifndef SWA_FileDescriptorListener_HH
#define SWA_FileDescriptorListener_HH

#include "boost/function.hpp"
#include "ListenerPriority.hh" 

namespace SWA
{

  class ActivityMonitor;

  class FileDescriptorListener
  {
    public:

      // The type for the function to be called on an fd 
      // event. Must return true if the callback should be 
      // requeued. Parameter is the relevant fd. 
      typedef boost::function<bool(int)> Callback;

      FileDescriptorListener( int fd, const Callback& callback, ActivityMonitor& monitor );
      FileDescriptorListener( const Callback& callback, ActivityMonitor& monitor );
      ~FileDescriptorListener();

      void setFd ( int fd );
      void clearFd();

      void activate(const bool queueImmediately = false);
      void cancel();
 
      void setPriority( const ListenerPriority& priority );
      const ListenerPriority& getPriority() { return priority; }

      int getFd() const { return fd; }

    private:
      void callCallback(int fd);
      void requeue() const;
      bool valid() const;
      bool readyToRead() const;


      void initFd();
      void updateFdStatus( bool makeActive );

      int fd;
      Callback callback;
      ListenerPriority priority;
      bool active;
      bool forceCallback;
      ActivityMonitor& monitor;

  };




}

#endif
