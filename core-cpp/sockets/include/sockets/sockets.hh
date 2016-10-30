//============================================================================
// UK Crown Copyright (c) 2005. All rights reserved.
//
// File:  socket.hh 
//
// Description:    
//     Using the template classes defined by the socket classes, build up
// a series of commonly required specialisations that can be instantiated
// by cuser applications.
//============================================================================//
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
