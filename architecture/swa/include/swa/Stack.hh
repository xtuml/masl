/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_Stack_HH
#define SWA_Stack_HH

#include "ProcessMonitor.hh"
#include "StackFrame.hh"
#include <vector>

namespace SWA {
class Stack {
  public:
    static Stack &getInstance() { return instance; }
    static Stack &getInstanceStartupSafe();

    void push(const StackFrame &frame) { frames.push_back(frame); }
    void pop() { frames.pop_back(); }

    const StackFrame &top() const { return frames.back(); }
    StackFrame &top() { return frames.back(); }

    const StackFrame &operator[](int index) const { return frames[index]; }
    StackFrame &operator[](int index) { return frames[index]; }

    const std::vector<StackFrame> &getStackFrames() const { return frames; }
    std::vector<StackFrame> &getStackFrames() { return frames; }

    bool empty() const { return frames.empty(); }

    class DeclareLocalVariable {
      public:
        template <class T> DeclareLocalVariable(int id, T &variable) {
            getInstance().top().pushVar(id, variable);
        }
        ~DeclareLocalVariable() { getInstance().top().popVar(); }
    };

    class DeclareParameter {
      public:
        template <class T> DeclareParameter(T &param) {
            getInstance().top().addParam(param);
        }
        ~DeclareParameter() {}
    };

    class DeclareThis {
      public:
        template <class T> DeclareThis(T *thisPtr) {
            getInstance().top().setThis(thisPtr);
        }
        ~DeclareThis() {}
    };

    class EnteringDomainService {
      public:
        EnteringDomainService(int domainId, int serviceId) {
            if (getInstance().empty())
                ProcessMonitor::getInstance().processRunning();
            getInstance().push(
                StackFrame(StackFrame::DomainService, domainId, serviceId));
        }
        ~EnteringDomainService() {
            getInstance().pop();
            if (getInstance().empty())
                ProcessMonitor::getInstance().processIdle();
        }
    };

    class EnteringTerminatorService {
      public:
        EnteringTerminatorService(int domainId, int termId, int serviceId) {
            if (getInstance().empty())
                ProcessMonitor::getInstance().processRunning();
            getInstance().push(StackFrame(StackFrame::TerminatorService,
                                          domainId, termId, serviceId));
        }
        ~EnteringTerminatorService() {
            getInstance().pop();
            if (getInstance().empty())
                ProcessMonitor::getInstance().processIdle();
        }
    };

    class EnteringObjectService {
      public:
        EnteringObjectService(int domainId, int objectId, int serviceId) {
            if (getInstance().empty())
                ProcessMonitor::getInstance().processRunning();
            getInstance().push(StackFrame(StackFrame::ObjectService, domainId,
                                          objectId, serviceId));
        }
        ~EnteringObjectService() {
            getInstance().pop();
            if (getInstance().empty())
                ProcessMonitor::getInstance().processIdle();
        }
    };

    class EnteringState {
      public:
        EnteringState(int domainId, int objectId, int stateId) {
            if (getInstance().empty())
                ProcessMonitor::getInstance().processRunning();
            getInstance().push(StackFrame(StackFrame::StateAction, domainId,
                                          objectId, stateId));
        }
        ~EnteringState() {
            getInstance().pop();
            if (getInstance().empty())
                ProcessMonitor::getInstance().processIdle();
        }
    };

    class EnteredAction {
      public:
        EnteredAction() { ProcessMonitor::getInstance().enteredAction(); }
        ~EnteredAction() {
            getInstance().top().setLine(0);
            ProcessMonitor::getInstance().leavingAction();
        }
    };

    class EnteredCatch {
      public:
        EnteredCatch(int lineNo) {
            getInstance().top().setLine(lineNo);
            ProcessMonitor::getInstance().enteredCatch();
        }
        ~EnteredCatch() { ProcessMonitor::getInstance().leavingCatch(); }
    };

    class ExecutingStatement {
      public:
        ExecutingStatement(int lineNo) {
            getInstance().top().setLine(lineNo);
            ProcessMonitor::getInstance().startStatement();
        }
        ~ExecutingStatement() { ProcessMonitor::getInstance().endStatement(); }
    };

  private:
    static Stack &instance;
    std::vector<StackFrame> frames;
};

} // namespace SWA

#endif
