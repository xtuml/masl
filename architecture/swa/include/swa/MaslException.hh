/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_MaslException_HH
#define SWA_MaslException_HH

#include "Exception.hh"

namespace SWA {
class MaslException : public Exception {
  public:
    MaslException() : Exception("") {}
    MaslException(const std::string &message) : Exception(message) {}
    ~MaslException() throw() {}
};

} // namespace SWA

#endif
