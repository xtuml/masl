//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_MainLoop_HH
#define SWA_MainLoop_HH

#include <map>
#include "boost/function.hpp"
#include <signal.h>

namespace SWA
{
  class ListenerPriority
  {
    public:
      static const ListenerPriority& getMinimum();
      static const ListenerPriority& getLow();
      static const ListenerPriority& getNormal();
      static const ListenerPriority& getHigh();
      static const ListenerPriority& getMaximum();

      operator int() () { return priority; }

    private:
      ListenerPriority(int priority);
      ListenerPriority(const ListenerPriority& low, const ListenerPriority& high );
      int priority;
  
  }


  class FileDescriptorListener
  {
    public:

      // The type for the function to be called on an fd 
      // event. Must return true if the callback should be 
      // requeued. Parameter is the relevant fd. 
      typedef boost::function<bool(int)> Callback;

      enum DirectoryListenMask { Access = DM_ACCESS, 
                                 Modify = DM_MODIFY,
                                 Create = DM_CREATE,
                                 Delete = DM_DELETE,
                                 Rename = DM_RENAME,
                                 Attrib = DM_ATTRIB,
                                 NoCancel = DM_MULTISHOT };

      FileDescriptorListener( int fd, const Callback& callback );

      void activateIOListen();
      void cancelIOListen();
 
      void activateReadListen();
      void activateWriteListen();
      void cancelReadWriteListen();

      void activateDirectoryListen(DirectoryListenMask mask);
      void cancelDirectoryListen();

      void setPriority( const ListenerPriority& priority );

      int getFd() { return fd; }
      const Callback& getCallback() { return callback; }

    private:
      int fd;
      Callback callback;

  };



  class ActivityMonitor
  {
    public:
      // The type for the function to be called on a POSIX.1b timer signal
      // Parameters are the timer id and overrun.
      typedef boost::function<void(int,int)> TimerCallback;

      // The type for the function to be called on any other signal.
      // Parameters are the pid and uid of the sending process.
      typedef boost::function<void (int,int)> NormalCallback;

      bool addListener ( const FileDescriptorListener& listener );

      void poll();

      void setFinished() { finished = true; }
      bool isFinished() { return finished; }      

    private:
      std::map<int,FileDescriptorListener::Callback> fdCallbacks;
      std::map<int,NormalCallback> normalCallbacks;
      std::map<int,NormalCallback> signalCallbacks;
      std::map<int,TimerCallback>  timerCallbacks;

      std::map<int,int> callbackIdByFd;
      std::map<int,int> callbackFdById;

      sigset_t signalMask;

      int nextFdId;
      int nextNormalId;
      int nextTimerId;

      bool finished;


  };


};

#endif
