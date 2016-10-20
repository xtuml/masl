//============================================================================
// UK Crown Copyright (c) 2005. All rights reserved.
//
// File:    
//
// Description:    
//
//============================================================================//
#ifndef SOCKET_SocketListenerP__
#define SOCKET_SocketListenerP__

#include <cerrno>
#include <string>

#include <sys/types.h>
#include <sys/socket.h>

#include "sockets/socketSocket.hh"
#include "sockets/socketLogging.hh"

namespace SKT {

// ***********************************************
// ***********************************************
// ***********************************************
template<class P>
class SocketListener : public Socket<P>
{
   public:
      typedef P          protocol_family_type;
      typedef Socket<P>  socket_type;
      
      typedef typename Socket<P>::SocketDescriptor socket_descriptor;
      typedef typename P::socket_address_type      socket_address_type;
      typedef typename P::address_family_type      address_family_type;
   
   public:
       SocketListener();
       SocketListener(const int iPort);
       SocketListener(const std::string& iHost, const int iPort);
 
       virtual ~SocketListener();
       
       void close();
       void establish ();
       void establish (const int iPort);
       void establish (const std::string& iHost, const int iPort);

       bool isListening();
       socket_descriptor  acceptSocket();
       socket_descriptor  acceptSocket(socket_address_type& iDestination);
   private:
       // prevent copy and assignment
       SocketListener(const SocketListener& rhs);
       SocketListener& operator=(const SocketListener& rhs);
       
   private:
      bool        listening_;
      
      int         port_;
      std::string host_;
};

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
SocketListener<P>::~SocketListener()
{
   Logger::trace("SocketListener<P>::~SocketListener", "Destructor");
   address_family_type::cleanup(this->sockAddr_);
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
SocketListener<P>::SocketListener():
      listening_(false),
      port_(0)
{
   Logger::trace("SocketListener<P>::SocketListener", "Constructor");
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
SocketListener<P>::SocketListener(const int iPort):
      listening_(false),
      port_(iPort)
{
   Logger::trace("SocketListener<P>::SocketListener", "Constructor");
   Logger::trace<int>("SocketListener<P>::SocketListener", "iPort",iPort);
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
SocketListener<P>::SocketListener(const std::string& iHost, const int iPort):
      listening_(false),
      port_(iPort),
      host_(iHost)
{
   Logger::trace     ("SocketListener<P>::SocketListener", "Constructor");
   Logger::trace<int>("SocketListener<P>::SocketListener", "iPort",iPort);
   Logger::trace     ("SocketListener<P>::SocketListener","iHost",iHost);
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
bool SocketListener<P>::isListening()
{
  return listening_;
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
void  SocketListener<P>::close()
{
  listening_ = false;
  Socket<P>::close();
  Socket<P>::socket();
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
void SocketListener<P>::establish()
{
    Logger::trace <int>("SocketListener<P>::establish", "socketD_",this->socketD_.descriptor_);
    if (listening_ == false){
	if (this->socketD_.isValid() == true){
            socket_type::address_family_type::populate(this->sockAddr_,host_,port_);
	    socket_type::protocol_family_type::bindSocket(this->socketD_.descriptor_,this->sockAddr_);
	    socket_type::protocol_family_type::listenSocket(this->socketD_.descriptor_);
	    listening_ = true;
	}
	else{
	  throw SocketException(std::string("SocketListener::start : socket has not been constructed (socketD_ == -1)"));
	}
    }
    else{
      Logger::trace ("SocketListener<P>::establish()","socket is already listening");
    }	
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
void SocketListener<P>::establish(const int iPort)
{
    Logger::trace<int>("SocketListener<P>::establish", "iPort",iPort);
    if (listening_ == false){
	 port_ = iPort;
	 host_.clear();
	 establish();
    }
    else{
      Logger::trace ("SocketListener<P>::establish(int)","socket is already listening");
    }	
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
void SocketListener<P>::establish(const std::string& iHost, const int iPort)
{
    Logger::trace<int>("SocketListener<P>::establish", "iPort",iPort);
    Logger::trace     ("SocketListener<P>::establish", "iHost",iHost);
    if (listening_ == false){
	port_ = iPort;
	host_ = iHost;
	establish();
    }
    else{
      Logger::trace ("SocketListener<P>::establish(std::string,int)","socket is already listening");
    }	
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
typename SocketListener<P>::socket_descriptor SocketListener<P>::acceptSocket()
{
    Logger::trace("SocketListener<P>::acceptSocket");
    
    socket_descriptor newConnection;
    if (listening_ == true){
	newConnection.descriptor_ = socket_type::protocol_family_type::acceptSocket(this->socketD_.descriptor_);
	Logger::trace<int>("SocketListener<P>::accept","newConnection",newConnection.descriptor_);
    }
    else{
      throw SocketException(std::string("SocketListener::accept failed: Listener not listening!!!"));
    }
    
    return newConnection;
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************
template<class P>
typename SocketListener<P>::socket_descriptor SocketListener<P>::acceptSocket(socket_address_type& iDestination)
{
    Logger::trace("SocketListener<P>::accept");
    
    socket_descriptor newConnection;
    if (listening_ == true){
	newConnection.descriptor_ = socket_type::protocol_family_type::acceptSocket(this->socketD_.descriptor_,iDestination);
	Logger::trace<int>("SocketListener<P>::accept","newConnection",newConnection.descriptor_);
    }
    else{
      throw SocketException(std::string("SocketListener::accept failed: Listener not listening!!!"));
    }
    return newConnection;
}

// ****************************************************************************
// ****************************************************************************
// ****************************************************************************

} // end namespace SKT 

#endif 
