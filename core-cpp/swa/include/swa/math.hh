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

#ifndef SWA_MATH_HH
#define SWA_MATH_HH

#include <cmath>
#include <typeinfo>
#include <iostream>
namespace SWA
{
  inline float remainder ( float lhs, float rhs )
  {
    return std::fmod(lhs,rhs);
  }

  inline double remainder ( double lhs, double rhs )
  {
    return std::fmod(lhs,rhs);
  }

  inline long double remainder ( long double lhs, long double rhs )
  {
    return std::fmod(lhs,rhs);
  }

  template<class T> 
  inline T remainder ( T lhs, T rhs )
  {
    return lhs % rhs;
  }

  template<class T> 
  inline T modulus ( T lhs, T rhs )
  {
    T r = remainder(lhs,rhs);
    return r + (r&&(((lhs<0)&&(rhs>0))||((lhs>0)&&(rhs<0)))?rhs:0);
  }

  template<class T1, class T2> 
  inline T1 rem ( T1 lhs, T2 rhs )
  {
    return remainder(lhs, static_cast<T1>(rhs));
  }

  template<class T1, class T2> 
  inline T1 mod ( T1 lhs, T2 rhs )
  {
    return modulus(lhs, static_cast<T1>(rhs));
  }

  template<class T1, class T2> 
  inline T1 pow ( T1 lhs, T2 rhs )
  {
    return static_cast<T1>(pow(static_cast<double>(lhs), static_cast<double>(rhs)));
  }

}

#endif
