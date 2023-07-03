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

#ifndef SOCKET_sockets__
#define SOCKET_sockets__

#include "sockets/socketOption.hh"
#include "sockets/socketCommon.hh"
#include "sockets/socketLogging.hh"

#include "sockets/socketSocket.hh"
#include "sockets/socketBuffer.hh"
#include "sockets/socketProtocol.hh"

#include "sockets/socketListener.hh"
#include "sockets/socketReliableClient.hh"
#include "sockets/socketReliableClientStream.hh"

#include "sockets/socketDatagram.hh"
#include "sockets/socketDatagramServer.hh"
#include "sockets/socketDatagramClient.hh"
#include "sockets/socketDatagramClientStream.hh"
#include "sockets/socketDatagramServerStream.hh"

namespace SKT {

// ***************************************************************************
// ***************************************************************************
// ***************************************************************************
// Define a common set of protocol classes that can be used as a template
// parameter to the Socket class. 
typedef StreamProtocol   <UnixAddressFamily> UnixStreamProtocol;
typedef DatagramProtocol <UnixAddressFamily> UnixDatagramProtocol;

typedef StreamProtocol  <InetAddressFamily> InternetStreamProtocol;
typedef DatagramProtocol<InetAddressFamily> InternetDatagramProtocol;

// ***************************************************************************
// ***************************************************************************
// ***************************************************************************
// Create two example types of buffer that canbe used by socketStream classes
typedef UnbufferedSocketBuf<char, std::char_traits<char> >      UnbufferedSocketBuffer;
typedef BufferedSocketBuf  <1024,char, std::char_traits<char> > BufferedSocketBuffer;

// ***************************************************************************
// ***************************************************************************
// ***************************************************************************
// Create a set of RELIABLE SOCKET classes
typedef SocketReliableClient<UnixStreamProtocol>     UnixReliableClient;
typedef SocketReliableClient<InternetStreamProtocol> InternetReliableClient;

typedef SocketReliableClientStream<UnixStreamProtocol,     UnbufferedSocketBuffer> UnbufferedUnixReliableClient;
typedef SocketReliableClientStream<InternetStreamProtocol, UnbufferedSocketBuffer> UnbufferedInternetReliableClient;
typedef SocketReliableClientStream<UnixStreamProtocol,     BufferedSocketBuffer>   BufferedUnixReliableClient;
typedef SocketReliableClientStream<InternetStreamProtocol, BufferedSocketBuffer>   BufferedInternetReliableClient;

typedef SocketListener<InternetStreamProtocol> InternetReliableListener;
typedef SocketListener<UnixStreamProtocol>     UnixReliableListener;

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
// Create a set of DATAGRAM SOCKET classes
typedef SocketDatagramClient<UnixDatagramProtocol>     UnixDatagramClient;
typedef SocketDatagramClient<InternetDatagramProtocol> InternetDatagramClient;

typedef SocketDatagramClientStream<UnixDatagramProtocol,     UnbufferedSocketBuffer>  UnbufferedUnixDatagramClientStream;
typedef SocketDatagramClientStream<InternetDatagramProtocol, UnbufferedSocketBuffer>  UnbufferedInternetDatagramClientStream;
typedef SocketDatagramClientStream<UnixDatagramProtocol,     BufferedSocketBuffer>    BufferedUnixDatagramClientStream;
typedef SocketDatagramClientStream<InternetDatagramProtocol, BufferedSocketBuffer>    BufferedInternetDatagramClientStream;

typedef SocketDatagramServer<UnixDatagramProtocol>       UnixDatagramServer;
typedef SocketDatagramServer<InternetDatagramProtocol>   InternetDatagramServer;

typedef SocketDatagramServerStream<UnixDatagramProtocol,     UnbufferedSocketBuffer>  UnbufferedUnixDatagramServerStream;
typedef SocketDatagramServerStream<InternetDatagramProtocol, UnbufferedSocketBuffer>  UnbufferedInternetDatagramServerStream;
typedef SocketDatagramServerStream<UnixDatagramProtocol,     BufferedSocketBuffer>    BufferedUnixDatagramServerStream;
typedef SocketDatagramServerStream<InternetDatagramProtocol, BufferedSocketBuffer>    BufferedInternetDatagramServerStream;

} // end namespace SKT 
#endif 
