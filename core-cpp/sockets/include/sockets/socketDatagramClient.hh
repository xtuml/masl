//============================================================================
// UK Crown Copyright (c) 2005. All rights reserved.
//
// File:  socketDatagramClient.hh 
//
// Description:    
//    Define a datagram client class.
//============================================================================//
#ifndef SOCKET_SocketDatagramClient__
#define SOCKET_SocketDatagramClient__

#include "sockets/socketLogging.hh"
#include "sockets/socketDatagram.hh"

namespace SKT {

// ***********************************************
// ***********************************************
// ***********************************************
template<class P>
class SocketDatagramClient : public SocketDatagram<P>
{
   public:
      typedef P                                protocol_family_type;
      typedef typename P::socket_address_type  socket_address_type;
      typedef typename P::address_family_type  address_family_type;

   public:
       SocketDatagramClient();
       SocketDatagramClient(const std::string& iRemoteHost, const int iRemotePort);
       virtual ~SocketDatagramClient() {};
       
       void connect();
       void connect (const std::string& iRemoteHost, const int iRemotePort);
       
       void disconnect();
   
   private:
       // prevent copy and assignment
       SocketDatagramClient(const SocketDatagramClient& rhs);
       SocketDatagramClient& operator=(const SocketDatagramClient& rhs);
};

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
SocketDatagramClient<P>::SocketDatagramClient()
{
   Logger::trace("SocketDatagramClient<P>::SocketDatagramClient", "Constructor");
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
SocketDatagramClient<P>::SocketDatagramClient(const std::string& iRemoteHost,  const int iRemotePort)
{
   Logger::trace("SocketDatagramClient<P>::SocketDatagramClient", "Constructor");
   this->remoteHost_ = iRemoteHost;
   this->remotePort_ = iRemotePort;
   address_family_type::populate(this->sockAddr_,iRemoteHost,iRemotePort);
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
void SocketDatagramClient<P>::connect()
{
    Logger::trace("SocketDatagramClient<P>::connect");
    if (this->remoteHost_.empty() == false){
        if (this->socketD_.isValid() == true){
	    try {
	       protocol_family_type::connectSocket(this->socketD_.descriptor_,this->sockAddr_);
    	
	        // If a client has already connected and connects again, the effect
	        // is to actually disconnect. Therefore just toggle the connected_
	        // class variable based on its current value.
	        this->connected_ == true ? this->connected_ = false : this->connected_ = true;
            } 
            catch(SKT::SocketException& ioe){
                  Logger::trace ("SocketReliableClient<P>::connect", std::string("connect failed :") + ioe.report());
	          this->connected_ = false;
            }
       }
       else{
         throw SocketException(std::string("SocketDatagram::connect : socket has not been constructed (socketD_ == -1)"));
       }
    }
    else{
      throw SocketException(std::string("SocketDatagram::connect : socket remote host not specified"));
    }  
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
void SocketDatagramClient<P>::disconnect()
{
    Logger::trace("SocketDatagramClient<P>::disconnect");
    
    if (this->connected_ == true){
        // call of connect to already connected 
	// client will cause a disconnect.
	connect();
    }
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
void SocketDatagramClient<P>::connect(const std::string& iRemoteHost, const int iRemotePort)
{
   Logger::trace("SocketDatagramClient<P>::connect(...)", "remoteHost_ ", this->remoteHost_);
   Logger::trace("SocketDatagramClient<P>::connect(...)", "remotePort_" , this->remotePort_);
   
   if (this->remoteHost_ == iRemoteHost && this->remotePort_ == iRemotePort){
       // Asking to connect to host and port the client was created
       // with. Therefore can forward call to parameterless connect.
       connect();
   }
   else{
       // need to connect to a new destination. reconfigure 
       // the address structure and call connect. As client is
       // connecting to a new host/port configuration the old
       // connection will be implicitly disconnected by the 
       // connect system call.
       this->remoteHost_ = iRemoteHost;  // new host
       this->remotePort_ = iRemotePort;  // new port
       this->connected_  = false;
       address_family_type::populate(this->sockAddr_,iRemoteHost,iRemotePort);
       connect();
   } 
}

} // end namespace SKT 

#endif 
