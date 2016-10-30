//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "swa/Process.hh"
#include "swa/Timestamp.hh"
#include "swa/Duration.hh"
#include "swa/TimerListener.hh"
#include "swa/ListenerPriority.hh"
#include "swa/PluginRegistry.hh"
#include "swa/Sequence.hh"
#include "swa/String.hh"
#include "swa/CommandLine.hh"

#include <boost/lexical_cast.hpp>
#include <iostream>

namespace BacklogMonitor
{
  const char* const PollIntervalOption = "-backlog-poll-interval";
  const char* const ReportThresholdOption = "-backlog-report-threshold";

  class BacklogMonitor 
  {
    public:
     static BacklogMonitor& getInstance();

     std::string getName() { return "Backlog Monitor"; }

     bool initialise();
     void startup();

     void setActive( bool active );
     bool isActive() const { return active; }

     void setPollInterval( const std::string& interval );
     void setReportThreshold( const std::string& threshold );
     std::string getPollInterval() const { return boost::lexical_cast<std::string>(pollInterval.seconds()); }
     std::string getReportThreshold() const { return boost::lexical_cast<std::string>(reportThreshold.seconds()); }

     BacklogMonitor();
    private:
      bool active;
      SWA::Duration pollInterval;
      SWA::Duration reportThreshold;
      SWA::Duration lastBacklog;

      SWA::Timestamp expectedTime;
      SWA::TimerListener timer;
      
      void timerCallback(int overrun);
      void report(const SWA::Duration& backlog) const;
      
  };


  BacklogMonitor::BacklogMonitor()
     :active(true),
      pollInterval(),
      reportThreshold(),
      lastBacklog(),
      expectedTime(),
      timer(SWA::ListenerPriority::getNormal(),boost::bind(&BacklogMonitor::timerCallback,this,_1))
      
  {
  }

  BacklogMonitor& BacklogMonitor::getInstance()
  {
    static BacklogMonitor singleton;
    return singleton;
  }

  bool BacklogMonitor::initialise()
  {
    SWA::PluginRegistry::getInstance().registerPropertySetter(getName(),"Poll interval (secs)",boost::bind(&BacklogMonitor::setPollInterval,this,_1));
    SWA::PluginRegistry::getInstance().registerPropertySetter(getName(),"Report Threshold (secs)",boost::bind(&BacklogMonitor::setReportThreshold,this,_1));
    SWA::PluginRegistry::getInstance().registerPropertyGetter(getName(),"Poll interval (secs)",boost::bind(&BacklogMonitor::getPollInterval,this));
    SWA::PluginRegistry::getInstance().registerPropertyGetter(getName(),"Report Threshold (secs)",boost::bind(&BacklogMonitor::getReportThreshold,this));
    SWA::PluginRegistry::getInstance().registerFlagSetter(getName(),"Active",boost::bind(&BacklogMonitor::setActive,this,_1));
    SWA::PluginRegistry::getInstance().registerFlagGetter(getName(),"Active",boost::bind(&BacklogMonitor::isActive,this));

    SWA::Process::getInstance().registerStartedListener(boost::bind(&BacklogMonitor::startup,this));

    SWA::CommandLine::getInstance().registerOption (SWA::NamedOption(PollIntervalOption,  "Poll interval for backlog reporting (0 = disabled)",false, "interval", true, false));
    SWA::CommandLine::getInstance().registerOption (SWA::NamedOption(ReportThresholdOption,  "Threshold for backlog reporting ",false, "threshold", true, false));

    return true;
  }

  void BacklogMonitor::setActive( bool active )
  { 
    this->active = active;
    if ( active && pollInterval > SWA::Duration::zero() )
    {
      expectedTime = SWA::Timestamp::now();
      timer.schedule(expectedTime,pollInterval);
    }
    else
    {
      timer.cancel();
    }
  }

  void BacklogMonitor::setPollInterval( const std::string& interval )
  { 
    pollInterval = SWA::Duration::fromSeconds(boost::lexical_cast<double>(interval));
    setActive(active);
  }

  void BacklogMonitor::setReportThreshold( const std::string& threshold )
  { 
    reportThreshold = SWA::Duration::fromSeconds(boost::lexical_cast<double>(threshold));
  }

  void BacklogMonitor::startup()
  {
    setPollInterval(SWA::CommandLine::getInstance().getOption(PollIntervalOption,"60"));
    setReportThreshold(SWA::CommandLine::getInstance().getOption(ReportThresholdOption,"10"));
    setActive(active);
  }

  void BacklogMonitor::timerCallback(int overrun)
  {
    SWA::Duration backlog = SWA::Timestamp::now()-expectedTime;
    expectedTime += pollInterval * (1+overrun);

    if ( backlog > reportThreshold )
    {
      report(backlog);
      lastBacklog = backlog;
    }
    else if ( backlog < lastBacklog )
    {
      report(backlog);
      lastBacklog = SWA::Duration::zero();
    }
  }

  void BacklogMonitor::report(const SWA::Duration& backlog) const
  {
    static const std::string suffixArray[] = { "h ", "m ", "s " };
    static const SWA::Sequence<SWA::String> suffixes(suffixArray,suffixArray+3);

    std::clog << "Backlog : " << backlog.format(SWA::Duration::Hour,SWA::Duration::Second,SWA::Duration::TowardsZero,true,1,false,0,"","",suffixes) << "\n" << std::flush;
  }

}


namespace
{
  bool registered = BacklogMonitor::BacklogMonitor::getInstance().initialise();  
}

