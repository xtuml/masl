//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "transient/ThreadListener.hh"

#include "swa/Process.hh"
#include "boost/bind.hpp"

namespace transient
{
  bool init = ThreadListener::initialise();

  bool ThreadListener::initialise()
  {
    getInstance();
    return true;
  }

  ThreadListener::ThreadListener()
  {
    SWA::Process::getInstance().registerThreadCompletedListener(boost::bind(&ThreadListener::performCleanup,this));
    SWA::Process::getInstance().registerThreadAbortedListener(boost::bind(&ThreadListener::performCleanup,this));
  }

  ThreadListener& ThreadListener::getInstance()
  {
    static ThreadListener instance;
    return instance;
  }


  void ThreadListener::addCleanup ( const boost::function<void()> function )
  {
    cleanupRoutines.push_back(function);
  }

  void ThreadListener::performCleanup()
  {
    for ( std::vector<boost::function<void()> >::const_iterator it = cleanupRoutines.begin(), end = cleanupRoutines.end();
          it != end; ++it )
    {
      (*it)();
    }
    cleanupRoutines.clear();
  }

}
