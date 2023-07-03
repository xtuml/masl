/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 * Classification: UK OFFICIAL
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_FallbackSingleton_HH
#define SWA_FallbackSingleton_HH

#include "boost/function.hpp"

#include <iostream>

namespace SWA
{

  template<class Singleton, class Fallback>
  class FallbackSingleton
  {
    public:
      typedef boost::function<Singleton&()> GetterFunction;

      static Singleton& getSingleton();
      static bool registerSingleton ( const GetterFunction& getter );


    private:
      static Fallback& getFallback();
      static GetterFunction& getSingletonGetter();

  };

  template<class Singleton, class Fallback>
  bool FallbackSingleton<Singleton,Fallback>::registerSingleton ( const GetterFunction& function )
  {
    getSingletonGetter() = function;
    return true;
  }

  template<class Singleton, class Fallback>
  typename FallbackSingleton<Singleton,Fallback>::GetterFunction& FallbackSingleton<Singleton,Fallback>::getSingletonGetter()
  {
    static GetterFunction getter;
    return getter;
  }

  template<class Singleton, class Fallback>
  Fallback& FallbackSingleton<Singleton,Fallback>::getFallback()
  {
    static Fallback fallback;
    return fallback;
  }


  template<class Singleton, class Fallback>
  Singleton& FallbackSingleton<Singleton,Fallback>::getSingleton()
  {
    static Singleton& singleton = getSingletonGetter()?getSingletonGetter()():getFallback();
    return singleton;
  }


}
#endif

