/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SQL_TimerMapperSql_HH
#define SQL_TimerMapperSql_HH

#include "TimerMapper.hh"

namespace SQL {
class TimerMapperSql {
  public:
    typedef TimerMapper::TimerIdType TimerIdType;

  public:
    virtual ~TimerMapperSql() {}

    virtual void initialise(TimerMapper &mapper) = 0;

    virtual uint32_t getRowCount() = 0;
    virtual uint32_t getMaxTimerId() = 0;

    virtual void executeCreate(const uint32_t timerId) = 0;
    virtual void executeDelete(const uint32_t timerId) = 0;
    virtual void executeUpdate(const TimerMapper::EventTimerData &data) = 0;

  protected:
    TimerMapperSql() {}

  private:
    TimerMapperSql(const TimerMapperSql &rhs);
    TimerMapperSql &operator=(const TimerMapperSql &rhs);
};

} // end namespace SQL

#endif
