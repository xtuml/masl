//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  EventFileWriter.hh
//
//============================================================================//
#include <cerrno>
#include <iostream>
#include <fstream>
#include <unistd.h>
#include <string.h>
#include <sstream>

#include "EventFileWriter.hh"

namespace {

// ***************************************************************
// ***************************************************************
std::string formFileName()
{
   std::string fileName;
   if (getenv("EVENT_FILE_ENABLED") != 0){
       std::string directory(getenv("EVENT_FILE_DIR") == 0 ? "" : getenv("EVENT_FILE_DIR"));
       if (!directory.empty() && *directory.rbegin() != '/'){
           directory += '/';
       }
       std::ostringstream fileNameStrm;
       fileNameStrm << directory << "events." << getpid();
       fileName = fileNameStrm.str();
   }
   return fileName;
}

}

namespace EVENTS {

// ***************************************************************
// ***************************************************************
EventFileWriter::EventFileWriter():
   fileName(formFileName()),
   ofile()
{
    if (!fileName.empty()){
        ofile = boost::shared_ptr< std::ofstream >(new  std::ofstream(fileName.c_str()));
        if (!(*ofile)){
            std::cout << "EventFileWriter failed to open event file " << fileName << ":" << strerror(errno) << std::endl;
            ofile.reset();
        }
    } 
}

// ***************************************************************
// ***************************************************************
EventFileWriter::~EventFileWriter()
{
  ofile && ofile->flush();
}

// ***************************************************************
// ***************************************************************
bool EventFileWriter::isEnabled()
{
   return bool(ofile);
}

// ***************************************************************
// ***************************************************************
void EventFileWriter::write(const std::string& buffer)
{
  ofile && *ofile << buffer;
}

// ***************************************************************
// ***************************************************************
void EventFileWriter::write(const void *buffer, size_t size)
{
   ofile && ofile->write(reinterpret_cast<const char*>(buffer),size);
}

// ***************************************************************
// ***************************************************************
void EventFileWriter::shutdown()
{
   ofile && ofile->flush();
}

} // end EVENTS namespace
