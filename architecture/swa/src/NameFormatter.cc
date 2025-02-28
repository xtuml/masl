/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "swa/NameFormatter.hh"
#include "swa/Process.hh"
#include <format>
#include <iostream>

namespace SWA {
    std::shared_ptr<NameFormatter> &getOverride() {
        static std::shared_ptr<NameFormatter> formatter;
        return formatter;
    }

    NameFormatter &NameFormatter::getInstance() {
        static NameFormatter instance;
        return getOverride() ? *getOverride().get() : instance;
    }

    bool NameFormatter::overrideFormatter(const std::shared_ptr<NameFormatter> newFormatter) {
        getOverride() = newFormatter;
        return true;
    }

    std::string NameFormatter::formatStackFrame(const ExceptionStackFrame &frame, bool showLineNo) {
        return formatStackFrame(frame.getType(), frame.getDomainId(), frame.getObjectId(), frame.getActionId()) +
               (showLineNo ? std::format(":{}", frame.getLine()) : "");
    }

    std::string NameFormatter::formatStackFrame(const StackFrame &frame, bool showLineNo) {
        return formatStackFrame(frame.getType(), frame.getDomainId(), frame.getObjectId(), frame.getActionId()) +
               (showLineNo ? std::format(":{}", frame.getLine()) : "");
    }

    std::string NameFormatter::formatStackFrame(StackFrame::ActionType type, int domainId, int objectId, int actionId) {
        std::string name;
        switch (type) {
            case StackFrame::DomainService:
                return formatDomainServiceName(domainId, actionId);
            case StackFrame::TerminatorService:
                return formatTerminatorServiceName(domainId, objectId, actionId);
            case StackFrame::ObjectService:
                return formatObjectServiceName(domainId, objectId, actionId);
            case StackFrame::StateAction:
                return formatStateName(domainId, objectId, actionId);
            default:
                return "UNKNOWN";
        }
    }

    std::string NameFormatter::getDomainName(int domainId) {
        return Process::getInstance().getDomain(domainId).getName();
    }

    std::string NameFormatter::getTerminatorName(int domainId, int terminatorId) const {
        return std::format("Terminator_{}", terminatorId);
    }

    std::string NameFormatter::getTerminatorServiceName(int domainId, int terminatorId, int serviceId) const {
        return std::format("Service_{}", serviceId);
    }

    std::string NameFormatter::getDomainServiceName(int domainId, int serviceId) const {
        return std::format("Service_{}", serviceId);
    }

    std::string NameFormatter::getObjectName(int domainId, int objectId) const {
        return std::format("Object_{}", objectId);
    }

    std::string NameFormatter::getObjectServiceName(int domainId, int objectId, int serviceId) const {
        return std::format("Service_{}", serviceId);
    }

    std::string NameFormatter::getStateName(int domainId, int objectId, int stateId) const {
        return std::format("State_{}", stateId);
    }

    std::string NameFormatter::getEventName(int domainId, int objectId, int eventId) const {
        return std::format("Event_{}", eventId);
    }

    int NameFormatter::getEventParentObjectId(int domainId, int objectId, int eventId) const {
        return objectId;
    }

    std::string NameFormatter::formatTerminatorName(int domainId, int terminatorId) {
        return std::format("{}::{}", getDomainName(domainId), getInstance().getTerminatorName(domainId, terminatorId));
    }

    std::string NameFormatter::formatDomainServiceName(int domainId, int serviceId) {
        return std::format("{}::{}", getDomainName(domainId), getInstance().getDomainServiceName(domainId, serviceId));
    }

    std::string NameFormatter::formatObjectName(int domainId, int objectId) {
        return std::format("{}::{}", getDomainName(domainId), getInstance().getObjectName(domainId, objectId));
    }

    std::string NameFormatter::formatTerminatorServiceName(int domainId, int terminatorId, int serviceId) {
        return std::format(
            "{}~>{}",
            formatTerminatorName(domainId, terminatorId),
            getInstance().getTerminatorServiceName(domainId, terminatorId, serviceId)
        );
    }

    std::string NameFormatter::formatObjectServiceName(int domainId, int objectId, int serviceId) {
        return std::format(
            "{}.{}",
            formatObjectName(domainId, objectId),
            getInstance().getObjectServiceName(domainId, objectId, serviceId)
        );
    }

    std::string NameFormatter::formatStateName(int domainId, int objectId, int stateId) {
        return std::format(
            "{}.{}", formatObjectName(domainId, objectId), getInstance().getStateName(domainId, objectId, stateId)
        );
    }

    std::string NameFormatter::formatEventName(int domainId, int objectId, int eventId) {
        return std::format(
            "{}.{}",
            formatObjectName(domainId, getInstance().getEventParentObjectId(domainId, objectId, eventId)),
            getInstance().getEventName(domainId, objectId, eventId)
        );
    }

    std::string NameFormatter::formatFileName(const ExceptionStackFrame &frame) {
        return formatFileName(frame.getType(), frame.getDomainId(), frame.getObjectId(), frame.getActionId());
    }
    std::string NameFormatter::formatFileName(const StackFrame &frame) {
        return formatFileName(frame.getType(), frame.getDomainId(), frame.getObjectId(), frame.getActionId());
    }
    std::string NameFormatter::formatFileName(StackFrame::ActionType type, int domainId, int objectId, int actionId) {
        std::string name;
        switch (type) {
            case StackFrame::DomainService:
                return formatDomainServiceFileName(domainId, actionId);
            case StackFrame::TerminatorService:
                return formatTerminatorServiceFileName(domainId, objectId, actionId);
            case StackFrame::ObjectService:
                return formatObjectServiceFileName(domainId, objectId, actionId);
            case StackFrame::StateAction:
                return formatStateFileName(domainId, objectId, actionId);
            default:
                return "<unknown>";
        }
    }

    std::string NameFormatter::formatDomainServiceFileName(int domainId, int serviceId) {
        return getInstance().getDomainServiceFileName(domainId, serviceId);
    }
    std::string NameFormatter::formatTerminatorServiceFileName(int domainId, int terminatorId, int serviceId) {
        return getInstance().getTerminatorServiceFileName(domainId, terminatorId, serviceId);
    }

    std::string NameFormatter::formatObjectServiceFileName(int domainId, int objectId, int serviceId) {
        return getInstance().getObjectServiceFileName(domainId, objectId, serviceId);
    }

    std::string NameFormatter::formatStateFileName(int domainId, int objectId, int stateId) {
        return getInstance().getStateFileName(domainId, objectId, stateId);
    }

    std::string NameFormatter::getDomainServiceFileName(int domainId, int serviceId) const {
        return "<unknown>";
    }
    std::string NameFormatter::getTerminatorServiceFileName(int domainId, int terminatorId, int serviceId) const {
        return "<unknown>";
    }
    std::string NameFormatter::getObjectServiceFileName(int domainId, int objectId, int serviceId) const {
        return "<unknown>";
    }
    std::string NameFormatter::getStateFileName(int domainId, int objectId, int stateId) const {
        return "<unknown>";
    }

} // namespace SWA
