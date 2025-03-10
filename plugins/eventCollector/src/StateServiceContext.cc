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

#include <string>
#include <vector>

#include "StateServiceContext.hh"

#include "swa/ProgramError.hh"
#include "swa/Stack.hh"
#include "swa/StackFrame.hh"

#include "metadata/MetaData.hh"

namespace EVENTS {

// ***************************************************************
// ***************************************************************
StateServiceContext::StateServiceContext(const SWA::StackFrame &frame,
                                         const std::size_t frameLevel,
                                         const SWA::DomainMetaData &domain,
                                         const SWA::ObjectMetaData &object)
    : frame(frame), frameLevel(frameLevel), domain(domain), object(object) {
    if (frame.getType() != SWA::StackFrame::StateAction) {
        throw SWA::ProgramError(
            "Failed to create  StateServiceContext : not a state action ");
    }
}

// ***************************************************************
// ***************************************************************
StateServiceContext::~StateServiceContext() {}

// ***************************************************************
// ***************************************************************
int StateServiceContext::getDomainId() const { return domain.getId(); }

// ***************************************************************
// ***************************************************************
int StateServiceContext::getObjectId() const { return object.getId(); }

// ***************************************************************
// ***************************************************************
int StateServiceContext::getStateTypeId() const {
    return object.getState(frame.getActionId()).getType();
}

// ***************************************************************
// ***************************************************************
int StateServiceContext::getServiceId() const {
    return object.getService(frame.getActionId()).getId();
}

// ***************************************************************
// ***************************************************************
const std::string StateServiceContext::getDomainName() const {
    return domain.getName();
}

// ***************************************************************
// ***************************************************************
const std::string StateServiceContext::getObjectName() const {
    return object.getName();
}

// ***************************************************************
// ***************************************************************
const std::string StateServiceContext::getStateName() const {
    return object.getState(frame.getActionId()).getName();
}

// ***************************************************************
// ***************************************************************
const std::string StateServiceContext::getStateType() const {
    std::string stateType("unknown");
    switch (object.getState(frame.getActionId()).getType()) {
    case SWA::StateMetaData::Assigner:
        stateType = "assigner";
        break;
    case SWA::StateMetaData::Start:
        stateType = "start";
        break;
    case SWA::StateMetaData::Normal:
        stateType = "normal";
        break;
    case SWA::StateMetaData::Creation:
        stateType = "creation";
        break;
    case SWA::StateMetaData::Terminal:
        stateType = "terminal";
        break;
    default: // let the default unknown value drop through
        break;
    };
    return stateType;
}

// ***************************************************************
// ***************************************************************
const std::string StateServiceContext::getStackFrameLevel() const {
    return std::format("{}", frameLevel);
}

} // namespace EVENTS
