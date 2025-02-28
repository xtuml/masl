/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "swa/FileDescriptorListener.hh"
#include "swa/ActivityMonitor.hh"
#include "swa/ProgramError.hh"
#include <errno.h>
#include <fcntl.h>
#include <signal.h>
#include <sys/ioctl.h>

// For gettid... see comment in initFd()
#include <cstring>
#include <sys/syscall.h>
#include <unistd.h>

namespace SWA {

    FileDescriptorListener::FileDescriptorListener(int fd, const Callback &callback, ActivityMonitor &monitor)
        : fd(fd),
          callback(callback),
          priority(ListenerPriority::getNormal()),
          active(false),
          forceCallback(false),
          monitor(monitor) {
        initFd();
        updateFdStatus(active);

        if (valid()) {
            monitor.addFdCallback(fd, [this](int fd) {
                callCallback(fd);
            });
        }
    }

    FileDescriptorListener::FileDescriptorListener(const Callback &callback, ActivityMonitor &monitor)
        : fd(-1),
          callback(callback),
          priority(ListenerPriority::getNormal()),
          active(false),
          forceCallback(false),
          monitor(monitor) {
        updateFdStatus(active);
    }

    FileDescriptorListener::~FileDescriptorListener() {
        clearFd();
    }

    void FileDescriptorListener::setFd(int fd) {
        if (valid())
            monitor.removeFdCallback(fd);
        cancel();
        this->fd = fd;
        initFd();
        updateFdStatus(active);
        if (valid()) {
            monitor.addFdCallback(fd, [this](int fd) {
                callCallback(fd);
            });
        }
    }

    void FileDescriptorListener::clearFd() {
        if (valid())
            monitor.removeFdCallback(fd);
        cancel();
        this->fd = -1;
    }

    void FileDescriptorListener::initFd() {
        if (fcntl(fd, F_SETSIG, priority.getValue()))
            throw ProgramError(strerror(errno));

        // Hmmm.... 'man 2 fcntl' says we need to use gettid
        // (rather than getpid) for F_SETOWN if the process is
        // running threaded, but then glibc doesn't expose this so
        // need to use a syscall. If we don't do this, then the
        // signal does not get delivered to the thread on rhel4u4
        // (it works fine with gettid or getpid on rhel5u4). Now,
        // you might think that we don't run this in a thread, and
        // in the general case you would be right. There is,
        // however, some legacy hackery which runs an OOA process
        // inside a thead of another process.

        // if ( fcntl(fd,F_SETOWN,getpid()) ) throw ProgramError(strerror(errno));
        if (fcntl(fd, F_SETOWN, syscall(SYS_gettid)))
            throw ProgramError(strerror(errno));
    }

    void FileDescriptorListener::updateFdStatus(bool makeActive) {
        if (valid()) {
            if (makeActive) {
                int flags = fcntl(fd, F_GETFL);
                if (flags == -1 || fcntl(fd, F_SETFL, flags | O_ASYNC))
                    throw ProgramError(strerror(errno));

                // May have been activity on the fd since the last check,
                // but before we activated signalling, so we need to check
                // for the fd being available for read and queue a
                // signal as appropriate.
                if (readyToRead()) {
                    requeue();
                }
            } else {
                int flags = fcntl(fd, F_GETFL);
                if (flags == -1) {
                    // Ignore a bad file descriptor - we are trying to cancel it
                    // anyway!
                    if (errno != EBADF)
                        throw ProgramError(strerror(errno));
                } else {
                    if (fcntl(fd, F_SETFL, flags & ~O_ASYNC))
                        throw ProgramError(strerror(errno));
                }
            }
        }
    }

    void FileDescriptorListener::activate(const bool queueImmediately) {
        active = true;

        if (queueImmediately) {
            forceCallback = true;
            requeue();
        } else {
            updateFdStatus(active);
        }
    }

    void FileDescriptorListener::cancel() {
        if (active) {
            active = false;
            updateFdStatus(active);
        }
    }

    void FileDescriptorListener::setPriority(const ListenerPriority &priority) {
        this->priority = priority;
        if (valid() && fcntl(fd, F_SETSIG, priority.getValue()))
            throw ProgramError(strerror(errno));
    }

    void FileDescriptorListener::requeue() const {
        if (valid()) {
            sigval data;
            data.sival_int = -fd;
            sigqueue(getpid(), priority.getValue(), data);
        }
    }

    bool FileDescriptorListener::valid() const {
        return !(fd < 0);
    }

    bool FileDescriptorListener::readyToRead() const {
        if (!valid())
            return false;

        fd_set readset;
        FD_ZERO(&readset);
        FD_SET(fd, &readset);
        struct timeval timeout;
        timeout.tv_sec = 0;
        timeout.tv_usec = 0;

        return select(fd + 1, &readset, 0, 0, &timeout) && FD_ISSET(fd, &readset);
    }

    void FileDescriptorListener::callCallback(int fd) {
        // Check that we are still interested in the same fd
        if (fd != this->fd)
            return;

        // Ignore if we have been turned off whilst the signal is queued
        if (!active)
            return;

        // Stop listening whilst callback is in progress or we may
        // get flooded with signals whilst the IO is going on.
        updateFdStatus(false);

        // Make sure the fd is still available for read to avoid
        // the race condition where a second signal is queued
        // before we turned off the signals whilst processing the
        // first. If the first callback then exhausts the read,
        // the second should be skipped.
        if (forceCallback || readyToRead()) {
            forceCallback = callback(fd);
        }

        if (forceCallback) {
            // Callback indicated that there was more to do, so
            // requeue the signal to ensure it gets called again.
            // Don't activate listening again yet, as we know that
            // we will get called again.
            requeue();
        } else {
            // Callback didn't think that there was anything more to
            // do, so reactivate the listen.
            updateFdStatus(active);
        }
    }
} // namespace SWA
