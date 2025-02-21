/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_Sequence_HH
#define SWA_Sequence_HH

#include "ObjectPtr.hh"
#include "boost/operators.hpp"
#include "collection.hh"
#include <algorithm>
#include <boost/functional/hash.hpp>
#include <unordered_set>
#include <nlohmann/json.hpp>
#include <set>
#include <vector>

namespace SWA {
template <class T> class Set;
template <class T> class Bag;

template <class T>
class Sequence : private boost::less_than_comparable<
                     Sequence<T>, boost::equality_comparable<Sequence<T>>> {
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

    Sequence(const Sequence &rhs)
        : data(rhs.getData()), unique(rhs.isUnique()), sorted(rhs.isSorted()) {}

    Sequence &operator=(const Sequence &rhs) {
        if (this != &rhs) {
            data = rhs.getData();
            unique = rhs.isUnique();
            sorted = rhs.isSorted();
        }
        return *this;
    }

    void clear() {
        Container().swap(data);
        unique = true;
        sorted = true;
    }

    iterator begin() { return data.begin(); }
    const_iterator begin() const { return data.begin(); }

    iterator end() { return data.end(); }
    const_iterator end() const { return data.end(); }

    size_type capacity() const { return data.capacity(); }
    size_type max_size() const { return data.max_size(); }
    size_type size() const { return data.size(); }
    bool empty() const { return data.empty(); }

    void reserve(const size_type size) { data.reserve(size); }
    void swap(Sequence &rhs) {
        data.swap(rhs.data);
        std::swap(unique, rhs.unique);
        std::swap(sorted, rhs.sorted);
    }
    void resize(const size_type size) { data.resize(size); }

    // Forward Container methods - other comparisons via boost superclasses
    bool operator==(const Sequence &rhs) const { return data == rhs.data; }

    bool operator<(const Sequence &rhs) const { return data < rhs.data; }

    // Reversible Container methods
    typedef typename Container::reverse_iterator reverse_iterator;
    typedef typename Container::const_reverse_iterator const_reverse_iterator;

    reverse_iterator rbegin() { return data.rbegin(); }
    const_reverse_iterator rbegin() const { return data.rbegin(); }

    reverse_iterator rend() { return data.rend(); }
    const_reverse_iterator rend() const { return data.rend(); }

    Sequence() : data(), unique(true), sorted(true) {}
    template <class It>
    Sequence(It i, It j, bool unique = false, bool sorted = false)
        : data(i, j), unique(unique), sorted(sorted) {}

    template <class It> void insert(It i, It j) {
        unique = false;
        sorted = false;
        data.insert(data.end(), i, j);
    }

    iterator insert(iterator pos, const T &rhs) {
        return data.insert(pos, rhs);
    }

    void pop_back() { data.pop_back(); }

    // Random Access Container Methods
    reference operator[](size_type n) { return data[n]; }
    const_reference operator[](size_type n) const { return data[n]; }

    //*********************************************************
    //       MASL Sequence extension methods
    // --------------------------------------------------------
    // These methods provide extra functionality to support
    // the masl language features with respect to Sequences.
    //*********************************************************

    // Construct from underlying container type
    Sequence(const Container &rhs) : data(rhs), unique(false), sorted(false) {}

    // Construct frm stl container
    template <class T2>
    explicit Sequence(const std::vector<T2> &rhs)
        : data(rhs.begin(), rhs.end()), unique(false), sorted(false) {}

    template <class T2>
    explicit Sequence(const std::set<T2> &rhs)
        : data(rhs.begin(), rhs.end()), unique(true), sorted(true) {}

    template <class T2>
    explicit Sequence(const std::multiset<T2> &rhs)
        : data(rhs.begin(), rhs.end()), unique(false), sorted(true) {}

    template <class T2, typename Hash>
    explicit Sequence(const std::unordered_set<T2,Hash> &rhs)
        : data(rhs.begin(), rhs.end()), unique(true), sorted(false) {}

    template <class T2, typename Hash>
    explicit Sequence(const std::unordered_multiset<T2,Hash> &rhs)
        : data(rhs.begin(), rhs.end()), unique(false), sorted(false) {}

    // Construct from collection
    template <class T2>
    Sequence(const Sequence<T2> &rhs)
        : data(rhs.begin(), rhs.end()), unique(rhs.isUnique()),
          sorted(rhs.isSorted()) {}

    template <class T2> Sequence &operator=(const Sequence<T2> &rhs) {
        data.assign(rhs.begin(), rhs.end());
        unique = rhs.isUnique();
        sorted = rhs.isSorted();
        return *this;
    }

    Sequence(const Bag<T> &rhs)
        : data(rhs.getData()), unique(rhs.isUnique()), sorted(rhs.isSorted()) {}

    template <class T2>
    Sequence(const Bag<T2> &rhs)
        : data(rhs.begin(), rhs.end()), unique(rhs.isUnique()),
          sorted(rhs.isSorted()) {}

    Sequence &operator=(const Bag<T> &rhs) {
        data = rhs.getData();
        unique = rhs.isUnique();
        sorted = rhs.isSorted();
        return *this;
    }

    template <class T2> Sequence &operator=(const Bag<T2> &rhs) {
        data.assign(rhs.begin(), rhs.end());
        unique = rhs.isUnique();
        sorted = rhs.isSorted();
        return *this;
    }

    Sequence(const Set<T> &rhs)
        : data(rhs.getData()), unique(rhs.isUnique()), sorted(rhs.isSorted()) {}

    template <class T2>
    Sequence(const Set<T2> &rhs)
        : data(rhs.begin(), rhs.end()), unique(rhs.isUnique()),
          sorted(rhs.isSorted()) {}

    Sequence &operator=(const Set<T> &rhs) {
        data = rhs.getData();
        unique = rhs.isUnique();
        sorted = rhs.isSorted();
        return *this;
    }

    template <class T2> Sequence &operator=(const Set<T2> &rhs) {
        data.assign(rhs.begin(), rhs.end());
        unique = rhs.isUnique();
        sorted = rhs.isSorted();
        return *this;
    }

    // Construct from single element
    explicit Sequence(const T &element)
        : data(NullCheck<T>::isNull(element) ? 0 : 1, element), unique(true),
          sorted(true) {}

    template <class T2>
    explicit Sequence(const ObjectPtr<T2> &element)
        : data(element ? 1 : 0, element), unique(true), sorted(true) {}

    // Add operator. Note that this add to the existing Sequence
    Sequence &operator+=(const T &rhs) {
        if (!NullCheck<T>::isNull(rhs)) {
            unique = false;
            sorted = false;
            data.push_back(rhs);
        }
        return *this;
    }

    template <class T2> Sequence &operator+=(const ObjectPtr<T2> &rhs) {
        if (rhs) {
            unique = false;
            sorted = false;
            data.push_back(rhs);
        }
        return *this;
    }

    template <class T2> Sequence &operator+=(const Sequence<T2> &rhs) {
        unique = false;
        sorted = false;
        data.insert(data.end(), rhs.begin(), rhs.end());
        return *this;
    }

    template <class T2> Sequence &operator+=(const Set<T2> &rhs) {
        unique = false;
        sorted = false;
        data.insert(data.end(), rhs.begin(), rhs.end());
        return *this;
    }

    template <class T2> Sequence &operator+=(const Bag<T2> &rhs) {
        unique = false;
        sorted = false;
        data.insert(data.end(), rhs.begin(), rhs.end());
        return *this;
    }

    // Returns a slice. This has the potential for
    // optimisation by returning a proxy object referencing
    // the original sequence. Note that masl indexing is 1-up.
    Sequence slice(size_type start, size_type finish) const {
        checkBounds(start);
        checkBounds(finish);
        Sequence result(begin() + start - 1, begin() + finish);
        return result;
    }

    // Returns the element at the supplied index. Note that
    // masl indexing is 1-up.
    const_reference access(size_type index) const {
        checkBounds(index);
        return data[index - 1];
    }

    reference access(size_type index) {
        checkBounds(index);
        return data[index - 1];
    }

    reference accessExtend(size_type index) {
        if (index > size()) {
            data.resize(index);
        }
        checkBounds(index);
        return data[index - 1];
    }

    size_type first() const { return 1; }
    size_type last() const { return size(); }

    const Container &getData() const { return data; }

    void forceUnique() const { unique = true; }
    void forceSorted() const { sorted = true; }

    bool isUnique() const { return unique; }
    bool isSorted() const { return sorted; }

    template <class T2> void erase(const T2 &rhs) {
        data.erase(std::remove(data.begin(), data.end(), rhs), data.end());
    }

    void insert(const T &rhs) { *this += rhs; }

    void push_back(const T &rhs) { *this += rhs; }

    std::back_insert_iterator<Sequence<T>> inserter() {
        return std::back_inserter(*this);
    }

    // Find functions
    const Sequence<T> &find() const { return *this; }

    // Find functions
    const_iterator find(const T &value) const {
        return std::find(begin(), end(), value);
    }

    template <class Predicate> Sequence<T> find(Predicate predicate) const {
        Sequence<T> result;
        SWA::copy_if(data.begin(), data.end(), result.inserter(), predicate);
        if (unique)
            result.forceUnique();
        if (sorted)
            result.forceUnique();
        return result;
    }

    template <class Predicate> T find_one(Predicate predicate) const {
        return SWA::find_one(data.begin(), data.end(), predicate);
    }

    T find_one() const { return SWA::find_one(data.begin(), data.end()); }

    template <class Predicate> T find_only(Predicate predicate) const {
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

        if (data.size() > 1) {
            throw SWA::ProgramError("Multiple occurences in find_only");
        }
        return data.size() ? data.front() : value_type();
    }

    T any() const {
        if (!data.size())
            throw ProgramError("Attempt to get element from empty Sequence");
        return data[0];
    }

    Sequence<T> any(size_type count) const {
        if (count >= data.size()) {
            // Need more than we have, so just return what we can
            return *this;
        } else {
            return Sequence<T>(data.begin(), data.begin() + count, unique,
                               sorted);
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

    void deleteInstance() {
        for (iterator it = begin(), endIt = end(); it != endIt; ++it) {
            it->deleteInstance();
        }
        clear();
    }

    friend void to_json(nlohmann::json &json, const SWA::Sequence<T> &v) {
        json = v.data;
    }

    friend void from_json(const nlohmann::json &json, SWA::Sequence<T> &v) {
        json.get_to(v.data);
        v.unique = false;
        v.sorted = false;
    }

  private:
    mutable Container data;
    mutable bool unique;
    mutable bool sorted;

    void checkBounds(size_type index) const {
        if (index < 1)
            throw ProgramError("Attempt to read sequence element before start");
        if (index > size())
            throw ProgramError("Attempt to read sequence element after end");
    }
};

template <class T>
::std::ostream &operator<<(::std::ostream &stream, const Sequence<T> &obj) {
    return stream << "Sequence size " << obj.size();
}

// Concatenation operator. Returns a new Sequence.
template <class T, class T2>
Sequence<T> operator+(Sequence<T> lhs, const T2 &rhs) {
    return lhs += rhs;
}

template <class T> inline std::size_t hash_value(const Sequence<T> &value) {
    return boost::hash_range(value.begin(), value.end());
}

} // namespace SWA

#endif
