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

#ifndef SWA_correlate_HH
#define SWA_correlate_HH

#include "Bag.hh"
#include "Set.hh"
#include "combine_collection.hh"
#include <iostream>
#include <boost/bind/bind.hpp>

namespace SWA
{
  using namespace boost::placeholders;

  // Correlate single instances
  template<class LhsObj, class RhsObj, class AssocObj>
  ObjectPtr<AssocObj> correlate_instance ( const LhsObj*                  lhs, 
                                           const ObjectPtr<RhsObj>&       rhs, 
                                           ObjectPtr<AssocObj> (LhsObj::* correlator)(const ObjectPtr<RhsObj>&) const )
  {
    return lhs&&rhs?(lhs->*correlator)(rhs):ObjectPtr<AssocObj>();
  }

  // Correlate single instances
  template<class LhsObj, class RhsObj, class AssocObj>
  ObjectPtr<AssocObj> correlate_instance ( const ObjectPtr<LhsObj>& lhs, 
                                           const ObjectPtr<RhsObj>& rhs, 
                                           ObjectPtr<AssocObj> (LhsObj::* correlator)(const ObjectPtr<RhsObj>&) const )
  {
    return lhs&&rhs?(lhs.get()->*correlator)(rhs):ObjectPtr<AssocObj>();
  }

  // Correlate single instance with bag. We don't put any ordering constraints on the instance based correlator, so result is a bag 
  template<class LhsObj, class RhsObj, class AssocObj>
  Bag<ObjectPtr<AssocObj> > correlate_bag ( const LhsObj*                             lhs, 
                                            const Bag<ObjectPtr<RhsObj> >&       rhs, 
                                            Bag<ObjectPtr<AssocObj> > (LhsObj::* correlator)( const Bag<ObjectPtr<RhsObj> >& ) const )
  {
    return lhs?(lhs->*correlator)(rhs):Bag<ObjectPtr<AssocObj> >();
  }

  // Correlate single instance with set 
  template<class LhsObj, class RhsObj, class AssocObj>
  Set<ObjectPtr<AssocObj> > correlate_set ( const LhsObj*                             lhs, 
                                            const Set<ObjectPtr<RhsObj> >&            rhs, 
                                            Bag<ObjectPtr<AssocObj> > (LhsObj::* correlator)( const Bag<ObjectPtr<RhsObj> >& ) const )
  {
    if ( lhs )
    {
      Set<ObjectPtr<AssocObj> > result = (lhs->*correlator)(rhs);
      result.forceUnique();
      return result;
    }
    else
    {
      return Set<ObjectPtr<AssocObj> >();
    }
  }

  // Correlate single instance with sequence. We don't put any ordering constraints on the instance based correlator, so result is a bag 
  template<class LhsObj, class RhsObj, template <class T> class RhsColl, class AssocObj>
  Bag<ObjectPtr<AssocObj> > correlate_bag ( const ObjectPtr<LhsObj>&                  lhs,  
                                                 const RhsColl<ObjectPtr<RhsObj> >&        rhs,  
                                                 Bag<ObjectPtr<AssocObj> > (LhsObj::* correlator)( const Bag<ObjectPtr<RhsObj> >& ) const )
  {
    return lhs?(lhs.get()->*correlator)(rhs):Bag<ObjectPtr<AssocObj> >();
  }


  // Correlate single instance with set 
  template<class LhsObj, class RhsObj, class AssocObj>
  Set<ObjectPtr<AssocObj> > correlate_set ( const ObjectPtr<LhsObj>&                  lhs, 
                                            const Set<ObjectPtr<RhsObj> >&            rhs, 
                                            Bag<ObjectPtr<AssocObj> > (LhsObj::* correlator)( const Bag<ObjectPtr<RhsObj> >& ) const )
  {  
    if ( lhs )
    {
      Set<ObjectPtr<AssocObj> > result = (lhs.get()->*correlator)(rhs);
      result.forceUnique();
      return result;
    }
    else
    {
      return Set<ObjectPtr<AssocObj> >();
    }
  }

  // Correlate collection with collection. No ordering guarantees, so result is a bag.
  template<class LhsObj, template <class T> class LhsColl, class RhsObj, template <class T> class RhsColl, class AssocObj>
  Bag<ObjectPtr<AssocObj> > correlate_bag ( const LhsColl<ObjectPtr<LhsObj> >& lhs, 
                                                 const RhsColl<ObjectPtr<RhsObj> >& rhs, 
                                                 Bag<ObjectPtr<AssocObj> > (LhsObj::* correlator)( const Bag<ObjectPtr<RhsObj> >& ) const )
  {  
    return combine_collection<Set, ObjectPtr<AssocObj> >(lhs, boost::bind(correlator,boost::bind(&ObjectPtr<LhsObj>::get,_1),rhs));
  }

  // Correlate set with set 
  template<class LhsObj, class RhsObj, class AssocObj>
  Set<ObjectPtr<AssocObj> > correlate_set ( const Set<ObjectPtr<LhsObj> >& lhs, 
                                            const Set<ObjectPtr<RhsObj> >& rhs, 
                                            Bag<ObjectPtr<AssocObj> > (LhsObj::* correlator)( const Bag<ObjectPtr<RhsObj> >& ) const )
  {
    Set<ObjectPtr<AssocObj> > result = combine_collection<Set, ObjectPtr<AssocObj> >(lhs, boost::bind(correlator,boost::bind(&ObjectPtr<LhsObj>::get,_1),rhs));
    result.forceUnique();
    return result;
  }

}

#endif
