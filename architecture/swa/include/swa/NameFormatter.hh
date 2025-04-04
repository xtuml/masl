/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_NameFormatter_HH
#define SWA_NameFormatter_HH

#include "Exception.hh"
#include "Stack.hh"

#include <functional>

namespace SWA {

    // *****************************************************************
    // *****************************************************************
    class NameFormatter {
      public:
        static bool overrideFormatter(const std::shared_ptr<NameFormatter> formatter);

      public:
        static std::string formatStackFrame(const ExceptionStackFrame &frame, bool showLineNo = true);
        static std::string formatStackFrame(const StackFrame &frame, bool showLineNo = true);
        static std::string formatStackFrame(StackFrame::ActionType type, int domainId, int objectId, int actionId);
        static std::string formatDomainServiceName(int domainId, int serviceId);
        static std::string formatTerminatorName(int domainId, int terminatorId);
        static std::string formatTerminatorServiceName(int domainId, int terminatorId, int serviceId);
        static std::string formatObjectName(int domainId, int objectId);
        static std::string formatObjectServiceName(int domainId, int objectId, int serviceId);
        static std::string formatStateName(int domainId, int objectId, int stateId);
        static std::string formatEventName(int domainId, int objectId, int eventId);

        static std::string formatFileName(const ExceptionStackFrame &frame);
        static std::string formatFileName(const StackFrame &frame);
        static std::string formatFileName(StackFrame::ActionType type, int domainId, int objectId, int actionId);
        static std::string formatDomainServiceFileName(int domainId, int serviceId);
        static std::string formatTerminatorServiceFileName(int domainId, int terminatorId, int serviceId);
        static std::string formatObjectServiceFileName(int domainId, int objectId, int serviceId);
        static std::string formatStateFileName(int domainId, int objectId, int stateId);

      private:
        static NameFormatter &getInstance();

        static std::string getDomainName(int domainId);

        virtual std::string getDomainServiceName(int domainId, int serviceId) const;
        virtual std::string getTerminatorName(int domainId, int terminatorId) const;
        virtual std::string getTerminatorServiceName(int domainId, int terminatorId, int serviceId) const;
        virtual std::string getObjectName(int domainId, int objectId) const;
        virtual std::string getObjectServiceName(int domainId, int objectId, int serviceId) const;
        virtual std::string getStateName(int domainId, int objectId, int stateId) const;
        virtual std::string getEventName(int domainId, int objectId, int eventId) const;

        virtual std::string getDomainServiceFileName(int domainId, int serviceId) const;
        virtual std::string getTerminatorServiceFileName(int domainId, int terminatorId, int serviceId) const;
        virtual std::string getObjectServiceFileName(int domainId, int objectId, int serviceId) const;
        virtual std::string getStateFileName(int domainId, int objectId, int stateId) const;

        virtual int getEventParentObjectId(int domainId, int objectId, int eventId) const;

      public:
        NameFormatter() {}
        virtual ~NameFormatter() {}
    };

} // namespace SWA

#endif
