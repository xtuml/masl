//============================================================================
// UK Crown Copyright (c) 2005. All rights reserved.
//
// File:    socketProtocol.hh
//
// Description:
//     Define a series of structures that encapsulate the address family and
// protocol family types that can be used during the creation of a socket. Each
// protocol family type defines a series of static functions that wrap the allowed
// function set for a particular protocol family. This enables the socket functions 
// bind,listen,connect and accept to be correctly type checked against the address 
// family the socket has been created with.
//
//============================================================================//
#ifndef SOCKET_socketProtocolP__
#define SOCKET_socketProtocolP__

#include <cerrno>
#include <cerrno>
#include <cstring>
#include <stdexcept>

#include <netdb.h>
#include <sys/un.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/tcp.h>
#include <netinet/in.h>

#include "sockets/socketCommon.hh"
#include "sockets/socketLogging.hh"

namespace SKT {

// *******************************************************************
// *******************************************************************
// *******************************************************************
static std::string herrorReport(const int iHerrno)
{
    std::string herror;
    #ifndef __osf__
    // DEC does not seem to support hstrerror, 
    // so exclude it from dec builds.
    herror = hstrerror(iHerrno);
    #endif
    return herror;
}

// *******************************************************************
// *******************************************************************
// *******************************************************************
// Provide an abstraction that describes the 
// Unix domain address family. 
struct UnixAddressFamily
{
    // POSIX declares AF_LOCAL, but this is not supported by DEC!!
    enum { AddressFamily = AF_UNIX, MAXPATHLENGTH=108 };
    
    typedef  struct sockaddr_un  socket_address_type;
    
    static const char* const getName() { return "UnixAddressFamily"; }
    
    static int getSize(const socket_address_type& iAddress) {  
        return strlen(iAddress.sun_path) + sizeof(iAddress.sun_family);
    }
    
    // undefined behaviour if iAddress has not been initialised.
    static std::string getHostName (const socket_address_type& iAddress)
    {
	std::string host(iAddress.sun_path);
	std::string::size_type delimiterIndex = host.find("::");
	if (delimiterIndex != std::string::npos){
	    host = host.substr(0,delimiterIndex);
	}
	return host;
    }

    // undefined behaviour if iAddress has not been initialised.
    static std::string getPort (const socket_address_type& iAddress)
    {
        std::string port(iAddress.sun_path);
	std::string::size_type delimiterIndex = port.find("::");
	if (delimiterIndex != std::string::npos){
	    port = port.substr(delimiterIndex+2);
	}
	return port;
    }
    
    
    // Provide a type checked method that enables unix 
    // address family structure types to be populated.
    static void populate(socket_address_type& ioAddress, const std::string& iHost, const int iPort){
       
       Logger::trace     ("UnixAddressFamily::populate","iHost",iHost);
       Logger::trace<int>("UnixAddressFamily::populate","iPort",iPort);
      
       memset(&ioAddress, 0, sizeof(socket_address_type));
       ioAddress.sun_family = AddressFamily;
       
       std::string unixPath("localHost");
       if (iHost.empty() == false){
           unixPath = iHost;
       }
       
       if (unixPath.size() < MAXPATHLENGTH-1){   // -1 - to account for '\0' that is appended by strcpy
          Logger::trace     ("UnixAddressFamily::populate","sun_path",unixPath);
          strcpy(ioAddress.sun_path,unixPath.c_str());
       }
       else{
          throw SocketException("UnixAddressFamily::populate failed : address name to long");
       } 
    }

    // Provide a mechanim for removing the unix socket file.
    static void cleanup(const socket_address_type& iAddress)
    {
         Logger::trace ("UnixAddressFamily::cleanup");
         if (iAddress.sun_path != NULL){
             Logger::trace ("UnixAddressFamily::cleanup - unlink file ",iAddress.sun_path);
	     unlink(iAddress.sun_path);
	 }    
    }

   private:
      // Do not want to construct instances of this class
      UnixAddressFamily();
     ~UnixAddressFamily();
};

// *******************************************************************
// *******************************************************************
// *******************************************************************
// Provide an abstraction that describes the 
// Internet domain address family. 
struct InetAddressFamily
{
    enum { AddressFamily = AF_INET };
    typedef  struct sockaddr_in  socket_address_type;

    static const char* const getName() { return "InetAddressFamily"; }
   
    static int getSize(const socket_address_type& iAddress) { 
       return sizeof(iAddress); 
    }
    
