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

#ifndef Sql_TimerMapperUnitOfWork_HH
#define Sql_TimerMapperUnitOfWork_HH

#include <set>

#include "sql/TimerMapper.hh"
#include "sql/UnitOfWorkObserver.hh"

namespace SQL {

// ***********************************************************************
//!
// ***********************************************************************

class TimerMapperUnitOfWork : public UnitOfWorkObserver
{
   public:
      TimerMapperUnitOfWork(TimerMapper& parent);
     ~TimerMapperUnitOfWork();

     void createTimer(const uint32_t timerId);     
     void deleteTimer(const uint32_t timerId);    
     void updateTimer(const TimerMapper::EventTimerData& data );    

   private:
      virtual void flush             (UnitOfWorkContext& context);
      virtual void committed         (UnitOfWorkContext& context);
      virtual void startTransaction  (UnitOfWorkContext& context);
      virtual void commitTransaction (UnitOfWorkContext& context);
      virtual void abortTransaction  (UnitOfWorkContext& context);

      void clear();
      bool isDirty(); 
      void primeForChanges();

   private:
       TimerMapper&  parent;

       typedef std::set<uint32_t> IdSet;

       typedef std::map<uint32_t,TimerMapper::EventTimerData> UpdateSet;

       IdSet  createSet;
       IdSet  deleteSet;
       UpdateSet  updateSet;
};

} // end namespace SQL

#endif
