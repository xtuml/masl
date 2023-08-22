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

#ifndef BOOST_TUPLE_HASH_HH
#define BOOST_TUPLE_HASH_HH

#include <stdint.h>

#include "boost/tuple/tuple.hpp"
#include "boost/functional/hash.hpp"

namespace boost { 

namespace tuples {

template<class T1>
std::size_t hash_value ( const tuple<T1>& tuple )
{
  std::size_t seed = 0;
  boost::hash_combine(seed,tuple.template get<0>());
  return seed;
}


template<class T1,class T2>
std::size_t hash_value ( const tuple<T1,T2>& tuple )
{
  std::size_t seed = 0;
  boost::hash_combine(seed,tuple.template get<0>());
  boost::hash_combine(seed,tuple.template get<1>());
  return seed;
}

template<class T1,class T2,class T3>
std::size_t hash_value ( const tuple<T1,T2,T3>& tuple )
{
  std::size_t seed = 0;
  boost::hash_combine(seed,tuple.template get<0>());
  boost::hash_combine(seed,tuple.template get<1>());
  boost::hash_combine(seed,tuple.template get<2>());
  return seed;
}
template<class T1,class T2,class T3,class T4>
std::size_t hash_value ( const tuple<T1,T2,T3,T4>& tuple )
{
  std::size_t seed = 0;
  boost::hash_combine(seed,tuple.template get<0>());
  boost::hash_combine(seed,tuple.template get<1>());
  boost::hash_combine(seed,tuple.template get<2>());
  boost::hash_combine(seed,tuple.template get<3>());
  return seed;
}
template<class T1,class T2,class T3,class T4,class T5>
std::size_t hash_value ( const tuple<T1,T2,T3,T4,T5>& tuple )
{
  std::size_t seed = 0;
  boost::hash_combine(seed,tuple.template get<0>());
  boost::hash_combine(seed,tuple.template get<1>());
  boost::hash_combine(seed,tuple.template get<2>());
  boost::hash_combine(seed,tuple.template get<3>());
  boost::hash_combine(seed,tuple.template get<4>());
  return seed;
}
template<class T1,class T2,class T3,class T4,class T5,class T6>
std::size_t hash_value ( const tuple<T1,T2,T3,T4,T5,T6>& tuple )
{
  std::size_t seed = 0;
  boost::hash_combine(seed,tuple.template get<0>());
  boost::hash_combine(seed,tuple.template get<1>());
  boost::hash_combine(seed,tuple.template get<2>());
  boost::hash_combine(seed,tuple.template get<3>());
  boost::hash_combine(seed,tuple.template get<4>());
  boost::hash_combine(seed,tuple.template get<5>());
  return seed;
}

template<class T1,class T2,class T3,class T4,class T5,class T6,class T7>
std::size_t hash_value ( const tuple<T1,T2,T3,T4,T5,T6,T7>& tuple )
{
  std::size_t seed = 0;
  boost::hash_combine(seed,tuple.template get<0>());
  boost::hash_combine(seed,tuple.template get<1>());
  boost::hash_combine(seed,tuple.template get<2>());
  boost::hash_combine(seed,tuple.template get<3>());
  boost::hash_combine(seed,tuple.template get<4>());
  boost::hash_combine(seed,tuple.template get<5>());
  boost::hash_combine(seed,tuple.template get<6>());
  return seed;
}
template<class T1,class T2,class T3,class T4,class T5,class T6,class T7,class T8>
std::size_t hash_value ( const tuple<T1,T2,T3,T4,T5,T6,T7,T8>& tuple )
{
  std::size_t seed = 0;
  boost::hash_combine(seed,tuple.template get<0>());
  boost::hash_combine(seed,tuple.template get<1>());
  boost::hash_combine(seed,tuple.template get<2>());
  boost::hash_combine(seed,tuple.template get<3>());
  boost::hash_combine(seed,tuple.template get<4>());
  boost::hash_combine(seed,tuple.template get<5>());
  boost::hash_combine(seed,tuple.template get<6>());
  boost::hash_combine(seed,tuple.template get<7>());
  return seed;
}

template<class T1,class T2,class T3,class T4,class T5,class T6,class T7,class T8,class T9>
std::size_t hash_value ( const tuple<T1,T2,T3,T4,T5,T6,T7,T8,T9>& tuple )
{
  std::size_t seed = 0;
  boost::hash_combine(seed,tuple.template get<0>());
  boost::hash_combine(seed,tuple.template get<1>());
  boost::hash_combine(seed,tuple.template get<2>());
  boost::hash_combine(seed,tuple.template get<3>());
  boost::hash_combine(seed,tuple.template get<4>());
  boost::hash_combine(seed,tuple.template get<5>());
  boost::hash_combine(seed,tuple.template get<6>());
  boost::hash_combine(seed,tuple.template get<7>());
  boost::hash_combine(seed,tuple.template get<8>());
  return seed;
}

template<class T1,class T2,class T3,class T4,class T5,class T6,class T7,class T8,class T9,class T10>
std::size_t hash_value ( const tuple<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10>& tuple )
{
  std::size_t seed = 0;
  boost::hash_combine(seed,tuple.template get<0>());
  boost::hash_combine(seed,tuple.template get<1>());
  boost::hash_combine(seed,tuple.template get<2>());
  boost::hash_combine(seed,tuple.template get<3>());
  boost::hash_combine(seed,tuple.template get<4>());
  boost::hash_combine(seed,tuple.template get<5>());
  boost::hash_combine(seed,tuple.template get<6>());
  boost::hash_combine(seed,tuple.template get<7>());
  boost::hash_combine(seed,tuple.template get<8>());
  boost::hash_combine(seed,tuple.template get<9>());
  return seed;
}

}
}

#endif