    // Returns textual name, i.e. dell121pc2.servalan.benhall.gchq
    static std::string getHostName (const socket_address_type& iAddress)
    {
	 struct hostent* hostAddr = gethostbyaddr(reinterpret_cast<const char*>(&(iAddress.sin_addr.s_addr)),sizeof(iAddress.sin_addr.s_addr),AF_INET);
	 if (hostAddr == NULL){
             std::string errorMsg("InetAddressFamily::getHost - gethostbyaddr failed : ");
	     errorMsg += herrorReport(h_errno);
	     throw SocketException(errorMsg);
	 }
	 return std::string(hostAddr->h_name);
    }

    // Returns textual IP4 address in dot notation, i.e. 146.198.16.91
    static std::string getHostAddress (const socket_address_type& iAddress)
    {
         char buffer[INET_ADDRSTRLEN];
	 const char* char_address = inet_ntop(AddressFamily,&iAddress.sin_addr.s_addr,buffer,INET_ADDRSTRLEN);
	 if (char_address == NULL){
             std::string errorMsg("InetAddressFamily::inet_ntop - inet_ntop failed : ");
	     errorMsg += strerror(errno);
	     throw SocketException(errorMsg);
	 }
	 return std::string(buffer);
    }
    
    static std::string getPort (const socket_address_type& iAddress)
    {
      std::ostringstream convertor;
      convertor << ntohs(iAddress.sin_port);
      return  convertor.str();
    }
  
    // Provide a type checked method that enables unix 
    // address family structure types to be populated.
    static void populate(socket_address_type& ioAddress, const std::string& iHost, const int iPort){
       Logger::trace     ("InetAddressFamily::populate","iHost",iHost);
       Logger::trace<int>("InetAddressFamily::populate","iPort",iPort);
      
       memset(&ioAddress, 0, sizeof(socket_address_type));
       ioAddress.sin_family = AddressFamily;
       ioAddress.sin_port   = htons(iPort);
      
       if (iHost.empty() == false){
          struct hostent* serverHostp = gethostbyname(iHost.c_str());
	  if (serverHostp == NULL) {
             std::string errorMsg("InetAddressFamily::populate - gethostbyname failed : ");
	     errorMsg += herrorReport(h_errno);
	     throw SocketException(errorMsg);
	  }
	  // already in network byte order so copy into required location
	  ::memcpy(&ioAddress.sin_addr.s_addr, serverHostp->h_addr, serverHostp->h_length);
       }
       else{
         ioAddress.sin_addr.s_addr = htonl(INADDR_ANY);
       } 
    }
    
    // Provide a type checked method that enables unix address family 
    // structure types to be populated using existing 32-bit IPv4 address.
    static void populate(socket_address_type& ioAddress, const in_addr_t iHost, const int iPort)
    {
       Logger::trace<int>      ("InetAddressFamily::populate","iPort",iPort);
       Logger::trace<in_addr_t>("InetAddressFamily::populate","iPort",iHost);
      
       memset(&ioAddress, 0, sizeof(socket_address_type));
       ioAddress.sin_family      = AddressFamily;
       ioAddress.sin_port        = htons(iPort);
       ioAddress.sin_addr.s_addr = iHost;
    }

    // Provide a mechanim for cleaing up socket after use.
    static void cleanup(const socket_address_type& iAddress)
    {
        // no cleanup required for AF_INET sockets
       Logger::trace ("InetAddressFamily::cleanup");
    }
    
   
   private:
      // Do not want to construct instances of this class
      InetAddressFamily();
     ~InetAddressFamily();
};

// *******************************************************************
// *******************************************************************
// *******************************************************************
// !!!!! THIS HAS NOT BEEN TESTED !!!!!!!
// Provide an abstraction that describes the IPv6
// Internet domain address family. 
// 
struct InetV6AddressFamily
{
    enum { AddressFamily = AF_INET6 };
    typedef  struct sockaddr_in6  socket_address_type;

    static const char* const getName() { return "InetV6AddressFamily"; }
   
    static int getSize(const socket_address_type& iAddress) { 
       return sizeof(iAddress); 
    }
  
