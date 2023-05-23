//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:  EventInspectorWriter.hh
//
//============================================================================//
#include <iostream>

#include "swa/Process.hh"
#include "EventInspectorWriter.hh"

#include "boost/bind.hpp"
#include "boost/lexical_cast.hpp"

namespace EVENTS {

// ***************************************************************
// ***************************************************************
std::string getListenerPort()
{
   std::string port((getenv("EVENT_INSPECTOR_PORT") == 0 ? "8500" : getenv("EVENT_INSPECTOR_PORT")));
   return port;
}

// ***************************************************************
// ***************************************************************
std::string getListenerHost()
{
   std::string host((getenv("EVENT_INSPECTOR_HOST") == 0 ? "" : getenv("EVENT_INSPECTOR_HOST")));
   return host;
}

// ***************************************************************
// ***************************************************************
EventInspectorWriter::EventInspectorWriter():
   port(getListenerPort()),
   host(getListenerHost()),
   socketListener(host,::boost::lexical_cast<int>(port)),
   clientSocket(),
   connectionClient   (boost::bind(&EventInspectorWriter::clientCallback,  this,_1), SWA::Process::getInstance().getActivityMonitor()),
   connectionListener (boost::bind(&EventInspectorWriter::listenerCallback,this,_1), SWA::Process::getInstance().getActivityMonitor())
{
    // Deploy the socket listener
    signal(SIGPIPE,SIG_IGN);
    socketListener.setOption(SKT::SoReuseAddr(SKT::enable));
    socketListener.establish();

    // Pass the socket listener descriptor on to the main event loop
    // so that the 'listenerCallback' function is invoked when there 
    // is activity on the descriptor.
    connectionListener.setFd(socketListener.getSocketFd().descriptor_);
    connectionListener.setPriority(SWA::ListenerPriority::getHigh());
    connectionListener.activate();
}

// ***************************************************************
// ***************************************************************
EventInspectorWriter::~EventInspectorWriter()
{

}

// ***************************************************************
// ***************************************************************
bool EventInspectorWriter::isEnabled()
{
   // return true if a clientSocket connection exists
   return bool(clientSocket);
}

// ***************************************************************
// ***************************************************************
void EventInspectorWriter::write(const std::string& buffer)
{
    if (clientSocket){
        try {
         clientSocket->write(buffer.data(),buffer.size());
        } 
        catch(const SKT::SocketIOException& se ){
          std::cerr << " EventInspectorWriter::write failed : " << se.report() << " " << se.reason() <<  std::endl;
          disconnectClient(); 
        }
    }
}

// ***************************************************************
// ***************************************************************
void EventInspectorWriter::write(const void *buffer, size_t size)
{
    if (clientSocket){
        try {
          clientSocket->write(reinterpret_cast<const char *>(buffer),size);
        } 
        catch(const SKT::SocketIOException& se){
          std::cerr << " EventInspectorWriter::write failed : " << se.report() << " " << se.reason() << std::endl;
          disconnectClient(); 
        }
    }
}

// ***************************************************************
// ***************************************************************
void EventInspectorWriter::shutdown()
{
  
}

// ***************************************************************
// ***************************************************************
bool EventInspectorWriter::listenerCallback(int listenFd)
{
   // This callback is invoked when the listener socket has a pending
   // client connection. As this application only supports a single client
   // connection need to delete the previous client before creating the new
   // one.
   try {
      ClientSocketType::SocketDescriptor clientSocketFd = socketListener.acceptSocket();
      if (clientSocketFd.isValid()){
          disconnectClient();
          clientSocket = boost::shared_ptr<ClientSocketType> (new ClientSocketType(clientSocketFd));
          connectionClient.setFd(clientSocketFd.descriptor_); 
          connectionClient.setPriority(SWA::ListenerPriority::getHigh());
          connectionClient.activate();
      }
   }
   catch(SKT::SocketException& se){
      std::cerr << "Event Inspector writer - Caught unexpected Listener SKT::SocketException : " << se.report() << std::endl;
      std::cerr << "Event Inspector disabled ...  " << std::endl;

      // Major error on the listener socket so disable
      // the inspector by removing the listener.
      socketListener.close();
   }

   // All processing has been done on the listener socket so report 
   // nothing more to do, but listen for more connections, back to 
   // the calling process. 
   return false;
}

// ***************************************************************
// ***************************************************************
bool EventInspectorWriter::clientCallback(int clientFd)
{
  // The inspector is only expecting to write information on the connection
  // not read any data back. Therefore undertake the client read to clear the
  // socket and then remove the client.
  try {
      char buffer[128];
      clientSocket->read(buffer,sizeof(buffer));
  }
  catch(const SKT::SocketIOException& se){
      std::cerr << "Event Inspector writer - Caught unexpected client read SKT::SocketException : " << se.report() << std::endl;
  }

  disconnectClient();

  // All the client processing has been done on the client socket so 
  // report nothing more to do back to the calling process. 
  return false;
}

// ***************************************************************
// ***************************************************************
void EventInspectorWriter::disconnectClient()
{
  clientSocket.reset();
  connectionClient.clearFd();
}

} // end EVENTS namespace
