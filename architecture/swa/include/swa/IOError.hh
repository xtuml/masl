/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_IOError_HH
#define SWA_IOError_HH

#include "boost/tuple/tuple.hpp"
#include <string>

#include "Exception.hh"

namespace SWA {
    class IOError : public Exception {
      public:
        IOError()
            : Exception(std::string("IO Error :")) {}
        IOError(const std::string &error)
            : Exception(std::string("IO Error :") + error) {}

        template <class T>
        IOError(const T &tuple)
            : Exception(std::string("IO Error :"), tuple) {}
    };

} // namespace SWA

#endif
