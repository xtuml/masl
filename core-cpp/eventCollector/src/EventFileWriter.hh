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

#ifndef Events_EventFileWriter_HH
#define Events_EventFileWriter_HH

#include <iosfwd>
#include <string>

#include "EventWriter.hh"

#include "boost/shared_ptr.hpp"

namespace EVENTS {

// ***************************************************************
//! Define a class that can be used to stream encoded data to a log
//! file. Default log file name is events.<pid> An example XML encoded 
//! data stream is shown below:
//! 
//! < thread action="started" process="POLY_sqlite_standalone" time="Mon Dec 15 15:21:21 2008 "  value="Main Loop">
//!   <event type="timer" action="fired" time="Mon Dec 15 15:21:22 2008 " >
//!     <timer name="timerId" value="21" />
//!   </event>
//!   <event type="deleted" action="created" time="Mon Dec 15 15:21:22 2008 " >
//!     <timer name="timerId" value="21" />
//!   </event>
//!   <event domain="POLY" type="service" action="called" time="Mon Dec 15 15:21:22 2008 " >
//!     <service object="Test_Case_12_21" name="testCaseVerification" />
//!   </event>
//!   <event domain="POLY" type="service" action="called" time="Mon Dec 15 15:21:23 2008 " >
//!     <service object="Test_Case_12_21" name="testCaseNext" />
//!   </event>
//!   <event domain="POLY" type="service" action="called" time="Mon Dec 15 15:21:23 2008 " >
//!     <service object="Polymorphic_Event_Test_Case" name="testCasesFinished" />
//!   </event>
//!   <event domain="POLY" type="service" action="called" time="Mon Dec 15 15:21:23 2008 " >
//!     <service object="Test_Case_12_21" name="testCaseDeletion" />
//!   </event>
//! </thread>
//!
//! Can use environment variable EVENT_FILE_ENABLED to enable the logging
//! and the environment variable EVENT_FILE_DIR to place the events.<pid>
//! file into another directory besides the current working directory.
// ***************************************************************
class EventFileWriter : public EventWriter
{
   public:
               EventFileWriter();
      virtual ~EventFileWriter();

      // ***************************************************************
      //! @return true if a file has been opened and can be writern to.
      // ***************************************************************
      virtual bool isEnabled();

      // ***************************************************************
      //! Write the supplied data to the file
      //!       
      //! @param buffer data to write.
      // ***************************************************************
      virtual void write(const std::string& buffer);

      // ***************************************************************
      //! Write the supplied data to the file
      //! 
      //! @param buffer data to write.
      //! @param size   number of bytes.
      // ***************************************************************
      virtual void write(const void *buffer, size_t size);

      virtual void shutdown();

   private:
      std::string fileName;
      boost::shared_ptr< std::ofstream > ofile;

};

} // end EVENTS namespace

#endif
