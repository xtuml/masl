//============================================================================
// UK Crown Copyright (c) 2005. All rights reserved.
//
// File:  socketDatagramServerStream.hh
//
// Description:    
//      Extend the SocketDatagramServer class so that it can provide the 
// functionality of C++ std::iostreams (use '<< and '>>' to stream data to
// and from the socket)
//============================================================================//
#ifndef SOCKET_SocketDatagramServerStream__
#define SOCKET_SocketDatagramServerStream__

#include "sockets/socketSocket.hh"
#include "sockets/socketLogging.hh"
#include "sockets/socketDatagram.hh"

namespace SKT {

// ***********************************************
// ***********************************************
// ***********************************************
template<class P, class B>
class SocketDatagramServerStream : public SocketDatagramServer<P>, public std::iostream
{
   public:
      typedef B socket_buffer_type;
      typedef P protocol_family_type;
      
      typedef typename P::socket_address_type  socket_address_type;
      typedef typename P::address_family_type  address_family_type;

   public:
       SocketDatagramServerStream(const std::string& iRemoteHost, const int iRemotePort);
      ~SocketDatagramServerStream();
   
   private:
       // prevent copy and assignment
       SocketDatagramServerStream(const SocketDatagramServerStream& rhs);
       SocketDatagramServerStream& operator=(const SocketDatagramServerStream& rhs);

   protected:
       socket_buffer_type  streambuf_;
};

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P, class B>
SocketDatagramServerStream<P,B>::SocketDatagramServerStream(const std::string& iLocalHost,  const int iLocalPort):
      SocketDatagramServer<P>(iLocalHost,iLocalPort),
      std::iostream(0),
      streambuf_ (-1)
      
{
   rdbuf(&streambuf_);
   streambuf_.setFd(this->socketD_.descriptor_);
   Logger::trace<int> ("SocketDatagramServer<S>::SocketDatagramServerStream", "descriptor_", this->socketD_.descriptor_);
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P, class B>
SocketDatagramServerStream<P,B>::~SocketDatagramServerStream()
{

}

} // end namespace SKT 

#endif 
