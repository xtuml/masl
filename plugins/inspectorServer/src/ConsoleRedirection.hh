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

#ifndef Inspector_ConsoleRedirection_HH
#define Inspector_ConsoleRedirection_HH

#include "sockets/sockets.hh"

namespace Inspector {

  class ConsoleRedirection
  {
    private:
      int save_stdin;
      int save_stdout;
      int save_stderr;

      typedef SKT::InternetReliableListener ServerSocketType;
      typedef SKT::BufferedInternetReliableClient   ClientSocketType;
      typedef SKT::BufferedInternetReliableClient::socket_descriptor SocketDescriptorType;

      ServerSocketType  serverSocket;
      ClientSocketType* clientSocket;

      bool connected;
      bool redirecting;

    public:

      ConsoleRedirection ( int port );

      ~ConsoleRedirection();

      bool attemptConnect();

      void disconnect();

      void startRedirection();

      void stopRedirection();

      bool isConnected() const { return connected; }

  };

}

#endif
