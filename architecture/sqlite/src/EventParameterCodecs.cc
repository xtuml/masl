/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "sqlite/EventParameterCodecs.hh"
#include "sqlite/BlobData.hh"
#include "swa/Event.hh"
#include "swa/tuple_hash.hh"

namespace SQLITE {

EventParameterCodecs &EventParameterCodecs::getInstance() {
    static EventParameterCodecs instance;
    return instance;
}

bool EventParameterCodecs::registerCodec(int domainId, int objectId,
                                         int eventId, const Encoder &encoder,
                                         const Decoder &decoder) {
    encoders.insert(
        Encoders::value_type(Key(domainId, objectId, eventId), encoder));
    decoders.insert(
        Decoders::value_type(Key(domainId, objectId, eventId), decoder));
    return true;
}

void EventParameterCodecs::encode(const std::shared_ptr<SWA::Event> &event,
                                  BlobData &buffer) const {
    encoders
        .find(Key(event->getDomainId(), event->getObjectId(),
                  event->getEventId()))
        ->second(event, buffer);
}

std::shared_ptr<SWA::Event>
EventParameterCodecs::decode(int domainId, int objectId, int eventId,
                             BlobData &buffer) const {
    return decoders.find(Key(domainId, objectId, eventId))->second(buffer);
}

} // namespace SQLITE
