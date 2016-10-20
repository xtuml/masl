//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef TRANSIENT_ThreadListener_HH
#define TRANSIENT_ThreadListener_HH

#include "boost/function.hpp"
#include <vector>

namespace transient
{
  class ThreadListener
  {
    public:
      void addCleanup ( const boost::function<void()> function );
      static ThreadListener& getInstance();
      static bool initialise();

    private:
      ThreadListener();
      void performCleanup();
      std::vector<boost::function<void()> > cleanupRoutines;
  };

}

#endif
