/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */


#include "Inspector.hh"
#include "ConnectionError.hh"
#include "inspector/ActionHandler.hh"
#include "inspector/DomainHandler.hh"
#include "inspector/EventHandler.hh"
#include "inspector/GenericObjectHandler.hh"
#include "inspector/ProcessHandler.hh"
#include "inspector/TerminatorHandler.hh"

#include "swa/CommandLine.hh"
#include "swa/EventTimer.hh"
#include "swa/EventTimers.hh"
#include "swa/NameFormatter.hh"
#include "swa/PluginRegistry.hh"
#include "swa/Process.hh"
#include "swa/Schedule.hh"
#include "swa/Stack.hh"

#include "metadata/MetaData.hh"
#include <format>
#include <print>

namespace {
    const char *ooaPort = getenv("OOA_PORT");
    const char *const PortNoOption = "-inspector-port";

    void startInspector(int basePort) {
        static Inspector::Inspector inspector(SWA::Process::getInstance().getIOContext().get_executor(), basePort);
    }

    bool initialise() {
        if (ooaPort || SWA::CommandLine::getInstance().optionPresent(PortNoOption)) {
            try {
                int ooaPortNo =
                    std::stoi(SWA::CommandLine::getInstance().getOption(PortNoOption, (ooaPort ? ooaPort : "")));
                startInspector(ooaPortNo);
            } catch (const std::invalid_argument &e) {
                std::cerr << "Could not parse port number" << std::endl;
            }
        }
        return true;
    }

    struct Init {
        Init() {
            SWA::CommandLine::getInstance().registerOption(SWA::NamedOption(
                PortNoOption,
                std::string("Inspector Port Number") + (ooaPort ? std::string(" (") + ooaPort + ")" : ""),
                false,
                "portNo",
                true,
                false
            ));
            SWA::Process::getInstance().registerStartedListener(&initialise);
        }

    } init;

} // namespace

