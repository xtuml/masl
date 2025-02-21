/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_FileDescriptorListener_HH
#define SWA_FileDescriptorListener_HH

#include "ListenerPriority.hh"
#include <functional>

namespace SWA {

class ActivityMonitor;

class FileDescriptorListener {
  public:
    // The type for the function to be called on an fd
    // event. Must return true if the callback should be
    // requeued. Parameter is the relevant fd.
    typedef std::function<bool(int)> Callback;

    FileDescriptorListener(int fd, const Callback &callback,
                           ActivityMonitor &monitor);
    FileDescriptorListener(const Callback &callback, ActivityMonitor &monitor);
    ~FileDescriptorListener();

    void setFd(int fd);
    void clearFd();

    void activate(const bool queueImmediately = false);
    void cancel();

    void setPriority(const ListenerPriority &priority);
    const ListenerPriority &getPriority() { return priority; }

    int getFd() const { return fd; }

  private:
    void callCallback(int fd);
    void requeue() const;
    bool valid() const;
    bool readyToRead() const;

    void initFd();
    void updateFdStatus(bool makeActive);

    int fd;
    Callback callback;
    ListenerPriority priority;
    bool active;
    bool forceCallback;
    ActivityMonitor &monitor;
};

} // namespace SWA

#endif
