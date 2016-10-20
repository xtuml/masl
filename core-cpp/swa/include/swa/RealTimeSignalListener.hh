//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_RealTimeSignalListener_HH
#define SWA_RealTimeSignalListener_HH

#include "boost/function.hpp"
#include "ListenerPriority.hh" 

namespace SWA
{

  class ActivityMonitor;

  class RealTimeSignalListener
  {
    public:

      // The type for the function to be called on a realtime signal.
      // Parameters are the pid and uid of the process that raised the signal
      typedef boost::function<void(int,int)> Callback;

      RealTimeSignalListener( const Callback& callback, ActivityMonitor& monitor );
      ~RealTimeSignalListener();

      void activate() { active = true; }
      void cancel() { active = false; }
 
      void queueSignal() const;
      void queueSignal( const ListenerPriority& priority ) const;

      void setPriority( const ListenerPriority& priority );
      const ListenerPriority& getPriority() { return priority; }


    private:
      void callCallback(int pid, int uid);

      int id;
      Callback callback;
      ListenerPriority priority;
      bool active;
      ActivityMonitor& monitor;

  };




}

#endif
