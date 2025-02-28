/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sqlite_Exception_HH
#define Sqlite_Exception_HH

#include <sstream>
#include <string>

#include "boost/tuple/tuple.hpp"
#include "sql/Exception.hh"

namespace SQLITE {

    // *****************************************************************
    // *****************************************************************
    class SqliteException : public ::SQL::SqlException {
      public:
        SqliteException() {}

        template <class T>
        SqliteException(const T &tuple)
            : SqlException(tuple) {}

        SqliteException(const std::string &error)
            : SqlException(error) {}
        virtual ~SqliteException() throw() {}
    };

    // *****************************************************************
    // *****************************************************************
    class SqliteSchemaException : public SqliteException {
      public:
        SqliteSchemaException(const std::string &message)
            : SqliteException(message) {}
        virtual ~SqliteSchemaException() throw() {}
    };

} // end namespace SQLITE

#endif
