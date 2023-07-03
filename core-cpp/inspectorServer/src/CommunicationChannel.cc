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
