/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "sql/DatabaseFactory.hh"

namespace SQL {

    // ********************************************************
    // ********************************************************
    DatabaseFactory &DatabaseFactory::singleton() {
        static DatabaseFactory instance;
        return instance;
    }

    // ********************************************************
    // ********************************************************
    DatabaseFactory::DatabaseFactory()
        : impl_(0) {}

    // ********************************************************
    // ********************************************************
    DatabaseFactory::~DatabaseFactory() {}

} // end namespace SQL
