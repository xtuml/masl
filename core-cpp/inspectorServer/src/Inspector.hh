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

#ifndef Inspector_Inspector_HH
#define Inspector_Inspector_HH


#include "inspector/CommunicationChannel.hh"
#include "ConsoleRedirection.hh"

#include "swa/FileDescriptorListener.hh"
#include "swa/ProcessMonitor.hh"
#include "swa/Stack.hh"
#include "swa/Duration.hh"
#include "swa/Timestamp.hh"
#include "swa/TimerListener.hh"

#include "inspector/types.hh"
#include <queue>

namespace Inspector
{

  class Inspector : public SWA::ProcessMonitor::MonitorConnection
  {
    private:
      enum InspectorRequestId {
        GET_PROCESS_DATA            ,
        REDIRECT_CONSOLE            ,
        RUN_DOMAIN_SERVICE          ,
        RUN_TERMINATOR_SERVICE      ,
        RUN_OBJECT_SERVICE          ,
        FIRE_EVENT                  ,
        SCHEDULE_EVENT              ,
        CANCEL_TIMER                ,
        TRACE_LINES                 ,
        TRACE_BLOCKS                ,
        TRACE_EXCEPTIONS            ,
        TRACE_EVENTS                ,
        STEP_LINES                  ,
        STEP_BLOCKS                 ,
        STEP_EXCEPTIONS             ,
        STEP_EVENTS                 ,
        ENABLE_TIMERS               ,
        CONTINUE_EXECUTION          ,
        PAUSE_EXECUTION             ,
        STEP_EXECUTION              ,
        SET_BREAKPOINT              ,
        GET_INSTANCE_DATA           ,
        GET_SELECTED_INSTANCE_DATA  ,
        GET_SINGLE_INSTANCE_DATA    ,
        GET_RELATED_INSTANCE_DATA   ,
        GET_EVENT_QUEUE             ,
        GET_TIMER_QUEUE             ,
        GET_LOCAL_VARIABLES         ,
        GET_STACK                   ,
        GET_ASSIGNER_STATE          ,
        RUN_SCHEDULE                ,
        GET_INSTANCE_COUNT          ,
        CREATE_INSTANCE_POPULATION  ,
        CREATE_SINGLE_INSTANCE      ,
        UPDATE_SINGLE_INSTANCE      ,
        DELETE_SINGLE_INSTANCE      ,
        CREATE_RELATIONSHIPS        ,
        CREATE_SUPERSUBTYPES        ,
        INVOKE_PLUGIN_ACTION        ,
        GET_PLUGIN_FLAG             ,     
        GET_PLUGIN_PROPERTY         , 
        SET_PLUGIN_FLAG             ,     
        SET_PLUGIN_PROPERTY         , 
      };

      enum InspectorInfoId {
        CURRENT_POSITION_INFO       ,
        RUNNING_INFO                ,
        IDLE_INFO                   ,               
        BACKLOG_INFO               
      };

    public:
      Inspector ( int port );
      std::string getName() { return "Inspector"; }

      void pause();

      void connectToClient();
      void disconnectFromClient();
      void processRequest();
      
      virtual void pauseRequested() { pause(); }

      virtual void processRunning();
      virtual void processIdle();

      virtual void startStatement();

      virtual void exceptionRaised ( const std::string& message );
      virtual void enteredCatch();

      virtual void enteredAction();
      virtual void leavingAction();

      virtual void transitioningState ( int domainId, int objectId, int instanceId, int oldState, int newState );
      virtual void transitioningAssignerState ( int domainId, int objectId, int oldState, int newState );
      virtual void generatingEvent ( const boost::shared_ptr<SWA::Event>& event );
      virtual void processingEvent ( const boost::shared_ptr<SWA::Event>& event );

      virtual void firingTimer     ( int timerId );
      virtual void cancellingTimer ( int timerId );
      virtual void settingTimer    ( int timerId, const SWA::Timestamp& timeout, const boost::shared_ptr< ::SWA::Event >& event);

      virtual void endMainLoop();


      bool connectCallback(int) { connectToClient(); return false; }
      bool requestCallback(int)
      {
        if ( requestChannel.ready() )
        {
          processRequest();
          return !requestChannel.empty(); 
        }
        else
        {
          return false;
        }
      }
      
    private:
      bool hitBreakpoint();
      bool getProcessData();
      bool redirectConsole();

      bool runDomainService();
      bool runObjectService();
      bool runTerminatorService();
      bool fireEvent();
      bool scheduleEvent();
      bool cancelTimer();

      bool setTraceLines();
      bool setTraceBlocks();
      bool setTraceExceptions();
      bool setTraceEvents();
      bool setTraceInput();
      bool setTraceOutput();
      bool getTraceInput();
      bool getTraceOutput();

      bool setStepLines();
      bool setStepBlocks();
      bool setStepExceptions();
      bool setStepEvents();

      bool setEnableTimers();

      bool continueExecution();
      bool pauseExecution();
      bool stepExecution();
      bool setBreakpoint();

      bool getAssignerState();
      bool getInstanceCount();
      bool getInstanceData();
      bool createInstancePopulation();
      bool createRelationships();
      bool createSuperSubtypes();
      bool getSingleInstanceData();
      bool updateSingleInstance();
      bool createSingleInstance();
      bool deleteSingleInstance();
      bool getSelectedInstanceData();
      bool getRelatedInstanceData();
      bool getEventQueue();
      bool getTimerQueue();
      bool getLocalVariables();
      bool getStack();
      bool runSchedule();

      bool invokePluginAction();
      bool getPluginFlag();
      bool getPluginProperty();
      bool setPluginFlag();
      bool setPluginProperty();

      void writeProcessStatus();
      void setRunning();
      void setIdle();
      void setPaused();
      void setNotPaused();
      void outputTrace ( std::string prefix, bool showLineNo, std::string message="" ) const;

      void resetStatus();

    private:
      CommunicationChannel requestChannel;
      CommunicationChannel infoChannel;   
      ConsoleRedirection consoleRedirect;
      SWA::FileDescriptorListener connectListener;
      SWA::FileDescriptorListener requestListener;

      bool traceLines;
      bool traceBlocks;
      bool traceExceptions;
      bool traceEvents;

      bool stepLines;
      bool stepBlocks;
      bool stepExceptions;
      bool stepEvents;

      bool stepToNext;


      typedef boost::function<bool()> CommandFunction;
      std::map<InspectorRequestId,CommandFunction> commandLookup;
      bool paused;
      bool idle;

      std::set<SWA::StackFrame> breakpoints;

      std::queue<Callable> inThreadQueue;
      std::queue<Callable> endThreadQueue;

      static SWA::Duration backlogPollInterval;
      SWA::Timestamp backlogTime;
      SWA::TimerListener backlogTimer;
      
      void backlogCallback(int overrun);

  };

}

#endif
