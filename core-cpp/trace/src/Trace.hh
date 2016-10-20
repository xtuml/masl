//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef Trace_HH
#define Trace_HH

#include "boost/unordered_set.hpp"

#include "swa/Event.hh"
#include "swa/Stack.hh"
#include "swa/ProcessMonitor.hh"

namespace Trace
{

  class Trace : public SWA::ProcessMonitor::MonitorConnection
  {

    public:
      static Trace& getInstance();

      std::string getName() { return "Trace"; }

      void processingEvent ( const boost::shared_ptr<SWA::Event>& event );

      void enteredAction();
      void leavingAction();

      void startStatement();

      bool isTraceLines() const { return traceLines; }
      void setTraceLines( bool flag ) { traceLines = flag; checkConnect(); }

      bool isTraceEvents() const { return traceEvents; }
      void setTraceEvents( bool flag ) { traceEvents = flag; checkConnect(); }

      bool isTraceActions() const { return traceActions; }
      void setTraceActions( bool flag ) { traceActions = flag; checkConnect(); }

      bool initialise();

      Trace();
      virtual ~Trace();

    private:
      void checkConnect();

    private:
      boost::unordered_set<int> domains;
      bool traceEvents;
      bool traceActions;
      bool traceLines;
      SWA::Stack& processStack;

  };

}

#endif
