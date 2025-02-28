/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "swa/RealTimeSignalListener.hh"
#include "swa/ActivityMonitor.hh"
#include "swa/ProgramError.hh"
#include <errno.h>
#include <fcntl.h>
#include <signal.h>
#include <sys/ioctl.h>

namespace SWA {

    RealTimeSignalListener::RealTimeSignalListener(const Callback &callback, ActivityMonitor &monitor)
        : id(monitor.addNormalCallback([this](int pid, int uid) {
              callCallback(pid, uid);
          })),
          callback(callback),
          priority(ListenerPriority::getNormal()),
          active(true),
          monitor(monitor) {}

    RealTimeSignalListener::~RealTimeSignalListener() {
        monitor.removeNormalCallback(id);
    }

    void RealTimeSignalListener::setPriority(const ListenerPriority &priority) {
        this->priority = priority;
    }

    void RealTimeSignalListener::queueSignal() const {
        sigval data;
        data.sival_int = id;
        sigqueue(getpid(), priority.getValue(), data);
    }

    void RealTimeSignalListener::queueSignal(const ListenerPriority &priority) const {
        sigval data;
        data.sival_int = id;
        sigqueue(getpid(), priority.getValue(), data);
    }

    void RealTimeSignalListener::callCallback(int pid, int uid) {
        if (active)
            callback(pid, uid);
    }
} // namespace SWA
