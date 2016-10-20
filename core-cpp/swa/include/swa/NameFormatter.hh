//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
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
