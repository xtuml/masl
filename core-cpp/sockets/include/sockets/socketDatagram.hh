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

#ifndef SOCKET_SocketDatagram__
#define SOCKET_SocketDatagram__

#include <string>

#include <unistd.h>

#include "sockets/socketSocket.hh"
#include "sockets/socketCommon.hh"
#include "sockets/socketLogging.hh"

namespace SKT {

// ***********************************************
// ***********************************************
// ***********************************************
template<class P>
class SocketDatagram : public Socket<P>
{
   public:
      typedef P                                protocol_family_type;
      typedef typename P::socket_address_type  socket_address_type;
      typedef typename P::address_family_type  address_family_type;

   public:
       
       // Create a raw DATAGRAM socket.
       SocketDatagram();
        
       // iLocalHost == "" - INADDR_ANY is used
       // iLocalPort == 0  - kernal will pick port number (ephemral port)
       void bind (const std::string& iLocalHost,  const int iLocalPort);
      
       // The mechanism for forming the datagram connection is different
       // for servers and clients. Therefore provide methods that derived
       // classes must implement.
       virtual void connect     (const std::string& iRemoteHost, const int iRemotePort) = 0;
       virtual void disconnect  () = 0;
       virtual bool isConnected () { return connected_; }

       // This class provides default implementations for the read and
       // write functions. The methods have been made virtual so that
       // derived classes that may need to provide different read/write 
       // implementations (e.g. to take into account different error codes)
       // can.
       virtual ssize_t read  (char *iBuffer, const int iBufferSize);
       virtual ssize_t write (char *iBuffer, const int iBufferSize);

       ssize_t sendtoSocket (const char *iBuffer, const int iBufferSize, const int flags);
       ssize_t sendtoSocket (const char *iBuffer, const int iBufferSize, const int flags, const socket_address_type& iDestination);

       ssize_t recvfromSocket (char *oBuffer, const int iBufferSize, const int flags, socket_address_type& iDestination);
       ssize_t recvfromSocket (char *oBuffer, const int iBufferSize, const int flags);

   protected:
       // Do not allow direct creation. Only derived classes 
       // can create and instance of this class. Doe not need 
       // to be virtual as do not expect to delete through a 
       // base class pointer.
       ~SocketDatagram() {};
  
   private:
       // prevent copy and assignment
       SocketDatagram(const SocketDatagram& rhs);
       SocketDatagram& operator=(const SocketDatagram& rhs);

   protected:
      bool        connected_;
      int         localPort_;
      std::string localHost_;

      int         remotePort_;
      std::string remoteHost_;
};

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
SocketDatagram<P>::SocketDatagram():
      connected_(false),
      localPort_(0),
      remotePort_(0)
{
   Logger::trace("SocketDatagram<P>::SocketDatagram", "Constructor");
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
void SocketDatagram<P>::bind(const std::string& iLocalHost,  const int iLocalPort)
{
    Logger::trace("SocketDatagram<P>::bind");
    if (this->socketD_.isValid() == true){
        localPort_ = iLocalPort;
	localHost_ = iLocalHost;
	socket_address_type sockAddrLocal;
	address_family_type::populate(sockAddrLocal,iLocalHost,iLocalPort);
	protocol_family_type::bindSocket(this->socketD_.descriptor_,&sockAddrLocal);
    }
    else{
      throw SocketException(std::string("SocketDatagram::bind : socket has not been constructed (socketD_ == -1)"));
    }
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
ssize_t SocketDatagram<P>::sendtoSocket (const char *buffer, const int iBufferSize, const int flags, const socket_address_type& iDestination)
{
   Logger::trace<int>("SocketDatagram<P>::sendtoSocket","descriptor", this->socketD_.descriptor_);
   return protocol_family_type::sendtoSocket(this->socketD_.descriptor_,buffer,iBufferSize,flags,iDestination);
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
ssize_t SocketDatagram<P>::sendtoSocket (const char *buffer, const int iBufferSize, const int flags)
{
  Logger::trace <int>("SocketDatagram<P>::sendtoSocket","descriptor", this->socketD_.descriptor_);
  return protocol_family_type::sendtoSocket(this->socketD_.descriptor_,buffer,iBufferSize,flags,this->sockAddr_);
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
ssize_t SocketDatagram<P>::recvfromSocket (char *buffer, const int iBufferSize, const int flags, socket_address_type& iDestination)
{
  Logger::trace <int>("SocketDatagram<P>::recvfromSocket","descriptor", this->socketD_.descriptor_);
  return protocol_family_type::recvfromSocket(this->socketD_.descriptor_,buffer,iBufferSize,flags,iDestination);
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
ssize_t SocketDatagram<P>::recvfromSocket (char *buffer, const int iBufferSize, const int flags)
{
  Logger::trace <int>("SocketDatagram<P>::recvfromSocket","descriptor", this->socketD_.descriptor_);
  socket_address_type sourceAddr;
  return protocol_family_type::recvfromSocket(this->socketD_.descriptor_,buffer,iBufferSize,flags,sourceAddr);
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
ssize_t SocketDatagram<P>::write (char *buffer, const int iBufferSize)
{
  Logger::trace <int>("SocketDatagram<P>::write","descriptor", this->socketD_.descriptor_);
  if (connected_ == false){
       throw SocketIOException("SocketDatagramServer<S>::write( ) : cannot be called as socket is not connected",0);
  }
  return ::write(this->socketD_.descriptor_,buffer,iBufferSize);
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
ssize_t SocketDatagram<P>::read (char *buffer, const int iBufferSize)
{
  Logger::trace <int>("SocketDatagram<P>::read","descriptor", this->socketD_.descriptor_);
  if (connected_ == false){
       throw SocketIOException("SocketDatagramServer<S>::read( ) : cannot be called as socket is not connected",0);
  }
  return ::read(this->socketD_.descriptor_,buffer,iBufferSize);
}

} // end namespace SKT 

#endif 
