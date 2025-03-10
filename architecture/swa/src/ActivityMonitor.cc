/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "swa/ActivityMonitor.hh"
#include "swa/ProgramError.hh"
#include <errno.h>
#include <fcntl.h>
#include <format>
#include <iostream>
#include <map>
#include <signal.h>
#include <string.h>

namespace {

    void ActivityMonitorSignalhandler(int signo) {
        // All signals should be handled by functionality in
        // the ActivityMonitor::pollActivity(..) method. This
        // uses sigwaitinfo to handle signals that have been
        // delivered to the process. Even though any registered signal
        // is handled by the associated signalCallback method, as a
        // saftey mechanism replace the installed global signal handler
        // with this empty function.

        // For signals that have a default action of SIG_IGN (i.e. SIGCHLD),
        // this dummy global handler must be installed to enable the signal
        // to be detected, and processed, by sigwaitinfo.
    }

} // namespace

namespace SWA {
    ActivityMonitor::ActivityMonitor()
        : initialised(false) {
        sigemptyset(&signalMask);
        sigaddset(&signalMask, SIGIO);
        for (int i = SIGRTMIN; i <= SIGRTMAX; ++i) {
            sigaddset(&signalMask, i);
        }
    }

    void ActivityMonitor::initialise() {
        // Defer initialisation of the activity monitor until it is actually
        // required for use. This is so that additional applications can link
        // against the SWA library and use thier own signal handling without having
        // to use the real-time signal implementation provided by this class.
        pthread_sigmask(SIG_BLOCK, &signalMask, 0);
        initialised = true;
    }

    void ActivityMonitor::addFdCallback(int fd, const FdCallback &callback) {
        fdCallbacks.insert(FdCallbackTable::value_type(fd, callback));
    }

    void ActivityMonitor::removeFdCallback(int fd) {
        fdCallbacks.erase(fd);
    }

    void ActivityMonitor::addFdExtCallback(int fd, const FdExtendedCallback &callback) {
        fdExtCallbacks.insert(FdExtCallbackTable::value_type(fd, callback));
    }

    void ActivityMonitor::removeFdExtCallback(int fd) {
        fdExtCallbacks.erase(fd);
    }

    void ActivityMonitor::addTimerCallback(timer_t id, const TimerCallback &callback) {
        timerCallbacks.insert(TimerCallbackTable::value_type(id, callback));
    }

    void ActivityMonitor::removeTimerCallback(timer_t id) {
        timerCallbacks.erase(id);
    }

    uint64_t ActivityMonitor::addSignalCallback(int signal, const SignalCallback &callback) {
        static uint64_t nextId = 0;

        struct sigaction sa = {};
        struct sigaction org_sa = {};

        sa.sa_flags = SA_RESTART;
        sa.sa_handler = ActivityMonitorSignalhandler;
        if (sigaction(signal, &sa, &org_sa) < 0) {
            throw ProgramError(std::string("ActivityMonitor::addSignalCallback sigaction failed : ") + strerror(errno));
        }

        // An error was discovered with the real-time signalling implementation
        // in that any signal delivered to a process that had a default signal
        // action of SIG_IGN would not be delivered to the callback processing
        // loop. To resolve this issue any registered signal is associated with
        // a dummy signal handler, while the orginal handler is stored-off. When
        // the signal is deregistered, the orginal signal handler is restored.
        if (orgSignalHandlers.find(signal) == orgSignalHandlers.end()) {
            orgSignalHandlers.insert(std::make_pair(signal, org_sa));
        }

        signalCallbacks.insert(
            SignalCallbackTable::value_type(signal, std::pair<uint64_t, SignalCallback>(nextId, callback))
        );
        sigaddset(&signalMask, signal);
        pthread_sigmask(SIG_BLOCK, &signalMask, 0);

        return nextId++;
    }

