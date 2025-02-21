/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_String_HH
#define SWA_String_HH

#include "boost/functional/hash.hpp"
#include "boost/operators.hpp"
#include "boost/range/iterator_range.hpp"
#include "collection.hh"
#include <algorithm>
#include <cctype>
#include <nlohmann/json.hpp>
#include <string>

namespace {
inline char ToUpper(char c) { return std::toupper(c); }

inline char ToLower(char c) { return std::tolower(c); }
} // namespace
namespace SWA {

template <class T> class Sequence;
template <class T> class Bag;
template <class T> class Set;

class String
    : private boost::less_than_comparable<String,
                                          boost::equality_comparable<String>> {
  public:
    //*********************************************************
    //       STL Container Compatibility Methods
    // --------------------------------------------------------
    // These methods implement the STL contract for the
    // relevant container type.
    //*********************************************************

    // Container methods
    typedef std::string Container;
    typedef Container::value_type value_type;

    typedef Container::reference reference;
    typedef Container::const_reference const_reference;

    typedef Container::pointer pointer;
    typedef Container::const_pointer const_pointer;

    typedef Container::iterator iterator;
    typedef Container::const_iterator const_iterator;

    typedef Container::difference_type difference_type;
    typedef Container::size_type size_type;

    String(const String &rhs) : data(rhs.data) {}
    String &operator=(const String &rhs) {
        data = rhs.data;
        return *this;
    }

    iterator begin() { return data.begin(); }
    const_iterator begin() const { return data.begin(); }

    iterator end() { return data.end(); }
    const_iterator end() const { return data.end(); }

    size_type size() const { return data.size(); }
    size_type max_size() const { return data.max_size(); }
    bool empty() const { return data.empty(); }

    void swap(String &rhs) { data.swap(rhs.data); }

    // Forward Container methods - other comparisons via boost superclasses
    bool operator==(const String &rhs) const { return data == rhs.data; }
    bool operator<(const String &rhs) const { return data < rhs.data; }

    // Reversible Container methods
    typedef Container::reverse_iterator reverse_iterator;
    typedef Container::const_reverse_iterator const_reverse_iterator;

    reverse_iterator rbegin() { return data.rbegin(); }
    const_reverse_iterator rbegin() const { return data.rbegin(); }

    reverse_iterator rend() { return data.rend(); }
    const_reverse_iterator rend() const { return data.rend(); }

    // Random Access Container Methods
    reference operator[](size_type n) { return data[n]; }
    const_reference operator[](size_type n) const { return data[n]; }

    // String Methods
    String(const value_type *cstr) : data(cstr) {}
    String(size_type n, value_type c) : data(n, c) {}

    String() : data() {}
    template <class It> String(It i, It j) : data(i, j) {}

    iterator insert(iterator p, value_type c) { return data.insert(p, c); }
    template <class It> void insert(iterator p, It i, It j) {
        data.insert(p, i, j);
    }

    iterator erase(iterator p) { return data.erase(p); }
    iterator erase(iterator p, iterator q) { return data.erase(p, q); }

    void push_back(value_type c) { data.push_back(c); }

    // std::string passthrough
    const value_type *c_str() const { return data.c_str(); }
    const Container &s_str() const { return data; }

    //*********************************************************
    //       std::string methods
    //--------------------------------------------------------

    String &replace(size_type idx, size_type len, const SWA::String &str) {
        data.replace(idx, len, str);
        return *this;
    }

    String &replace(size_type idx, size_type len, const SWA::String &str,
                    size_type str_idx, size_type str_num) {
        data.replace(idx, len, str, str_idx, str_num);
        return *this;
    }

    String &replace(size_type idx, size_type len, const value_type *cstr) {
        data.replace(idx, len, cstr);
        return *this;
    }

    String &replace(iterator beg, iterator end, const value_type *cstr) {
        data.replace(beg, end, cstr);
        return *this;
    }

    String &replace(size_type idx, size_type len, const value_type *value_types,
                    size_type value_types_len) {
        data.replace(idx, len, value_types, value_types_len);
        return *this;
    }

    String &replace(iterator beg, iterator end, const value_type *value_types,
                    size_type value_types_len) {
        data.replace(beg, end, value_types, value_types_len);
        return *this;
    }

    String &replace(size_type idx, size_type len, size_type num, value_type c) {
        data.replace(idx, len, num, c);
        return *this;
    }

    String &replace(iterator beg, iterator end, size_type num, value_type c) {
        data.replace(beg, end, num, c);
        return *this;
    }

    template <class InputIterator>
    String &replace(iterator beg, iterator end, InputIterator newBeg,
                    InputIterator newEnd) {
        data.replace(beg, end, newBeg, newEnd);
        return *this;
    }

    String &append(const String &str) {
        data.append(str);
        return *this;
    }

    String &append(const String &str, size_type str_idx, size_type str_num) {
        data.append(str, str_idx, str_num);
        return *this;
    }

    String &append(const value_type *value_types) {
        data.append(value_types);
        return *this;
    }

    String &append(const value_type *value_types, size_type value_types_len) {
        data.append(value_types, value_types_len);
        return *this;
    }

    String &append(size_type num, value_type c) {
        data.append(num, c);
        return *this;
    }

    template <class InputIterator>
    String &append(InputIterator begin, InputIterator end) {
        data.append(begin, end);
        return *this;
    }

    String &assign(const String &str) {
        data.assign(str);
        return *this;
    }

    String &assign(const String &str, size_type str_idx, size_type str_num) {
        data.assign(str, str_idx, str_num);
        return *this;
    }

    String &assign(const value_type *value_types) {
        data.assign(value_types);
        return *this;
    }

    String &assign(const value_type *value_types, size_type value_types_len) {
        data.assign(value_types, value_types_len);
        return *this;
    }

    String &assign(size_type num, value_type c) {
        data.assign(num, c);
        return *this;
    }

    template <class InputIterator>
    String &assign(InputIterator begin, InputIterator end) {
        data.assign(begin, end);
        return *this;
    }

    String &insert(size_type idx, const String &str) {
        data.insert(idx, str);
        return *this;
    }

    String &insert(size_type idx, const String &str, size_type str_idx,
                   size_type str_num) {
        data.insert(idx, str, str_idx, str_num);
        return *this;
    }

    String &insert(size_type idx, const value_type *cstr) {
        data.insert(idx, cstr);
        return *this;
    }

    String &insert(size_type idx, const value_type *value_types,
                   size_type value_types_len) {
        data.insert(idx, value_types, value_types_len);
        return *this;
    }

    String &insert(size_type idx, size_type num, value_type c) {
        data.insert(idx, num, c);
        return *this;
    }

    void insert(iterator pos, size_type num, value_type c) {
        data.insert(pos, num, c);
    }

    String &clear() {
        data.clear();
        return *this;
    }

    void resize(size_type num) { data.resize(num); }

    void resize(size_type num, value_type c) { data.resize(num, c); }

    size_type capacity() const { return data.capacity(); }

    void reserve() { data.shrink_to_fit(); }

    void reserve(size_type num) { data.reserve(num); }

    //*********************************************************
    //       MASL String extension methods
    // --------------------------------------------------------
    // These methods provide extra functionality to support
    // the masl language features with respect to Strings.
    //*********************************************************

    // Construct from underlying container type
    String(const Container &rhs) : data(rhs) {}

    // Construct sequence type
    template <class V>
    String(const Sequence<V> &rhs) : data(rhs.begin(), rhs.end()) {}

    // Construct sequence type
    template <class V>
    String(const Set<V> &rhs) : data(rhs.begin(), rhs.end()) {}

    // Construct sequence type
    template <class V>
    String(const Bag<V> &rhs) : data(rhs.begin(), rhs.end()) {}

    // Construct from boost iterator range
    explicit String(const boost::iterator_range<const_iterator> &rhs)
        : data(rhs.begin(), rhs.end()) {}

    operator const Container &() const { return data; }

    template <class V> operator Set<V>() const {
        return Set<V>(data.begin(), data.end());
    }
    template <class V> operator Bag<V>() const {
        return Bag<V>(data.begin(), data.end());
    }
    template <class V> operator Sequence<V>() const {
        return Sequence<V>(data.begin(), data.end());
    }

    // Construct from single element
    String(value_type element) : data(1, element) {}

    // Append operator. Note that this appends to the existing String
    String &operator+=(const String &rhs) {
        data += rhs.data;
        return *this;
    }

    // Append operator. Note that this appends to the existing String
    String &operator+=(value_type rhs) {
        data += rhs;
        return *this;
    }

    // Concatenation operator. Returns a new String.
    String operator+(const String &rhs) const { return data + rhs.data; }

    // Concatenation operator. Returns a new String.
    String operator+(value_type rhs) const { return data + rhs; }

    // Returns a slice. This has the potential for
    // optimisation by returning a proxy object referencing
    // the original String. Note that masl indexing is 1-up.
    String slice(size_type start, size_type finish) const {
        checkBounds(start);
        checkBounds(finish);
        String result(begin() + start - 1, begin() + finish);
        return result;
    }

    // Returns the element at the supplied index. Note that
    // masl indexing is 1-up.
    value_type access(size_type index) const {
        checkBounds(index);
        return data[index - 1];
    }

    value_type &access(size_type index) {
        checkBounds(index);
        return data[index - 1];
    }

    value_type &accessExtend(size_type index) {
        if (index > size()) {
            data.append(index - size(), '\0');
        }
        checkBounds(index);
        return data[index - 1];
    }

    size_type first() const { return 1; }
    size_type last() const { return size(); }

    std::back_insert_iterator<Container> inserter() {
        return std::back_inserter(data);
    }

    String lower() const {
        std::string result;
        std::transform(data.begin(), data.end(), std::back_inserter(result),
                       ToLower);
        return result;
    }

    int firstCharPos(value_type ch) const {
        size_t index = data.find(ch);
        return (index == std::string::npos) ? 0 : index + 1;
    }

    String upper() const {
        std::string result;
        std::transform(data.begin(), data.end(), std::back_inserter(result),
                       ToUpper);
        return result;
    }

    ::std::ostream &write(::std::ostream &stream) const {
        return stream << data;
    }

    ::std::istream &read(::std::istream &stream) { return stream >> data; }

    size_type find(const String &search) {
        size_t index = data.find(search);
        return (index == std::string::npos) ? 0 : index + 1;
    }

    size_type find(const String &search, size_t startPos) {
        size_t index = data.find(search, startPos - 1);
        return (index == std::string::npos) ? 0 : index + 1;
    }

    const_iterator iteratorAt(size_type pos) const { return begin() + pos - 1; }

    const_iterator iteratorAfter(size_type pos) const { return begin() + pos; }

    iterator iteratorAt(size_type pos) { return begin() + pos - 1; }

    iterator iteratorAfter(size_type pos) { return begin() + pos; }

    friend void to_json(nlohmann::json &json, const SWA::String &v) {
        json = v.data;
    }

    friend void from_json(const nlohmann::json &json, SWA::String &v) {
        json.get_to(v.data);
    }

  private:
    Container data;

    void checkBounds(size_type index) const {
        if (index < 1)
            throw SWA::ProgramError(
                "Attempt to read String element before start");
        if (index > size())
            throw SWA::ProgramError("Attempt to read String element after end");
    }
};

inline ::std::ostream &operator<<(::std::ostream &stream, const String &obj) {
    return obj.write(stream);
}
inline ::std::istream &operator>>(::std::istream &stream, String &obj) {
    return obj.read(stream);
}

// Concatenation operator. Returns a new String.
inline String operator+(const String::value_type &lhs, const String &rhs) {
    return String(lhs) + rhs;
}

// Concatenation operator. Returns a new String.
inline String operator+(const String::value_type *lhs, const String &rhs) {
    return String(lhs) + rhs;
}

inline std::size_t hash_value(const String &value) {
    return boost::hash_value(static_cast<std::string>(value));
}

} // namespace SWA

#endif
