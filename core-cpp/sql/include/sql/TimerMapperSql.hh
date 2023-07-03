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

#ifndef SQL_TimerMapperSql_HH
#define SQL_TimerMapperSql_HH

#include "TimerMapper.hh"

namespace SQL
{
 class TimerMapperSql
 {
    public:
        typedef TimerMapper::TimerIdType TimerIdType;

    public:
        virtual ~TimerMapperSql(){} 

        virtual void initialise( TimerMapper& mapper ) = 0;

        virtual uint32_t getRowCount()   = 0;
        virtual uint32_t getMaxTimerId() = 0;

        virtual void executeCreate  (const uint32_t timerId) = 0;
        virtual void executeDelete  (const uint32_t timerId) = 0;
        virtual void executeUpdate( const TimerMapper::EventTimerData& data ) = 0;

     protected:
        TimerMapperSql() {} 

     private:
        TimerMapperSql(const TimerMapperSql& rhs);
        TimerMapperSql& operator=(const TimerMapperSql& rhs);
 };

} // end namespace SQL

#endif