    void ActivityMonitor::removeSignalCallback(uint64_t id) {
        int signal = -1;
        for (SignalCallbackTable::iterator it = signalCallbacks.begin(), end = signalCallbacks.end(); it != end;) {
            if (it->second.first == id) {
                signal = it->first;
                signalCallbacks.erase(it++);
            } else {
                ++it;
            }
        }

        if (signal > 0 && signalCallbacks.find(signal) == signalCallbacks.end()) {
            // Reinstate the default handler for the requested signal.
            sigset_t resetMask;
            sigemptyset(&resetMask);
            sigaddset(&resetMask, signal);
            pthread_sigmask(SIG_UNBLOCK, &resetMask, 0);

            sigdelset(&signalMask, signal);

            // When the signal was registered its orginal signal handler was stored
            // off so it could be replaced when the signal callbacl was removed.
            // Therefore remove the dummy default handler and place the orginal
            // back.
            SignalHandlerType::iterator signalItr = orgSignalHandlers.find(signal);
            if (signalItr != orgSignalHandlers.end()) {
                struct sigaction &sa = signalItr->second;
                if (sigaction(signal, &sa, 0) < 0) {
                    throw ProgramError(
                        std::string("ActivityMonitor::removeSignalCallback "
                                    "sigaction failed : ") +
                        strerror(errno)
                    );
                }
                orgSignalHandlers.erase(signalItr);
            }
        }
    }

    int ActivityMonitor::addNormalCallback(const NormalCallback &callback) {
        static int nextId = 1;

        normalCallbacks.insert(NormalCallbackTable::value_type(nextId, callback));
        return nextId++;
    }

    void ActivityMonitor::removeNormalCallback(int id) {
        normalCallbacks.erase(id);
    }

    void f_ignoreFd(int) {}
    void f_ignoreFdExt(int, int) {}
    void f_ignoreNormal(int, int) {}
    void f_ignoreTimer(int) {}

    ActivityMonitor::FdCallback ignoreFd(f_ignoreFd);
    ActivityMonitor::FdExtendedCallback ignoreFdExt(f_ignoreFdExt);

    ActivityMonitor::NormalCallback ignoreNormal(f_ignoreNormal);
    ActivityMonitor::TimerCallback ignoreTimer(f_ignoreTimer);

    const ActivityMonitor::FdCallback &ActivityMonitor::getFdCallback(int fd) const {
        FdCallbackTable::const_iterator it = fdCallbacks.find(fd);

        if (it != fdCallbacks.end())
            return it->second;
        else
            return ignoreFd;
    }

    const ActivityMonitor::FdExtendedCallback &ActivityMonitor::getFdExtCallback(int fd) const {
        FdExtCallbackTable::const_iterator it = fdExtCallbacks.find(fd);

        if (it != fdExtCallbacks.end())
            return it->second;
        else
            return ignoreFdExt;
    }

    const ActivityMonitor::NormalCallback &ActivityMonitor::getNormalCallback(int id) const {
        NormalCallbackTable::const_iterator it = normalCallbacks.find(id);

        if (it != normalCallbacks.end())
            return it->second;
        else
            return ignoreNormal;
    }

    const void ActivityMonitor::processSignalCallbacks(int signal, int pid, int uid) const {
        std::pair<SignalCallbackTable::const_iterator, SignalCallbackTable::const_iterator> range =
            signalCallbacks.equal_range(signal);

        for (SignalCallbackTable::const_iterator it = range.first; it != range.second; ++it) {
            it->second.second(pid, uid);
        }
    }

    const ActivityMonitor::TimerCallback &ActivityMonitor::getTimerCallback(timer_t id) const {
        std::map<timer_t, TimerCallback>::const_iterator it = timerCallbacks.find(id);

        if (it != timerCallbacks.end())
            return it->second;
        else
            return ignoreTimer;
    }

