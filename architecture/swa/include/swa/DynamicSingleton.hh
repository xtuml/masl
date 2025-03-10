/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_DynamicSingleton_HH
#define SWA_DynamicSingleton_HH

#include <functional>

#include <iostream>

namespace SWA {

    template <class Singleton>
    class DynamicSingleton {
      public:
        typedef std::function<Singleton &()> GetterFunction;

        static Singleton &getSingleton();
        static bool registerSingleton(GetterFunction getter);

      private:
        static GetterFunction &getSingletonGetter();
        GetterFunction getter;
    };

    template <class Singleton>
    bool DynamicSingleton<Singleton>::registerSingleton(GetterFunction getter) {
        getSingletonGetter() = getter;
        return true;
    }

    template <class Singleton>
    typename DynamicSingleton<Singleton>::GetterFunction &DynamicSingleton<Singleton>::getSingletonGetter() {
        static GetterFunction getter;
        return getter;
    }

    template <class Singleton>
    Singleton &DynamicSingleton<Singleton>::getSingleton() {
        static Singleton &singleton = getSingletonGetter()();
        return singleton;
    }

} // namespace SWA
#endif
