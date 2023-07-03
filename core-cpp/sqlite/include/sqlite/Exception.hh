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

#ifndef Sqlite_Exception_HH
#define Sqlite_Exception_HH

#include <string>
#include <sstream>

#include "sql/Exception.hh"
#include "boost/tuple/tuple.hpp"

namespace SQLITE {

// *****************************************************************
// *****************************************************************
class SqliteException : public ::SQL::SqlException
{
  public:
      SqliteException () {}

      template <class T>
      SqliteException (const T& tuple):SqlException(tuple) {}

      SqliteException (const std::string& error):SqlException(error) {}
      virtual ~SqliteException() throw() {}
};

// *****************************************************************
// *****************************************************************
class SqliteSchemaException : public SqliteException
{
  public:
    SqliteSchemaException (const std::string& message):SqliteException(message) {}
    virtual ~SqliteSchemaException() throw() {}
};

} // end namespace SQLITE

#endif
