/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_Process_HH
#define SWA_Process_HH

#include "ActivityMonitor.hh"
#include "Domain.hh"
#include "EventQueue.hh"
#include "boost/signals2.hpp"

#include <deque>
#include <string>

namespace SWA {

class Schedule;
class CommandLine;

class Process {
  public:
    enum EventQueuePriority {
        LOCAL_QUEUE,
        MAIN_QUEUE,
        SCENARIO_QUEUE,
        EXTERNAL_QUEUE
    };

    static Process &getInstance();

    EventQueue &getEventQueue() { return eventQueue; }

    Domain &registerDomain(const std::string &name);

    const Domain &getDomain(const std::string &name) const;
    Domain &getDomain(const std::string &name);

    const Domain &getDomain(int id) const;
    Domain &getDomain(int id);

    const CommandLine &getCommandLine() const;
    CommandLine &getCommandLine();

    const std::string &getName() const;

    const void setProjectName(const std::string &name) { projectName = name; }
    const std::string &getProjectName() const { return projectName; }

    void runStartup() const;

    void initialise();
    void endInitialisation() const;
    void endStartup() const;

    boost::signals2::connection
    registerStartupListener(const std::function<void()> &function);
    boost::signals2::connection
    registerInitialisingListener(const std::function<void()> &function);
    boost::signals2::connection
    registerInitialisedListener(const std::function<void()> &function);
    boost::signals2::connection
    registerStartedListener(const std::function<void()> &function);
    boost::signals2::connection
    registerPreSchedulesListener(const std::function<void()> &function);
    boost::signals2::connection
    registerPreScheduleListener(const std::function<void()> &function);
    boost::signals2::connection
    registerPostScheduleListener(const std::function<void()> &function);
    boost::signals2::connection
    registerNextSchedulePhaseListener(const std::function<void()> &function);
    boost::signals2::connection
    registerPostSchedulesListener(const std::function<void()> &function);
    boost::signals2::connection registerThreadStartedListener(
        const std::function<void(const std::string &)> &function);
    boost::signals2::connection
    registerThreadCompletingListener(const std::function<void()> &function);
    boost::signals2::connection
    registerThreadCompletedListener(const std::function<void()> &function);
    boost::signals2::connection
    registerThreadAbortedListener(const std::function<void()> &function);
    boost::signals2::connection
    registerShutdownListener(const std::function<void()> &function);

    void runSchedules();

    void runScheduleFile(const std::string &fileName);
    void runSchedule(const Schedule &schedule);
    void startNextSchedulePhase();

    void idle(int timeout_secs);
    void pause();
    void runService(std::function<void()> service, const std::string &input);
    void forceTerminate();

    void loadDynamicLibraries(const std::string &libName,
                              const std::string &interfaceSuffix,
                              bool loadProjectLib);
    void loadDynamicProjectLibrary(const std::string &libName);

    bool coldStart() const;

    // static references to Domain classes held in the DomainList are held
    // by other areas of the generated code. Need to therefore use a deque
    // as this does not copy contained objects into a new memory area on a
    // resize.
    typedef std::deque<Domain> DomainList;
    const DomainList &getDomains() const { return domains; }

    void mainLoop();

    ActivityMonitor &getActivityMonitor() { return activityMonitor; }

    static void shutdownHandler(int, int);

    void requestShutdown() { shutdownRequested = true; }

  private:
    Process();

    EventQueue eventQueue;
    typedef std::map<std::string, int> DomainLookup;
    DomainLookup domainLookup;
    DomainList domains;

    mutable std::string name;
    std::string projectName;

    bool shutdownRequested;

    ActivityMonitor activityMonitor;

    typedef boost::signals2::signal_type<
        void(), boost::signals2::keywords::mutex_type<
                    boost::signals2::dummy_mutex>>::type VoidSignal;
    typedef boost::signals2::signal_type<
        void(const std::string &),
        boost::signals2::keywords::mutex_type<boost::signals2::dummy_mutex>>::
        type StringSignal;

    VoidSignal starting;
    VoidSignal initialising;
    VoidSignal initialised;
    VoidSignal started;
    VoidSignal preSchedules;
    VoidSignal preSchedule;
    VoidSignal postSchedule;
    VoidSignal nextSchedulePhase;
    VoidSignal postSchedules;
    StringSignal threadStarted;
    VoidSignal threadCompleting;
    VoidSignal threadCompleted;
    VoidSignal threadAborted;
    VoidSignal shutdown;

    class ProcessingThread {
      public:
        ProcessingThread(const std::string &name);
        ~ProcessingThread();

        void completing();
        void complete();
        void abort();

      private:
        ProcessingThread(const ProcessingThread &rhs);
        ProcessingThread &operator=(const ProcessingThread &rhs);

      private:
        bool inProgress;
        std::string name;
    };
};

} // namespace SWA

#endif
