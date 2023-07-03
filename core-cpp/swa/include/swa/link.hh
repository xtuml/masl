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

#ifndef SWA_link_HH
#define SWA_link_HH

#include "Bag.hh"
#include "Set.hh"
#include "combine_collection.hh"
#include <boost/bind/bind.hpp>

namespace SWA
{
  using namespace boost::placeholders;

    // unlink all collection non associative
  template<class LhsObj, template <class T> class LhsColl>
  void unlink ( const LhsColl<ObjectPtr<LhsObj> >& lhs, 
                void (LhsObj::*                    unlinker)() )
  {
    for ( typename LhsColl<ObjectPtr<LhsObj> >::const_iterator it = lhs.begin(), end = lhs.end(); it != end; ++it )
    {
      (it->get()->*unlinker)();
    }
  }


  // link collection with collection to many
  template<class LhsObj, template <class T> class LhsColl, class RhsObj, template <class T> class RhsColl, class AssocObj>
  Set<ObjectPtr<AssocObj> > link ( const LhsColl<ObjectPtr<LhsObj> >&   lhs, 
                                   const RhsColl<ObjectPtr<RhsObj> >&   rhs, 
                                   Set<ObjectPtr<AssocObj> > (LhsObj::* linker)( const Bag<ObjectPtr<RhsObj> >& ) )
  {
    Set<ObjectPtr<AssocObj> > result = combine_collection<Set, ObjectPtr<AssocObj> >(lhs, boost::bind(linker,boost::bind(&ObjectPtr<LhsObj>::get,_1),rhs));
    result.forceUnique();
    return result;
  }

  // unlink collection with collection to many
  template<class LhsObj, template <class T> class LhsColl, class RhsObj, template <class T> class RhsColl, class AssocObj>
  Set<ObjectPtr<AssocObj> > unlink ( const LhsColl<ObjectPtr<LhsObj> >&   lhs, 
                                     const RhsColl<ObjectPtr<RhsObj> >&   rhs, 
                                     Set<ObjectPtr<AssocObj> > (LhsObj::* unlinker)( const Bag<ObjectPtr<RhsObj> >& ) )
  {
    Set<ObjectPtr<AssocObj> > result = combine_collection<Set, ObjectPtr<AssocObj> >(lhs, boost::bind(unlinker,boost::bind(&ObjectPtr<LhsObj>::get,_1),rhs));
    result.forceUnique();
    return result;
  }


  // unlink all collection to one
  template<class LhsObj, template <class T> class LhsColl, class AssocObj>
  Set<ObjectPtr<AssocObj> > unlink ( const LhsColl<ObjectPtr<LhsObj> >& lhs, 
                                     ObjectPtr<AssocObj> (LhsObj::*     unlinker)() )
  {
    Set<ObjectPtr<AssocObj> > result;
    result.reserve(lhs.size());
    for ( typename LhsColl<ObjectPtr<LhsObj> >::const_iterator it = lhs.begin(), end = lhs.end(); it != end; ++it )
    {
      result += (it->get()->*unlinker)();
    }
    result.forceUnique();
    return result;
  }

  // unlink all collection to many
  template<class LhsObj, template <class T> class LhsColl, class AssocObj>
  Set<ObjectPtr<AssocObj> > unlink ( const LhsColl<ObjectPtr<LhsObj> >&   lhs, 
                                     Set<ObjectPtr<AssocObj> > (LhsObj::* unlinker)() )
  {
    Set<ObjectPtr<AssocObj> > result = combine_collection<Set, ObjectPtr<AssocObj> >(lhs, boost::bind(unlinker,boost::bind(&ObjectPtr<LhsObj>::get,_1)));
    result.forceUnique();
    return result;
  }



}

#endif
