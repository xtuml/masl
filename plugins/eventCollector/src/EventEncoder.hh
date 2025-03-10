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

#ifndef Events_EventEncoder_HH
#define Events_EventEncoder_HH

#include <list>
#include <memory>
#include <stdint.h>

namespace EVENTS {

class EventWriter;
class EventContext;
class ProcessContext;
class DomainServiceContext;
class ObjectServiceContext;
class StateServiceContext;
class EventFinishedContext;
class TerminatorServiceContext;

class EventEncoder {
  public:
    EventEncoder();
    virtual ~EventEncoder();

    void addWriter(const ::std::shared_ptr<EventWriter> &writer);

    // THREAD EVENTS
    void writeThreadStarted(const ProcessContext &processContext,
                            const std::string &tag);
    void writeThreadFinished(const ProcessContext &processContext);
    void writeThreadAborted(const ProcessContext &processContext);

    // ACTION EVENTS
    void writeDomainService(const ProcessContext &processContext,
                            const DomainServiceContext &context);
    void writeTerminatorService(const ProcessContext &processContext,
                                const TerminatorServiceContext &context);
    void writeObjectService(const ProcessContext &processContext,
                            const ObjectServiceContext &context);
    void writeStateService(const ProcessContext &processContext,
                           const StateServiceContext &context);

    // SIGNAL EVENTS
    void writeGeneratingEvent(const ProcessContext &processContext,
                              const EventContext &eventContext);
    void writeProcessingEvent(const ProcessContext &processContext,
                              const EventContext &eventContext);

    // TIMER EVENTS
    void writeTimerCreated(const ProcessContext &processContext,
                           const uint32_t timerId);
    void writeTimerDeleted(const ProcessContext &processContext,
                           const uint32_t timerId);
    void writeTimerFired(const ProcessContext &processContext,
                         const uint32_t timerId);
    void writeTimerCancelled(const ProcessContext &processContext,
                             const uint32_t timerId);
    void writeTimerSet(const ProcessContext &processContext,
                       const uint32_t timerId);

    void writeFinishedEvent(const ProcessContext &processContext,
                            const EventFinishedContext &context);

    void shutdown();

  private:
    virtual void doWriteEncoded(
        const std::list<::std::shared_ptr<EventWriter>> &writers) const = 0;

    virtual void doEncodeThreadStarted(const ProcessContext &processContext,
                                       const std::string &tag) = 0;
    virtual void
    doEncodeThreadFinished(const ProcessContext &processContext) = 0;
    virtual void
    doEncodeThreadAborted(const ProcessContext &processContext) = 0;

    virtual void doEncodeDomainService(const ProcessContext &processContext,
                                       const DomainServiceContext &context) = 0;
    virtual void
    doEncodeTerminatorService(const ProcessContext &processContext,
                              const TerminatorServiceContext &context) = 0;

    virtual void doEncodeObjectService(const ProcessContext &processContext,
                                       const ObjectServiceContext &context) = 0;
    virtual void doEncodeStateService(const ProcessContext &processContext,
                                      const StateServiceContext &context) = 0;

    virtual void doEncodeGeneratingEvent(const ProcessContext &processContext,
                                         const EventContext &context) = 0;
    virtual void doEncodeProcessingEvent(const ProcessContext &processContext,
                                         const EventContext &context) = 0;

    virtual void doEncodeTimerCreated(const ProcessContext &processContext,
                                      const uint32_t timerId) = 0;
    virtual void doEncodeTimerDeleted(const ProcessContext &processContext,
                                      const uint32_t timerId) = 0;
    virtual void doEncodeTimerFired(const ProcessContext &processContext,
                                    const uint32_t timerId) = 0;
    virtual void doEncodeTimerCancelled(const ProcessContext &processContext,
                                        const uint32_t timerId) = 0;
    virtual void doEncodeTimerSet(const ProcessContext &processContext,
                                  const uint32_t timerId) = 0;

    virtual void doEncodeFinishedEvent(const ProcessContext &processContext,
                                       const EventFinishedContext &context) = 0;

    bool aWriterIsEnabled();

  private:
    std::list<::std::shared_ptr<EventWriter>> writers;
};

} // namespace EVENTS

#endif
