/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_console_HH
#define SWA_console_HH

#include "Device.hh"

namespace SWA {

    Device &console();
    Device &error_log();
    Device &system_log();

} // namespace SWA

#endif
