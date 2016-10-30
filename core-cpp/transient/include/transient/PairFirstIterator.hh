//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef TRANSIENT_PairFirstIterator_HH
#define TRANSIENT_PairFirstIterator_HH

#include "boost/operators.hpp"

namespace transient
{

  template<class InnerIterator>
  class PairFirstIterator : public std::iterator<std::input_iterator_tag, typename InnerIterator::value_type::first_type>, public boost::input_iteratable<PairFirstIterator<InnerIterator>,typename InnerIterator::value_type::first_type*>
  {
    public:
      PairFirstIterator ( const InnerIterator& pos ) : pos(pos) {}
      const typename InnerIterator::value_type::first_type& operator*() const { return pos->first; }
      PairFirstIterator& operator++()  { ++pos; return *this; }
      bool operator== ( const PairFirstIterator& rhs ) const { return pos == rhs.pos; }

    private:
      InnerIterator pos;
  };

}

#endif
