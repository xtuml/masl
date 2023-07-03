/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 * Classification: UK OFFICIAL
 * ----------------------------------------------------------------------------
 */

#include "Inspector.hh"
#include "inspector/ProcessHandler.hh"
#include "inspector/ActionHandler.hh"
#include "inspector/EventHandler.hh"
#include "inspector/GenericObjectHandler.hh"
#include "inspector/TerminatorHandler.hh"
#include "inspector/DomainHandler.hh"
#include "ConnectionError.hh"

#include "swa/Process.hh"
#include "swa/CommandLine.hh"
#include "swa/PluginRegistry.hh"
#include "swa/NameFormatter.hh"
#include "swa/Stack.hh"
#include "swa/EventTimers.hh"
#include "swa/EventTimer.hh"
#include "swa/ListenerPriority.hh"
#include "swa/Schedule.hh"

#include "metadata/MetaData.hh"
#include "boost/lexical_cast.hpp"

using namespace boost::placeholders;


namespace
{
  const char* ooaPort = getenv("OOA_PORT");
  const char* const PortNoOption = "-inspector-port";

  void startInspector(int basePort)
  {
    static Inspector::Inspector inspector(basePort);
  }

  bool initialise()
  {
    if ( ooaPort || SWA::CommandLine::getInstance().optionPresent(PortNoOption) )
    {
      try
      {
        int ooaPortNo = boost::lexical_cast<int>(SWA::CommandLine::getInstance().getOption(PortNoOption,(ooaPort?ooaPort:"")));
        startInspector(ooaPortNo);
      }
      catch ( const boost::bad_lexical_cast& e )
      {
        std::cerr << "Could not parse port number" << std::endl;
      }
    }
    return true;
  }

  struct Init
  {
    Init()
    {
      SWA::CommandLine::getInstance().registerOption (SWA::NamedOption(PortNoOption,  std::string("Inspector Port Number") + (ooaPort?std::string(" (") + ooaPort + ")":""), false, "portNo", true, false));
      SWA::Process::getInstance().registerStartedListener(&initialise);

    }

  } init;




}


