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
