//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "swa/RealTimeSignalListener.hh"
#include "swa/ActivityMonitor.hh"
#include "swa/ProgramError.hh"
#include <fcntl.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <signal.h>
#include "boost/bind.hpp"

namespace SWA
{

  RealTimeSignalListener::RealTimeSignalListener( const Callback& callback, ActivityMonitor& monitor )
    : id(monitor.addNormalCallback(boost::bind(&RealTimeSignalListener::callCallback,this,_1,_2))),
      callback(callback),
      priority(ListenerPriority::getNormal()),
      active(true),
      monitor(monitor)
  {
  }

  RealTimeSignalListener::~RealTimeSignalListener()
  {
    monitor.removeNormalCallback(id);
  }


  void RealTimeSignalListener::setPriority( const ListenerPriority& priority )
  {
    this->priority = priority;
  }
  
  void RealTimeSignalListener::queueSignal() const
  {
    sigval data;
    data.sival_int = id;
    sigqueue(getpid(),priority.getValue(),data);
  }

 void RealTimeSignalListener::queueSignal( const ListenerPriority& priority ) const
  {
    sigval data;
    data.sival_int = id;
    sigqueue(getpid(),priority.getValue(),data);
  }

  void RealTimeSignalListener::callCallback( int pid, int uid )
  {
    if ( active ) callback ( pid, uid );
  }
}
