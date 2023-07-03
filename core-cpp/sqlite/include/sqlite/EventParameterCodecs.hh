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

#ifndef Sqlite_EventParameterCodecs_HH
#define Sqlite_EventParameterCodecs_HH

#include <boost/tuple/tuple.hpp>
#include <boost/tuple/tuple_comparison.hpp>
#include <boost/function.hpp>
#include <boost/unordered_map.hpp>
#include <boost/shared_ptr.hpp>
#include "Exception.hh"

#include "BlobData.hh"

namespace SWA
{
  class Event;
}

namespace SQLITE
{

  class EventParameterCodecs
  {
    public:
      static EventParameterCodecs& getInstance();
      
      typedef boost::function<void(boost::shared_ptr<SWA::Event>,BlobData&)> Encoder;
      typedef boost::function<boost::shared_ptr<SWA::Event>(BlobData&)>      Decoder;

      bool registerCodec ( int domainId, int objectId, int eventId, const Encoder& encoder, const Decoder& decoder );
      
      void encode ( const boost::shared_ptr<SWA::Event>& event, BlobData& buffer ) const;
      boost::shared_ptr<SWA::Event> decode ( int domainId, int objectId, int eventId, BlobData& buffer ) const;

    private:
      typedef boost::tuple<int,int,int> Key;
      typedef boost::unordered_map<Key,Encoder> Encoders;
      typedef boost::unordered_map<Key,Decoder> Decoders;

      Encoders encoders;
      Decoders decoders;
  };
}

#endif
