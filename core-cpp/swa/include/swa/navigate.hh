//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_navigate_HH
#define SWA_navigate_HH

#include "combine_collection.hh"
#include "Bag.hh"
#include "Set.hh"
#include <boost/bind.hpp>

namespace SWA
{

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
