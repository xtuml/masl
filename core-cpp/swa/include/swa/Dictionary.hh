//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_Dictionary_HH
#define SWA_Dictionary_HH

#include <boost/unordered_map.hpp>
#include <boost/operators.hpp>
#include <map>

#include "Set.hh"
#include "Bag.hh"

namespace SWA
{
  template<class Key,class Value>
  class Dictionary : private boost::less_than_comparable<Dictionary<Key, Value>, 
                               boost::equality_comparable<Dictionary<Key, Value>
                               > 
                             >
  {
    public:
      typedef boost::unordered_map<Key,Value> Container;

      typedef typename Container::iterator iterator;
      typedef typename Container::const_iterator const_iterator;

      typedef typename Container::size_type size_type;
      typedef typename Container::value_type value_type;

      typedef typename Container::reference reference;
      typedef typename Container::const_reference const_reference;

      typedef typename Container::pointer pointer;
      typedef typename Container::const_pointer const_pointer;

      typedef typename Container::difference_type difference_type;

      iterator begin() { return data.begin(); }
      const_iterator begin() const { return data.begin(); }
      
      iterator end() { return data.end(); }
      const_iterator end() const { return data.end(); }

      size_type size() const { return data.size(); }
      void clear() { data.clear(); }

      iterator insert(iterator pos, const value_type& rhs)
      {
        return data.insert(pos,rhs);
      }


    public:
      Dictionary () : data() {}
      Dictionary ( const Dictionary& rhs ) : data(rhs.data) {}

      const Container& getData() const { return data; }

      explicit Dictionary ( const Container& rhs ) : data(rhs) {}

      template<class Key2,class Value2>
      explicit Dictionary ( const Dictionary<Key2,Value2>& rhs ) : data(rhs.data.begin(),rhs.data.end()) {}

      template<class Key2,class Value2>
      explicit Dictionary ( const boost::unordered_map<Key2,Value2>& rhs ) : data(rhs.begin(),rhs.end()) {}

      template<class Key2,class Value2>
      explicit Dictionary ( const std::map<Key2,Value2>& rhs ) : data(rhs.begin(),rhs.end()) {}

      const Value& getValue ( const Key& key ) const
      {
        const_iterator pos = data.find(key);
        if ( pos == data.end() ) throw ProgramError ("key does not occur in dictionary");
        else return pos->second;
      }

      bool hasValue ( const Key& key ) const { return data.find(key) != data.end(); }

      Value& setValue ( const Key& key ) { return data[key]; }

      void eraseValue ( const Key& key ) { data.erase(key); }

      Set<Key> getKeys() const { return Set<Key>(key_iterator(data.begin()),key_iterator(data.end()),true,false); }

      Bag<Value> getValues() const { return Bag<Value>(value_iterator(data.begin()),value_iterator(data.end())); }

      Value& operator[] ( const Key& key ) { return data[key]; }
      const Value& operator[] ( const Key& key ) const { return data[key]; }

      bool operator== ( const Dictionary& rhs ) const { return std::map<Key, Value>(begin(), end()) == std::map<Key, Value>(rhs.begin(), rhs.end()); }
      bool operator< ( const Dictionary& rhs ) const { return std::map<Key, Value>(begin(), end()) < std::map<Key, Value>(rhs.begin(), rhs.end()); }

    private:
      Container data;

      class key_iterator : public std::iterator<std::input_iterator_tag, Key>, public boost::input_iteratable<key_iterator,Key*>
      {
        public:
          key_iterator ( const const_iterator& pos ) : pos(pos) {}
          const Key& operator*() const { return pos->first; }
          key_iterator& operator++()  { ++pos; return *this; }
          bool operator== ( const key_iterator& rhs ) const { return pos == rhs.pos; }

        private:
          const_iterator pos;
      };

      class value_iterator : public std::iterator<std::input_iterator_tag, Value>, public boost::input_iteratable<value_iterator,Value*>
      {
        public:
          value_iterator ( const const_iterator& pos ) : pos(pos) {}
          const Value& operator*() const { return pos->second; }
          value_iterator& operator++()  { ++pos; return *this; }
          bool operator== ( const value_iterator& rhs ) const { return pos == rhs.pos; }

        private:
          const_iterator pos;
      };

  };

  template<class K,class V>
  ::std::ostream& operator<< ( ::std::ostream& stream,
                               const Dictionary<K,V>& obj )
  {
    return stream << "Dictionary size " << obj.size();
  }

  template<class K,class V>
  inline std::size_t hash_value ( const Dictionary<K,V>& value )
  {
    return boost::hash_range(value.begin(),value.end());
  }

}

#endif
