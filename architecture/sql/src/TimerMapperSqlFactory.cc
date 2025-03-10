/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "sql/TimerMapperSqlFactory.hh"
namespace SQL {

    // **********************************************************************
    // **********************************************************************
    TimerMapperSqlFactory &TimerMapperSqlFactory::singleton() {
        static TimerMapperSqlFactory instance;
        return instance;
    }

    // **********************************************************************
    // **********************************************************************
    TimerMapperSqlFactory::TimerMapperSqlFactory() {}

    // **********************************************************************
    // **********************************************************************
    TimerMapperSqlFactory::~TimerMapperSqlFactory() {}

    // **********************************************************************
    // **********************************************************************
    bool TimerMapperSqlFactory::registerImpl(const std::shared_ptr<TimerMapperSql> &impl) {
        impl_ = impl;
        return true;
    }

    // **********************************************************************
    // **********************************************************************
    std::shared_ptr<TimerMapperSql> &TimerMapperSqlFactory::getImpl() {
        return impl_;
    }

} // end namespace SQL
