//============================================================================
//
// File:    
//
// Description:    
//
// UK Crown Copyright (c) 2005. All rights reserved.
//============================================================================//
#ifndef SOCKET_SocketReliableClientStreamP__
#define SOCKET_SocketReliableClientStreamP__

#include <string>

#include "sockets/socketLogging.hh"
#include "sockets/socketSocket.hh"
#include "sockets/socketReliableClient.hh"

namespace SKT {

// ***********************************************
// ***********************************************
// ***********************************************
template <class P, class B>
class SocketReliableClientStream : public SocketReliableClient<P>, public std::iostream
{
  public:
  	typedef B  socket_buffer_type;
        typedef P  protocol_family_type;
      
	typedef typename P::socket_address_type                     socket_address_type;
        typedef typename P::address_family_type                     address_family_type;
        typedef typename SocketReliableClient<P>::socket_descriptor socket_descriptor;
  
  public:
       SocketReliableClientStream();
       SocketReliableClientStream(const socket_descriptor& iFd);
       SocketReliableClientStream(const std::string& iDestinationHost,
                                  const int          iDestinationPort);

       // Use this constructor if the local interface and port number
       // need to be set to a known value (don't let kernal assign
       // an ephemeral port). Set iLocalInterface to "" to use INADDR_ANY 
       SocketReliableClientStream(const std::string& iDestinationHost,
                          const int          iDestinationPort,
		          const std::string& iLocalInterface,
                          const int          iLocalPort);
      
       virtual ~SocketReliableClientStream();
       
       void setSocketDescriptor(const socket_descriptor& iDescriptor);

       virtual void connect();
       virtual void connect(const std::string& iDestinationHost, const int iDestinationPort);
      
       virtual bool isConnected();
         
   private:
       // The iostream  that this class inherits from has a stream buffer
       // that has a private assignmnet operator and private copy constructor.
       // Therefore make the corresponding operator and constructor private
       // for this class.
       SocketReliableClientStream(const SocketReliableClientStream& iRhs);
       SocketReliableClientStream& operator=(const SocketReliableClientStream& iRhs);
       