namespace Inspector
{
  Inspector::Inspector ( int port )
    : requestChannel(20000+port),
      infoChannel(30000+port),
      consoleRedirect(40000+port),
      connectListener(requestChannel.getServerFd(),boost::bind(&Inspector::connectCallback,this,_1),SWA::Process::getInstance().getActivityMonitor()),
      requestListener(boost::bind(&Inspector::requestCallback,this,_1),SWA::Process::getInstance().getActivityMonitor()),
      traceLines(false),
      traceBlocks(false),
      traceExceptions(false),
      traceEvents(false),
      stepLines(false),
      stepBlocks(false),
      stepExceptions(false),
      stepEvents(false),
      stepToNext(false),
      paused(false),
      idle(true),
      backlogTime(),
      backlogTimer(SWA::ListenerPriority::getNormal(),boost::bind(&Inspector::backlogCallback,this,_1))
  {
    connectListener.setPriority(SWA::ListenerPriority::getHigh());
    requestListener.setPriority(SWA::ListenerPriority::getHigh());
 
    connectListener.activate();

    commandLookup.insert(std::make_pair(GET_PROCESS_DATA,           boost::bind(&Inspector::getProcessData,this)));
    commandLookup.insert(std::make_pair(REDIRECT_CONSOLE,           boost::bind(&Inspector::redirectConsole,this)));
    commandLookup.insert(std::make_pair(RUN_DOMAIN_SERVICE,         boost::bind(&Inspector::runDomainService,this)));
    commandLookup.insert(std::make_pair(RUN_TERMINATOR_SERVICE,     boost::bind(&Inspector::runTerminatorService,this)));
    commandLookup.insert(std::make_pair(RUN_OBJECT_SERVICE,         boost::bind(&Inspector::runObjectService,this)));
    commandLookup.insert(std::make_pair(FIRE_EVENT,                 boost::bind(&Inspector::fireEvent,this)));
    commandLookup.insert(std::make_pair(SCHEDULE_EVENT,             boost::bind(&Inspector::scheduleEvent,this)));
    commandLookup.insert(std::make_pair(CANCEL_TIMER,               boost::bind(&Inspector::cancelTimer,this)));
    commandLookup.insert(std::make_pair(TRACE_LINES,                boost::bind(&Inspector::setTraceLines,this)));
    commandLookup.insert(std::make_pair(TRACE_BLOCKS,               boost::bind(&Inspector::setTraceBlocks,this)));
    commandLookup.insert(std::make_pair(TRACE_EXCEPTIONS,           boost::bind(&Inspector::setTraceExceptions,this)));
    commandLookup.insert(std::make_pair(TRACE_EVENTS,               boost::bind(&Inspector::setTraceEvents,this)));
    commandLookup.insert(std::make_pair(STEP_LINES,                 boost::bind(&Inspector::setStepLines,this)));
    commandLookup.insert(std::make_pair(STEP_BLOCKS,                boost::bind(&Inspector::setStepBlocks,this)));
    commandLookup.insert(std::make_pair(STEP_EXCEPTIONS,            boost::bind(&Inspector::setStepExceptions,this)));
    commandLookup.insert(std::make_pair(STEP_EVENTS,                boost::bind(&Inspector::setStepEvents,this)));
    commandLookup.insert(std::make_pair(ENABLE_TIMERS,              boost::bind(&Inspector::setEnableTimers,this)));
    commandLookup.insert(std::make_pair(CONTINUE_EXECUTION,         boost::bind(&Inspector::continueExecution,this)));
    commandLookup.insert(std::make_pair(PAUSE_EXECUTION,            boost::bind(&Inspector::pauseExecution,this)));
    commandLookup.insert(std::make_pair(STEP_EXECUTION,             boost::bind(&Inspector::stepExecution,this)));
    commandLookup.insert(std::make_pair(SET_BREAKPOINT,             boost::bind(&Inspector::setBreakpoint,this)));
    commandLookup.insert(std::make_pair(GET_INSTANCE_DATA,          boost::bind(&Inspector::getInstanceData,this)));
    commandLookup.insert(std::make_pair(GET_SELECTED_INSTANCE_DATA, boost::bind(&Inspector::getSelectedInstanceData,this)));
    commandLookup.insert(std::make_pair(GET_SINGLE_INSTANCE_DATA,   boost::bind(&Inspector::getSingleInstanceData,this)));
    commandLookup.insert(std::make_pair(GET_RELATED_INSTANCE_DATA,  boost::bind(&Inspector::getRelatedInstanceData,this)));
    commandLookup.insert(std::make_pair(GET_EVENT_QUEUE,            boost::bind(&Inspector::getEventQueue,this)));
    commandLookup.insert(std::make_pair(GET_TIMER_QUEUE,            boost::bind(&Inspector::getTimerQueue,this)));
    commandLookup.insert(std::make_pair(GET_LOCAL_VARIABLES,        boost::bind(&Inspector::getLocalVariables,this)));
    commandLookup.insert(std::make_pair(GET_STACK,                  boost::bind(&Inspector::getStack,this)));
    commandLookup.insert(std::make_pair(GET_ASSIGNER_STATE,         boost::bind(&Inspector::getAssignerState,this)));
    commandLookup.insert(std::make_pair(RUN_SCHEDULE,               boost::bind(&Inspector::runSchedule,this)));
    commandLookup.insert(std::make_pair(GET_INSTANCE_COUNT,         boost::bind(&Inspector::getInstanceCount,this)));
    commandLookup.insert(std::make_pair(CREATE_INSTANCE_POPULATION, boost::bind(&Inspector::createInstancePopulation,this)));
    commandLookup.insert(std::make_pair(CREATE_SINGLE_INSTANCE,     boost::bind(&Inspector::createSingleInstance,this)));
    commandLookup.insert(std::make_pair(UPDATE_SINGLE_INSTANCE,     boost::bind(&Inspector::updateSingleInstance,this)));
    commandLookup.insert(std::make_pair(DELETE_SINGLE_INSTANCE,     boost::bind(&Inspector::deleteSingleInstance,this)));
    commandLookup.insert(std::make_pair(CREATE_RELATIONSHIPS,       boost::bind(&Inspector::createRelationships,this)));
    commandLookup.insert(std::make_pair(CREATE_SUPERSUBTYPES,       boost::bind(&Inspector::createSuperSubtypes,this)));
    commandLookup.insert(std::make_pair(INVOKE_PLUGIN_ACTION,       boost::bind(&Inspector::invokePluginAction,this)));
    commandLookup.insert(std::make_pair(GET_PLUGIN_FLAG,            boost::bind(&Inspector::getPluginFlag,this)));
    commandLookup.insert(std::make_pair(GET_PLUGIN_PROPERTY,        boost::bind(&Inspector::getPluginProperty,this)));
    commandLookup.insert(std::make_pair(SET_PLUGIN_FLAG,            boost::bind(&Inspector::setPluginFlag,this)));
    commandLookup.insert(std::make_pair(SET_PLUGIN_PROPERTY,        boost::bind(&Inspector::setPluginProperty,this)));
  
    registerMonitor();
  }

