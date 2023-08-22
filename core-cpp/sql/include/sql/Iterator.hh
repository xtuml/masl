/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 * Classification: UK OFFICIAL
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_Iterator_HH
#define Sql_Iterator_HH

#include <iterator>

#include "boost/operators.hpp"

namespace SQL {

template <class Container>
class AssociativeInsertIterator : public std::iterator<std::output_iterator_tag,void,void,void,void>
{
  public:
     explicit AssociativeInsertIterator(Container& cont):container(cont){}
    ~AssociativeInsertIterator(){}

     AssociativeInsertIterator<Container>& operator=(const typename Container::value_type& value) 
     {
         container.insert(value);
         return *this;
     }

     AssociativeInsertIterator<Container>& operator* ()    { return *this; } 
     AssociativeInsertIterator<Container>& operator++()    { return *this; } 
     AssociativeInsertIterator<Container>& operator++(int) { return *this; } 

   private:
     Container& container;
};


template <class Container>
inline AssociativeInsertIterator<Container> associativeInserter(Container& cont)
{
  return AssociativeInsertIterator<Container>(cont);
}

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

} // end namespace SQL
#endif

