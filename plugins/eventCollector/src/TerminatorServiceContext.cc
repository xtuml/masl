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

#include "TerminatorServiceContext.hh"

#include "swa/ProgramError.hh"
#include "swa/Stack.hh"
#include "swa/StackFrame.hh"

#include "metadata/MetaData.hh"

#include <format>

namespace EVENTS {

// ***************************************************************
// ***************************************************************
TerminatorServiceContext::TerminatorServiceContext(
    const SWA::StackFrame &frame, const std::size_t frameLevel,
    const SWA::DomainMetaData &domain)
    : frame(frame), frameLevel(frameLevel), domain(domain) {
    if (frame.getType() != SWA::StackFrame::TerminatorService) {
        throw SWA::ProgramError("Failed to create TerminatorServiceContext : "
                                "not a  based terminator service ");
    }
}

// ***************************************************************
// ***************************************************************
TerminatorServiceContext::~TerminatorServiceContext() {}

// ***************************************************************
// ***************************************************************
int TerminatorServiceContext::getDomainId() const { return domain.getId(); }

// ***************************************************************
// ***************************************************************
int TerminatorServiceContext::getServiceId() const {
    return frame.getActionId();
}

// ***************************************************************
// ***************************************************************
const std::string TerminatorServiceContext::getKeyLetters() const {
    return domain.getTerminator(frame.getObjectId()).getKeyLetters();
}

// ***************************************************************
// ***************************************************************
const bool TerminatorServiceContext::isMulti() const {
    return domain.getTerminator(frame.getObjectId())
               .getService(frame.getActionId())
               .getType() == SWA::ServiceMetaData::ProjectTerminator;
}

// ***************************************************************
// ***************************************************************
const std::string TerminatorServiceContext::getDomainName() const {
    return domain.getName();
}

// ***************************************************************
// ***************************************************************
const std::string TerminatorServiceContext::getServiceName() const {
    return domain.getTerminator(frame.getObjectId())
        .getService(frame.getActionId())
        .getName();
}

// ***************************************************************
// ***************************************************************
const std::string TerminatorServiceContext::getStackFrameLevel() const {
    return std::format("{}", frameLevel);
}

} // namespace EVENTS