  SWA::Duration Inspector::backlogPollInterval = SWA::Duration::fromSeconds(10);

  void Inspector::backlogCallback(int overrun)
  {
    try
    {
      SWA::Duration backlog = SWA::Timestamp::now()-backlogTime;
      backlogTime += backlogPollInterval * (1+overrun);
      if ( infoChannel.isConnected() )
      {
        infoChannel << (int)BACKLOG_INFO << backlog.millis();
        infoChannel.flush();
      }
    }
    catch ( const ConnectionError& e ) 
    {
      std::cerr << "Lost Inspector Connection (" << e.what() << ")" << std::endl;
      disconnectFromClient();
    }


  }

  void Inspector::connectToClient()
  {
    signal(SIGPIPE,SIG_IGN);
    connectListener.cancel();

    requestChannel.attemptConnect();
    infoChannel.attemptConnect();
    consoleRedirect.attemptConnect();

    requestListener.setFd(requestChannel.getClientFd());
    requestListener.activate();

    connectToMonitor();
    backlogTime = SWA::Timestamp::now();
    backlogTimer.schedule(backlogTime,backlogPollInterval);
    writeProcessStatus();
  }

  void Inspector::disconnectFromClient()
  {
    backlogTimer.cancel();
    disconnectFromMonitor();
    requestChannel.disconnect();
    infoChannel.disconnect();
    consoleRedirect.disconnect();

    requestListener.clearFd();
    connectListener.activate();

    resetStatus();

    signal(SIGPIPE,SIG_DFL);

    SWA::EventTimers::getInstance().resumeTimers();
  }

  void Inspector::resetStatus()
  {
    traceLines = false;
    traceBlocks = false;
    traceExceptions = false;
    traceEvents = false;

    stepLines = false;
    stepBlocks = false;
    stepExceptions = false;
    stepEvents = false;

    stepToNext = false;
    paused = false;

    breakpoints.clear();

  }

  void Inspector::processRequest()
  {
    try
    {
      int commandId;
      requestChannel >> commandId;

      std::map<InspectorRequestId,CommandFunction>::const_iterator foundCommand = commandLookup.find(static_cast<InspectorRequestId>(commandId));

      if ( foundCommand == commandLookup.end() )
      {
        std::cerr << "Invalid Command " << commandId << std::endl;
        return;
      }

      CommandFunction command = foundCommand->second;

      bool success = command();

      requestChannel << success;
      requestChannel.flush();

      while ( !inThreadQueue.empty() )
      {
        setRunning();
        boost::function<void()> func = inThreadQueue.front();
        // Make sure we pop before running function, or we 
        // might end up trying to run it again as 
        // processRequest is reentrant. 
        inThreadQueue.pop();
        func();
        setIdle();
      }

    }
    catch ( const ConnectionError& e ) 
    {
      std::cerr << "Lost Inspector Connection (" << e.what() << ")" << std::endl;
      disconnectFromClient();
    }
  }


  bool Inspector::getProcessData()
  {
    requestChannel << SWA::ProcessMetaData::getProcess();
    return true;
  }

