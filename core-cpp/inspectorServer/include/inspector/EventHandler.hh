//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef Inspector_EventHandler_HH
#define Inspector_EventHandler_HH

#include "CommunicationChannel.hh"
#include "swa/Event.hh"
#include <boost/shared_ptr.hpp>

namespace Inspector
{
  class EventHandler
  {
    public:
      virtual boost::shared_ptr<SWA::Event> getEvent ( CommunicationChannel& channel ) const = 0;

      virtual void writeParameters ( const SWA::Event& event, BufferedOutputStream& stream ) const = 0;

      virtual ~EventHandler();
  };
}

#endif
