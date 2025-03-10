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
