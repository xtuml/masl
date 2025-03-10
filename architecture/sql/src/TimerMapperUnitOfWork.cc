/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "TimerMapperUnitOfWork.hh"
#include "sql/DatabaseFactory.hh"
#include "sql/DatabaseUnitOfWork.hh"
#include "sql/Exception.hh"
#include "sql/TimerMapper.hh"

namespace SQL {

    // ***********************************************************************
    // ***********************************************************************
    TimerMapperUnitOfWork::TimerMapperUnitOfWork(TimerMapper &parent)
        : parent(parent)

    {}

    // ***********************************************************************
    // ***********************************************************************
    TimerMapperUnitOfWork::~TimerMapperUnitOfWork() {}

    // ***********************************************************************
    // ***********************************************************************
    void TimerMapperUnitOfWork::createTimer(const uint32_t timerId) {
        createSet.insert(timerId);
        primeForChanges();
    }

    // ***********************************************************************
    // ***********************************************************************
    void TimerMapperUnitOfWork::deleteTimer(const uint32_t timerId) {
        // check that timer has been created and deleted in the
        // same  transaction as this does not require database changes.
        bool sameTransaction = createSet.find(timerId) != createSet.end();
        createSet.erase(timerId);
        updateSet.erase(timerId);

        if (sameTransaction == false) {
            deleteSet.insert(timerId);
        }
        primeForChanges();
    }

    // ***********************************************************************
    // ***********************************************************************
    void TimerMapperUnitOfWork::updateTimer(const TimerMapper::EventTimerData &data) {
        updateSet[data.id] = data;
        primeForChanges();
    }

    // ***********************************************************************
    // ***********************************************************************
    void TimerMapperUnitOfWork::committed(UnitOfWorkContext &context) {
        clear();
    }

    // ***********************************************************************
    // ***********************************************************************
    void TimerMapperUnitOfWork::startTransaction(UnitOfWorkContext &context) {
        if (isDirty()) {
            throw SqlException("TimerMapperUnitOfWork::start_transaction : mapper is not clean");
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    void TimerMapperUnitOfWork::flush(UnitOfWorkContext &context) {
        commitTransaction(context);
    }

    // ***********************************************************************
    // ***********************************************************************
    void TimerMapperUnitOfWork::commitTransaction(UnitOfWorkContext &context) {
        for (IdSet::const_iterator it = createSet.begin(), end = createSet.end(); it != end; ++it)
            parent.commitCreate(*it);
        for (UpdateSet::const_iterator it = updateSet.begin(), end = updateSet.end(); it != end; ++it)
            parent.commitUpdate(it->second);
        for (IdSet::const_iterator it = deleteSet.begin(), end = deleteSet.end(); it != end; ++it)
            parent.commitDelete(*it);
    }

    // ***********************************************************************
    // ***********************************************************************
    void TimerMapperUnitOfWork::abortTransaction(UnitOfWorkContext &context) {
        clear();
    }

    // ***********************************************************************
    // ***********************************************************************
    void TimerMapperUnitOfWork::clear() {
        createSet.clear();
        updateSet.clear();
        deleteSet.clear();
    }

    // ***********************************************************************
    // ***********************************************************************
    void TimerMapperUnitOfWork::primeForChanges() {
        if (isDirty()) {
            DatabaseFactory::singleton().getImpl().getCurrentUnitOfWork().registerDirtyObserver(this);
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    bool TimerMapperUnitOfWork::isDirty() {
        return !(createSet.empty() && updateSet.empty() && deleteSet.empty());
    }
} // end namespace SQL
