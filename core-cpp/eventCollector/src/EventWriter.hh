//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  EventWriter.hh
//
//============================================================================//
#ifndef Events_EventWriter_HH
#define Events_EventWriter_HH

#include <string>

namespace EVENTS {

class EventWriter
{
   public:
      virtual ~EventWriter(){}

      virtual bool isEnabled() = 0;
      virtual void write(const std::string&  buffer)      = 0;
      virtual void write(const void *buffer, size_t size) = 0;

      virtual void shutdown() = 0;

   protected:
        EventWriter(){}
};

} // end EVENTS namespace

#endif
