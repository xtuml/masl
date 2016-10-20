//============================================================================
// UK Crown Copyright (c) 2005. All rights reserved.
//
// File:  socket.hh 
//
// Description:    
//
//============================================================================//
#ifndef SOCKET_SocketDatagramServerP__
#define SOCKET_SocketDatagramServerP__

#include "sockets/socketSocket.hh"
#include "sockets/socketLogging.hh"
#include "sockets/socketDatagram.hh"

namespace SKT {

// ***********************************************
// ***********************************************
// ***********************************************
template<class P>
class SocketDatagramServer : public SocketDatagram<P>
{
   public:
      typedef P                                protocol_family_type;
      typedef typename P::socket_address_type  socket_address_type;
      typedef typename P::address_family_type  address_family_type;

   public:
       // Create a raw DATAGRAM socket. Use this constructor 
       // with the bind  method to form a DATAGRAM SERVER.
       SocketDatagramServer(const std::string& iLocalHost, const int iLocalPort);
       virtual ~SocketDatagramServer();
       
       void connect    (const std::string& iRemoteHost, const int iRemotePort);
       void disconnect ();
       
   private:
       // prevent copy and assignment
       SocketDatagramServer(const SocketDatagramServer& rhs);
       SocketDatagramServer& operator=(const SocketDatagramServer& rhs);
};

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
SocketDatagramServer<P>::SocketDatagramServer(const std::string& iLocalHost,  const int iLocalPort)
{
   Logger::trace("SocketDatagramServer<P>::SocketDatagramServer", "Constructor");
   this->localHost_ = iLocalHost;
   this->localPort_ = iLocalPort;
   address_family_type::populate(this->sockAddr_,this->localHost_,this->localPort_);
   protocol_family_type::bindSocket(this->socketD_.descriptor_,this->sockAddr_);  
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
SocketDatagramServer<P>::~SocketDatagramServer()
{
   Logger::trace("SocketDatagramServer<P>::~SocketDatagramServer", "Destructor");
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
void SocketDatagramServer<P>::connect(const std::string& iRemoteHost, const int iRemotePort)
{
   Logger::trace      ("SocketDatagramServer<P>::connect", "iRemoteHost",iRemoteHost);
   Logger::trace<int> ("SocketDatagramServer<P>::connect", "iRemotePort",iRemotePort);
   
   if (this->remoteHost_ == iRemoteHost || this->remotePort_ != iRemotePort){
       // connecting to a new destination
       this->remoteHost_ = iRemoteHost;
       this->remotePort_ = iRemotePort;
       this->connected_  = false;
   }   
  
   socket_address_type sockAddrRemote;
   address_family_type::populate(sockAddrRemote,this->remoteHost_,this->remotePort_);
   protocol_family_type::connectSocket(this->socketD_.descriptor_,sockAddrRemote); 
   this->connected_ == true ? this->connected_ = false : this->connected_ = true;
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
void SocketDatagramServer<P>::disconnect()
{
    Logger::trace("SocketDatagramClient<S>::disconnect");
    
    if (this->connected_ == true){
        // call of connect to already connected client will
	// cause a disconnect.
	connect(this->remoteHost_,this->remotePort_);
    }
}

} // end namespace SKT 

#endif 
