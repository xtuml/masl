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

#include "sqlite/EventParameterCodecs.hh"
#include "sqlite/BlobData.hh"
#include "swa/Event.hh"
#include "swa/tuple_hash.hh"


namespace SQLITE
{

  EventParameterCodecs& EventParameterCodecs::getInstance()
  {
    static EventParameterCodecs instance;
    return instance;
  }

  bool EventParameterCodecs::registerCodec ( int domainId, int objectId, int eventId, const Encoder& encoder, const Decoder& decoder )
  {
    encoders.insert(Encoders::value_type(Key(domainId,objectId,eventId),encoder));
    decoders.insert(Decoders::value_type(Key(domainId,objectId,eventId),decoder));
    return true;
  }

  void EventParameterCodecs::encode ( const boost::shared_ptr<SWA::Event>& event, BlobData& buffer ) const
  {
    encoders.find(Key(event->getDomainId(),event->getObjectId(),event->getEventId()))->second(event,buffer);
  }

  boost::shared_ptr<SWA::Event> EventParameterCodecs::decode ( int domainId, int objectId, int eventId, BlobData& buffer ) const
  {
    return decoders.find(Key(domainId,objectId,eventId))->second(buffer);
  }


}

