//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   Exception.hh
//
//============================================================================//
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
