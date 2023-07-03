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

#ifndef SWA_RangeIterator_HH
#define SWA_RangeIterator_HH

#include <iterator>
#include <boost/operators.hpp>

namespace SWA {

  // ****************************************************************************
  // Define an iterator class that can be used to iterate 
  // over any range without using any values outside that 
  // range. 
  // ****************************************************************************
  template <class T>
  class RangeIterator : private boost::incrementable<RangeIterator<T>,
                                boost::decrementable<RangeIterator<T>, 
                                boost::equality_comparable<RangeIterator<T> > > >
  {
    public:
      typedef std::bidirectional_iterator_tag iterator_category;
      typedef ptrdiff_t difference_type;
      typedef const T value_type;
      typedef const T& reference;
      typedef const T* pointer;

    public:
      RangeIterator( reference startValue, reference endValue )
        : currentValue(startValue),
          endValue(endValue),
          end(false) {}

      RangeIterator( reference endValue)
        : currentValue(endValue),
          endValue(endValue),
          end(true){}

     ~RangeIterator(){}

      const RangeIterator& operator++()    
      { 
        // check whether have already reached the end. 
        // If have then nothing more to do.
        if (!end)
        {
          if (currentValue == endValue)
          {
            end = true;
          }
          else
          {
            ++currentValue;
          }
        }
        return *this;
      }

      const RangeIterator& operator--()    
      { 
        if ( end )
        {
          end = false;
          currentValue = endValue;
        }
        else
        {
          --currentValue;
        }
        return *this;
      }

      reference operator* () const { return  currentValue;  }
      pointer   operator->() const { return  &currentValue; }

      bool operator==(const RangeIterator& rhs) const 
      { 
        if (end == true && rhs.end == true) { return true;  }
        if (end == true || rhs.end == true) { return false; }
        return currentValue == rhs.currentValue;
      }

    private:
      T  currentValue;
      T  endValue;
      bool end;
  };

}

#endif
