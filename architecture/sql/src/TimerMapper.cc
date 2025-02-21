/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "sql/TimerMapper.hh"
#include "TimerMapperUnitOfWork.hh"
#include "sql/TimerMapperSql.hh"
#include "sql/TimerMapperSqlFactory.hh"
#include "sql/Util.hh"
#include "swa/Process.hh"
#include <algorithm>

namespace SQL {
namespace {
bool initialise() {
    SWA::Process::getInstance().registerInitialisingListener(
        &TimerMapper::init);
    return true;
}

// Force initialisation
const bool initialised = initialise();
} // namespace

// ***************************************************************
// ***************************************************************
void TimerMapper::init() { singleton().initialise(); }

// ***************************************************************
// ***************************************************************
TimerMapper &TimerMapper::singleton() {
    static TimerMapper instance;
    return instance;
}

// ***************************************************************
// ***************************************************************
TimerMapper::TimerMapper()
    : unitOfWork(new TimerMapperUnitOfWork(*this)), timerSql() {}

// ***************************************************************
// ***************************************************************
TimerMapper::~TimerMapper() {}

void TimerMapper::initialise() {
    if (timerSql.get() == 0) {
        timerSql = TimerMapperSqlFactory::singleton().getImpl();
    }
    nextId = timerSql->getMaxTimerId() + 1;
    timerSql->initialise(*this);
}

void TimerMapper::restoreTimer(const EventTimerData &timerData) {
    addTimer(timerData.id)
        .restore(timerData.expiryTime, timerData.period, timerData.scheduled,
                 timerData.expired, timerData.missed, timerData.event);
}

// ***************************************************************
// ***************************************************************
TimerMapper::TimerIdType TimerMapper::createTimerInner() {
    unitOfWork->createTimer(nextId);
    return nextId++;
}

// ***************************************************************
// ***************************************************************
void TimerMapper::deleteTimerInner(const TimerIdType id) {
    unitOfWork->deleteTimer(id);
}

// ***************************************************************
// ***************************************************************
void TimerMapper::updateTimerInner(const SWA::EventTimer &eventTimer) {
    unitOfWork->updateTimer(EventTimerData(eventTimer));
}

// ***************************************************************
// ***************************************************************
void TimerMapper::commitCreate(TimerIdType id) { timerSql->executeCreate(id); }

// ***************************************************************
// ***************************************************************
void TimerMapper::commitDelete(TimerIdType id) { timerSql->executeDelete(id); }

// ***************************************************************
// ***************************************************************
void TimerMapper::commitUpdate(const EventTimerData &timerData) {
    timerSql->executeUpdate(timerData);
}

namespace {
bool registered = TimerMapper::registerSingleton(&TimerMapper::singleton);
}

} // namespace SQL
