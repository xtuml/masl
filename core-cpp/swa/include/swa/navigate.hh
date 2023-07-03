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

#ifndef SWA_navigate_HH
#define SWA_navigate_HH

#include "combine_collection.hh"
#include "Bag.hh"
#include "Set.hh"
#include <boost/bind/bind.hpp>

namespace SWA
{
  using namespace boost::placeholders;

  template<class DestObj, class SrcObj, class Navigator>
  ObjectPtr<DestObj> navigate_one ( const SrcObj* src, Navigator navigator )
  {
    return src?navigator(src):ObjectPtr<DestObj>();
  }

  template<class DestObj, class SrcObj, class Navigator>
  ObjectPtr<DestObj> navigate_one ( const ObjectPtr<SrcObj>& src, Navigator navigator )
  {
    return src?navigator(src.get()):ObjectPtr<DestObj>();
  }

  template<class DestObj, class SrcObj, class Navigator>
  Set<ObjectPtr<DestObj> > navigate_one ( const Set<ObjectPtr<SrcObj> >& src, Navigator navigator )
  {
    Set<ObjectPtr<DestObj> > result;
    result.reserve(src.size());
    for ( typename Set<ObjectPtr<SrcObj> >::const_iterator it = src.begin(), end = src.end(); it != end; ++it )
    {
      if ( *it )
      {
        ObjectPtr<DestObj> res = navigator(it->get());
        if ( res ) result+= res;
      }
    }
    result.forceUnique();
    return result;
  }

  template<class DestObj, class SrcObj, template <class T> class SrcColl, class Navigator>
  Bag<ObjectPtr<DestObj> > navigate_one_bag ( const SrcColl<ObjectPtr<SrcObj> >& src, Navigator navigator )
  {
    Bag<ObjectPtr<DestObj> > result;
    result.reserve(src.size());
    for ( typename SrcColl<ObjectPtr<SrcObj> >::const_iterator it = src.begin(), end = src.end(); it != end; ++it )
    {
      if ( *it )
      {
        ObjectPtr<DestObj> res = navigator(it->get());
        if ( res ) result += res;
      }
    }
    return result;
  }

  template<class DestObj, class SrcObj, class Navigator>
  Set<ObjectPtr<DestObj> > navigate_many ( const SrcObj* src, Navigator navigator )
  {
    return src?navigator(src):Set<ObjectPtr<DestObj> >();
  }

  template<class DestObj, class SrcObj, class Navigator>
  Set<ObjectPtr<DestObj> > navigate_many ( const ObjectPtr<SrcObj>& src, Navigator navigator )
  {
    return src?navigator(src.get()):Set<ObjectPtr<DestObj> >();
  }

  template<class DestObj, class SrcObj, class Navigator>
  Set<ObjectPtr<DestObj> > navigate_many ( const Set<ObjectPtr<SrcObj> >& src, Navigator navigator )
  {
    Set<ObjectPtr<DestObj> > result = combine_collection<Set, ObjectPtr<DestObj> >(src, boost::bind(navigator,boost::bind(&ObjectPtr<SrcObj>::get,_1)));
    result.forceUnique();
    return result;
  }

  template<class DestObj, class SrcObj, template <class T> class SrcColl, class Navigator>
  Bag<ObjectPtr<DestObj> > navigate_many_bag ( const SrcColl<ObjectPtr<SrcObj> >& src, Navigator navigator )
  {
    return combine_collection<Set, ObjectPtr<DestObj> >(src, boost::bind(navigator,boost::bind(&ObjectPtr<SrcObj>::get,_1)));
  }

}

#endif
