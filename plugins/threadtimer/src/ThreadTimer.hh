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

#ifndef ThreadTimer_HH
#define ThreadTimer_HH

#include "swa/Duration.hh"
#include "swa/ProcessMonitor.hh"

namespace ThreadTimer {

    class ThreadTimer : public SWA::ProcessMonitor::MonitorConnection {

      public:
        static ThreadTimer &getInstance();

        virtual std::string getName() {
            return "Thread Timer";
        }

        virtual void enteredAction();
        virtual void leavingAction();
        virtual void threadCompleting();
        virtual void threadCompleted();

        std::string getThreshold() const;
        void setThreshold(const std::string &millis);

        bool initialise();

        ThreadTimer();
        virtual ~ThreadTimer();

        void setActive(bool flag);
        bool isActive() const {
            return active;
        }
        void setTimeActions(bool flag) {
            this->timeActions = flag;
        }
        bool isTimeActions() const {
            return timeActions;
        }

      private:
        void formatLine(
            std::ostream &stream,
            const std::string &name,
            const SWA::Duration &startReal,
            const SWA::Duration &startUser,
            const SWA::Duration &startSystem
        );

      private:
        bool active;
        bool timeActions;
        bool timingThread;
        bool timingAction;
        SWA::Stack &processStack;
        std::string actionName;
        SWA::Duration threadStartUser;
        SWA::Duration threadStartSystem;
        SWA::Duration threadStartReal;
        SWA::Duration actionStartUser;
        SWA::Duration actionStartSystem;
        SWA::Duration actionStartReal;
        SWA::Duration threshold;
        std::ostringstream report;
    };

} // namespace ThreadTimer

#endif
