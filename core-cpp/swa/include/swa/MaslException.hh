//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_MaslException_HH
#define SWA_MaslException_HH

#include "Exception.hh"

namespace SWA
{
  class MaslException : public Exception
  {
    public:
      MaslException() : Exception("") {}
      MaslException(const std::string& message) : Exception(message) {}
      ~MaslException() throw() {}
  };

}

#endif
