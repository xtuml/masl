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

#ifndef SWA_combine_collection_HH
#define SWA_combine_collection_HH

#include "Bag.hh"

namespace SWA
{

  template<template <class> class DestColl, class Dest, class SrcColl, class Function>
  Bag<Dest> combine_collection ( const SrcColl& src, Function function )
  {
    Bag<Dest> result;
    for ( typename SrcColl::const_iterator it = src.begin(), end = src.end(); it != end; ++it )
    {
      DestColl<Dest> res = function(*it);

      // Normal (ie gnu's) reservation will only be enough for 
      // one lot of extra elements or double size. On the 
      // assumption that remaining of the results will be 
      // similar to the ones done so far preallocate enough 
      // memory for the remaining results at the average size 
      // so far (x2 for luck!). This also prevents the normal 
      // reallocation doubling the size when we know we are 
      // actually near the end and unlikely to need that much. 
      // If the first result is abnormally large, then we 
      // may end up with far too much allocated, but that is a 
      // risk it is probably worth taking. 
      if ( result.capacity() < result.size() + res.size() )
      {
        size_t done = std::distance(src.begin(),it);
        size_t remaining = std::distance(it,src.end());
        size_t averageSize = (result.size()+res.size())/(done+1);
        result.reserve(result.size() + std::max(averageSize*remaining,res.size())*2);
      }
      result+= res;
    }
    return result;
  }

}
#endif
