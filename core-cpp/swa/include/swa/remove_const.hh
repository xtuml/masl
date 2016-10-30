//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef BOOST_REMOVE_CONST_HH
#define BOOST_REMOVE_CONST_HH

#include "boost/type_traits.hpp"

namespace boost
{
  template<>
  template<class T1,class T2>
  struct remove_const< std::pair<T1,T2> >
  {
    typedef std::pair<typename remove_const<T1>::type,
                      typename remove_const<T2>::type> type;
  };
}

#endif