   protected:
       socket_buffer_type  streambuf_;
};

// ***********************************************
// ***********************************************
// ***********************************************
template <class P, class B>
SocketReliableClientStream<P,B>::SocketReliableClientStream():
       SocketReliableClient<P>(),
       std::iostream(0),
       streambuf_(-1)
{
     Logger::trace     ("SocketReliableClientStream<P,B>::SocketReliableClientStream","Address  Family",  address_family_type::getName());
     Logger::trace     ("SocketReliableClientStream<P,B>::SocketReliableClientStream","Protocol Family",  protocol_family_type::getName());
     Logger::trace<int>("SocketReliableClientStream<P,B>::SocketReliableClientStream", "socketD_.descriptor_", this->socketD_.descriptor_);
     rdbuf(&streambuf_);
     streambuf_.setFd(this->socketD_.descriptor_);
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P, class B>
SocketReliableClientStream<P,B>::SocketReliableClientStream(const socket_descriptor& iFd):
       SocketReliableClient<P>(iFd),
       std::iostream(0),
       streambuf_(iFd.descriptor_)
{
     Logger::trace     ("SocketReliableClientStream<P,B>::SocketReliableClientStream","Address  Family",  address_family_type::getName());
     Logger::trace     ("SocketReliableClientStream<P,B>::SocketReliableClientStream","Protocol Family",  protocol_family_type::getName());
     Logger::trace<int>("SocketReliableClientStream<P,B>::SocketReliableClientStream", "socketD_.descriptor_", this->socketD_.descriptor_);
     rdbuf(&streambuf_);
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P, class B>
SocketReliableClientStream<P,B>::SocketReliableClientStream(const std::string& iDestinationHost,
                                                            const int          iDestinationPort):
     SocketReliableClient<P>(iDestinationHost,iDestinationPort),
     std::iostream(0),
     streambuf_(-1)
{
     Logger::trace     ("SocketReliableClientStream<P,B>::SocketReliableClientStream","Address  Family",  address_family_type::getName());
     Logger::trace     ("SocketReliableClientStream<P,B>::SocketReliableClientStream","Protocol Family",  protocol_family_type::getName());
     Logger::trace     ("SocketReliableClientStream<P,B>::SocketReliableClientStream", "iDestinationHost",iDestinationHost);
     Logger::trace<int>("SocketReliableClientStream<P,B>::SocketReliableClientStream", "iDestinationPort",iDestinationPort);
     rdbuf(&streambuf_);
     streambuf_.setFd(this->socketD_.descriptor_);
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P, class B>
SocketReliableClientStream<P,B>::SocketReliableClientStream(const std::string& iDestinationHost,
                                            const int          iDestinationPort,
			                    const std::string& iLocalInterface,
                                            const int          iLocalPort):
     SocketReliableClient<P>(iDestinationHost,iDestinationPort,iLocalInterface,iLocalPort),
     std::iostream(0),
     streambuf_(-1)
{
     Logger::trace     ("SocketReliableClientStream<P,B>::SocketReliableClientStream", "Address  Family", address_family_type::getName());
     Logger::trace     ("SocketReliableClientStream<P,B>::SocketReliableClientStream", "Protocol Family", protocol_family_type::getName());
     Logger::trace     ("SocketReliableClientStream<P,B>::SocketReliableClientStream", "iDestinationHost",iDestinationHost);
     Logger::trace<int>("SocketReliableClientStream<P,B>::SocketReliableClientStream", "iDestinationPort",iDestinationPort);
     Logger::trace     ("SocketReliableClientStream<P,B>::SocketReliableClientStream", "iLocalInterface", iLocalInterface);
     Logger::trace<int>("SocketReliableClientStream<P,B>::SocketReliableClientStream", "iLocalPort",      iLocalPort);   
     rdbuf(&streambuf_);
     streambuf_.setFd(this->socketD_.descriptor_);
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P, class B>
SocketReliableClientStream<P,B>::~SocketReliableClientStream()
{
   Logger::trace ("SocketReliableClientStream<P,B>::~SocketReliableClientStream", "DESTRUCTOR");
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P, class B>
void SocketReliableClientStream<P,B>::setSocketDescriptor(const socket_descriptor& iDescriptor)
{
    this->setSocketFd(iDescriptor);
    streambuf_.setFd(this->socketD_.descriptor_);
    clear();
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P, class B>
void SocketReliableClientStream<P,B>::connect()
{
    if (this->connected_ == true && fail() == true){
	SocketReliableClient<P>::disconnectionDetected(); 
	streambuf_.setFd(this->socketD_.descriptor_);
	clear();
    }
    SocketReliableClient<P>::connect();
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P, class B>
void SocketReliableClientStream<P,B>::connect(const std::string& iDestinationHost, const int iDestinationPort)
{
    if (this->connected_ == true && fail() == true){
        SocketReliableClient<P>::disconnectionDetected();
	streambuf_.setFd(this->socketD_.descriptor_);
	clear();
    }
    SocketReliableClient<P>::connect(iDestinationHost,iDestinationPort);
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P, class B>
bool SocketReliableClientStream<P,B>::isConnected()
{
    // If the socket_buffer associated with this stream encounters
    // a failure condition, the socket buffer cannot directory report
    // the error to this SocketReliableClientStream class. Therefore
    // when testing to see if the class is connected, need to set the
    // connected_ member to false is the stream indicates a error condition.
    if (this->connected_ == true && fail() == true){
        SocketReliableClient<P>::disconnectionDetected();
	streambuf_.setFd(this->socketD_.descriptor_);
	clear();
    }
    return this->connected_;
}

} // end namespace SKT 

#endif 
