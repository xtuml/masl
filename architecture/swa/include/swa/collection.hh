/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_collection_HH
#define SWA_collection_HH

#include "ProgramError.hh"
#include <functional>

namespace SWA {
    template <class T>
    class Set;
    template <class T>
    class Sequence;
    template <class T>
    class ObjectPtr;

    template <class T>
    bool forceTrue(const T &val) {
        return true;
    }

    template <class InputIterator, class OutputIterator, class Predicate>
    OutputIterator copy_if(InputIterator begin, InputIterator end, OutputIterator destBegin, Predicate p) {
        while (begin != end) {
            if (p(*begin))
                *destBegin++ = *begin;
            ++begin;
        }
        return destBegin;
    }

    template <class Iterator, class Predicate>
    typename Iterator::value_type find_one(Iterator begin, Iterator end, Predicate predicate) {
        Iterator it = std::find_if(begin, end, predicate);
        return (it == end ? typename Iterator::value_type() : *it);
    }

    template <class Iterator>
    typename Iterator::value_type find_one(Iterator begin, Iterator end) {
        if (begin == end) {
            return typename Iterator::value_type();
        } else {
            return *begin;
        }
    }

    template <class Iterator, class Predicate>
    typename Iterator::value_type find_only(Iterator begin, Iterator end, Predicate predicate) {
        typename Iterator::value_type result;
        Iterator it = std::find_if(begin, end, predicate);
        if (it != end) {
            result = *it++;
            if (std::find_if(it, end, predicate) != end) {
                throw SWA::ProgramError("Multiple occurences in find_only");
            }
        }
        return result;
    }

    template <class Iterator>
    typename Iterator::value_type find_only(Iterator begin, Iterator end) {
        if (begin == end) {
            return typename Iterator::value_type();
        } else {
            typename Iterator::value_type result = *begin++;
            if (begin != end) {
                throw SWA::ProgramError("Multiple occurences in find_only");
            }
            return result;
        }
    }

    template <class Iterator, class BinaryPredicate>
    Sequence<typename Iterator::value_type> ordered_by(Iterator begin, Iterator end, BinaryPredicate predicate) {
        Sequence<typename Iterator::value_type> result(begin, end);
        std::sort(result.begin(), result.end(), predicate);
        return result;
    }

    template <class Iterator, class BinaryPredicate>
    Sequence<typename Iterator::value_type>
    reverse_ordered_by(Iterator begin, Iterator end, BinaryPredicate predicate) {
        Sequence<typename Iterator::value_type> result(begin, end);
        std::sort(result.rbegin(), result.rend(), predicate);
        return result;
    }

    template <class Iterator>
    Sequence<typename Iterator::value_type> ordered_by(Iterator begin, Iterator end) {
        Sequence<typename Iterator::value_type> result(begin, end);
        std::sort(result.begin(), result.end());
        return result;
    }

    template <class Iterator>
    Sequence<typename Iterator::value_type> reverse_ordered_by(Iterator begin, Iterator end) {
        Sequence<typename Iterator::value_type> result(begin, end);
        std::sort(result.rbegin(), result.rend());
        return result;
    }

} // namespace SWA
#endif
