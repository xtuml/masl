/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_ConstraintError_HH
#define SWA_ConstraintError_HH

#include <string>

#include "Exception.hh"

namespace SWA {
class ConstraintError : public Exception {
  public:
    ConstraintError(const std::string &error)
        : Exception(std::string("Constraint Error :") + error) {}
    ConstraintError() : Exception("Constraint Error") {}
    ~ConstraintError() throw() {}
};

} // namespace SWA

#endif
