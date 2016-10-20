//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_ConstraintError_HH
#define SWA_ConstraintError_HH

#include <string>

#include "Exception.hh"

namespace SWA
{
  class ConstraintError : public Exception
  {
    public:
       ConstraintError(const std::string& error): Exception(std::string("Constraint Error :") + error) {}
       ConstraintError() : Exception("Constraint Error") {}
      ~ConstraintError() throw() {}
  };

}

#endif
