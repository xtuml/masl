/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef FunctionOverrider_HH
#define FunctionOverrider_HH

#include <functional>

namespace SWA {

    template <class FunctionType>
    class FunctionOverrider {
      public:
        typedef std::function<FunctionType> FunctionPtr;

        FunctionOverrider(FunctionPtr defaultFn)
            : defaultFn(defaultFn), overrideFn(0) {}

        FunctionPtr getFunction() const {
            return overrideFn ? overrideFn : defaultFn;
        }

        void override(FunctionPtr fn) {
            overrideFn = fn;
        }

        void cancelOverride() {
            overrideFn = 0;
        }

        bool isOverridden() {
            return overrideFn != 0;
        }

      private:
        const FunctionPtr defaultFn;
        FunctionPtr overrideFn;
    };
} // namespace SWA

#endif
