//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  EventInspectorWriter.hh
//
//============================================================================//
#ifndef Events_EventInspectorWriter_HH
#define Events_EventInspectorWriter_HH

#include "EventWriter.hh"

#include "sockets/sockets.hh"
#include "swa/FileDescriptorListener.hh"
#include "boost/shared_ptr.hpp"

namespace EVENTS {

// ***************************************************************
//! Define a class that can be used to stream encoded data to the
//! required connected client. The class deploys a listener socket
//! on port 8500 (or env EVENT_INSPECTOR_PORT). A client can connect
//! to this port and receive encoded data on the events that are occuring
//! in the process. An example XML encoded data stream is shown below:
//! 
//! < thread action="started" process="POLY_sqlite_standalone" time="Mon Dec 15 15:21:21 2008 "  value="Main Loop">
//!   <event type="timer" action="fired" time="Mon Dec 15 15:21:22 2008 " >
//!     <timer name="timerId" value="21" />
//!   </event>
//!   <event type="deleted" action="created" time="Mon Dec 15 15:21:22 2008 " >
//!     <timer name="timerId" value="21" />
//!   </event>
//!   <event domain="POLY" type="service" action="called" time="Mon Dec 15 15:21:22 2008 " >
//!     <service object="Test_Case_12_21" name="testCaseVerification" />
//!   </event>
//!   <event domain="POLY" type="service" action="called" time="Mon Dec 15 15:21:23 2008 " >
//!     <service object="Test_Case_12_21" name="testCaseNext" />
//!   </event>
//!   <event domain="POLY" type="service" action="called" time="Mon Dec 15 15:21:23 2008 " >
//!     <service object="Polymorphic_Event_Test_Case" name="testCasesFinished" />
//!   </event>
//!   <event domain="POLY" type="service" action="called" time="Mon Dec 15 15:21:23 2008 " >
//!     <service object="Test_Case_12_21" name="testCaseDeletion" />
//!   </event>
//! </thread>
//!
//! Can use environment variable EVENT_INSPECTOR_HOST to bind  
//! listener port to a specific interface/host.
// ***************************************************************
class EventInspectorWriter : public EventWriter
{
   public:
               EventInspectorWriter();
      virtual ~EventInspectorWriter();

      // ***************************************************************
      //! @return true if a client connection exists
      // ***************************************************************
      virtual bool isEnabled();

      // ***************************************************************
      //! Write the supplied data to the connected client
      //!       
      //! @param buffer data to write.
      // ***************************************************************
      virtual void write(const std::string&  buffer);

      // ***************************************************************
      //! Write the supplied data to the connected client
      //! 
      //! @param buffer data to write.
      //! @param size   number of bytes.
      // ***************************************************************
      virtual void write(const void *buffer, size_t size);

      // ***************************************************************
      //! 
      // ***************************************************************
      virtual void shutdown();

      // ***************************************************************
      //! When the socket descriptor associated with the client connection
      //! needs to be serviced (due to activity on the socket). The main 
      //! process activity monitor will invoke this callback.
      //!
      //! @param clientFd the active client socket descriptor. 
      // ***************************************************************
      bool clientCallback (int clientFd);

      // ***************************************************************
      //! When the socket descriptor associated with the socket listener
      //! needs to be serviced (due to a connection request). The main 
      //! process activity monitor will invoke this callback.
      //!
      //! @param listenFd the active listener socket descriptor. 
      // ***************************************************************
      bool listenerCallback (int listenFd);

   private:

      void disconnectClient();

   private:
        std::string port;
        std::string host;

        typedef SKT::InternetReliableClient   ClientSocketType;
        typedef SKT::InternetReliableListener ListenerSocketType;

        ListenerSocketType                  socketListener;
        boost::shared_ptr<ClientSocketType> clientSocket;

        SWA::FileDescriptorListener connectionClient;
        SWA::FileDescriptorListener connectionListener;
};

} // end EVENTS namespace

#endif