  bool Inspector::redirectConsole()
  {
    bool redirect;
    requestChannel >> redirect;

    if ( redirect )
    {
      consoleRedirect.startRedirection();
    }
    else
    {
      consoleRedirect.stopRedirection();
    }

    return true;
  }

  bool Inspector::runDomainService()
  {
    int domainId;
    int serviceId;
    requestChannel >> domainId >> serviceId;

    inThreadQueue.push(ProcessHandler::getInstance().getDomainHandler(domainId).getServiceHandler(serviceId).getInvoker(requestChannel));

    return true;
  }

  bool Inspector::runTerminatorService()
  {
    int domainId;
    int terminatorId;
    int serviceId;
    requestChannel >> domainId >> terminatorId >> serviceId;

    inThreadQueue.push(ProcessHandler::getInstance().getDomainHandler(domainId).getTerminatorHandler(terminatorId).getServiceHandler(serviceId).getInvoker(requestChannel));

    return true;
  }


  bool Inspector::runObjectService()
  {
    int domainId;
    int objectId;
    int serviceId;
    requestChannel >> domainId >> objectId >> serviceId;

    inThreadQueue.push(ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId).getServiceHandler(serviceId).getInvoker(requestChannel));

    return true;
  }

  bool Inspector::fireEvent()
  {
    int domainId;
    int objectId;
    int eventId;
    requestChannel >> domainId >> objectId >> eventId;

    boost::shared_ptr<SWA::Event> event = ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId).getEventHandler(eventId).getEvent(requestChannel);
    
    if (event) SWA::Process::getInstance().getEventQueue().addEvent(event);

    return true;
  }

  bool Inspector::scheduleEvent()
  {
    SWA::EventTimers::TimerIdType timerId;
    SWA::Timestamp expiry;
    SWA::Duration period;
    int domainId;
    int objectId;
    int eventId;
    requestChannel >> timerId >> expiry >> period >> domainId >> objectId >> eventId;

    boost::shared_ptr<SWA::Event> event = ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId).getEventHandler(eventId).getEvent(requestChannel);

    if (event) SWA::EventTimers::getInstance().scheduleTimer(timerId,expiry,period,event);

    return true;
  }

  bool Inspector::cancelTimer()
  {
    SWA::EventTimers::TimerIdType timerId;
    requestChannel >> timerId;

    SWA::EventTimers::getInstance().cancelTimer(timerId);

    return true;
  }

  bool Inspector::setTraceLines()
  {
    requestChannel >> traceLines;
    return true;
  }

  bool Inspector::setTraceBlocks()
  {
    requestChannel >> traceBlocks;
    return true;
  }

  bool Inspector::setTraceExceptions()
  {
    requestChannel >> traceExceptions;
    return true;
  }

  bool Inspector::setTraceEvents()
  {
    requestChannel >> traceEvents;
    return true;
  }

  bool Inspector::setTraceInput()
  {
    bool traceInput;
    requestChannel >> traceInput;

    return true;
  }

  bool Inspector::setTraceOutput()
  {
    bool traceOutput;
    requestChannel >> traceOutput;

    return true;
  }

  bool Inspector::getTraceInput()
  {
    requestChannel << false;
    return true;
  }

  bool Inspector::getTraceOutput()
  {
    requestChannel << false;
    return true;
  }


  bool Inspector::setStepLines()
  {
    requestChannel >> stepLines;
    return true;
  }

  bool Inspector::setStepBlocks()
  {
    requestChannel >> stepBlocks;
    return true;
  }

  bool Inspector::setStepExceptions()
  {
    requestChannel >> stepExceptions;
    return true;
  }

  bool Inspector::setStepEvents()
  {
    requestChannel >> stepEvents;
    return true;
  }


  bool Inspector::setEnableTimers()
  {
    bool timers_enabled;
    requestChannel >> timers_enabled;
    if ( timers_enabled )
    {
       SWA::EventTimers::getInstance().resumeTimers();
    }
    else
    {
       SWA::EventTimers::getInstance().suspendTimers();
    }
    return true;
  }

  bool Inspector::continueExecution()
  {
    stepToNext = false;
    setNotPaused();
    return true;
  }

  bool Inspector::pauseExecution()
  {
    stepToNext = true;
    setPaused();  
    return true;
  }

  bool Inspector::stepExecution()
  {
    stepToNext = true;
    setNotPaused();
    return true;
  }

  bool Inspector::setBreakpoint()
  {
    bool set;
    int actionType;
    int domainId;
    int objectId;
    int actionId;
    int lineNo;
    requestChannel >> actionType 
                   >> domainId
                   >> objectId
                   >> actionId
                   >> lineNo
                   >> set;

    if(set)
    {
      return breakpoints.insert(SWA::StackFrame(SWA::StackFrame::ActionType(actionType),domainId,objectId, actionId,lineNo)).second;
    }
    else
    {
      return breakpoints.erase(SWA::StackFrame(SWA::StackFrame::ActionType(actionType),domainId,objectId, actionId,lineNo));
    }
  }

  bool Inspector::getAssignerState()
  { 
    int domainId;
    int objectId;
    requestChannel >> domainId >> objectId;
    requestChannel << 0;
    return true;
  }

  bool Inspector::getInstanceCount()
  { 
    int domainId;
    int objectId;
    requestChannel >> domainId >> objectId;
    requestChannel << ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId).getCardinality();
    return true;
  }

  bool Inspector::createInstancePopulation()
  {     
    int domainId;
    int objectId;
    int popSize;
    requestChannel >> domainId >> objectId >> popSize;

    GenericObjectHandler& handler = ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId);

    for ( int i = 0; i < popSize; ++i )
    {
      handler.createInstance(requestChannel);
    }
    requestChannel.flush();

    return true;
  }

  bool Inspector::createSingleInstance()
  { 
    int domainId;
    int objectId;
    requestChannel >> domainId >> objectId;

    ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId).createInstance(requestChannel);
    requestChannel.flush();

    return true;
  }

  bool Inspector::deleteSingleInstance()
  { 
    int domainId;
    int objectId;
    int instanceId;
    requestChannel >> domainId >> objectId >> instanceId;

    ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId).deleteInstance(requestChannel, instanceId);
    return true;
  }

  bool Inspector::createRelationships() 
  {
    int domainId;
    int relId;
    int popSize;
    requestChannel >> domainId >> relId >> popSize;

    DomainHandler& handler = ProcessHandler::getInstance().getDomainHandler(domainId);

    for ( int i = 0; i < popSize; ++i )
    {
      handler.createRelationship(requestChannel, relId);
    }
    requestChannel.flush();

    return true;
  }

  bool Inspector::createSuperSubtypes()
  {
    int domainId;
    int relId;
    int popSize;
    requestChannel >> domainId >> relId >> popSize;

    DomainHandler& handler = ProcessHandler::getInstance().getDomainHandler(domainId);

    for ( int i = 0; i < popSize; ++i )
    {
      handler.createRelationship(requestChannel, relId);
    }
    requestChannel.flush();

    return true;
  }

  bool Inspector::updateSingleInstance() { return false; }


  bool Inspector::getInstanceData()
  { 
    int domainId;
    int objectId;
    requestChannel >> domainId >> objectId;

    ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId).writePopulation(requestChannel);

    return true;
  }


  bool Inspector::getSingleInstanceData()
  { 
    int domainId;
    int objectId;
    int instanceId;

    requestChannel >> domainId >> objectId >> instanceId;

    ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId).writeInstance(requestChannel, instanceId);

    return true;
  }


  bool Inspector::getSelectedInstanceData()  
  {
    int domainId;
    int objectId;
    std::vector<int> pks;

    requestChannel >> domainId >> objectId >> pks;

    ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId).writeSelectedInstances(requestChannel,pks);
     
    return true;
  }

  bool Inspector::getRelatedInstanceData()
  {
    int domainId;
    int objectId;
    int pk;
    int relId;
    requestChannel >> domainId >> objectId >> pk >> relId;

    ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId).writeRelatedInstances(requestChannel, pk, relId);

    return true;
  }


  bool Inspector::getEventQueue()
  {
    requestChannel << SWA::Process::getInstance().getEventQueue().getEvents();

    return true;
  }

  bool Inspector::getTimerQueue()
  {
    requestChannel << SWA::EventTimers::getInstance().getQueuedEvents();
    return true;
  }

  bool Inspector::getLocalVariables()
  {
    int stackDepth;
    requestChannel >> stackDepth;

    const SWA::StackFrame& frame = SWA::Stack::getInstance()[stackDepth];

    switch ( frame.getType() )
    {
      case SWA::StackFrame::DomainService:
        ProcessHandler::getInstance().getDomainHandler(frame.getDomainId()).getServiceHandler(frame.getActionId()).writeLocalVars(requestChannel,frame);
        break;
      case SWA::StackFrame::TerminatorService:
        ProcessHandler::getInstance().getDomainHandler(frame.getDomainId()).getTerminatorHandler(frame.getObjectId()).getServiceHandler(frame.getActionId()).writeLocalVars(requestChannel,frame);
        break;
      case SWA::StackFrame::ObjectService:
        ProcessHandler::getInstance().getDomainHandler(frame.getDomainId()).getGenericObjectHandler(frame.getObjectId()).getServiceHandler(frame.getActionId()).writeLocalVars(requestChannel,frame);
        break;
      case SWA::StackFrame::StateAction:
        ProcessHandler::getInstance().getDomainHandler(frame.getDomainId()).getGenericObjectHandler(frame.getObjectId()).getStateHandler(frame.getActionId()).writeLocalVars(requestChannel,frame);
        break;
    }
    return true;
  }

  bool Inspector::hitBreakpoint()
  {
    if ( breakpoints.size() > 0 && !SWA::Stack::getInstance().empty())
    {
      return breakpoints.count(SWA::Stack::getInstance().top());
    }
    else
    {
      return false;
    }
  }  

  bool Inspector::getStack()
  {
    requestChannel << SWA::Stack::getInstance().getStackFrames();
    return true;
  }
  
  void Inspector::endMainLoop()
  {
    while ( !endThreadQueue.empty() )
    {
      endThreadQueue.front()();
      endThreadQueue.pop();
    }
  }

  bool Inspector::runSchedule()
  {
    std::string filename;
    std::string script;
    requestChannel >> filename >> script; 

    SWA::Schedule schedule(filename,script);

    if ( schedule.isValid() )
    {
      endThreadQueue.push(boost::bind(&SWA::Process::runSchedule,boost::ref(SWA::Process::getInstance()),schedule));
      return true;
    }
    else
    {
      return false;
    }
  }

  void Inspector::outputTrace ( std::string prefix, bool showLineNo, std::string message ) const
  {
    std::cout << "Inspector: " << std::string((SWA::Stack::getInstance().getStackFrames().size())*2,' ') << prefix;
    if ( !SWA::Stack::getInstance().getStackFrames().empty() )
    {
      std::cout << SWA::NameFormatter::formatStackFrame(SWA::Stack::getInstance().top(),showLineNo);
      if ( message.size() > 0 )
      {
        std::cout << " : ";
      }
    }
    std::cout << message << std::endl;
  }


  bool Inspector::invokePluginAction()
  {
    std::string pluginName;
    std::string actionName;

    requestChannel >> pluginName >> actionName;

    SWA::PluginRegistry::getInstance().invokeAction(pluginName,actionName);

    return true;
  }

  bool Inspector::getPluginFlag()
  {
    std::string pluginName;
    std::string flagName;

    requestChannel >> pluginName >> flagName;
    requestChannel << SWA::PluginRegistry::getInstance().getFlag(pluginName,flagName);

    return true;
  }

  bool Inspector::getPluginProperty()
  {
    std::string pluginName;
    std::string propertyName;

    requestChannel >> pluginName >> propertyName;
    requestChannel <<SWA::PluginRegistry::getInstance().getProperty(pluginName,propertyName);

    return true;
  }

  bool Inspector::setPluginFlag()
  {
    std::string pluginName;
    std::string flagName;
    bool value;

    requestChannel >> pluginName >> flagName >> value;
    SWA::PluginRegistry::getInstance().setFlag(pluginName,flagName, value);

    return true;
  }

  bool Inspector::setPluginProperty()
  {
    std::string pluginName;
    std::string propertyName;
    std::string value;

    requestChannel >> pluginName >> propertyName >> value;
    SWA::PluginRegistry::getInstance().setProperty(pluginName,propertyName, value);

    return true;
  }



  void Inspector::startStatement()
  {
    while(requestChannel.ready()) processRequest();

    if ( traceLines ) outputTrace("  ",true);

    if ( stepLines || stepToNext || hitBreakpoint() ) pause();
  }

  void Inspector::enteredAction()
  {
    while(requestChannel.ready()) processRequest();

    if ( traceBlocks || traceLines ) outputTrace("->",false);

    if ( stepBlocks || stepLines || stepToNext || hitBreakpoint() ) pause();
  }

  void Inspector::leavingAction()
  {
    while(requestChannel.ready()) processRequest();

    if ( traceBlocks || traceLines ) outputTrace("<-",false);

    if ( stepBlocks || stepLines || stepToNext || hitBreakpoint()) pause();
  }

  void Inspector::enteredCatch()
  {
    while(requestChannel.ready()) processRequest();

    if ( traceExceptions ) outputTrace("! ",false,"Caught exception ");

    if ( stepExceptions || stepBlocks || stepLines || stepToNext || hitBreakpoint()) pause();
  }

  void Inspector::exceptionRaised ( const std::string& message )
  {
    while(requestChannel.ready()) processRequest();

    if ( traceExceptions ) outputTrace("! ",false," : Raised exception \"" + message + "\"");

    if ( stepExceptions ) pause();    
  }

  std::string getInstanceText ( int domainId, int objectId, SWA::IdType instanceId )
  {
    return SWA::NameFormatter::formatObjectName(domainId, objectId) + 
                  "(" + ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId).getIdentifierText(instanceId) + ")";
  }

  void Inspector::transitioningState ( int domainId, int objectId, int instanceId, int oldState, int newState )
  {
    while(requestChannel.ready()) processRequest();

    if ( traceEvents ) outputTrace("* ",true, "State Transition for " + getInstanceText(domainId,objectId,instanceId) + " from " + SWA::NameFormatter::formatStateName(domainId, objectId, oldState) + " to " + SWA::NameFormatter::formatStateName(domainId, objectId, newState));
  }

  void Inspector::transitioningAssignerState ( int domainId, int objectId, int oldState, int newState )
  {
    while(requestChannel.ready()) processRequest();

    if ( traceEvents ) outputTrace("* ",true, "Assigner State Transition from " + SWA::NameFormatter::formatStateName(domainId, objectId, oldState) + " to " + SWA::NameFormatter::formatStateName(domainId, objectId, newState));
  }

  void Inspector::generatingEvent ( const boost::shared_ptr<SWA::Event>& event )
  {
    while(requestChannel.ready()) processRequest();

    if ( traceEvents )
    {
      std::string message = "Generating Event " + SWA::NameFormatter::formatEventName(event->getDomainId(), event->getObjectId(), event->getEventId());
      if ( event->getHasSource() )
      {
        message+= " from " + getInstanceText(event->getDomainId(),event->getSourceObjectId(),event->getSourceInstanceId());
      }

      if ( event->getHasDest() )
      {
        message+= " to " + getInstanceText(event->getDomainId(),event->getObjectId(),event->getDestInstanceId());
      }

      outputTrace("* ",true, message);
    }

    if ( stepEvents ) pause();    
  }

  void Inspector::processingEvent ( const boost::shared_ptr<SWA::Event>& event )
  {
    while(requestChannel.ready()) processRequest();

    if ( traceEvents )
    {
      std::string message = "Processing Event " + SWA::NameFormatter::formatEventName(event->getDomainId(), event->getObjectId(), event->getEventId());
      if ( event->getHasSource() )
      {
        message+= " from " + getInstanceText(event->getDomainId(),event->getSourceObjectId(),event->getSourceInstanceId());
      }

      if ( event->getHasDest() )
      {
        message+= " to " + getInstanceText(event->getDomainId(),event->getObjectId(),event->getDestInstanceId());
      }

      outputTrace("* ",false, message);
    }
  }

  void Inspector::cancellingTimer ( int timerId )
  {
    while(requestChannel.ready()) processRequest();

    if ( traceEvents )
    {
      std::string message = "Cancelling timer " + boost::lexical_cast<std::string>(timerId);
      outputTrace("* ",false, message);
    }
  }

  void Inspector::settingTimer ( int timerId, const SWA::Timestamp& timeout, const boost::shared_ptr< ::SWA::Event >& event)
  {
    while(requestChannel.ready()) processRequest();

    if ( traceEvents )
    {
      std::string message = "Scheduling timer " + boost::lexical_cast<std::string>(timerId) + " at " +  boost::lexical_cast<std::string>(timeout) + " for event " + SWA::NameFormatter::formatEventName(event->getDomainId(), event->getObjectId(), event->getEventId());
      if ( event->getHasSource() )
      {
        message+= " from " + getInstanceText(event->getDomainId(),event->getSourceObjectId(),event->getSourceInstanceId());
      }

      if ( event->getHasDest() )
      {
        message+= " to " + getInstanceText(event->getDomainId(),event->getObjectId(),event->getDestInstanceId());
      }

      outputTrace("* ",true, message);
    }
  }

  void Inspector::firingTimer ( int timerId )
  {
    while(requestChannel.ready()) processRequest();

    if ( traceEvents )
    {
      std::string message = "Firing timer " + boost::lexical_cast<std::string>(timerId);
      outputTrace("* ",false, message);
    }
  }



  void Inspector::processRunning()
  {
    // Whilst processing is going on we will be 
    // servicing the sockets explicitly, so turn off the 
    // listeners. This prevents all the signals for IO state 
    // changes getting queued up, and possibly blowing the 
    // queue size. 
    if ( requestChannel.isConnected() )
    {
      requestListener.cancel();
    }
    else
    {
      connectListener.cancel();
    }
    setRunning();
  }

  void Inspector::processIdle()
  {
    setIdle();
    // Turn listening back on. (See startProcessingEventQueue comment)
    if ( requestChannel.isConnected() )
    {
      requestListener.activate();
    }
    else
    {
      connectListener.activate();
    }
  }

  void Inspector::writeProcessStatus()
  {
    try
    {
      if ( infoChannel.isConnected() )
      {
        if ( paused )
        {
          if ( SWA::Stack::getInstance().empty() )
          {
            infoChannel << (int)CURRENT_POSITION_INFO 
                        << (int)-1;
          }
          else
          {
             infoChannel << (int)CURRENT_POSITION_INFO
                         << SWA::Stack::getInstance().top();
          }
        }  
        else 
        {
          if ( idle )
          {
            infoChannel << (int)IDLE_INFO;
          }
          else
          {
            infoChannel << (int)RUNNING_INFO;
          }
        }
        infoChannel.flush();
      }
    }
    catch ( const ConnectionError& e ) 
    {
      std::cerr << "Lost Inspector Connection (" << e.what() << ")" << std::endl;
      disconnectFromClient();
    }

  }



  void Inspector::setRunning()
  {
    idle = false;
    writeProcessStatus();
  }

  void Inspector::setIdle()
  {
    idle = true;
    writeProcessStatus();
  }

  void Inspector::setPaused()
  {
    paused = true;
    writeProcessStatus();
  }

  void Inspector::setNotPaused()
  {
    paused = false;
    writeProcessStatus();
  }



  void Inspector::pause()
  {
    setPaused();
    if ( !requestChannel.isConnected() )
    {
      connectToClient();
    }

    if ( requestChannel.isConnected() )
    {
      requestListener.cancel();
    }

    while ( requestChannel.isConnected() && paused )
    {
      processRequest();
    }

    if ( requestChannel.isConnected() )
    {
      requestListener.activate();
    }

  }

}
