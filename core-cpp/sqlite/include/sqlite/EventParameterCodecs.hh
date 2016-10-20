//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
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
