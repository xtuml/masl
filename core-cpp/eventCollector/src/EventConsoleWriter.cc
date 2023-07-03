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

#include <cerrno>
#include <iostream>
#include <fstream>
#include <unistd.h>
#include <stdlib.h>
#include <sstream>

#include "EventConsoleWriter.hh"

namespace {

// ***************************************************************
// ***************************************************************
std::ostream* getConsoleStream()
{
   std::ostream* console = 0;
   if (getenv("EVENT_CONSOLE_ENABLED") != 0){
       console = &std::cout;
   }
   return console;
}

}

namespace EVENTS {

// ***************************************************************
// ***************************************************************
EventConsoleWriter::EventConsoleWriter():
   console(getConsoleStream())
{
}

// ***************************************************************
// ***************************************************************
EventConsoleWriter::~EventConsoleWriter()
{
  console && console->flush();
}

// ***************************************************************
// ***************************************************************
bool EventConsoleWriter::isEnabled()
{
   return console;  // use implicit conversion to bool.
}

// ***************************************************************
// ***************************************************************
void EventConsoleWriter::write(const std::string& buffer)
{
  console && *console << buffer << std::flush;
}

// ***************************************************************
// ***************************************************************
void EventConsoleWriter::write(const void *buffer, size_t size)
{
   // only write formatted text. So ignore this call.
}

// ***************************************************************
// ***************************************************************
void EventConsoleWriter::shutdown()
{
   console && console->flush();
}

} // end EVENTS namespace
