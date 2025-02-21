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
#include <string.h>
#include <sstream>

#include "EventFileWriter.hh"

namespace {

// ***************************************************************
// ***************************************************************
std::string formFileName()
{
   std::string fileName;
   if (getenv("EVENT_FILE_ENABLED") != 0){
       std::string directory(getenv("EVENT_FILE_DIR") == 0 ? "" : getenv("EVENT_FILE_DIR"));
       if (!directory.empty() && *directory.rbegin() != '/'){
           directory += '/';
       }
       std::ostringstream fileNameStrm;
       fileNameStrm << directory << "events." << getpid();
       fileName = fileNameStrm.str();
   }
   return fileName;
}

}

namespace EVENTS {

// ***************************************************************
// ***************************************************************
EventFileWriter::EventFileWriter():
   fileName(formFileName()),
   ofile()
{
    if (!fileName.empty()){
        ofile = std::shared_ptr< std::ofstream >(new  std::ofstream(fileName.c_str()));
        if (!(*ofile)){
            std::cout << "EventFileWriter failed to open event file " << fileName << ":" << strerror(errno) << std::endl;
            ofile.reset();
        }
    } 
}

// ***************************************************************
// ***************************************************************
EventFileWriter::~EventFileWriter()
{
  ofile && ofile->flush();
}

// ***************************************************************
// ***************************************************************
bool EventFileWriter::isEnabled()
{
   return ofile.get();  // use implicit conversion to bool.
}

// ***************************************************************
// ***************************************************************
void EventFileWriter::write(const std::string& buffer)
{
  ofile && *ofile << buffer;
}

// ***************************************************************
// ***************************************************************
void EventFileWriter::write(const void *buffer, size_t size)
{
   ofile && ofile->write(reinterpret_cast<const char*>(buffer),size);
}

// ***************************************************************
// ***************************************************************
void EventFileWriter::shutdown()
{
   ofile && ofile->flush();
}

} // end EVENTS namespace
