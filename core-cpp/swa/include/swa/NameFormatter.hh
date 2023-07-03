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

#ifndef SWA_NameFormatter_HH
#define SWA_NameFormatter_HH


#include "Exception.hh"
#include "Stack.hh"

#include "boost/function.hpp"

namespace SWA
{

  // *****************************************************************
  // *****************************************************************
  class NameFormatter
  {
    public:
      static bool overrideFormatter ( const boost::shared_ptr<NameFormatter> formatter );

    public:
      static std::string formatStackFrame ( const ExceptionStackFrame& frame, bool showLineNo = true );
      static std::string formatStackFrame ( const StackFrame& frame, bool showLineNo = true );
      static std::string formatStackFrame ( StackFrame::ActionType type, int domainId, int objectId, int actionId );
      static std::string formatDomainServiceName ( int domainId, int serviceId );
      static std::string formatTerminatorName ( int domainId, int terminatorId );
      static std::string formatTerminatorServiceName ( int domainId, int terminatorId, int serviceId );
      static std::string formatObjectName ( int domainId, int objectId );
      static std::string formatObjectServiceName ( int domainId, int objectId, int serviceId );
      static std::string formatStateName ( int domainId, int objectId, int stateId );
      static std::string formatEventName ( int domainId, int objectId, int eventId );

    private:
      static NameFormatter& getInstance();

      static std::string getDomainName ( int domainId );

      virtual std::string getDomainServiceName ( int domainId, int serviceId ) const;
      virtual std::string getTerminatorName ( int domainId, int terminatorId ) const;
      virtual std::string getTerminatorServiceName ( int domainId, int terminatorId, int serviceId ) const;
      virtual std::string getObjectName ( int domainId, int objectId ) const;
      virtual std::string getObjectServiceName ( int domainId, int objectId, int serviceId ) const;
      virtual std::string getStateName ( int domainId, int objectId, int stateId ) const;
      virtual std::string getEventName ( int domainId, int objectId, int eventId ) const;
      virtual int getEventParentObjectId ( int domainId, int objectId, int eventId ) const;

    public:
      NameFormatter() {}
      virtual ~NameFormatter() {}

  };




}

#endif
