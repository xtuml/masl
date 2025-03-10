/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_Exception_HH
#define Sql_Exception_HH

#include <sstream>
#include <string>

#include "boost/tuple/tuple.hpp"
#include "boost/tuple/tuple_io.hpp"
#include "swa/Exception.hh"

namespace SQL {

    // *****************************************************************
    // *****************************************************************
    template <class T>
    inline std::string streamTuple(const T &tuple) {
        std::ostringstream textStream;
        textStream << ::boost::tuples::set_open(' ') << ::boost::tuples::set_close(' ')
                   << ::boost::tuples::set_delimiter(' ') << tuple;
        return textStream.str();
    }

    // *****************************************************************
    // *****************************************************************
    class SqlException : public SWA::Exception {
      public:
        SqlException() {}

        template <class T>
        SqlException(const T &tuple)
            : Exception(streamTuple(tuple)) {}

        SqlException(const std::string &error)
            : Exception(error) {}
        ~SqlException() throw() {}
    };

} // namespace SQL

#endif
