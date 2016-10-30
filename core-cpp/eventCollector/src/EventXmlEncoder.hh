//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  EventCollector.hh
//
//============================================================================//
#ifndef Events_EventXmlEncoder_HH
#define Events_EventXmlEncoder_HH
#include <string>

#include "EventEncoder.hh"

namespace EVENTS {
class StateServiceContext;
class ObjectServiceContext;
class DomainServiceContext;
class EventFinishedContext;
class EventXmlEncoder : public EventEncoder
{
   public:
               EventXmlEncoder();
      virtual ~EventXmlEncoder();

    private:
       virtual void doWriteEncoded(const std::list< ::boost::shared_ptr<EventWriter> >& writers) const;

       virtual void doEncodeThreadStarted  (const ProcessContext& processContext, const std::string& tag);
       virtual void doEncodeThreadFinished (const ProcessContext& processContext);
       virtual void doEncodeThreadAborted  (const ProcessContext& processContext);

       virtual void doEncodeDomainService     (const ProcessContext& processContext, const DomainServiceContext& serviceContext);
       virtual void doEncodeTerminatorService (const ProcessContext& processContext, const TerminatorServiceContext& context);
       virtual void doEncodeObjectService     (const ProcessContext& processContext, const ObjectServiceContext& objectContext);
       virtual void doEncodeStateService      (const ProcessContext& processContext, const StateServiceContext&  stateContext);

       virtual void doEncodeGeneratingEvent  (const ProcessContext& processContext, const EventContext&  context);
       virtual void doEncodeProcessingEvent  (const ProcessContext& processContext, const EventContext&  context);

       virtual void doEncodeTimerCreated   (const ProcessContext& processContext, const uint32_t timerId);
       virtual void doEncodeTimerDeleted   (const ProcessContext& processContext, const uint32_t timerId);
       virtual void doEncodeTimerFired     (const ProcessContext& processContext, const uint32_t timerId);
       virtual void doEncodeTimerCancelled (const ProcessContext& processContext, const uint32_t timerId);
       virtual void doEncodeTimerSet       (const ProcessContext& processContext, const uint32_t timerId);

       virtual void doEncodeFinishedEvent  (const ProcessContext& processContext, const EventFinishedContext& context);

    private:
         void formTimeAttribute    (const ProcessContext& processContext, std::string& buffer);
         void formEventTag         (const ProcessContext& processContext, const std::string& type,   const std::string& action, const std::string& frame, const bool isSingleLine, std::string& buffer);
         void formServiceTag       (const ProcessContext& processContext, const std::string& domain, const std::string& name,   const std::string& type,  std::string& buffer);
         void formTerminatorTag    (const ProcessContext& processContext, const std::string& domain, const std::string& keyletters, const std::string& name,  const bool isMulti, std::string& buffer);

         void formObjectServiceTag (const ProcessContext& processContext, const std::string& domain, const std::string& object, const std::string& name,  const std::string& type, std::string& buffer);
         void formStateServiceTag  (const ProcessContext& processContext, const std::string& domain, const std::string& object, const std::string& state, const std::string& type, std::string& buffer);

         void formSignalTag        (const ProcessContext& processContext, const EventContext& context, std::string& buffer);
         void formSourceTag        (const ProcessContext& processContext, const EventContext& context, std::string& buffer);
         void formDestinationTag   (const ProcessContext& processContext, const EventContext& context, std::string& buffer);

         void formTimerTag         (const ProcessContext& processContext, const std::string& value, std::string& buffer);
         void formParameterTag     (const ProcessContext& processContext, const std::string& name,  const std::string& value, std::string& buffer);

    private:
       mutable std::string xmlBuffer;
       enum { notASingleLine = 0, isASingleLine = 1};
};


} // end EVENTS namespace

#endif
