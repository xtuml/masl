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

#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <errno.h>
#include <unistd.h>
#include <fcntl.h>
#include <iostream>

#include "ConsoleRedirection.hh"
#include "ConnectionError.hh"

namespace Inspector
{

  ConsoleRedirection::ConsoleRedirection ( int port )
    : save_stdin(-1),
      save_stdout(-1),
      save_stderr(-1),
      serverSocket(port),
      clientSocket(NULL),
      connected(false),
      redirecting(false)
  {
    serverSocket.setOption(SKT::SoReuseAddr(SKT::enable));
    serverSocket.establish();
  }

  ConsoleRedirection::~ConsoleRedirection()
  {
     try{
       disconnect();
     }
     catch(ConnectionError& se){
       std::cerr << " ConsoleRedirection::~ConsoleRedirection - caught unexpected ConnectionError : " << se.what() << std::endl;
     }
     catch(...){
       std::cerr << " ConsoleRedirection::~ConsoleRedirection - caught unexpected exception" << std::endl;
     }
  }

  bool ConsoleRedirection::attemptConnect()
  {
    try {
      SocketDescriptorType clientConnection = serverSocket.acceptSocket();
      if (clientConnection.isValid()){
          disconnect();  // remove any old client.
          clientSocket = new ClientSocketType(clientConnection);
          connected = true;
      }
      else{
        if (errno != EAGAIN){
	    throw ConnectionError(std::string("ConsoleRedirection::attemptConnect - Non-blocking socket accept failed - ") + strerror(errno));
        }
      }
    }
    catch(SKT::SocketException& se){
       std::cerr << "ConsoleRedirection::attemptConnect - Caught unexpected SKT::SocketException : " << se.report() << std::endl;
       throw ConnectionError("ConsoleRedirection::attemptConnect - failed");
    }
    return connected;
  }

  void ConsoleRedirection::disconnect()
  {
    if(connected){
      stopRedirection();
      delete clientSocket;
      clientSocket = NULL;
      connected = false;
    }
  }

  void ConsoleRedirection::startRedirection()
  {
    if ( connected && !redirecting )
    {
      // Save the original std file handles
      save_stdin  = dup(STDIN_FILENO);
      save_stdout = dup(STDOUT_FILENO);
      save_stderr = dup(STDERR_FILENO);

      // Redirect std file handles to the console socket
      dup2(clientSocket->getSocketFd().descriptor_,STDIN_FILENO);
      dup2(clientSocket->getSocketFd().descriptor_,STDOUT_FILENO);
      dup2(clientSocket->getSocketFd().descriptor_,STDERR_FILENO);

      redirecting = true;
    }
  }

  void ConsoleRedirection::stopRedirection()
  {
    if ( connected && redirecting )
    {
      redirecting = false;

      // Connect the std files back to their originals
      dup2(save_stdin ,STDIN_FILENO);
      dup2(save_stdout,STDOUT_FILENO);
      dup2(save_stderr,STDERR_FILENO);

      // Close the saved file handles
      close(save_stdin);
      close(save_stdout);
      close(save_stderr);

      // Clear the error status of the streams
      std::cout.clear();
      std::cerr.clear();
      std::cin.clear();
    }
  }

}
