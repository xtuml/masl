/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_Set_HH
#define SWA_Set_HH

#include "ObjectPtr.hh"
#include "boost/operators.hpp"
#include "collection.hh"
#include <algorithm>
#include <boost/functional/hash.hpp>
#include <nlohmann/json.hpp>
#include <numeric>
#include <set>
#include <unordered_set>
#include <vector>

namespace SWA {
    template <class T>
    class Sequence;
    template <class T>
    class Bag;

    template <class T>
    class Set : private boost::less_than_comparable<
                    Set<T>,
                    boost::equality_comparable<
                        Set<T>,
                        boost::less_than_comparable<
                            Set<T>,
                            Bag<T>,
                            boost::equality_comparable<
                                Set<T>,
                                Bag<T>,
                                boost::less_than_comparable<
                                    Set<T>,
                                    Sequence<T>,
                                    boost::equality_comparable<Set<T>, Sequence<T>>>>>>> {
      public:
        //*********************************************************
        //       STL Container Compatibility Methods
        // --------------------------------------------------------
        // These methods implement the STL contract for the
        // relevant container type.
        //*********************************************************

        // Container methods
        typedef std::vector<T> Container;
        typedef typename Container::value_type value_type;

        typedef typename Container::reference reference;
        typedef typename Container::const_reference const_reference;

        typedef typename Container::pointer pointer;
        typedef typename Container::const_pointer const_pointer;

        typedef typename Container::iterator iterator;
        typedef typename Container::const_iterator const_iterator;

        typedef typename Container::difference_type difference_type;
        typedef typename Container::size_type size_type;

        Set(const Set &rhs)
            : data(rhs.data), unique(rhs.unique), sorted(rhs.sorted) {}
        Set &operator=(const Set &rhs) {
            if (this != &rhs) {
                data = rhs.data;
                unique = rhs.unique;
                sorted = rhs.sorted;
            }
            return *this;
        }

        void clear() {
            Container().swap(data);
            unique = true;
            sorted = true;
        }

        void reserve(const size_type size) {
            data.reserve(size);
        }
        void swap(Set &rhs) {
            data.swap(rhs.data);
            std::swap(unique, rhs.unique);
            std::swap(sorted, rhs.sorted);
        }
        void resize(const size_type size) {
            data.resize(size);
        }

        iterator begin() {
            rationalise();
            return data.begin();
        }
        const_iterator begin() const {
            rationalise();
            return data.begin();
        }

        iterator end() {
            rationalise();
            return data.end();
        }
        const_iterator end() const {
            rationalise();
            return data.end();
        }

        size_type capacity() const {
            return data.capacity();
        }
        size_type max_size() const {
            return data.max_size();
        }
        size_type size() const {
            rationalise();
            return data.size();
        }
        bool empty() const {
            return data.empty();
        }

        // Forward Container methods - other comparisons via boost superclasses
        bool operator==(const Set &rhs) const {
            rationalise(true);
            rhs.rationalise(true);
            return data == rhs.data;
        }
        bool operator==(const Bag<T> &rhs) const {
            return *this == Set(rhs);
        }
        bool operator==(const Sequence<T> &rhs) const {
            return *this == Set(rhs);
        }

        bool operator<(const Set &rhs) const {
            rationalise(true);
            rhs.rationalise(true);
            return data < rhs.data;
        }
        bool operator<(const Bag<T> &rhs) const {
            return *this < Set(rhs);
        }
        bool operator<(const Sequence<T> &rhs) const {
            return *this < Set(rhs);
        }

        // Reversible Container methods
        typedef typename Container::reverse_iterator reverse_iterator;
        typedef typename Container::const_reverse_iterator const_reverse_iterator;

        reverse_iterator rbegin() {
            rationalise();
            return data.rbegin();
        }
        const_reverse_iterator rbegin() const {
            rationalise();
            return data.rbegin();
        }

        reverse_iterator rend() {
            rationalise();
            return data.rend();
        }
        const_reverse_iterator rend() const {
            rationalise();
            return data.rend();
        }

        Set()
            : data(), unique(true), sorted(true) {}

        template <class It>
        Set(It i, It j, bool unique = false, bool sorted = false)
            : data(i, j), unique(unique), sorted(sorted) {}

        template <class It>
        void insert(It i, It j) {
            unique = false;
            sorted = false;
            data.insert(data.end(), i, j);
        }

        iterator insert(iterator pos, const T &rhs) {
            unique = false;
            sorted = false;
            return data.insert(pos, rhs);
        }

        //*********************************************************
        //       MASL Set extension methods
        // --------------------------------------------------------
        // These methods provide extra functionality to support
        // the masl language features with respect to Sets.
        //*********************************************************

        // Construct from underlying container type
        Set(const Container &rhs)
            : data(rhs), unique(false), sorted(false) {}

        // Construct frm stl container
        template <class T2>
        explicit Set(const std::vector<T2> &rhs)
            : data(rhs.begin(), rhs.end()), unique(false), sorted(false) {}

        template <class T2>
        explicit Set(const std::set<T2> &rhs)
            : data(rhs.begin(), rhs.end()), unique(true), sorted(true) {}

        template <class T2>
        explicit Set(const std::multiset<T2> &rhs)
            : data(rhs.begin(), rhs.end()), unique(false), sorted(true) {}

        template <class T2, typename Hash>
        explicit Set(const std::unordered_set<T2, Hash> &rhs)
            : data(rhs.begin(), rhs.end()), unique(true), sorted(false) {}

        template <class T2, typename Hash>
        explicit Set(const std::unordered_multiset<T2, Hash> &rhs)
            : data(rhs.begin(), rhs.end()), unique(false), sorted(false) {}

        // Construct from collection
        template <class T2>
        Set(const Set<T2> &rhs)
            : data(rhs.begin(), rhs.end()), unique(rhs.isUnique()), sorted(rhs.isSorted()) {}

        template <class T2>
        Set &operator=(const Set<T2> &rhs) {
            data.assign(rhs.begin(), rhs.end());
            unique = rhs.isUnique();
            sorted = rhs.isSorted();
            return *this;
        }

        Set(const Sequence<T> &rhs)
            : data(rhs.getData()), unique(rhs.isUnique()), sorted(rhs.isSorted()) {}

        template <class T2>
        Set(const Sequence<T2> &rhs)
            : data(rhs.begin(), rhs.end()), unique(rhs.isUnique()), sorted(rhs.isSorted()) {}

        Set &operator=(const Sequence<T> &rhs) {
            data = rhs.getData();
            unique = rhs.isUnique();
            sorted = rhs.isSorted();
            return *this;
        }

        template <class T2>
        Set &operator=(const Sequence<T2> &rhs) {
            data.assign(rhs.begin(), rhs.end());
            unique = rhs.isUnique();
            sorted = rhs.isSorted();
            return *this;
        }

        Set(const Bag<T> &rhs)
            : data(rhs.getData()), unique(rhs.isUnique()), sorted(rhs.isSorted()) {}

        template <class T2>
        Set(const Bag<T2> &rhs)
            : data(rhs.begin(), rhs.end()), unique(rhs.isUnique()), sorted(rhs.isSorted()) {}

        Set &operator=(const Bag<T> &rhs) {
            data = rhs.getData();
            unique = rhs.isUnique();
            sorted = rhs.isSorted();
            return *this;
        }

        template <class T2>
        Set &operator=(const Bag<T2> &rhs) {
            data.assign(rhs.begin(), rhs.end());
            unique = rhs.isUnique();
            sorted = rhs.isSorted();
            return *this;
        }

        // Construct from single element
        explicit Set(const T &element)
            : data(NullCheck<T>::isNull(element) ? 0 : 1, element), unique(true), sorted(true) {}

        template <class T2>
        explicit Set(const ObjectPtr<T2> &element)
            : data(element ? 1 : 0, element), unique(true), sorted(true) {}

        // Add operator. Note that this add to the existing Set
        Set &operator+=(const T &rhs) {
            if (!NullCheck<T>::isNull(rhs)) {
                unique = false;
                sorted = false;
                data.push_back(rhs);
            }
            return *this;
        }

        template <class T2>
        Set &operator+=(const ObjectPtr<T2> &rhs) {
            if (rhs) {
                unique = false;
                sorted = false;
                data.push_back(rhs);
            }
            return *this;
        }

        template <class T2>
        Set &operator+=(const Sequence<T2> &rhs) {
            unique = false;
            sorted = false;
            data.insert(data.end(), rhs.begin(), rhs.end());
            return *this;
        }

        template <class T2>
        Set &operator+=(const Set<T2> &rhs) {
            unique = false;
            sorted = false;
            data.insert(data.end(), rhs.begin(), rhs.end());
            return *this;
        }

        template <class T2>
        Set &operator+=(const Bag<T2> &rhs) {
            unique = false;
            sorted = false;
            data.insert(data.end(), rhs.begin(), rhs.end());
            return *this;
        }

        size_type first() const {
            return 1;
        }
        size_type last() const {
            rationalise();
            return size();
        }

        const Container &getData() const {
            rationalise();
            return data;
        }

        void forceUnique() const {
            unique = true;
        }
        void forceSorted() const {
            sorted = true;
        }

        bool isUnique() const {
            return unique;
        }
        bool isSorted() const {
            return sorted;
        }

        template <class T2>
        void erase(const T2 &rhs) {
            rationalise();
            data.erase(std::remove(data.begin(), data.end(), rhs), data.end());
        }

        void insert(const T &rhs) {
            *this += rhs;
        }

        void push_back(const T &rhs) {
            *this += rhs;
        }

        std::back_insert_iterator<Set<T>> inserter() {
            return std::back_inserter(*this);
        }

        void deleteInstance() {
            for (iterator it = begin(), endIt = end(); it != endIt; ++it) {
                it->deleteInstance();
            }
            clear();
        }

        // Find functions
        const Set<T> &find() const {
            return *this;
        }

        // Find functions
        const_iterator find(const T &value) const {
            rationalise();
            return std::find(begin(), end(), value);
        }

        template <class Predicate>
        Set<T> find(Predicate predicate) const {
            Set<T> result;
            SWA::copy_if(data.begin(), data.end(), result.inserter(), predicate);
            if (unique)
                result.forceUnique();
            return result;
        }

        template <class Predicate>
        T find_one(Predicate predicate) const {
            return SWA::find_one(data.begin(), data.end(), predicate);
        }

        T find_one() const {
            return SWA::find_one(data.begin(), data.end());
        }

        template <class Predicate>
        T find_only(Predicate predicate) const {
            value_type result;
            iterator it = std::find_if(data.begin(), data.end(), predicate);
            if (it != data.end()) {
                result = *it++;

                while (it != data.end()) {
                    it = std::find_if(it, data.end(), predicate);

                    if (it != data.end() && *it != result) {
                        throw SWA::ProgramError("Multiple occurences in find_only");
                    }
                }
            }
            return result;
        }

        T find_only() const {
            if (!data.size())
                return value_type();

            if (unique) {
                if (data.size() > 1) {
                    throw SWA::ProgramError("Multiple occurences in find_only");
                }
                return data.size() ? data.front() : value_type();
            } else {
                // Can avoid sorting here, making it O(n) rather than O(nlogn)
                const_iterator it = data.begin();
                value_type result = *it++;

                while (it != data.end()) {
                    if (*it++ != result) {
                        throw SWA::ProgramError("Multiple occurences in find_only");
                    }
                }
            }
        }

        T any() const {
            if (!data.size())
                throw ProgramError("Attempt to get element from empty set");
            return data[0];
        }

        Set<T> any(size_type count) const {
            if (count >= data.size()) {
                // Need more than we have, so just return what we can
                return *this;
            } else if (unique) {
                return Set<T>(data.begin(), data.begin() + count, unique, sorted);
            } else {
                // add one at a time until we have enough using an
                // unordered set to remove dups.
                std::unordered_set<T, boost::hash<T>> result;
                for (typename Container::const_iterator it = data.begin(); it != data.end() && result.size() < count;
                     ++it) {
                    result.insert(*it);
                }
                return Set<T>(result);
            }
        }

        // Ordering functions
        Sequence<T> ordered_by() const {
            Sequence<T> result = SWA::ordered_by(begin(), end());
            if (unique)
                result.forceUnique();
            return result;
        }

        template <class Predicate>
        Sequence<T> ordered_by(Predicate predicate) const {
            Sequence<T> result = SWA::ordered_by(begin(), end(), predicate);
            if (unique)
                result.forceUnique();
            return result;
        }

        Sequence<T> reverse_ordered_by() const {
            Sequence<T> result = SWA::reverse_ordered_by(begin(), end());
            if (unique)
                result.forceUnique();
            return result;
        }

        template <class Predicate>
        Sequence<T> reverse_ordered_by(Predicate predicate) const {
            Sequence<T> result = SWA::reverse_ordered_by(begin(), end(), predicate);
            if (unique)
                result.forceUnique();
            return result;
        }

        // Union operator. Returns a new set.
        Set<T> set_union(const Set &rhs) const {
            rationalise(true);
            rhs.rationalise(true);
            Set result;
            std::set_union(begin(), end(), rhs.begin(), rhs.end(), result.inserter());
            result.forceSorted();
            result.forceUnique();
            return result;
        }

        // Disunion operator. Returns a new set.
        Set set_disunion(const Set &rhs) const {
            rationalise(true);
            rhs.rationalise(true);
            Set result;
            std::set_symmetric_difference(begin(), end(), rhs.begin(), rhs.end(), result.inserter());
            result.forceSorted();
            result.forceUnique();
            return result;
        }

        // Intersection operator. Returns a new set.
        Set set_intersection(const Set &rhs) const {
            rationalise(true);
            rhs.rationalise(true);
            Set result;
            std::set_intersection(begin(), end(), rhs.begin(), rhs.end(), result.inserter());
            result.forceSorted();
            result.forceUnique();
            return result;
        }

        // Not In operator. Returns a new set.
        Set set_not_in(const Set &rhs) const {
            rationalise(true);
            rhs.rationalise(true);
            Set result;
            std::set_difference(begin(), end(), rhs.begin(), rhs.end(), result.inserter());
            result.forceSorted();
            result.forceUnique();
            return result;
        }

        friend void to_json(nlohmann::json &json, const SWA::Set<T> v) {
            v.rationalise();
            json = v.data;
        }

        friend void from_json(const nlohmann::json &json, SWA::Set<T> &v) {
            json.get_to(v.data);
            v.unique = false;
            v.sorted = false;
        }

      private:
        mutable Container data;
        mutable bool unique;
        mutable bool sorted;

        void rationalise(bool forceSort = false) const {
            if (!unique || (!sorted && forceSort)) {
                if (!sorted) {
                    std::sort(data.begin(), data.end());
                    sorted = true;
                }
                data.erase(std::unique(data.begin(), data.end()), data.end());
                unique = true;
            }
        }

        template <class T2>
        friend class Set;
    };

    template <class T>
    ::std::ostream &operator<<(::std::ostream &stream, const Set<T> &obj) {
        return stream << "Set size " << obj.size();
    }

    // Concatenation operator. Returns a new sequence.
    template <class T, class T2>
    Set<T> operator+(Set<T> lhs, const T2 &rhs) {
        return lhs += rhs;
    }

    template <class T>
    inline std::size_t hash_value(const Set<T> &value) {
        return boost::hash_range(value.begin(), value.end());
    }

} // namespace SWA

#endif
