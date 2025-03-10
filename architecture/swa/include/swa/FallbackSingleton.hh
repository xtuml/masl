/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_FallbackSingleton_HH
#define SWA_FallbackSingleton_HH

#include <functional>

#include <iostream>

namespace SWA {

    template <class Singleton, class Fallback>
    class FallbackSingleton {
      public:
        typedef std::function<Singleton &()> GetterFunction;

        static Singleton &getSingleton();
        static bool registerSingleton(const GetterFunction &getter);

      private:
        static Fallback &getFallback();
        static GetterFunction &getSingletonGetter();
    };

    template <class Singleton, class Fallback>
    bool FallbackSingleton<Singleton, Fallback>::registerSingleton(const GetterFunction &function) {
        getSingletonGetter() = function;
        return true;
    }

    template <class Singleton, class Fallback>
    typename FallbackSingleton<Singleton, Fallback>::GetterFunction &
    FallbackSingleton<Singleton, Fallback>::getSingletonGetter() {
        static GetterFunction getter;
        return getter;
    }

    template <class Singleton, class Fallback>
    Fallback &FallbackSingleton<Singleton, Fallback>::getFallback() {
        static Fallback fallback;
        return fallback;
    }

    template <class Singleton, class Fallback>
    Singleton &FallbackSingleton<Singleton, Fallback>::getSingleton() {
        static Singleton &singleton = getSingletonGetter() ? getSingletonGetter()() : getFallback();
        return singleton;
    }

} // namespace SWA
#endif
