/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "swa/Stack.hh"

namespace SWA {

    Stack &Stack::instance = Stack::getInstanceStartupSafe();

    Stack &Stack::getInstanceStartupSafe() {
        // Adaption of a 'Myers Singleton', uses a function static
        // for the actual storage, but a pointer for all accesses so
        // that they can be used inline. If the pointer was not used
        // the getInstance() call could not be declared inline
        // because there could then be separate statics all over the
        // place, unless the compiler is very standard compliant and
        // does clever stuff to eliminate them. I'm not taking the
        // chance.
        static Stack singleton;
        return singleton;
    }

} // namespace SWA
