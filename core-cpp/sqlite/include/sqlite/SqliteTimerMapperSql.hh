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

#ifndef Sqlite_TimerMapperSql_HH
#define Sqlite_TimerMapperSql_HH

#include <stdint.h>

#include "PreparedStatement.hh"

#include "sql/TimerMapper.hh"
#include "sql/TimerMapperSql.hh"

#include "boost/shared_ptr.hpp"

namespace SQLITE {

// *****************************************************************
//! Sqlite Timer class that provides the required SQL constructs to 
//! persist and manipulate timers. The SQLite implementation uses a 
//! single timer table to hold the timer details and any event data
//! associated with the event that should be fired when the timer
//! expires. The event data is serialised into an ASN.1 stream and
//! stored in the parameter blob column. The serialisation to ASN.1 
//! is undertaken in the generated SQLite domain specific code. Each
//! ASN.1 definition file is extended to include types that map to
//! the parameters of an event. 
// *****************************************************************
class SqliteTimerMapperSql : public ::SQL::TimerMapperSql
{
 public:
    typedef ::SQL::TimerMapperSql::TimerIdType TimerIdType;

 public:
       static SqliteTimerMapperSql& singleton();
       
                SqliteTimerMapperSql(); 
       virtual ~SqliteTimerMapperSql(); 

       // *****************************************************************
       //! @return the number of timers in the Timer table.
       // *****************************************************************
       uint32_t getRowCount();

       // *****************************************************************
       //! @return the maximum timer id currently being used.
       // *****************************************************************
       TimerIdType getMaxTimerId();

       // *****************************************************************
       //! Insert the supplied timer id into the timer table. All timer detail
       //! columns will hold default timer values. The insert modifications will 
       //! be held by the associated unit of work object and applied to the database
       //! at the end of the application thread.
       //! 
       //! @param timerId new timerId to add to Timer table.
       // *****************************************************************
       void executeCreate(const TimerIdType timerId);

       // *****************************************************************
       //! Delete the supplied timer id into the timer table. The row deleteion  
       //! will be held by the associated unit of work object and applied to the 
       //! database at the end of the application thread.
       //! 
       //! @param timerId timerId to delete from Timer table.
       // *****************************************************************
       void executeDelete(const TimerIdType timerId);

       // *****************************************************************
       //! Update the supplied timer details into the timer table.  The update
       //! modifications will be held by the associated unit of work object and 
       //! applied to the database at the end of the application thread.
       //! 
       //! @param timerId      timerId to modify.
       //! @param timerDetails the new timer details.
       // *****************************************************************
       void executeUpdate(const SQL::TimerMapper::EventTimerData& data );

       void initialise( SQL::TimerMapper& mapper );

   private:
        TimerIdType getFunctionValue(const std::string& functionQuery);


   private:
       SqliteTimerMapperSql(const SqliteTimerMapperSql& rhs); 
       SqliteTimerMapperSql& operator=(const SqliteTimerMapperSql& rhs); 

   private:
       PreparedStatement createStatement;
       PreparedStatement deleteStatement;
       PreparedStatement updateStatement;
};

} // end namespace SQLITE
#endif
