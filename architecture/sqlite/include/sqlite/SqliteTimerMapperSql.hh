/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sqlite_TimerMapperSql_HH
#define Sqlite_TimerMapperSql_HH

#include <stdint.h>

#include "PreparedStatement.hh"

#include "sql/TimerMapper.hh"
#include "sql/TimerMapperSql.hh"

#include <memory>

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
class SqliteTimerMapperSql : public ::SQL::TimerMapperSql {
  public:
    typedef ::SQL::TimerMapperSql::TimerIdType TimerIdType;

  public:
    static SqliteTimerMapperSql &singleton();

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
    //! be held by the associated unit of work object and applied to the
    //! database at the end of the application thread.
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
    void executeUpdate(const SQL::TimerMapper::EventTimerData &data);

    void initialise(SQL::TimerMapper &mapper);

  private:
    TimerIdType getFunctionValue(const std::string &functionQuery);

  private:
    SqliteTimerMapperSql(const SqliteTimerMapperSql &rhs);
    SqliteTimerMapperSql &operator=(const SqliteTimerMapperSql &rhs);

  private:
    PreparedStatement createStatement;
    PreparedStatement deleteStatement;
    PreparedStatement updateStatement;
};

} // end namespace SQLITE
#endif