namespace Inspector {
    Inspector::Inspector(asio::any_io_executor executor, asio::ip::port_type port)
        : requestChannel("request", executor, 20000 + port),
          infoChannel("info", executor, 30000 + port),
          consoleRedirect(executor, 40000 + port),
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
          backlogTimer(executor) {

        commandLookup.emplace(GET_PROCESS_DATA, [this]() {
            return getProcessData();
        });
        commandLookup.emplace(REDIRECT_CONSOLE, [this]() {
            return redirectConsole();
        });
        commandLookup.emplace(RUN_DOMAIN_SERVICE, [this]() {
            return runDomainService();
        });
        commandLookup.emplace(RUN_TERMINATOR_SERVICE, [this]() {
            return runTerminatorService();
        });
        commandLookup.emplace(RUN_OBJECT_SERVICE, [this]() {
            return runObjectService();
        });
        commandLookup.emplace(FIRE_EVENT, [this]() {
            return fireEvent();
        });
        commandLookup.emplace(SCHEDULE_EVENT, [this]() {
            return scheduleEvent();
        });
        commandLookup.emplace(CANCEL_TIMER, [this]() {
            return cancelTimer();
        });
        commandLookup.emplace(TRACE_LINES, [this]() {
            return setTraceLines();
        });
        commandLookup.emplace(TRACE_BLOCKS, [this]() {
            return setTraceBlocks();
        });
        commandLookup.emplace(TRACE_EXCEPTIONS, [this]() {
            return setTraceExceptions();
        });
        commandLookup.emplace(TRACE_EVENTS, [this]() {
            return setTraceEvents();
        });
        commandLookup.emplace(STEP_LINES, [this]() {
            return setStepLines();
        });
        commandLookup.emplace(STEP_BLOCKS, [this]() {
            return setStepBlocks();
        });
        commandLookup.emplace(STEP_EXCEPTIONS, [this]() {
            return setStepExceptions();
        });
        commandLookup.emplace(STEP_EVENTS, [this]() {
            return setStepEvents();
        });
        commandLookup.emplace(ENABLE_TIMERS, [this]() {
            return setEnableTimers();
        });
        commandLookup.emplace(CONTINUE_EXECUTION, [this]() {
            return continueExecution();
        });
        commandLookup.emplace(PAUSE_EXECUTION, [this]() {
            return pauseExecution();
        });
        commandLookup.emplace(STEP_EXECUTION, [this]() {
            return stepExecution();
        });
        commandLookup.emplace(SET_BREAKPOINT, [this]() {
            return setBreakpoint();
        });
        commandLookup.emplace(GET_INSTANCE_DATA, [this]() {
            return getInstanceData();
        });
        commandLookup.emplace(GET_SELECTED_INSTANCE_DATA, [this]() {
            return getSelectedInstanceData();
        });
        commandLookup.emplace(GET_SINGLE_INSTANCE_DATA, [this]() {
            return getSingleInstanceData();
        });
        commandLookup.emplace(GET_RELATED_INSTANCE_DATA, [this]() {
            return getRelatedInstanceData();
        });
        commandLookup.emplace(GET_EVENT_QUEUE, [this]() {
            return getEventQueue();
        });
        commandLookup.emplace(GET_TIMER_QUEUE, [this]() {
            return getTimerQueue();
        });
        commandLookup.emplace(GET_LOCAL_VARIABLES, [this]() {
            return getLocalVariables();
        });
        commandLookup.emplace(GET_STACK, [this]() {
            return getStack();
        });
        commandLookup.emplace(GET_ASSIGNER_STATE, [this]() {
            return getAssignerState();
        });
        commandLookup.emplace(RUN_SCHEDULE, [this]() {
            return runSchedule();
        });
        commandLookup.emplace(GET_INSTANCE_COUNT, [this]() {
            return getInstanceCount();
        });
        commandLookup.emplace(CREATE_INSTANCE_POPULATION, [this]() {
            return createInstancePopulation();
        });
        commandLookup.emplace(CREATE_SINGLE_INSTANCE, [this]() {
            return createSingleInstance();
        });
        commandLookup.emplace(UPDATE_SINGLE_INSTANCE, [this]() {
            return updateSingleInstance();
        });
        commandLookup.emplace(DELETE_SINGLE_INSTANCE, [this]() {
            return deleteSingleInstance();
        });
        commandLookup.emplace(CREATE_RELATIONSHIPS, [this]() {
            return createRelationships();
        });
        commandLookup.emplace(CREATE_SUPERSUBTYPES, [this]() {
            return createSuperSubtypes();
        });
        commandLookup.emplace(INVOKE_PLUGIN_ACTION, [this]() {
            return invokePluginAction();
        });
        commandLookup.emplace(GET_PLUGIN_FLAG, [this]() {
            return getPluginFlag();
        });
        commandLookup.emplace(GET_PLUGIN_PROPERTY, [this]() {
            return getPluginProperty();
        });
        commandLookup.emplace(SET_PLUGIN_FLAG, [this]() {
            return setPluginFlag();
        });
        commandLookup.emplace(SET_PLUGIN_PROPERTY, [this]() {
            return setPluginProperty();
        });

        activateAsyncListen();
        registerMonitor();
    }

    void Inspector::activateAsyncListen() {
        if (!requestChannel.isConnected()) {
            requestChannel.asyncAcceptReady([this]() {
                connectToClient();
            });
        } else {
            requestChannel.asyncReadReady([this]() {
                SWA::Process::getInstance().getIOContext().dispatch(SWA::Process::getInstance().wrapProcessingThread(
                    "Inspector Request",
                    [this]() {
                        processRequest();
                        if (requestChannel.isConnected() && requestChannel.empty()) {
                            activateAsyncListen();
                        }
                    }
                ));
            });
        }
    }

    void Inspector::deactivateAsyncListen() {
        requestChannel.cancelAccept();
        requestChannel.cancelRead();
    }

    void Inspector::pollBacklog() {
        backlogTimer.expires_after(backlogPollInterval);
        backlogTimer.async_wait([this](const asio::error_code &ec) {
            if (ec == asio::error::operation_aborted)
                return;

            if (infoChannel.isConnected()) {
                infoChannel << (int)BACKLOG_INFO
                            << std::chrono::duration_cast<std::chrono::milliseconds>(
                                   std::chrono::system_clock::now() - backlogTimer.expires_at()
                               )
                                   .count();
                infoChannel.flush();
            }
            pollBacklog();
        });
    }

    void Inspector::connectToClient() {
        requestChannel.attemptConnect();
        infoChannel.attemptConnect();
        consoleRedirect.attemptConnect();
        pollBacklog();

        activateAsyncListen();

        connectToMonitor();

        writeProcessStatus();
    }

