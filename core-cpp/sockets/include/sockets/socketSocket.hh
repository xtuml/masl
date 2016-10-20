//============================================================================
// UK Crown Copyright (c) 2005. All rights reserved.
//
// File:  socketSocket.hh 
//
// Description:    
//   Define a class that will create a socket of the required type and address family.
// This is used as a base class which all socket clients and servers should extend from.
//============================================================================//

#ifndef SOCKET_socket__
#define SOCKET_socket__

#include <cerrno>
#include <string>

#include <signal.h>
#include <sys/types.h>
#include <sys/socket.h>

#include "sockets/socketLogging.hh"
#include "sockets/socketCommon.hh"

#include "sockets/socketOption.hh"
#include "sockets/socketProtocol.hh"

namespace SKT {

// ***********************************************
// ***********************************************
// ***********************************************
template<class P>
class Socket
{
    public:
       typedef P                               protocol_family_type;
       typedef typename P::address_family_type address_family_type;
       typedef typename P::socket_address_type socket_address_type;
    
    public:
        // Define a class that can hold the socket descriptor for a
	// specific socket specialisation. This provides a compile time 
	// firewall that for eaxample will prevent the socket descriptor 
	// from a SocketServer<InternetTCPSocket> accept call being passed 
	// into the constructor of a SocketClient<UnixTCPSocket,..>
	// This structure also helps when NON_BLOCKING IO is used as the
	// SocketServer accept system call will return with a descriptor
	// value of -1. This can be checked before a SocketClient is
	// constructed.
        struct SocketDescriptor
        {
           typedef Socket socket_type;
           SocketDescriptor():descriptor_(-1) { }
	   SocketDescriptor(const int iDescriptor):
	        descriptor_(iDescriptor) { }
             
	     inline bool isValid() const { return descriptor_ >= 0;}
	     
	     int descriptor_;
        }; 
   
    public:
       Socket();
       Socket(const SocketDescriptor& socketFd);
       virtual ~Socket();      

       inline const SocketDescriptor& getSocketFd() const { return socketD_; }
       
       // Set this socket to the supplied socket descriptor
       void setSocketFd(const SocketDescriptor& iDescriptor);
       
       // Define a templated method that can be used to set a socket option. 
       // Any type instance passed to this method when it is invoked must define a
       // public set method that takes a const int, the socket fd. This provides
       // a mechanism for setting all the possible socket options through one interface 
       // method. The socket suite currently provides option classes SocketOption and
       // FcntlDescriptorFlagOption. Others can be created as required and added to the suite. 
       template <class T>
       void setOption(const T& iOption) { iOption.set(socketD_.descriptor_); }
       
       // Define a templated method that can be used to get the value of a socket option. 
       // Any type instance passed to this method when it is invoked must define a
       // public get method that takes a const int, the socket fd. This provides
       // a mechanism for getting all the possible socket options through one interface 
       // method. The socket suite currently provides option classes SocketOption and
       // FcntlDescriptorFlagOption. Others can be created as required and added to the suite. 
       template <class T>
       void getOption(T& ioOption) { ioOption.get(socketD_.descriptor_); }
           
    
       // If a client connects with a server and sends data, it is possible for the
       // server connection to go down. If the client continues writing a SIGPIPE signal
       // will be returned with a default action that will terminate the application. Therefore
       // user can install a no-op handler and the write will return with an EPIPE error code
       // that is detected and is used to indicate a closed connection. The same applies for
       // a server writing and a client connection going down.
       void setSigPipeHandler( void (*iHandler)(int))
       {
          struct sigaction sa;
	  sigemptyset(&sa.sa_mask);
	  sa.sa_handler  = iHandler; 
          sa.sa_flags    = SA_RESTART;
          sigemptyset(&sa.sa_mask);
          if(::sigaction(SIGPIPE, &sa, NULL) < 0){
             throw SocketException( std::string("Unable to install SIGPIPE signal handler : ") + ::strerror(errno));
          }

       }

       // Close the socket
       virtual void close();
       
    protected:
	// create the raw socket descriptor.
	void socket();
            
    private:
        // prevent copy and assignment
	Socket(const Socket& rhs);
        Socket& operator=(const Socket& rhs);
            
    protected:
        SocketDescriptor    socketD_;
	socket_address_type sockAddr_;
};

// ***********************************************
// ***********************************************
// ***********************************************
template<class P>
Socket<P>::Socket(const SocketDescriptor& socketFd):
       socketD_(socketFd)
{
   Logger::trace<int>("Socket<P>::Socket CONSTRUCTOR","socketFd",socketFd.descriptor_);
   Logger::trace("Socket<P>::Socket","Address  Family",address_family_type::getName());
   Logger::trace("Socket<P>::Socket","Protocol Family",protocol_family_type::getName());
}

// ***********************************************
// ***********************************************
// ***********************************************
template<class P>
Socket<P>::Socket():
     socketD_(-1)
{
   Logger::trace("Socket<P>::Socket","CONSTRUCTOR");
   Logger::trace("Socket<P>::Socket","Address  Family",address_family_type::getName());
   Logger::trace("Socket<P>::Socket","Protocol Family",protocol_family_type::getName());
   
   socket();
}

// ***********************************************
// ***********************************************
// ***********************************************
template<class P>
Socket<P>::~Socket()
{
  Logger::trace("Socket<P>::~Socket","DESTRUCTOR");
  this->close();
}

// ***********************************************
// ***********************************************
// ***********************************************
template<class P>
void Socket<P>::setSocketFd(const SocketDescriptor& iDescriptor)
{
   Logger::trace("Socket<P>::setSocketFd","current fd : ", socketD_.descriptor_);
   Logger::trace("Socket<P>::setSocketFd","new  fd    : ", iDescriptor.descriptor_);
   if (socketD_.isValid()) { 
      ::close(socketD_.descriptor_); 
   }
   socketD_ = iDescriptor;
}

// ***********************************************
// ***********************************************
// ***********************************************
template<class P>
void Socket<P>::close()
{
   Logger::trace("Socket<P>::close","current fd : ", socketD_.descriptor_);
   if (socketD_.isValid()) { 
       ::close(socketD_.descriptor_); 
       socketD_.descriptor_ = -1;
   }
}

// ***********************************************
// ***********************************************
// ***********************************************
template<class P>
void Socket<P>::socket()
{
  if ( (socketD_.descriptor_ = ::socket(address_family_type::AddressFamily,protocol_family_type::type,0)) < 0){
      throw SocketException(std::string("Socket Creation Failed : ") + strerror(errno));
  }
  Logger::trace<int>("Socket<P>::socket","socketD_",socketD_.descriptor_);
}

// ***********************************************
// ***********************************************
// ***********************************************

} // end namespace SKT 
#endif 