    // Provide a type checked method that enables unix 
    // address family structure types to be populated.
    static void populate(socket_address_type& ioAddress, const std::string& iHost, const int iPort){
       Logger::trace     ("InetV6AddressFamily::populate","iHost",iHost);
       Logger::trace<int>("InetV6AddressFamily::populate","iPort",iPort);
      
       memset(&ioAddress, 0, sizeof(socket_address_type));
       ioAddress.sin6_family = AddressFamily;
       ioAddress.sin6_port   = htons(iPort);
      
       if (iHost.empty() == false){
          // gethostbyname only supports IPv4 addresses, 
	  // therefore use getaddrinfo
	  struct addrinfo  hints;
	  struct addrinfo* addrResultList = NULL;
	  
	  memset(&hints, 0, sizeof(struct addrinfo));
	  hints.ai_family = AddressFamily;
	  
	  int addrInfoResult = 0;
	  if ((addrInfoResult = getaddrinfo(iHost.c_str(),NULL,&hints,&addrResultList)) != 0){
	     std::string errorMsg("InetV6AddressFamily::populate - getaddrinfo failed : ");
	     errorMsg += gai_strerror(addrInfoResult);
	     throw SocketException(errorMsg);
	  }
	  socket_address_type* addrInfo = reinterpret_cast<socket_address_type*>(addrResultList->ai_addr); 
	  ::memcpy(&ioAddress.sin6_addr,&addrInfo->sin6_addr,addrResultList->ai_addrlen);
	  freeaddrinfo(addrResultList);
        }
        else{
           ioAddress.sin6_addr = in6addr_any;
        } 
    }

    // Provide a mechanim for cleaing up socket after use.
    static void cleanup(const socket_address_type& iAddress)
    {
       Logger::trace ("InetV6AddressFamily::cleanup");
       // no cleanup required for AF_INET6 sockets
    }
   
   private:
      // Do not want to construct instances of this class
      InetV6AddressFamily();
     ~InetV6AddressFamily();
};

// *******************************************************************
// *******************************************************************
// *******************************************************************
template <class AF>
struct StreamProtocol
{
   typedef AF                               address_family_type;
   typedef typename AF::socket_address_type socket_address_type;
   
   enum { type     = SOCK_STREAM }; // made seperate enums in case the
   enum { protocol = IPPROTO_TCP }; // constants have the same value

   static const char* const getName() { return "StreamProtocol"; }
   
   // Define the set of socket functions that can 
   // be used with this type of protocol definition.
   // Note they are all type-checked with the correct 
   // address family structure.
   static void bindSocket    (const int iSockfd, socket_address_type& ioAddress);
   static void connectSocket (const int iSockfd, const socket_address_type& iServAddr); 
   static int  acceptSocket  (const int iSockfd, socket_address_type& iServAddr);
   static int  acceptSocket  (const int iSockfd); 
   static void listenSocket  (const int iSockfd, const int backlog=50);					
};

// *******************************************************************
// *******************************************************************
// *******************************************************************
template <class AF>
struct DatagramProtocol
{
   typedef AF                               address_family_type;
   typedef typename AF::socket_address_type socket_address_type;

   enum { type     = SOCK_DGRAM }; // made seperate enums in case the
   enum { protocol = IPPROTO_UDP}; // constants have the same value

   static const char* const getName() { return "DatagramProtocol"; }
   
   // Define the set of socket functions that can 
   // be used with this type of protocol definition. 
   // Note they are all type-checked with the correct 
   // address family structure.
   static void    connectSocket  (const int iSockfd, const socket_address_type& iServAddr);
   static void    bindSocket     (const int iSockfd, socket_address_type& ioAddress);      
   static ssize_t sendtoSocket   (const int iSockfd, const char *ibuffer, const int iBufferSize, const int flags, const socket_address_type& iDestinationAddress);
   static ssize_t recvfromSocket (const int iSockfd, char *oBuffer, const int iBufferSize, const int iFlags, socket_address_type& iDestinationAddress);
};

// *******************************************************************
// *******************************************************************
// *******************************************************************
template <class AF>
struct IPProtocol
{
   typedef AF                               address_family_type;
   typedef typename AF::socket_address_type socket_address_type;

