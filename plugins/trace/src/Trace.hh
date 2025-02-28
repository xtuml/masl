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

#ifndef Trace_HH
#define Trace_HH

#include <unordered_set>

#include "swa/Event.hh"
#include "swa/ProcessMonitor.hh"
#include "swa/Stack.hh"

namespace Trace {

    class Trace : public SWA::ProcessMonitor::MonitorConnection {

      public:
        static Trace &getInstance();

        std::string getName() {
            return "Trace";
        }

        void processingEvent(const std::shared_ptr<SWA::Event> &event);

        void enteredAction();
        void leavingAction();

        void startStatement();

        bool isTraceLines() const {
            return traceLines;
        }
        void setTraceLines(bool flag) {
            traceLines = flag;
            checkConnect();
        }

        bool isTraceEvents() const {
            return traceEvents;
        }
        void setTraceEvents(bool flag) {
            traceEvents = flag;
            checkConnect();
        }

        bool isTraceActions() const {
            return traceActions;
        }
        void setTraceActions(bool flag) {
            traceActions = flag;
            checkConnect();
        }

        bool initialise();

        Trace();
        virtual ~Trace();

      private:
        void checkConnect();

      private:
        std::unordered_set<int> domains;
        bool traceEvents;
        bool traceActions;
        bool traceLines;
        SWA::Stack &processStack;
    };

} // namespace Trace

#endif
