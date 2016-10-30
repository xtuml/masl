//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_FileDescriptorListener_HH
#define SWA_FileDescriptorListener_HH

#include "boost/function.hpp"
#include "ListenerPriority.hh" 

namespace SWA
{

  class ActivityMonitor;

  class FileDescriptorListener
  {
    public:

      // The type for the function to be called on an fd 
      // event. Must return true if the callback should be 
      // requeued. Parameter is the relevant fd. 
      typedef boost::function<bool(int)> Callback;

      FileDescriptorListener( int fd, const Callback& callback, ActivityMonitor& monitor );
      FileDescriptorListener( const Callback& callback, ActivityMonitor& monitor );
      ~FileDescriptorListener();

      void setFd ( int fd );
      void clearFd();

      void activate(const bool queueImmediately = false);
      void cancel();
 
      void setPriority( const ListenerPriority& priority );
      const ListenerPriority& getPriority() { return priority; }

      int getFd() const { return fd; }

    private:
      void callCallback(int fd);
      void requeue() const;
      bool valid() const;
      bool readyToRead() const;


      void initFd();
      void updateFdStatus( bool makeActive );

      int fd;
      Callback callback;
      ListenerPriority priority;
      bool active;
      bool forceCallback;
      ActivityMonitor& monitor;

  };




}

#endif
