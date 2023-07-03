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

#ifndef SOCKET_SocketDatagramClientStream__
#define SOCKET_SocketDatagramClientStream__

#include "sockets/socketSocket.hh"
#include "sockets/socketLogging.hh"
#include "sockets/socketDatagram.hh"

namespace SKT {

// ***********************************************
// ***********************************************
// ***********************************************
template<class P, class B>
class SocketDatagramClientStream : public SocketDatagramClient<P>, public std::iostream
{
   public:
      typedef B socket_buffer_type;
      typedef P protocol_family_type ;

      typedef typename P::socket_address_type  socket_address_type;
      typedef typename P::address_family_type  address_family_type;

   public:
       SocketDatagramClientStream();
       SocketDatagramClientStream(const std::string& iRemoteHost, const int iRemotePort);
       virtual ~SocketDatagramClientStream();
      
   private:
       // prevent copy and assignment
       // The iostream  that this class inherits from has a stream buffer
       // that has a private assignmnet operator and private copy constructor.
       // Therefore make the corresponding operator and constructor private
       // for this class.
       SocketDatagramClientStream(const SocketDatagramClientStream& rhs);
       SocketDatagramClientStream& operator=(const SocketDatagramClientStream& rhs);

   protected:
       socket_buffer_type  streambuf_;
};

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P, class B>
SocketDatagramClientStream<P,B>::SocketDatagramClientStream():
     SocketDatagramClient<P>(),
     std::iostream(0),
     streambuf_ (-1)
{
   Logger::trace("SocketDatagramClient<S>::SocketDatagramClient", "Constructor");
   
   // configure the stream
   rdbuf(&streambuf_); 
   streambuf_.setFd(this->socketD_.descriptor_);
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P, class B>
SocketDatagramClientStream<P,B>::SocketDatagramClientStream(const std::string& iRemoteHost,  const int iRemotePort):
      SocketDatagramClient<P>(iRemoteHost,iRemotePort),
      std::iostream(0),
      streambuf_ (-1)
      
{
   Logger::trace("SocketDatagramClient<S>::SocketDatagramClient", "Constructor");
   this->remoteHost_ = iRemoteHost;
   this->remotePort_ = iRemotePort;
   
   address_family_type::populate(this->sockAddr_,iRemoteHost,iRemotePort);
   
   // The stream uses read/write system calls so the datagram 
   // must call connect to enable these system calls to be used.
   SocketDatagramClient<P>::connect(); 
  
   // configure the stream
   rdbuf(&streambuf_);
   streambuf_.setFd(this->socketD_.descriptor_);
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P, class B>
SocketDatagramClientStream<P,B>::~SocketDatagramClientStream()
{
   Logger::trace ("SocketDatagramClientStream<P,B>::~SocketDatagramClientStream", "DESTRUCTOR");

}

} // end namespace SKT 

#endif 
