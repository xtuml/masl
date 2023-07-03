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

#include <algorithm>
#include <time.h>

#include "swa/Stack.hh"
#include "swa/Process.hh"

#include "eventCollector/ProcessContext.hh"

#include "boost/lexical_cast.hpp"

namespace EVENTS {


// ***************************************************************
// ***************************************************************
ProcessContext::ProcessContext()
{

}

// ***************************************************************
// ***************************************************************
ProcessContext::~ProcessContext()
{

}

// ***************************************************************
// ***************************************************************
pid_t ProcessContext::getPid()  const
{
   return getpid();
}

// ***************************************************************
// ***************************************************************
const std::string  ProcessContext::getName() const
{
    return SWA::Process::getInstance().getName();
}

// ***************************************************************
// ***************************************************************
const std::string  ProcessContext::getDomainName(int domainId) const
{
    return SWA::Process::getInstance().getDomain(domainId).getName();
}

// ***************************************************************
// ***************************************************************
const std::string  ProcessContext::getFrameLevel() const
{
   std::size_t frameLevel = SWA::Stack::getInstance().getStackFrames().size();
   return boost::lexical_cast<std::string>(frameLevel);
}

// ***************************************************************
// ***************************************************************
const std::string  ProcessContext::getTime() const
{
    time_t currentTime = time(0); 
    struct tm *brkTime = gmtime(&currentTime);

    char xmlTime[32];
    if (strftime(xmlTime,sizeof(xmlTime),"%FT%T",brkTime) == 0){
        // The memory allocated for the textual representation of
        // the time is not big enough. This should not happen as
        // the format should be '2008-12-16T13:47:09', rather than
        // stopping the event logging by throwing an exception just
        // use a dummy valid time for the return value.
        strcpy(xmlTime,"2008-01-01T00:00:00");
    }
    std::string timeText(xmlTime);
    return timeText;
}


}
