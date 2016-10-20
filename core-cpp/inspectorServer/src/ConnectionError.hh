//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef ConnectionError_HH
#define ConnectionError_HH

#include <stdexcept>

namespace Inspector
{
  class ConnectionError : public std::runtime_error
  {
    public:
      ConnectionError ( const std::string& message ) : runtime_error(message) {}
  };
}

#endif