    bool ActivityMonitor::pollActivity(int timeout_ms) const {
        if (!initialised) {
            throw ProgramError("ActivityMonitor::pollActivity failed : monitor has "
                               "not been initialised");
        }

        siginfo_t info = {0};
        timespec timeout = {timeout_ms / 1000, (timeout_ms % 1000) * 100000};

        while (sigtimedwait(&signalMask, &info, &timeout) < 0) {
            if (errno == EAGAIN) {
                return false;
            } else if (errno == EINTR) {
                // Must have been interrupted by caught unblocked
                // signal, so just let the loop come round again.
                return false;
            } else {
                throw ProgramError(std::string("sigwaitinfo: ") + strerror(errno));
            }
        }

        processActivity(info);

        return true;
    }

    void ActivityMonitor::waitOnActivity() const {
        if (!initialised) {
            throw ProgramError("ActivityMonitor::waitOnActivity failed : monitor "
                               "has not been initialised");
        }

        siginfo_t info = {0};

        while (sigwaitinfo(&signalMask, &info) < 0) {
            if (errno == EINTR) {
                // Must have been interrupted by caught unblocked
                // signal, so just let the loop come round again.
                return;
            } else {
                throw ProgramError(std::string("sigwaitinfo: ") + strerror(errno));
            }
        }

        processActivity(info);
    }

    void ActivityMonitor::processActivity(const siginfo_t &info) const {
        switch (info.si_code) {
            case SI_USER: {
                // Signal generated by kill, sigsend or raise. There
                // can be no value associated with this to contain the
                // handler id, so assume it is handled by a per-signal
                // handler.
                processSignalCallbacks(info.si_signo, info.si_pid, info.si_uid);
            } break;

            case SI_TIMER: {
                // Signal generated by a POSIX.1b timer
                // The SI_TIMER code is associated with timer_create(...) calls, as the
                // associated signal event data is populated before the call to
                // timer_create the time id is held as a pointer in the union sigval
                // field. Therefore access it accordingly
                timer_t *key = (timer_t *)info.si_value.sival_ptr;
                getTimerCallback (*key)(info.si_overrun);
            } break;

            case SI_QUEUE: {
                // Signal generated by a POSIX.1b sigqueue. This could
                // either be a requeueing of an fd callback or a
                // (possibly external) application generated signal.
                // As we only have a single integer in the data field
                // to play with, we use an id range to distinguish
                // between the two. fd callbacks will always have a
                // negative id which is equivalent to -fd. (file
                // descriptors must be +ve). Any +ve id must therefore
                // be an application signal.
                int id = info.si_value.sival_int;
                if (id >= 0) {
                    getNormalCallback(id)(info.si_pid, info.si_uid);
                } else {
                    // an fd requeue signal. Handle the callback, then
                    // requeue again if more data is pending.
                    int fd = -id;
                    getFdCallback(fd)(fd);
                    getFdExtCallback(fd)(fd, -1);
                }
            } break;

            case SI_ASYNCIO: {
                // Signal generated by a POSIX.1b asynchronous IO event
                getNormalCallback(info.si_value.sival_int)(info.si_pid, info.si_uid);
            } break;

            case SI_MESGQ: {
                // Signal generated by an POSIX.1b messaging event
                getNormalCallback(info.si_value.sival_int)(info.si_pid, info.si_uid);
            } break;

            default: {
                if (info.si_code > 0) {
                    if (info.si_signo == SIGIO || (info.si_signo >= SIGRTMIN && info.si_signo <= SIGRTMAX)) {
                        // Kernel generated signal, and it's realtime or
                        // SIGIO too so it must be IO waiting on a fp with
                        // O_ASYNC and F_SETSIG set.
                        if (info.si_code == POLL_IN || info.si_code == POLL_OUT || info.si_code == POLL_HUP) {
                            getFdCallback(info.si_fd)(info.si_fd);
                            getFdExtCallback(info.si_fd)(info.si_fd, info.si_code);
                        }
                    } else {
                        // Kernel generated other signal
                        processSignalCallbacks(info.si_signo, info.si_pid, info.si_uid);
                    }

                } else {
                    throw SWA::ProgramError(std::format("unrecognised signal type : {}", info.si_code));
                }
            }
        }
    }

} // namespace SWA
