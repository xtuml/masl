/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#pragma once

#include <boost/operators.hpp>

namespace transient {

    template <class InnerIterator>
    class PairSecondIterator
        : public std::iterator<std::input_iterator_tag, typename InnerIterator::value_type::second_type>,
          public boost::
              input_iteratable<PairSecondIterator<InnerIterator>, typename InnerIterator::value_type::second_type *> {
      public:
        PairSecondIterator(const InnerIterator &pos)
            : pos(pos) {}
        const typename InnerIterator::value_type::second_type &operator*() const {
            return pos->second;
        }
        PairSecondIterator &operator++() {
            ++pos;
            return *this;
        }
        bool operator==(const PairSecondIterator &rhs) const {
            return pos == rhs.pos;
        }

      private:
        InnerIterator pos;
    };

} // namespace transient