    void Inspector::disconnectFromClient() {
        backlogTimer.cancel();
        disconnectFromMonitor();
        requestChannel.disconnect();
        infoChannel.disconnect();
        consoleRedirect.disconnect();
        resetStatus();

        activateAsyncListen();

        SWA::EventTimers::getInstance().resumeTimers();
    }

    void Inspector::resetStatus() {
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

    void Inspector::processRequest() {
        try {
            int commandId;
            requestChannel >> commandId;

            std::map<InspectorRequestId, CommandFunction>::const_iterator foundCommand =
                commandLookup.find(static_cast<InspectorRequestId>(commandId));

            if (foundCommand == commandLookup.end()) {
                std::println(stderr,"Invalid Command {}", commandId);
                return;
            }

            CommandFunction command = foundCommand->second;

            bool success = command();

            requestChannel << success;
            requestChannel.flush();

            while (!inThreadQueue.empty()) {
                setRunning();
                auto func = inThreadQueue.front();
                // Make sure we pop before running function, or we
                // might end up trying to run it again as
                // processRequest is reentrant.
                inThreadQueue.pop();
                func();
                setIdle();
            }

        } catch (const ConnectionError &e) {
            std::println(stderr, "Lost Inspector Connection ({})", e.what());
            disconnectFromClient();
        }
    }

    bool Inspector::getProcessData() {
        requestChannel << SWA::ProcessMetaData::getProcess();
        return true;
    }

    bool Inspector::redirectConsole() {
        bool redirect;
        requestChannel >> redirect;

        if (redirect) {
            consoleRedirect.startRedirection();
        } else {
            consoleRedirect.stopRedirection();
        }

        return true;
    }

    bool Inspector::runDomainService() {
        int domainId;
        int serviceId;
        requestChannel >> domainId >> serviceId;

        inThreadQueue.push(
            ProcessHandler::getInstance().getDomainHandler(domainId).getServiceHandler(serviceId).getInvoker(
                requestChannel
            )
        );

        return true;
    }

    bool Inspector::runTerminatorService() {
        int domainId;
        int terminatorId;
        int serviceId;
        requestChannel >> domainId >> terminatorId >> serviceId;

        inThreadQueue.push(ProcessHandler::getInstance()
                               .getDomainHandler(domainId)
                               .getTerminatorHandler(terminatorId)
                               .getServiceHandler(serviceId)
                               .getInvoker(requestChannel));

        return true;
    }

    bool Inspector::runObjectService() {
        int domainId;
        int objectId;
        int serviceId;
        requestChannel >> domainId >> objectId >> serviceId;

        inThreadQueue.push(ProcessHandler::getInstance()
                               .getDomainHandler(domainId)
                               .getGenericObjectHandler(objectId)
                               .getServiceHandler(serviceId)
                               .getInvoker(requestChannel));

        return true;
    }

    bool Inspector::fireEvent() {
        int domainId;
        int objectId;
        int eventId;
        requestChannel >> domainId >> objectId >> eventId;

        auto event = ProcessHandler::getInstance()
                         .getDomainHandler(domainId)
                         .getGenericObjectHandler(objectId)
                         .getEventHandler(eventId)
                         .getEvent(requestChannel);

        if (event)
            SWA::Process::getInstance().getEventQueue().addEvent(event);

        return true;
    }

    bool Inspector::scheduleEvent() {
        SWA::EventTimers::TimerIdType timerId;
        SWA::Timestamp expiry;
        SWA::Duration period;
        int domainId;
        int objectId;
        int eventId;
        requestChannel >> timerId >> expiry >> period >> domainId >> objectId >> eventId;

        auto event = ProcessHandler::getInstance()
                         .getDomainHandler(domainId)
                         .getGenericObjectHandler(objectId)
                         .getEventHandler(eventId)
                         .getEvent(requestChannel);

        if (event)
            SWA::EventTimers::getInstance().scheduleTimer(timerId, expiry, period, event);

        return true;
    }

    bool Inspector::cancelTimer() {
        SWA::EventTimers::TimerIdType timerId;
        requestChannel >> timerId;

        SWA::EventTimers::getInstance().cancelTimer(timerId);

        return true;
    }

    bool Inspector::setTraceLines() {
        requestChannel >> traceLines;
        return true;
    }

    bool Inspector::setTraceBlocks() {
        requestChannel >> traceBlocks;
        return true;
    }

    bool Inspector::setTraceExceptions() {
        requestChannel >> traceExceptions;
        return true;
    }

    bool Inspector::setTraceEvents() {
        requestChannel >> traceEvents;
        return true;
    }

    bool Inspector::setTraceInput() {
        bool traceInput;
        requestChannel >> traceInput;

        return true;
    }

    bool Inspector::setTraceOutput() {
        bool traceOutput;
        requestChannel >> traceOutput;

        return true;
    }

    bool Inspector::getTraceInput() {
        requestChannel << false;
        return true;
    }

    bool Inspector::getTraceOutput() {
        requestChannel << false;
        return true;
    }

    bool Inspector::setStepLines() {
        requestChannel >> stepLines;
        return true;
    }

    bool Inspector::setStepBlocks() {
        requestChannel >> stepBlocks;
        return true;
    }

    bool Inspector::setStepExceptions() {
        requestChannel >> stepExceptions;
        return true;
    }

    bool Inspector::setStepEvents() {
        requestChannel >> stepEvents;
        return true;
    }

    bool Inspector::setEnableTimers() {
        bool timers_enabled;
        requestChannel >> timers_enabled;
        if (timers_enabled) {
            SWA::EventTimers::getInstance().resumeTimers();
        } else {
            SWA::EventTimers::getInstance().suspendTimers();
        }
        return true;
    }

    bool Inspector::continueExecution() {
        stepToNext = false;
        setNotPaused();
        return true;
    }

    bool Inspector::pauseExecution() {
        stepToNext = true;
        setPaused();
        return true;
    }

    bool Inspector::stepExecution() {
        stepToNext = true;
        setNotPaused();
        return true;
    }

    bool Inspector::setBreakpoint() {
        bool set;
        int actionType;
        int domainId;
        int objectId;
        int actionId;
        int lineNo;
        requestChannel >> actionType >> domainId >> objectId >> actionId >> lineNo >> set;

        if (set) {
            return breakpoints
                .insert(SWA::StackFrame(SWA::StackFrame::ActionType(actionType), domainId, objectId, actionId, lineNo))
                .second;
        } else {
            return breakpoints.erase(
                SWA::StackFrame(SWA::StackFrame::ActionType(actionType), domainId, objectId, actionId, lineNo)
            );
        }
    }

    bool Inspector::getAssignerState() {
        int domainId;
        int objectId;
        requestChannel >> domainId >> objectId;
        requestChannel << 0;
        return true;
    }

    bool Inspector::getInstanceCount() {
        int domainId;
        int objectId;
        requestChannel >> domainId >> objectId;
        requestChannel << ProcessHandler::getInstance()
                              .getDomainHandler(domainId)
                              .getGenericObjectHandler(objectId)
                              .getCardinality();
        return true;
    }

    bool Inspector::createInstancePopulation() {
        int domainId;
        int objectId;
        int popSize;
        requestChannel >> domainId >> objectId >> popSize;

        GenericObjectHandler &handler =
            ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId);

        for (int i = 0; i < popSize; ++i) {
            handler.createInstance(requestChannel);
        }
        requestChannel.flush();

        return true;
    }

