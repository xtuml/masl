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

#ifndef SWA_DynamicSingleton_HH
#define SWA_DynamicSingleton_HH

#include "boost/function.hpp"

#include <iostream>

namespace SWA
{

template<class Singleton>
class DynamicSingleton
{
  public:
    typedef boost::function<Singleton&()> GetterFunction;

    static Singleton& getSingleton();
    static bool registerSingleton(GetterFunction getter);

  private:
    static GetterFunction& getSingletonGetter();
    GetterFunction getter;
};


template<class Singleton>
bool DynamicSingleton<Singleton>::registerSingleton(GetterFunction getter) 
{
  getSingletonGetter() = getter;
  return true;
}

template<class Singleton>
typename DynamicSingleton<Singleton>::GetterFunction& DynamicSingleton<Singleton>::getSingletonGetter() 
{
  static GetterFunction getter;
  return getter;
}

template<class Singleton>
Singleton& DynamicSingleton<Singleton>::getSingleton()
{
  static Singleton& singleton = getSingletonGetter()();
  return singleton;
}


}
#endif

