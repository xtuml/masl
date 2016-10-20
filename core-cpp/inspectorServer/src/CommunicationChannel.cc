//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
#include <string>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <errno.h>
#include <unistd.h>
#include <fcntl.h>
#include <vector>
#include <iostream>

#include "inspector/CommunicationChannel.hh"
#include "ConnectionError.hh"

namespace Inspector
{

  CommunicationChannel::CommunicationChannel ( int port ) : 
         serverSocket(port),
         clientSocket(NULL),
         connected(false)
  {
    serverSocket.setOption(SKT::SoReuseAddr(SKT::enable));
    serverSocket.establish();
  }

  CommunicationChannel::~CommunicationChannel()
  {
     try{
       disconnect();
     }
     catch(ConnectionError& se){
       std::cerr << " CommunicationChannel::~CommunicationChannel - caught unexpected SocketException : " << se.what() << std::endl;
     }
     catch(...){
       std::cerr << " CommunicationChannel::~CommunicationChannel - caught unexpected exception" << std::endl;
     }
  } 

  bool CommunicationChannel::attemptConnect()
  {
    try {
      SocketDescriptorType clientConnection = serverSocket.acceptSocket();
      if (clientConnection.isValid()){
          disconnect();  // remove any old client.
          clientSocket = new ClientSocketType(clientConnection);
          outputStream.setFp(clientConnection.descriptor_);
          inputStream.setFp (clientConnection.descriptor_);
          connected = true;
      }
      else{
        if (errno != EAGAIN){
	    throw ConnectionError(std::string("Inspector::CommunicationChannel::attemptConnect - Non-blocking socket accept failed - ") + strerror(errno));
        }
      }
    }
    catch(SKT::SocketException& se){
       std::cerr << "CommunicationChannel::attemptConnect - Caught unexpected SKT::SocketException : " << se.report() << std::endl;
       throw ConnectionError("Inspector::CommunicationChannel::attemptConnect - failed");
    }
    return connected;
  } 

  void CommunicationChannel::disconnect()
  {
    try {
        if(connected){
           outputStream.setFp(-1);
           inputStream.setFp(-1);

           delete clientSocket;
           clientSocket = NULL;

           connected = false;
        } 
    }
    catch(SKT::SocketException& se){
       std::cerr << "CommunicationChannel::disconnect - Caught unexpected SKT::SocketException : " << se.report() << std::endl;
       throw ConnectionError("Inspector::CommunicationChannel::disconnect - failed");
    }
  } 

}
