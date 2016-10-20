//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_TimerListener_HH
#define SWA_TimerListener_HH

#include "Timestamp.hh"
#include "Duration.hh"
#include <boost/function.hpp>
#include <boost/utility.hpp>

namespace SWA
{
  class ListenerPriority;

  class TimerListener : public boost::noncopyable
  {
    public:
      typedef boost::function<void(int)> Callback;

      TimerListener( const ListenerPriority& priority, const Callback& callback );
      ~TimerListener ();

      void schedule ( const Timestamp& expiryTime, const Duration& interval = Duration::zero() );
      void cancel();


    private:
      void timerFired ( int32_t overrun );

      void setTime( const timespec& expiryTime, const timespec& interval );

    private:

      timer_t  timerId;
      const Callback callback;

      Timestamp expiryTime;
      Duration  interval;
      bool      active;


  };

}


#endif
