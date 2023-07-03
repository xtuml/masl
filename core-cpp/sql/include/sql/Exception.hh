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
