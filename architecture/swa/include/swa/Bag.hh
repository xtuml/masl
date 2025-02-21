/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_Bag_HH
#define SWA_Bag_HH

#include <vector>
#include <set>
#include <unordered_set>
#include <boost/functional/hash.hpp>
#include <algorithm>
#include "ObjectPtr.hh"
#include "collection.hh"
#include "boost/operators.hpp"
#include <nlohmann/json.hpp>

namespace SWA
{
  template<class T> class Sequence;
  template<class T> class Set;

  template<class T>
  class Bag : private boost::less_than_comparable<Bag<T>, 
                        boost::equality_comparable<Bag<T>, 
                          boost::less_than_comparable<Bag<T>,Sequence<T>, 
                            boost::equality_comparable<Bag<T>, Sequence<T>
                            > 
                          > 
                        >
                      >
  {
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

      Bag ( const Bag& rhs ) : data(rhs.getData()), unique(rhs.isUnique()), sorted(rhs.isSorted()) {}

      Bag& operator= ( const Bag& rhs ) { if ( this != &rhs ) { data = rhs.getData(); unique = rhs.isUnique(); sorted = rhs.isSorted(); } return *this; }

      void clear() { Container().swap(data); unique = true; sorted = true; }

      iterator begin() { return data.begin(); }
      const_iterator begin() const { return data.begin(); }
      
      iterator end() { return data.end(); }
      const_iterator end() const { return data.end(); }

      size_type capacity() const { return data.capacity(); }  
      size_type max_size() const { return data.max_size(); }
      size_type size() const { return data.size(); }  
      bool empty() const { return data.empty(); }

      void reserve(const size_type size) { data.reserve(size); }
      void swap ( Bag& rhs ) { data.swap(rhs.data); std::swap(unique,rhs.unique); std::swap(sorted,rhs.sorted); }
      void resize (const size_type size) { data.resize(size); }

      // Forward Container methods - other comparisons via boost superclasses
      bool operator== ( const Bag& rhs ) const { sort(); rhs.sort(); return data == rhs.data; }
      bool operator== ( const Sequence<T>& rhs ) const { return *this == Bag(rhs); }
      bool operator<  ( const Bag& rhs ) const { sort(); rhs.sort(); return data <  rhs.data; }
      bool operator<  ( const Sequence<T>& rhs ) const { return *this <  Bag(rhs); }

      // Reversible Container methods
      typedef typename Container::reverse_iterator reverse_iterator;
      typedef typename Container::const_reverse_iterator const_reverse_iterator;

      reverse_iterator rbegin() { return data.rbegin(); }
      const_reverse_iterator rbegin() const { return data.rbegin(); }
      
      reverse_iterator rend() { return data.rend(); }
      const_reverse_iterator rend() const { return data.rend(); }

      Bag ( ) : data(),unique(true),sorted(true) {}
      template<class It>
      Bag ( It i, It j, bool unique = false, bool sorted = false ) : data(i,j), unique(unique),sorted(sorted) {}

      template<class It>
      void insert ( It i, It j ) { unique = false; sorted = false; data.insert(data.end(),i,j); }

      iterator insert(iterator pos, const T& rhs)
      {
        unique = false; sorted = false;
        return data.insert(pos,rhs);
      }


      //*********************************************************
      //       MASL Bag extension methods
      // --------------------------------------------------------
      // These methods provide extra functionality to support 
      // the masl language features with respect to Bags. 
      //*********************************************************

      // Construct from underlying container type
      Bag ( const Container& rhs ) : data(rhs), unique(false), sorted(false) {}

      // Construct frm stl container
      template<class T2>
      explicit Bag ( const std::vector<T2>& rhs ) : data(rhs.begin(),rhs.end()), unique(false), sorted(false) {}

      template<class T2>
      explicit Bag ( const std::set<T2>& rhs ) : data(rhs.begin(),rhs.end()), unique(true), sorted(true) {}

      template<class T2>
      explicit Bag ( const std::multiset<T2>& rhs ) : data(rhs.begin(),rhs.end()), unique(false), sorted(true) {}

      template<class T2,typename Hash>
      explicit Bag ( const std::unordered_set<T2,Hash>& rhs ) : data(rhs.begin(),rhs.end()), unique(true), sorted(false) {}

      template<class T2,typename Hash>
      explicit Bag ( const std::unordered_multiset<T2,Hash>& rhs ) : data(rhs.begin(),rhs.end()), unique(false), sorted(false) {}

      // Construct from collection
      template<class T2>
      Bag ( const Bag<T2>& rhs ) : data(rhs.begin(),rhs.end()), unique(rhs.isUnique()), sorted(rhs.isSorted()) {}

      template<class T2>
      Bag& operator= ( const Bag<T2>& rhs ) { data.assign(rhs.begin(),rhs.end()); unique = rhs.isUnique(); sorted = rhs.isSorted(); return *this; }


      Bag ( const Sequence<T>& rhs ) : data(rhs.getData()), unique(rhs.isUnique()), sorted(rhs.isSorted()) {}

      template<class T2>
      Bag ( const Sequence<T2>& rhs ) : data(rhs.begin(),rhs.end()), unique(rhs.isUnique()), sorted(rhs.isSorted()) {}

      Bag& operator= ( const Sequence<T>& rhs ) { data = rhs.getData(); unique = rhs.isUnique(); sorted = rhs.isSorted(); return *this; }

      template<class T2>
      Bag& operator= ( const Sequence<T2>& rhs ) { data.assign(rhs.begin(),rhs.end()); unique = rhs.isUnique(); sorted = rhs.isSorted(); return *this; }


      Bag ( const Set<T>& rhs ) : data(rhs.getData()), unique(rhs.isUnique()), sorted(rhs.isSorted()) {}

      template<class T2>
      Bag ( const Set<T2>& rhs ) : data(rhs.begin(),rhs.end()), unique(rhs.isUnique()), sorted(rhs.isSorted()) {}

      Bag& operator= ( const Set<T>& rhs ) { data = rhs.getData(); unique = rhs.isUnique(); sorted = rhs.isSorted(); return *this; }

      template<class T2>
      Bag& operator= ( const Set<T2>& rhs ) { data.assign(rhs.begin(),rhs.end()); unique = rhs.isUnique(); sorted = rhs.isSorted(); return *this; }




      // Construct from single element 
      explicit Bag ( const T& element ) : data(NullCheck<T>::isNull(element)?0:1,element), unique(true),sorted(true) {}
 
      template<class T2>
      explicit Bag ( const ObjectPtr<T2>& element ) : data(element?1:0,element), unique(true), sorted(true) {}

      // Add operator. Note that this add to the existing bag
      Bag& operator += ( const T& rhs )
      {
        if ( ! NullCheck<T>::isNull(rhs) )
        {
          unique = false;
          sorted = false;
          data.push_back(rhs);
        }
        return *this;
      }

      template<class T2>
      Bag& operator += ( const ObjectPtr<T2>& rhs )
      {
        if ( rhs )
        {
          unique = false;
          sorted = false;
          data.push_back(rhs);
        }
        return *this;
      }

      template<class T2>
      Bag& operator += ( const Sequence<T2>& rhs )
      {
        unique = false;
        sorted = false;
        data.insert(data.end(),rhs.begin(),rhs.end());
        return *this;
      }

      template<class T2>
      Bag& operator += ( const Set<T2>& rhs )
      {
        unique = false;
        sorted = false;
        data.insert(data.end(),rhs.begin(),rhs.end());
        return *this;
      }

      template<class T2>
      Bag& operator += ( const Bag<T2>& rhs )
      {
        unique = false;
        sorted = false;
        data.insert(data.end(),rhs.begin(),rhs.end());
        return *this;
      }

      size_type first() const { return 1; }
      size_type last() const { return size(); }

      const Container& getData() const { return data; }

      void forceUnique() const { unique = true; }
      void forceSorted() const { sorted = true; }

      bool isUnique() const { return unique; }
      bool isSorted() const { return sorted; }

      template<class T2>
      void erase(const T2& rhs)
      {
         data.erase(std::remove(data.begin(),data.end(),rhs),data.end());
      }

      void insert(const T& rhs)
      {
         *this += rhs;
      }

      void push_back(const T& rhs)
      {
         *this += rhs;
      }

      std::back_insert_iterator<Bag<T> > inserter()
      {
        return std::back_inserter(*this);
      }

      // Find functions
      const Bag<T>& find() const
      {
        return *this;
      }

      // Find functions
      const_iterator find(const T& value) const
      {
        return std::find(begin(),end(),value);
      }

      template<class Predicate>
      Bag<T> find ( Predicate predicate ) const
      {
        Bag<T> result;
        SWA::copy_if(data.begin(),data.end(),result.inserter(),predicate);
        if ( unique ) result.forceUnique();
        if ( sorted ) result.forceUnique();
        return result;
      }

      template<class Predicate>
      T find_one ( Predicate predicate ) const
      {
        return SWA::find_one(data.begin(),data.end(),predicate);
      }

      T find_one() const
      {
        return SWA::find_one(data.begin(),data.end());
      }

      template<class Predicate>
      T find_only ( Predicate predicate ) const
      {
        value_type result;
        iterator it = std::find_if(data.begin(),data.end(),predicate);
        if ( it != data.end() ) 
        {
          result = *it++;

          while ( it != data.end() )
          {
            it = std::find_if(it,data.end(),predicate );

            if ( it != data.end() && *it != result )
            {
              throw SWA::ProgramError("Multiple occurences in find_only");
            }
          }
        }
        return result;
      }

      T find_only() const
      {
        if ( ! data.size() ) return value_type();

        if ( data.size() > 1 )
        {
          throw SWA::ProgramError("Multiple occurences in find_only");
        }
        return data.size()?data.front():value_type();
      }


      T any () const
      {
        if ( ! data.size() ) throw ProgramError ("Attempt to get element from empty bag");
        return data[0];
      }

      Bag<T> any ( size_type count ) const
      {
        if ( count >= data.size() )
        {
          // Need more than we have, so just return what we can
          return *this;
        }
        else
        {
          return Bag<T>(data.begin(),data.begin()+count,unique,sorted);
        }
      }

      // Ordering functions
      Sequence<T> ordered_by () const
      {
        Sequence<T> result = SWA::ordered_by(begin(),end());
        if ( unique ) result.forceUnique();
        return result;
      }

      template<class Predicate>
      Sequence<T> ordered_by ( Predicate predicate ) const
      {
        Sequence<T> result = SWA::ordered_by(begin(),end(),predicate);
        if ( unique ) result.forceUnique();
        return result;
      }

      Sequence<T> reverse_ordered_by () const
      {
        Sequence<T> result = SWA::reverse_ordered_by(begin(),end());
        if ( unique ) result.forceUnique();
        return result;
      }

      template<class Predicate>
      Sequence<T> reverse_ordered_by ( Predicate predicate ) const
      {
        Sequence<T> result = SWA::reverse_ordered_by(begin(),end(),predicate);
        if ( unique ) result.forceUnique();
        return result;
      }

      void deleteInstance()
      {
        for ( iterator it = begin(), endIt = end(); it != endIt; ++it )
        {
          it->deleteInstance();
        }
        clear();
      }

      Bag<T> set_union ( const Bag& rhs ) const
      {
	sort();
        rhs.sort();
        Bag result;
        std::set_union(begin(),end(),rhs.begin(),rhs.end(),result.inserter()); 
        result.forceSorted();
        if ( isUnique() && rhs.isUnique() ) result.forceUnique();
        return result;
      }

      Bag set_disunion ( const Bag& rhs ) const
      {
	sort();
        rhs.sort();
        Bag result;
        std::set_symmetric_difference(begin(),end(),rhs.begin(),rhs.end(),result.inserter()); 
        result.forceSorted();
        if ( isUnique() && rhs.isUnique() ) result.forceUnique();
        return result;
      }

      Bag set_intersection ( const Bag& rhs ) const
      {
	sort();
        rhs.sort();
        Bag result;
        std::set_intersection(begin(),end(),rhs.begin(),rhs.end(),result.inserter()); 
        result.forceSorted();
        if ( isUnique() && rhs.isUnique() ) result.forceUnique();
        return result;
      }

      Bag set_not_in ( const Bag& rhs ) const
      {
	sort();
        rhs.sort();
        Bag result;
        std::set_difference(begin(),end(),rhs.begin(),rhs.end(),result.inserter()); 
        result.forceSorted();
        if ( isUnique() && rhs.isUnique() ) result.forceUnique();
        return result;
      }


      friend void to_json(nlohmann::json& json, const SWA::Bag<T> v ){
          json = v.data;
      }

      friend void from_json(const nlohmann::json& json, SWA::Bag<T>& v ){
          json.get_to(v.data);
          v.unique = false;
          v.sorted = false;
      }


  private:
      mutable Container data;
      mutable bool unique;
      mutable bool sorted;

      void sort() const
      {
        if ( !sorted )
        {
	  std::sort(data.begin(),data.end());
          sorted = true;
        }
     }
  };

  template<class T>
  ::std::ostream& operator<< ( ::std::ostream& stream,
                               const Bag<T>& obj )
  {
    return stream << "Bag size " << obj.size();
  }


  // Concatenation operator. Returns a new bag.
  template<class T, class T2>
  Bag<T> operator + ( Bag<T> lhs, const T2& rhs )
  {
    return lhs+= rhs;
  }

  template<class T>
  inline std::size_t hash_value ( const Bag<T>& value )
  {
    return boost::hash_range(value.begin(),value.end());
  }



}

#endif
