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

#include <algorithm>
#include <print>

#include "metadata/MetaData.hh"
#include "swa/Exception.hh"
#include "swa/Process.hh"
#include "swa/Stack.hh"
#include "swa/StackFrame.hh"
#include "swa/Timestamp.hh"

#include "EventConsoleWriter.hh"
#include "EventFileWriter.hh"
#include "EventInspectorWriter.hh"
#include "EventXmlEncoder.hh"

#include "DomainServiceContext.hh"
#include "EventContext.hh"
#include "EventFinishedContext.hh"
#include "ObjectServiceContext.hh"
#include "StateServiceContext.hh"
#include "TerminatorServiceContext.hh"
#include "eventCollector/EventCollector.hh"

namespace {
bool initialiseCollector() {
    static ::EVENTS::EventCollector collector;
    return true;
}

struct init {
    init() {
        SWA::Process::getInstance().registerStartedListener(
            &initialiseCollector);
    }
} x;

} // namespace

namespace EVENTS {

// ***************************************************************
// ***************************************************************
EventCollector::EventCollector() {

    std::shared_ptr<EventEncoder> xmlEncoder(new EventXmlEncoder);

    xmlEncoder->addWriter(std::shared_ptr<EventWriter>(new EventFileWriter));
    xmlEncoder->addWriter(std::shared_ptr<EventWriter>(new EventConsoleWriter));

    try {
        xmlEncoder->addWriter(
            std::shared_ptr<EventWriter>(new EventInspectorWriter));
    } catch (const SKT::SocketException &se) {
        std::println(stderr, "EventInspectorWriter initialisation failed : {}",
                     se.report());
        std::println(stderr, "EventInspectorWriter has been disabled ...  ");
    }
    eventEncoders.push_back(xmlEncoder);

    registerMonitor();
    connectToMonitor();

    SWA::Process::getInstance().registerThreadStartedListener(
        [this](const std::string &tag) { threadStarted(tag); });
    SWA::Process::getInstance().registerThreadCompletedListener(
        [this]() { threadCompleted(); });
    SWA::Process::getInstance().registerThreadAbortedListener(
        [this]() { threadAborted(); });
    SWA::Process::getInstance().registerShutdownListener(
        [this]() { shutdown(); });
}

// ***************************************************************
// ***************************************************************
EventCollector::~EventCollector() {}

// ***************************************************************
// ***************************************************************
std::string EventCollector::getName() { return "EventCollector"; }

// ***************************************************************
// ***************************************************************
void EventCollector::threadStarted(const std::string &tag) {
    for (const auto &encoder : eventEncoders) {
        encoder->writeThreadStarted(processContext, tag);
    }
}

// ***************************************************************
// ***************************************************************
void EventCollector::threadCompleted() {
    const EventFinishedContext finishedContext("thread");
    for (const auto &encoder : eventEncoders) {
        encoder->writeFinishedEvent(processContext, finishedContext);
    }
    for (const auto &encoder : eventEncoders) {
        encoder->writeThreadFinished(processContext);
    }
}

// ***************************************************************
// ***************************************************************
void EventCollector::threadAborted() {
    std::cout << "[ Thread aborted ]" << std::endl;
    for (const auto &encoder : eventEncoders) {
        encoder->writeThreadAborted(processContext);
    }
}

// ***************************************************************
// ***************************************************************
void EventCollector::startProcessingEventQueue() {
    // std::cout << "[ Start Processing EventQueue ]" << std::endl;
}

// ***************************************************************
// ***************************************************************
void EventCollector::endProcessingEventQueue() {
    // std::cout << "[ Finished Processing EventQueue ] " << std::endl;
}

// ***************************************************************
// ***************************************************************
void EventCollector::enteredAction() {
    SWA::Stack &maslStack = SWA::Stack::getInstance();
    const SWA::StackFrame &currentFrame = maslStack.top();
    const std::size_t frameLevel = maslStack.getStackFrames().size();

    try {

        const SWA::DomainMetaData &actionsDomain =
            SWA::ProcessMetaData::getProcess().getDomain(
                currentFrame.getDomainId());
        switch (currentFrame.getType()) {
        case SWA::StackFrame::DomainService: {
            const DomainServiceContext domainServiceContext(
                currentFrame, frameLevel, actionsDomain);
            for (const auto &encoder : eventEncoders) {
                encoder->writeDomainService(processContext,
                                            domainServiceContext);
            }
        } break;

        case SWA::StackFrame::TerminatorService: {
            const TerminatorServiceContext terminatorServiceContext(
                currentFrame, frameLevel, actionsDomain);
            for (const auto &encoder : eventEncoders) {
                encoder->writeTerminatorService(processContext,
                                                terminatorServiceContext);
            }
        } break;

        case SWA::StackFrame::ObjectService: {
            const ObjectServiceContext objectServiceContext(
                currentFrame, frameLevel, actionsDomain,
                actionsDomain.getObject(currentFrame.getObjectId()));
            for (const auto &encoder : eventEncoders) {
                encoder->writeObjectService(processContext,
                                            objectServiceContext);
            }
        } break;

        case SWA::StackFrame::StateAction: {
            const StateServiceContext stateServiceContext(
                currentFrame, frameLevel, actionsDomain,
                actionsDomain.getObject(currentFrame.getObjectId()));

            for (const auto &encoder : eventEncoders) {
                encoder->writeStateService(processContext, stateServiceContext);
            }
        } break;

        default: {
            std::println("EventCollector::enteredAction : encoutered invalid "
                         "action type in switch statement {}",
                         static_cast<int>(currentFrame.getType()));
            return;
        } break;
        }
    } catch (::SWA::Exception &e) {
        std::println("EventCollector::enteredAction : meta data lookup failed "
                     "for action : {},{},{} : {}",
                     currentFrame.getDomainId(), currentFrame.getObjectId(),
                     currentFrame.getActionId(), e.what());
    }
}

// ***************************************************************
// ***************************************************************
void EventCollector::leavingAction() {}

// ***************************************************************
// ***************************************************************
void EventCollector::leftAction() {
    SWA::Stack &maslStack = SWA::Stack::getInstance();
    const SWA::StackFrame &currentFrame = maslStack.top();

    try {

        switch (currentFrame.getType()) {
        case SWA::StackFrame::DomainService: {
            const EventFinishedContext finishedContext("service");
            for (const auto &encoder : eventEncoders) {
                encoder->writeFinishedEvent(processContext, finishedContext);
            }
        } break;

        case SWA::StackFrame::ObjectService: {
            const EventFinishedContext finishedContext("service");
            for (const auto &encoder : eventEncoders) {
                encoder->writeFinishedEvent(processContext, finishedContext);
            }
        } break;

        case SWA::StackFrame::StateAction: {
            const EventFinishedContext finishedContext("state");
            for (const auto &encoder : eventEncoders) {
                encoder->writeFinishedEvent(processContext, finishedContext);
            }
        } break;

        default: {
            std::println("EventCollector::leftAction : encoutered invalid "
                         "action type in switch statement {}",
                         static_cast<int>(currentFrame.getType()));
            return;
        } break;
        }
    } catch (::SWA::Exception &e) {
        std::println("EventCollector::leftAction : meta data lookup failed for "
                     "action : {},{},{}",
                     currentFrame.getDomainId(), currentFrame.getObjectId(),
                     currentFrame.getActionId());
    }
}

// ***************************************************************
// ***************************************************************
void EventCollector::exceptionRaised(const std::string &message) {}

// ***************************************************************
// ***************************************************************
void EventCollector::exceptionCaught() {}

// ***************************************************************
// ***************************************************************
void EventCollector::exceptionPropagated() {}

// ***************************************************************
// ***************************************************************
void EventCollector::generatingEvent(int domainId, int objectId, int eventId,
                                     int destObjectId, SWA::IdType instanceId) {
    EventContext eventContext(domainId, objectId, eventId, destObjectId,
                              instanceId);
    for (const auto &encoder : eventEncoders) {
        encoder->writeGeneratingEvent(processContext, eventContext);
    }
}

// ***************************************************************
// ***************************************************************
void EventCollector::processingEvent(int domainId, int objectId, int eventId,
                                     int destObjectId, SWA::IdType instanceId) {
    EventContext eventContext(domainId, objectId, eventId, destObjectId,
                              instanceId);
    for (const auto &encoder : eventEncoders) {
        encoder->writeProcessingEvent(processContext, eventContext);
    }
}

// ***************************************************************
// ***************************************************************
void EventCollector::generatingEvent(int domainId, int objectId, int eventId,
                                     int destObjectId) {
    EventContext eventContext(domainId, objectId, eventId, destObjectId);
    for (const auto &encoder : eventEncoders) {
        encoder->writeGeneratingEvent(processContext, eventContext);
    }
}

// ***************************************************************
// ***************************************************************
void EventCollector::processingEvent(int domainId, int objectId, int eventId,
                                     int destObjectId) {
    EventContext eventContext(domainId, objectId, eventId, destObjectId);
    for (const auto &encoder : eventEncoders) {
        encoder->writeProcessingEvent(processContext, eventContext);
    }
}

// ***************************************************************
// ***************************************************************
void EventCollector::firingTimer(int timerId) {
    for (const auto &encoder : eventEncoders) {
        encoder->writeTimerFired(processContext, timerId);
    }
}

// ***************************************************************
// ***************************************************************
void EventCollector::creatingTimer(int timerId) {
    for (const auto &encoder : eventEncoders) {
        encoder->writeTimerCreated(processContext, timerId);
    }
}

// ***************************************************************
// ***************************************************************
void EventCollector::deletingTimer(int timerId) {
    for (const auto &encoder : eventEncoders) {
        encoder->writeTimerDeleted(processContext, timerId);
    }
}

// ***************************************************************
// ***************************************************************
void EventCollector::cancellingTimer(int timerId) {
    for (const auto &encoder : eventEncoders) {
        encoder->writeTimerCancelled(processContext, timerId);
    }
}

// ***************************************************************
// ***************************************************************
void EventCollector::settingTimer(int timerId, const SWA::Timestamp &timeout,
                                  const SWA::Duration &period,
                                  const std::shared_ptr<::SWA::Event> &event) {
    for (const auto &encoder : eventEncoders) {
        encoder->writeTimerSet(processContext, timerId);
    }
}

// ***************************************************************
// ***************************************************************
void EventCollector::shutdown() {
    for (const auto &encoder : eventEncoders) {
        encoder->shutdown();
    }
}

} // namespace EVENTS
