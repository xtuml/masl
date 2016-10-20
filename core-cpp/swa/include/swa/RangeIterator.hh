//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
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
