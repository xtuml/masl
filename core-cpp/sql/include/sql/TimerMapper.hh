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

#ifndef SQL_TimerMapper_HH
#define SQL_TimerMapper_HH

#include <set>
#include <map>

#include "swa/EventTimers.hh"
#include "swa/EventTimer.hh"
#include "boost/shared_ptr.hpp"

namespace SQL
{

// ***************************************************************
// @brief Class To map architecture timers to a database table
//!
//! The architecture has an in-built timer implementation that can be 
//! used to schedule events. The interface for this timer is provided 
//! by the ::SWA::TimerImpl class. This allows persistence mechanisms
//! to provide the requried implementation. In this case the created 
//! timers are mapped to a database table. The parameters of the
//! event can also be stored persistently; this is undertaken in the
//! defining domain of the event. Only the basic details about the 
//! timer are stored by the table that underpins this class i.e. timerId,
//! source object id, destination object id.
//!   All timer modifications are stored up in the timer unit of work and 
//! applied at the end of the thread but before the transaction is committed. 
// ***************************************************************
class TimerMapperSql;
class TimerMapperUnitOfWork;

class TimerMapper : public ::SWA::EventTimers
{
   public:
     typedef  ::SWA::EventTimers::TimerIdType        TimerIdType;
     
   public:
      // ***************************************************************
      //! Make this timer class a singleton.
      // ***************************************************************
      static TimerMapper& singleton();
      static void init();

   public:
      struct EventTimerData
      {        
        EventTimerData ()
          : id(),
            scheduled(),
            expired(),
            missed(),
            expiryTime(),
            period(),
            event() {}

        EventTimerData ( const SWA::EventTimer& eventTimer )
          : id(eventTimer.getId()),
            scheduled(eventTimer.isScheduled()),
            expired(eventTimer.isExpired()),
            missed(eventTimer.getMissed()),
            expiryTime(eventTimer.getExpiryTime()),
            period(eventTimer.getPeriod()),
            event(eventTimer.getEvent()) {}

        TimerIdType id;
        bool scheduled;
        bool expired;
        int missed;
        SWA::Timestamp expiryTime;
        SWA::Duration period;
        boost::shared_ptr<SWA::Event> event;
      };

      void restoreTimer ( const EventTimerData& timerData );


      virtual TimerIdType createTimerInner ();
      virtual void deleteTimerInner ( const TimerIdType id );
      virtual void updateTimerInner ( const SWA::EventTimer& eventTimer );

      // ***************************************************************
      //! Unit Of Work callback function to apply all timer insert operations
      //! into the timer source table.
      // ***************************************************************
      void commitCreate(TimerIdType id);

      // ***************************************************************
      //! Unit Of Work callback function to apply all timer delete operations
      //! into the timer source table.
      // ***************************************************************
      void commitDelete(TimerIdType id);

      // ***************************************************************
      //! Unit Of Work callback function to apply all timer update operations
      //! into the timer source table.
      // ***************************************************************
      void commitUpdate( const EventTimerData& timerData );

   private:
       TimerMapper(); 
      ~TimerMapper();

       virtual void initialise();

   private:
       TimerMapper(const TimerMapper& rhs);
       TimerMapper& operator=(const TimerMapper& rhs);

   private:
       boost::shared_ptr<TimerMapperUnitOfWork> unitOfWork;
       boost::shared_ptr<TimerMapperSql>        timerSql;
      
      TimerIdType nextId;

};

} // end namespace SWA

#endif
