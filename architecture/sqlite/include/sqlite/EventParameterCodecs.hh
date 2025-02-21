/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sqlite_EventParameterCodecs_HH
#define Sqlite_EventParameterCodecs_HH

#include "Exception.hh"
#include <functional>
#include <memory>
#include <boost/tuple/tuple.hpp>
#include <boost/tuple/tuple_comparison.hpp>
#include <boost/unordered_map.hpp>

#include "BlobData.hh"

namespace SWA {
class Event;
}

namespace SQLITE {

class EventParameterCodecs {
  public:
    static EventParameterCodecs &getInstance();

    typedef std::function<void(std::shared_ptr<SWA::Event>, BlobData &)>
        Encoder;
    typedef std::function<std::shared_ptr<SWA::Event>(BlobData &)> Decoder;

    bool registerCodec(int domainId, int objectId, int eventId,
                       const Encoder &encoder, const Decoder &decoder);

    void encode(const std::shared_ptr<SWA::Event> &event,
                BlobData &buffer) const;
    std::shared_ptr<SWA::Event> decode(int domainId, int objectId,
                                         int eventId, BlobData &buffer) const;

  private:
    typedef boost::tuple<int, int, int> Key;
    typedef std::unordered_map<Key, Encoder, boost::hash<Key>> Encoders;
    typedef std::unordered_map<Key, Decoder, boost::hash<Key>> Decoders;

    Encoders encoders;
    Decoders decoders;
};
} // namespace SQLITE

#endif
