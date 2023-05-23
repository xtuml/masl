//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  EventConsoleWriter.hh
//
//============================================================================//
#include <cerrno>
#include <iostream>
#include <fstream>
#include <unistd.h>
#include <stdlib.h>
#include <sstream>

#include "EventConsoleWriter.hh"

namespace {

// ***************************************************************
// ***************************************************************
std::ostream* getConsoleStream()
{
   std::ostream* console = 0;
   if (getenv("EVENT_CONSOLE_ENABLED") != 0){
       console = &std::cout;
   }
   return console;
}

}

namespace EVENTS {

// ***************************************************************
// ***************************************************************
EventConsoleWriter::EventConsoleWriter():
   console(getConsoleStream())
{
}

// ***************************************************************
// ***************************************************************
EventConsoleWriter::~EventConsoleWriter()
{
  console && console->flush();
}

// ***************************************************************
// ***************************************************************
bool EventConsoleWriter::isEnabled()
{
   return bool(console);
}

// ***************************************************************
// ***************************************************************
void EventConsoleWriter::write(const std::string& buffer)
{
  console && *console << buffer << std::flush;
}

// ***************************************************************
// ***************************************************************
void EventConsoleWriter::write(const void *buffer, size_t size)
{
   // only write formatted text. So ignore this call.
}

// ***************************************************************
// ***************************************************************
void EventConsoleWriter::shutdown()
{
   console && console->flush();
}

} // end EVENTS namespace
