//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "ThreadTimer.hh"
#include "swa/Stack.hh"
#include "swa/NameFormatter.hh"
#include "swa/CommandLine.hh"
#include "swa/Process.hh"
#include "swa/Duration.hh"
#include "swa/String.hh"
#include "swa/PluginRegistry.hh"
#include "metadata/MetaData.hh"
#include <iostream>
#include <boost/lexical_cast.hpp>

namespace
{
  const char* const ReportThresholdOption = "-tt-threshold";
  const char* const DetailedReportOff = "-tt-detailed-off";
  const char* const ReportOff = "-tt-off";

  bool started()
  {
    ThreadTimer::ThreadTimer::getInstance().initialise();
    return true;
  }

  bool initialise()
  {
    SWA::CommandLine::getInstance().registerOption (SWA::NamedOption(ReportThresholdOption,  "Thread timing report threshold (ms)",false, "threshold", true, false));
    SWA::CommandLine::getInstance().registerOption (SWA::NamedOption(DetailedReportOff,  "Thread timing detailed report",false));
    SWA::CommandLine::getInstance().registerOption (SWA::NamedOption(ReportOff,  "Thread timing reporting off",false));

    SWA::Process::getInstance().registerStartedListener(&started);
    return true;
  }

  bool init = initialise();

}

namespace ThreadTimer
{

  ThreadTimer::ThreadTimer()
    : active(!SWA::CommandLine::getInstance().optionPresent(ReportOff)),
      timeActions(!SWA::CommandLine::getInstance().optionPresent(DetailedReportOff)),
      timingThread(false),
      timingAction(false),
      processStack(SWA::Stack::getInstance()),
      threshold(SWA::Duration::fromMillis(SWA::CommandLine::getInstance().getIntOption(ReportThresholdOption,0)))
  {
  }

  ThreadTimer& ThreadTimer::getInstance()
  {
    static ThreadTimer singleton;
    return singleton;
  }

  bool ThreadTimer::initialise()
  {

    SWA::PluginRegistry::getInstance().registerFlagSetter(getName(),"Active",boost::bind(&ThreadTimer::setActive,this,_1));
    SWA::PluginRegistry::getInstance().registerFlagGetter(getName(),"Active",boost::bind(&ThreadTimer::isActive,this));
    SWA::PluginRegistry::getInstance().registerFlagSetter(getName(),"Show Detail",boost::bind(&ThreadTimer::setTimeActions,this,_1));
    SWA::PluginRegistry::getInstance().registerFlagGetter(getName(),"Show Detail",boost::bind(&ThreadTimer::isTimeActions,this));
    SWA::PluginRegistry::getInstance().registerPropertySetter(getName(),"Threshold (ms)",boost::bind(&ThreadTimer::setThreshold,this,_1));
    SWA::PluginRegistry::getInstance().registerPropertyGetter(getName(),"Threshold (ms)",boost::bind(&ThreadTimer::getThreshold,this));

    registerMonitor();
    if ( active )
    {
      connectToMonitor();
    }
    return true;
  }


  void ThreadTimer::setActive( bool flag )
  { 
    active = flag;
    if ( active )
    {
      connectToMonitor();
    }
    else
    {
      disconnectFromMonitor();
    }
  }


  void ThreadTimer::enteredAction()
  {
    if ( !active ) return;

    if ( processStack.getStackFrames().size() == 1 )
    {
      if ( !timingThread )
      {
        timingThread = true;
        timingAction = timeActions;
        report.str("");
        threadStartReal = SWA::Duration::real();
        threadStartUser = SWA::Duration::user();
        threadStartSystem = SWA::Duration::system();
        actionName = SWA::NameFormatter::formatStackFrame(processStack.top(),false);
      }
    
      if ( timingAction )
      {
        actionStartReal = SWA::Duration::real();
        actionStartUser = SWA::Duration::user();
        actionStartSystem = SWA::Duration::system();
      }
    }
  }

  void ThreadTimer::formatLine ( std::ostream& stream, const std::string& name, const SWA::Duration& startReal, const SWA::Duration& startUser, const SWA::Duration& startSystem )
  {
    stream << std::setw(60) << std::left << (name + " : ") << std::right
                  << "Real : " << std::setw(5) << (SWA::Duration::real() - startReal).millis() << "ms "
                  << "User : " << std::setw(5) << (SWA::Duration::user() - startUser).millis() << "ms "
                  << "Sys : "  << std::setw(5) << (SWA::Duration::system() - startSystem).millis() << "ms\n";
  }

  void ThreadTimer::leavingAction()
  {
    if ( timingAction && processStack.getStackFrames().size() == 1 )
    {
      formatLine(report,"  " + SWA::NameFormatter::formatStackFrame(processStack.top(),false),actionStartReal,actionStartUser,actionStartSystem);
    }
  }

  void ThreadTimer::threadCompleting()
  {
    if ( timingAction )
    {
      actionStartReal = SWA::Duration::real();
      actionStartUser = SWA::Duration::user();
      actionStartSystem = SWA::Duration::system();
    }
  }


  void ThreadTimer::threadCompleted()
  {
    if ( !timingThread ) return;

    SWA::Duration elapsedReal = SWA::Duration::real() - threadStartReal;

    if ( elapsedReal >= threshold )
    {
      formatLine(std::cout,actionName,threadStartReal,threadStartUser,threadStartSystem);
      if ( timingAction )
      {
        std::cout << report.str();
        formatLine(std::cout,"  Commit",actionStartReal,actionStartUser,actionStartSystem);
      }
      std::cout << std::flush;
    }
    timingThread = false;
    timingAction = false;
  }

  std::string ThreadTimer::getThreshold() const
  {
    return boost::lexical_cast<std::string>(threshold.millis());
  } 

  void ThreadTimer::setThreshold( const std::string& millis )
  {
    threshold = SWA::Duration::fromMillis(boost::lexical_cast<int>(millis));
    std::cout << "Report threshold = " << threshold << std::endl;
  } 


  ThreadTimer::~ThreadTimer()
  {
  }


}
