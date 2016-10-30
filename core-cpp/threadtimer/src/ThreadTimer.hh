//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef ThreadTimer_HH
#define ThreadTimer_HH

#include "swa/Duration.hh"
#include "swa/ProcessMonitor.hh"

namespace ThreadTimer
{

  class ThreadTimer : public SWA::ProcessMonitor::MonitorConnection
  {

    public:
     static ThreadTimer& getInstance();

     virtual std::string getName() { return "Thread Timer"; }

     virtual void enteredAction();
     virtual void leavingAction();
     virtual void threadCompleting();
     virtual void threadCompleted();

     std::string getThreshold() const;
     void setThreshold( const std::string& millis);

     bool initialise();

     ThreadTimer();
     virtual ~ThreadTimer();

     void setActive( bool flag );
     bool isActive() const { return active; }
     void setTimeActions( bool flag ) { this->timeActions = flag; }
     bool isTimeActions() const { return timeActions; }

    private:
      void formatLine ( std::ostream& stream, const std::string& name, const SWA::Duration& startReal, const SWA::Duration& startUser, const SWA::Duration& startSystem );


    private:
      bool active;
      bool timeActions;
      bool timingThread;
      bool timingAction;
      SWA::Stack& processStack;
      std::string actionName;
      SWA::Duration threadStartUser;
      SWA::Duration threadStartSystem;
      SWA::Duration threadStartReal;
      SWA::Duration actionStartUser;
      SWA::Duration actionStartSystem;
      SWA::Duration actionStartReal;
      SWA::Duration threshold;
      std::ostringstream report;

  };

}

#endif
