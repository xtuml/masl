//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef TRANSIENT_PairSecondIterator_HH
#define TRANSIENT_PairSecondIterator_HH

#include "boost/operators.hpp"

namespace transient
{

  template<class InnerIterator>
  class PairSecondIterator : public std::iterator<std::input_iterator_tag, typename InnerIterator::value_type::second_type>, public boost::input_iteratable<PairSecondIterator<InnerIterator>,typename InnerIterator::value_type::second_type*>
  {
    public:
      PairSecondIterator ( const InnerIterator& pos ) : pos(pos) {}
      const typename InnerIterator::value_type::second_type& operator*() const { return pos->second; }
      PairSecondIterator& operator++()  { ++pos; return *this; }
      bool operator== ( const PairSecondIterator& rhs ) const { return pos == rhs.pos; }

    private:
      InnerIterator pos;
  };

}

#endif