    bool Inspector::createSingleInstance() {
        int domainId;
        int objectId;
        requestChannel >> domainId >> objectId;

        ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId).createInstance(
            requestChannel
        );
        requestChannel.flush();

        return true;
    }

    bool Inspector::deleteSingleInstance() {
        int domainId;
        int objectId;
        int instanceId;
        requestChannel >> domainId >> objectId >> instanceId;

        ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId).deleteInstance(
            requestChannel, instanceId
        );
        return true;
    }

    bool Inspector::createRelationships() {
        int domainId;
        int relId;
        int popSize;
        requestChannel >> domainId >> relId >> popSize;

        DomainHandler &handler = ProcessHandler::getInstance().getDomainHandler(domainId);

        for (int i = 0; i < popSize; ++i) {
            handler.createRelationship(requestChannel, relId);
        }
        requestChannel.flush();

        return true;
    }

    bool Inspector::createSuperSubtypes() {
        int domainId;
        int relId;
        int popSize;
        requestChannel >> domainId >> relId >> popSize;

        DomainHandler &handler = ProcessHandler::getInstance().getDomainHandler(domainId);

        for (int i = 0; i < popSize; ++i) {
            handler.createRelationship(requestChannel, relId);
        }
        requestChannel.flush();

        return true;
    }

    bool Inspector::updateSingleInstance() {
        return false;
    }

    bool Inspector::getInstanceData() {
        int domainId;
        int objectId;
        requestChannel >> domainId >> objectId;

        ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId).writePopulation(
            requestChannel
        );

        return true;
    }

    bool Inspector::getSingleInstanceData() {
        int domainId;
        int objectId;
        int instanceId;

        requestChannel >> domainId >> objectId >> instanceId;

        ProcessHandler::getInstance().getDomainHandler(domainId).getGenericObjectHandler(objectId).writeInstance(
            requestChannel, instanceId
        );

        return true;
    }

    bool Inspector::getSelectedInstanceData() {
        int domainId;
        int objectId;
        std::vector<int> pks;

        requestChannel >> domainId >> objectId >> pks;

        ProcessHandler::getInstance()
            .getDomainHandler(domainId)
            .getGenericObjectHandler(objectId)
            .writeSelectedInstances(requestChannel, pks);

        return true;
    }

    bool Inspector::getRelatedInstanceData() {
        int domainId;
        int objectId;
        int pk;
        int relId;
        requestChannel >> domainId >> objectId >> pk >> relId;

        ProcessHandler::getInstance()
            .getDomainHandler(domainId)
            .getGenericObjectHandler(objectId)
            .writeRelatedInstances(requestChannel, pk, relId);

        return true;
    }

    bool Inspector::getEventQueue() {
        requestChannel << SWA::Process::getInstance().getEventQueue().getEvents();

        return true;
    }

    bool Inspector::getTimerQueue() {
        requestChannel << SWA::EventTimers::getInstance().getQueuedEvents();
        return true;
    }

    bool Inspector::getLocalVariables() {
        int stackDepth;
        requestChannel >> stackDepth;

        const SWA::StackFrame &frame = SWA::Stack::getInstance()[stackDepth];

        switch (frame.getType()) {
            case SWA::StackFrame::DomainService:
                ProcessHandler::getInstance()
                    .getDomainHandler(frame.getDomainId())
                    .getServiceHandler(frame.getActionId())
                    .writeLocalVars(requestChannel, frame);
                break;
            case SWA::StackFrame::TerminatorService:
                ProcessHandler::getInstance()
                    .getDomainHandler(frame.getDomainId())
                    .getTerminatorHandler(frame.getObjectId())
                    .getServiceHandler(frame.getActionId())
                    .writeLocalVars(requestChannel, frame);
                break;
            case SWA::StackFrame::ObjectService:
                ProcessHandler::getInstance()
                    .getDomainHandler(frame.getDomainId())
                    .getGenericObjectHandler(frame.getObjectId())
                    .getServiceHandler(frame.getActionId())
                    .writeLocalVars(requestChannel, frame);
                break;
            case SWA::StackFrame::StateAction:
                ProcessHandler::getInstance()
                    .getDomainHandler(frame.getDomainId())
                    .getGenericObjectHandler(frame.getObjectId())
                    .getStateHandler(frame.getActionId())
                    .writeLocalVars(requestChannel, frame);
                break;
        }
        return true;
    }

    bool Inspector::hitBreakpoint() {
        if (breakpoints.size() > 0 && !SWA::Stack::getInstance().empty()) {
            return breakpoints.count(SWA::Stack::getInstance().top());
        } else {
            return false;
        }
    }

    bool Inspector::getStack() {
        requestChannel << SWA::Stack::getInstance().getStackFrames();
        return true;
    }

    void Inspector::endMainLoop() {
        while (!endThreadQueue.empty()) {
            endThreadQueue.front()();
            endThreadQueue.pop();
        }
    }

    bool Inspector::runSchedule() {
        std::string filename;
        std::string script;
        requestChannel >> filename >> script;

        SWA::Schedule schedule(filename, script);

        if (schedule.isValid()) {
            endThreadQueue.push([schedule]() {
                SWA::Process::getInstance().runSchedule(schedule);
            });
            return true;
        } else {
            return false;
        }
    }

    void Inspector::outputTrace(std::string prefix, bool showLineNo, std::string message) const {
        std::print("Inspector: {:{}}", prefix, SWA::Stack::getInstance().getStackFrames().size() * 2 + prefix.size());
        if (!SWA::Stack::getInstance().getStackFrames().empty()) {
            std::print("{}", SWA::NameFormatter::formatStackFrame(SWA::Stack::getInstance().top(), showLineNo));
            if (message.size() > 0) {
                std::print(" : ");
            }
        }
        std::println("{}", message);
    }

    bool Inspector::invokePluginAction() {
        std::string pluginName;
        std::string actionName;

        requestChannel >> pluginName >> actionName;

        SWA::PluginRegistry::getInstance().invokeAction(pluginName, actionName);

        return true;
    }

    bool Inspector::getPluginFlag() {
        std::string pluginName;
        std::string flagName;

        requestChannel >> pluginName >> flagName;
        requestChannel << SWA::PluginRegistry::getInstance().getFlag(pluginName, flagName);

        return true;
    }

    bool Inspector::getPluginProperty() {
        std::string pluginName;
        std::string propertyName;

        requestChannel >> pluginName >> propertyName;
        requestChannel << SWA::PluginRegistry::getInstance().getProperty(pluginName, propertyName);

        return true;
    }

    bool Inspector::setPluginFlag() {
        std::string pluginName;
        std::string flagName;
        bool value;

        requestChannel >> pluginName >> flagName >> value;
        SWA::PluginRegistry::getInstance().setFlag(pluginName, flagName, value);

        return true;
    }

    bool Inspector::setPluginProperty() {
        std::string pluginName;
        std::string propertyName;
        std::string value;

        requestChannel >> pluginName >> propertyName >> value;
        SWA::PluginRegistry::getInstance().setProperty(pluginName, propertyName, value);

        return true;
    }

    void Inspector::startStatement() {
        while (requestChannel.ready())
            processRequest();

        if (traceLines)
            outputTrace("  ", true);

        if (stepLines || stepToNext || hitBreakpoint())
            pause();
    }

    void Inspector::enteredAction() {
        while (requestChannel.ready())
            processRequest();

        if (traceBlocks || traceLines)
            outputTrace("->", false);

        if (stepBlocks || stepLines || stepToNext || hitBreakpoint())
            pause();
    }

    void Inspector::leavingAction() {
        while (requestChannel.ready())
            processRequest();

        if (traceBlocks || traceLines)
            outputTrace("<-", false);

        if (stepBlocks || stepLines || stepToNext || hitBreakpoint())
            pause();
    }

    void Inspector::enteredCatch() {
        while (requestChannel.ready())
            processRequest();

        if (traceExceptions)
            outputTrace("! ", false, "Caught exception ");

        if (stepExceptions || stepBlocks || stepLines || stepToNext || hitBreakpoint())
            pause();
    }

    void Inspector::exceptionRaised(const std::string &message) {
        while (requestChannel.ready())
            processRequest();

        if (traceExceptions)
            outputTrace("! ", false, "Raised exception \"" + message + "\"");

        if (stepExceptions)
            pause();
    }

    std::string getInstanceText(int domainId, int objectId, SWA::IdType instanceId) {
        return SWA::NameFormatter::formatObjectName(domainId, objectId) + "(" +
               ProcessHandler::getInstance()
                   .getDomainHandler(domainId)
                   .getGenericObjectHandler(objectId)
                   .getIdentifierText(instanceId) +
               ")";
    }

    void Inspector::transitioningState(int domainId, int objectId, int instanceId, int oldState, int newState) {
        while (requestChannel.ready())
            processRequest();

        if (traceEvents)
            outputTrace(
                "* ",
                true,
                "State Transition for " + getInstanceText(domainId, objectId, instanceId) + " from " +
                    SWA::NameFormatter::formatStateName(domainId, objectId, oldState) + " to " +
                    SWA::NameFormatter::formatStateName(domainId, objectId, newState)
            );
    }

    void Inspector::transitioningAssignerState(int domainId, int objectId, int oldState, int newState) {
        while (requestChannel.ready())
            processRequest();

        if (traceEvents)
            outputTrace(
                "* ",
                true,
                "Assigner State Transition from " + SWA::NameFormatter::formatStateName(domainId, objectId, oldState) +
                    " to " + SWA::NameFormatter::formatStateName(domainId, objectId, newState)
            );
    }

    void Inspector::generatingEvent(const std::shared_ptr<SWA::Event> &event) {
        while (requestChannel.ready())
            processRequest();

        if (traceEvents) {
            std::string message =
                "Generating Event " +
                SWA::NameFormatter::formatEventName(event->getDomainId(), event->getObjectId(), event->getEventId());
            if (event->getHasSource()) {
                message +=
                    " from " +
                    getInstanceText(event->getDomainId(), event->getSourceObjectId(), event->getSourceInstanceId());
            }

            if (event->getHasDest()) {
                message +=
                    " to " + getInstanceText(event->getDomainId(), event->getObjectId(), event->getDestInstanceId());
            }

            outputTrace("* ", true, message);
        }

        if (stepEvents)
            pause();
    }

    void Inspector::processingEvent(const std::shared_ptr<SWA::Event> &event) {
        while (requestChannel.ready())
            processRequest();

        if (traceEvents) {
            std::string message =
                "Processing Event " +
                SWA::NameFormatter::formatEventName(event->getDomainId(), event->getObjectId(), event->getEventId());
            if (event->getHasSource()) {
                message +=
                    " from " +
                    getInstanceText(event->getDomainId(), event->getSourceObjectId(), event->getSourceInstanceId());
            }

            if (event->getHasDest()) {
                message +=
                    " to " + getInstanceText(event->getDomainId(), event->getObjectId(), event->getDestInstanceId());
            }

            outputTrace("* ", false, message);
        }
    }

    void Inspector::cancellingTimer(int timerId) {
        while (requestChannel.ready())
            processRequest();

        if (traceEvents) {
            outputTrace("* ", false, std::format("Cancelling timer {}", timerId));
        }
    }

    void Inspector::settingTimer(
        int timerId,
        const SWA::Timestamp &timeout,
        const SWA::Duration &period,
        const std::shared_ptr<::SWA::Event> &event
    ) {
        while (requestChannel.ready())
            processRequest();

        if (traceEvents) {
            std::string message = std::format(
                "Scheduling timer {} at {} period {} for event {}",
                timerId,
                timeout,
                period,
                SWA::NameFormatter::formatEventName(event->getDomainId(), event->getObjectId(), event->getEventId())
            );
            if (event->getHasSource()) {
                message +=
                    " from " +
                    getInstanceText(event->getDomainId(), event->getSourceObjectId(), event->getSourceInstanceId());
            }

            if (event->getHasDest()) {
                message +=
                    " to " + getInstanceText(event->getDomainId(), event->getObjectId(), event->getDestInstanceId());
            }

            outputTrace("* ", true, message);
        }
    }

    void Inspector::firingTimer(int timerId, int overrun) {
        while (requestChannel.ready())
            processRequest();

        if (traceEvents) {
            outputTrace("* ", false, std::format("Firing timer {}, overrun {}", timerId, overrun));
        }
    }

    void Inspector::processRunning() {
        deactivateAsyncListen();
        setRunning();
    }

    void Inspector::processIdle() {
        setIdle();
        activateAsyncListen();
    }

    void Inspector::writeProcessStatus() {
        try {
            if (infoChannel.isConnected()) {
                if (paused) {
                    if (SWA::Stack::getInstance().empty()) {
                        infoChannel << (int)CURRENT_POSITION_INFO << (int)-1;
                    } else {
                        infoChannel << (int)CURRENT_POSITION_INFO << SWA::Stack::getInstance().top();
                    }
                } else {
                    if (idle) {
                        infoChannel << (int)IDLE_INFO;
                    } else {
                        infoChannel << (int)RUNNING_INFO;
                    }
                }
                infoChannel.flush();
            }
        } catch (const ConnectionError &e) {
            std::println(stderr, "Lost Inspector Connection ({})", e.what());
            disconnectFromClient();
        }
    }

    void Inspector::setRunning() {
        idle = false;
        writeProcessStatus();
    }

    void Inspector::setIdle() {
        idle = true;
        writeProcessStatus();
    }

    void Inspector::setPaused() {
        paused = true;
        writeProcessStatus();
    }

    void Inspector::setNotPaused() {
        paused = false;
        writeProcessStatus();
    }

    void Inspector::pause() {
        setPaused();
        if (!requestChannel.isConnected()) {
            connectToClient();
        }

        deactivateAsyncListen();

        while (requestChannel.isConnected() && paused) {
            processRequest();
        }

        activateAsyncListen();
    }

} // namespace Inspector
