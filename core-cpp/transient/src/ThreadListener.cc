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

#include "transient/ThreadListener.hh"
#include "swa/Process.hh"
#include "boost/bind/bind.hpp"

namespace transient
{
  bool init = ThreadListener::initialise();

  bool ThreadListener::initialise()
  {
    getInstance();
    return true;
  }

  ThreadListener::ThreadListener()
  {
    SWA::Process::getInstance().registerThreadCompletedListener(boost::bind(&ThreadListener::performCleanup,this));
    SWA::Process::getInstance().registerThreadAbortedListener(boost::bind(&ThreadListener::performCleanup,this));
  }

  ThreadListener& ThreadListener::getInstance()
  {
    static ThreadListener instance;
    return instance;
  }


  void ThreadListener::addCleanup ( const boost::function<void()> function )
  {
    cleanupRoutines.push_back(function);
  }

  void ThreadListener::performCleanup()
  {
    for ( std::vector<boost::function<void()> >::const_iterator it = cleanupRoutines.begin(), end = cleanupRoutines.end();
          it != end; ++it )
    {
      (*it)();
    }
    cleanupRoutines.clear();
  }

}
