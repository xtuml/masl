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
#ifndef Inspector_CommunicationChannel_HH
#define Inspector_CommunicationChannel_HH

#include "sockets/sockets.hh"
#include "BufferedIO.hh"

namespace Inspector {

  class CommunicationChannel
  {
    private:
      typedef SKT::InternetReliableListener ServerSocketType;
      typedef SKT::InternetReliableClient   ClientSocketType;
      typedef SKT::InternetReliableClient::socket_descriptor SocketDescriptorType;

      ServerSocketType  serverSocket;
      ClientSocketType* clientSocket;

      bool connected;

      BufferedOutputStream outputStream;
      BufferedInputStream inputStream;

    public:
      CommunicationChannel ( int port );
      ~CommunicationChannel();
      bool isConnected() const { return connected; }
      bool attemptConnect();
      void disconnect();

      int getServerFd() const { return serverSocket.getSocketFd().descriptor_; }
      int getClientFd() const { return clientSocket->getSocketFd().descriptor_; }

      template<class T> CommunicationChannel& operator<<(const T& val);
      template<class T> CommunicationChannel& operator>>(T& val);

      BufferedOutputStream& getOutputStream() { return outputStream; }
      BufferedInputStream& getInputStream() { return inputStream; }

      bool empty() { return inputStream.empty(); }

      bool ready() { return connected && inputStream.available(); }
      void flush() { outputStream.flush(); }
  };


  template<class T>
  CommunicationChannel& CommunicationChannel::operator<<(const T& val)
  {
    outputStream.write(val);
    return *this;
  }

  template<class T>
  CommunicationChannel& CommunicationChannel::operator>>(T& val)
  {
    inputStream.read(val);
    return *this;
  }

}

#endif
