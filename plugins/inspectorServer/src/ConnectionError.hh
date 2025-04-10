/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef ConnectionError_HH
#define ConnectionError_HH

#include <stdexcept>

namespace Inspector
{
  class ConnectionError : public std::runtime_error
  {
    public:
      ConnectionError ( const std::string& message ) : runtime_error(message) {}
  };
}

#endif
