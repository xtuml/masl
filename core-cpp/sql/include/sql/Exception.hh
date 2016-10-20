//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   Exception.hh
//
//============================================================================//
#ifndef Sql_Exception_HH
#define Sql_Exception_HH

#include <string>
#include <sstream>

#include "swa/Exception.hh"
#include "boost/tuple/tuple.hpp"
#include "boost/tuple/tuple_io.hpp"

namespace SQL {

// *****************************************************************
// *****************************************************************
template <class T>
inline std::string streamTuple(const T& tuple)
{
    std::ostringstream textStream;
    textStream << ::boost::tuples::set_open(' ') << ::boost::tuples::set_close(' ') << ::boost::tuples::set_delimiter(' ') << tuple;
    return textStream.str();
}

// *****************************************************************
// *****************************************************************
class SqlException : public SWA::Exception
{
  public:
      SqlException () {}

      template <class T>
      SqlException (const T& tuple):Exception(streamTuple(tuple)) {}

      SqlException (const std::string& error): Exception(error) {}
     ~SqlException() throw() {}
};

} // end namespace SQLITE

#endif