   enum { type     = SOCK_RAW   };
   enum { protocol = IPPROTO_UDP};
};

// *******************************************************************
// *******************************************************************
// *******************************************************************
template <class AF>
void StreamProtocol<AF>::bindSocket(const int iSockfd, socket_address_type& ioAddress)
{
   // For a client the bind system call does not need to be invoked as
   // all it will do is fill in the local-addr information. This is done
   // by the connect anyway.

   Logger::trace<int>("StreamProtocol::socketBind","iSockfd",iSockfd);
   if (::bind(iSockfd,reinterpret_cast<struct sockaddr*>(&ioAddress), address_family_type::getSize(ioAddress)) < 0){
      throw SocketBindException(errno);
   }
}

// *******************************************************************
// *******************************************************************
// *******************************************************************
template <class AF>
void StreamProtocol<AF>::connectSocket (const int iSockfd, const socket_address_type& iServAddr) 
{ 
  Logger::trace<int>("StreamProtocol::socketConnect","iSockfd",iSockfd);
  if ( ::connect(iSockfd, reinterpret_cast<const struct sockaddr*>(&iServAddr), address_family_type::getSize(iServAddr)) < 0 ){
     throw SocketConnectException(errno);
  }
}

// *******************************************************************
// *******************************************************************
// *******************************************************************
template <class AF>
int StreamProtocol<AF>::acceptSocket  (const int iSockfd, socket_address_type& iServAddr) 
{ 
   Logger::trace<int>("StreamProtocol::socketAccept","iSockfd",iSockfd);
   socklen_t addrLength_ = address_family_type::getSize(iServAddr);
   return ::accept(iSockfd,reinterpret_cast<struct sockaddr*>(&iServAddr),&addrLength_);
}

// *******************************************************************
// *******************************************************************
// *******************************************************************
template <class AF>
int StreamProtocol<AF>::acceptSocket  (const int iSockfd) 
{ 
  Logger::trace<int>("StreamProtocol::socketAccept","iSockfd",iSockfd);
  return ::accept(iSockfd,NULL,NULL);
}

// *******************************************************************
// *******************************************************************
// *******************************************************************
template <class AF>
void StreamProtocol<AF>::listenSocket  (const int iSockfd, const int backlog)					
{ 
   Logger::trace<int>("StreamProtocol::listenSocket","iSockfd",iSockfd);
   if (::listen(iSockfd,backlog) != 0){
       throw SocketListenException(errno);
   };
}

// *******************************************************************
// *******************************************************************
// *******************************************************************
template <class AF>
void DatagramProtocol<AF>::connectSocket (const int iSockfd, const socket_address_type& iServAddr) 
{ 
   // When called for a connectionless protcol all that is done is 
   // to store the specified servaddr so that the system knows where 
   // to send any future data when a write is called with the socketfd.
   // Also only datagrams from this address will be received by the socket. 
   // The connect should return straight away as no exchange for data occurs 
   // between the host and foreign system. Another advantage of calling connect for
   // this protocol is that the host will be informed if it attempts to send a datagram
   // to an invalid address.

  Logger::trace<int>("DatagramProtocol::connectSocket","iSockfd",iSockfd);
  if ( ::connect(iSockfd, reinterpret_cast<const struct sockaddr*>(&iServAddr), address_family_type::getSize(iServAddr)) < 0 ){
     throw SocketConnectException(errno);
  }
}
   
// *******************************************************************
// *******************************************************************
// *******************************************************************
template <class AF>
void DatagramProtocol<AF>::bindSocket (const int iSockfd, socket_address_type& ioAddress)      
{
   Logger::trace<int>("DatagramProtocol::bindSocket","iSockfd",iSockfd);
   if (::bind(iSockfd,reinterpret_cast<struct sockaddr*>(&ioAddress), address_family_type::getSize(ioAddress)) < 0){
       throw SocketBindException(errno);
   }
}

// *******************************************************************
// *******************************************************************
// *******************************************************************
template <class AF>
ssize_t DatagramProtocol<AF>::sendtoSocket (const int iSockfd, const char *ibuffer, const int iBufferSize, const int flags, const socket_address_type& iDestinationAddress)
{
   return ::sendto(iSockfd,ibuffer,iBufferSize,flags,reinterpret_cast<const sockaddr*>(&iDestinationAddress),address_family_type::getSize(iDestinationAddress));
}

// *******************************************************************
// *******************************************************************
// *******************************************************************
template <class AF>
ssize_t DatagramProtocol<AF>::recvfromSocket (const int iSockfd, char *oBuffer, const int iBufferSize, const int iFlags, socket_address_type& iDestinationAddress)
{
   socklen_t destinationLen = address_family_type::getSize(iDestinationAddress);
   return ::recvfrom(iSockfd,oBuffer,iBufferSize,iFlags,reinterpret_cast<sockaddr*>(&iDestinationAddress), &destinationLen);
}

} // end namespace SKT 
#endif 
