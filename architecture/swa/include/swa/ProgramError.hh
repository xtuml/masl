/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_ProgramError_HH
#define SWA_ProgramError_HH

#include "boost/tuple/tuple.hpp"
#include <string>

#include "Exception.hh"

namespace SWA {
class ProgramError : public Exception {
  public:
    ProgramError() : Exception(std::string("Program Error :")) {}
    ProgramError(const std::string &error)
        : Exception(std::string("Program Error :") + error) {}

    template <class T>
    ProgramError(const T &tuple)
        : Exception(std::string("Program Error :"), tuple) {}
};
} // namespace SWA

#endif
