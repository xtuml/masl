//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_Parameter_HH
#define SWA_Parameter_HH

namespace SWA
{
  // Very lightweight class for storing parameter 
  // references. There is no type or constness checking here - 
  // it is assumed that any one calling the getter or setter 
  // has meta data available which will tell them the type and 
  // readonly status of the variable. Consequently the 
  // reference to the variable is just stored as a void* of 
  // the variable's address. We could do some safety checks 
  // using type_info, but that would just add runtime 
  // overheads. We could also use the boost::any class, but 
  // again that is too heavyweight for what we need here, as 
  // we are not interested in storing values, just references 
  // to them. Consequently we would just be using it to store 
  // pointers, in which case it decomposes to what we have 
  // here (but with the type_info checking), but with lots of 
  // heap news and deletes which we don't need, because all 
  // pointers are the same size and can be stored in a void*. 
  class Parameter
  {
    public:
      Parameter() : valuePtr(0) {}

      template<class T>
      Parameter(const T& value) 
        : valuePtr(&value)
      {
      }

      template<class T>
      const T& getValue() const
      {
        return *static_cast<const T*>(valuePtr);
      }

      template<class T>
      void setValue( const T& newValue )
      {
        *const_cast<T*>(static_cast<const T*>(valuePtr)) = newValue;
      }

    private:
      const void* valuePtr;
  };


}

#endif
