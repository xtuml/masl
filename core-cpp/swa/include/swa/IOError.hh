//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_IOError_HH
#define SWA_IOError_HH

#include <string>
#include "boost/tuple/tuple.hpp"

#include "Exception.hh"

namespace SWA
{
  class IOError : public Exception
  {
    public:
       IOError () : Exception(std::string("IO Error :")) {}
       IOError ( const std::string& error ) : Exception(std::string("IO Error :") + error) {}

       template <class T>
       IOError (const T& tuple):Exception(std::string("IO Error :"),tuple) {}
  };

}

#endif
