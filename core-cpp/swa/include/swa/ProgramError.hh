//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_ProgramError_HH
#define SWA_ProgramError_HH

#include <string>
#include "boost/tuple/tuple.hpp"

#include "Exception.hh"

namespace SWA
{
  class ProgramError : public Exception
  {
    public:
      ProgramError () : Exception(std::string("Program Error :")) {}
      ProgramError ( const std::string& error ) : Exception(std::string("Program Error :") + error) {}

      template <class T>
      ProgramError (const T& tuple):Exception(std::string("Program Error :"),tuple) {}
  };
}

#endif
