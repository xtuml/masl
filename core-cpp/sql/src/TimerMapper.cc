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
#include "sql/Util.hh"
#include "sql/TimerMapper.hh"
#include "sql/TimerMapperSql.hh"
#include "sql/TimerMapperSqlFactory.hh"
#include "TimerMapperUnitOfWork.hh"
#include "swa/Process.hh"

namespace SQL
{
  namespace
  {
    bool initialise()
    {
      SWA::Process::getInstance().registerInitialisingListener(&TimerMapper::init);  
      return true;
    }

    // Force initialisation
    const bool initialised = initialise();
  }

// ***************************************************************
// ***************************************************************
void TimerMapper::init()
{
   singleton().initialise();
}

// ***************************************************************
// ***************************************************************
TimerMapper& TimerMapper::singleton()
{
   static TimerMapper instance;
   return instance;
}

// ***************************************************************
// ***************************************************************
TimerMapper::TimerMapper():
   unitOfWork(new TimerMapperUnitOfWork(*this)),
   timerSql()
{
} 

// ***************************************************************
// ***************************************************************
TimerMapper::~TimerMapper() 
{

}

void TimerMapper::initialise()
{
  if (timerSql.get() == 0){
      timerSql = TimerMapperSqlFactory::singleton().getImpl();
  }
  nextId = timerSql->getMaxTimerId() + 1;
  timerSql->initialise(*this);
}

void TimerMapper::restoreTimer ( const EventTimerData& timerData )
{
 addTimer(timerData.id).restore(timerData.expiryTime, timerData.period, timerData.scheduled, timerData.expired, timerData.missed, timerData.event );
}


// ***************************************************************
// ***************************************************************
TimerMapper::TimerIdType TimerMapper::createTimerInner()
{
  unitOfWork->createTimer(nextId);
  return nextId++;
}

// ***************************************************************
// ***************************************************************
void TimerMapper::deleteTimerInner(const TimerIdType id)
{
  unitOfWork->deleteTimer(id);
}


// ***************************************************************
// ***************************************************************
void TimerMapper::updateTimerInner (const SWA::EventTimer& eventTimer)
{
  unitOfWork->updateTimer(EventTimerData(eventTimer));
}

// ***************************************************************
// ***************************************************************
void TimerMapper::commitCreate ( TimerIdType id )
{
  timerSql->executeCreate(id);
}

// ***************************************************************
// ***************************************************************
void TimerMapper::commitDelete ( TimerIdType id )
{
  timerSql->executeDelete(id);
}

// ***************************************************************
// ***************************************************************
void TimerMapper::commitUpdate ( const EventTimerData& timerData )
{
  timerSql->executeUpdate(timerData);
}

namespace {
  bool registered = TimerMapper::registerSingleton( &TimerMapper::singleton );
}

} // end namespace SQLITE
