//============================================================================
// UK Crown Copyright (c) 2005. All rights reserved.
//
// File:    socketOption.hh
//
// Description:    
//     Define a series of classes that will enable socket configuration using 
// set/get sockopt and fcntl. Some of the  options can be set and get while 
// others only allow one of these mechanisms. Therefore a specialisation of the
// SocketOption class has been produced to handle these cases. 
//    The actual options that can be used for a particular platform are defined 
// in a file socketOption.<platform_name>.
// These platform specific files are included at the end of this file, only the
// options for the platform currently being built against will be exposed to the
// compiler.
//     If the current set of options does not include the functionality to configure
// a required option then either the existing classes can be specialised for the
// required option, or if it requires new system calls then a new option class can be
// created. This class needs to support a set and get method taking an integer
// parameter; the socket descriptor.
// 
//============================================================================//
#ifndef SOCKET_socketOption__
#define SOCKET_socketOption__

#include <cerrno>

#include <fcntl.h>
#include <sys/types.h>
#include <sys/socket.h>

#include "sockets/socketCommon.hh"

namespace SKT {

// Define some integer constants that can be 
// enable and disable the socket options.
const int enable  = 1;
const int disable = 0;

// Some of the socket options support both the 'get' and 'set' operations 
// while others only support one. Therefore to allow for specialisation
// of the Option classes, define an OptionAccess enum type with the
// allowed values.
enum OptionAccess { CAN_DO_GETSET = 0,
                    CAN_DO_GET    = 1,
                    CAN_DO_SET    = 2 };
       
// **********************************************************
// **********************************************************
// **********************************************************
// Class the wraps the getsockopt and setsockopt system calls.
template <class T,int level,int option, OptionAccess=CAN_DO_GETSET>
class SocketOption
{
   public:
     SocketOption(const T iValue = T()):
         value_(iValue){ }
     
     void set(const int sockFd) const
     {
        const void* optval = &value_;
        socklen_t   optlen = sizeof(T);       
        if (setsockopt(sockFd,level,option,optval,optlen) < 0) {
            throw SocketOptionException(std::string("SocketOption setsockopt Failed : ") + strerror(errno));
        }
     }
     
     void get(const int sockFd)
     {
        void*      optval = &value_;
        socklen_t  optlen = sizeof(T);       
        if (getsockopt(sockFd,level,option,optval,&optlen) < 0) {
            throw SocketOptionException(std::string("SocketOption getsockopt Failed : ") + strerror(errno));
        }
     }

     void     setValue (const T iValue) { value_ = iValue; }
     const T& getValue () const         { return value_;   }
    
    private: 
       T value_;
};

// **********************************************************
// **********************************************************
// **********************************************************
// Provide a partial specialisation of the SocketOption class
// so that socket options that provide a get but no set method
// will cause a compile time error, if the user attempts to call
// a set operation.
template <class T, int level, int option>
class SocketOption<T,level,option,CAN_DO_GET>
{
   public:
     SocketOption():value_(){ }

     void get(const int sockFd)
     {
        socklen_t optlen = sizeof(T); 
	void*     optval = &value_;
	if (getsockopt(sockFd,level,option,optval,&optlen) < 0) {
            throw SocketOptionException(std::string("SocketOption getsockopt Failed : ") + strerror(errno));
        }
     }

     const T& getValue () const { return value_; }

    private: 
       T  value_;
};

// **********************************************************
// **********************************************************
// **********************************************************
// Provide a partial specialisation of the SocketOption class
// so that socket options that provide a set but no get method
// will cause a compile time error, if the user attempts to call
// a get operation.
template <class T, int level, int option>
class SocketOption<T,level,option,CAN_DO_SET>
{
   public:
     SocketOption(const T iValue):
         value_(iValue){ }

     void set(const int sockFd) const
     {
	const void* optval = &value_;
        socklen_t   optlen = sizeof(T); 
        if (setsockopt(sockFd,level,option,optval,optlen) < 0) {
            throw SocketOptionException(std::string("SocketOption setsockopt Failed : ") + strerror(errno));
        }
     }
     
     void  setValue (const T iValue) { value_ = iValue; }
    
    private: 
       T  value_;
};


// **********************************************************
// **********************************************************
// **********************************************************
// Define a class that uses the fcntl system call 
// to configure a socket option
template<int setFlag, int getFlag, int option>
class FcntlDescriptorFlagOption
{
    public:
       FcntlDescriptorFlagOption(bool iEnable = false):value_(iEnable) { } 
      ~FcntlDescriptorFlagOption() { } 
    
       void set(const int iSockFd) const 
       {
	  // Must get the current flags and OR the bit set to set the
	  // required flag. Just setting the flag will clear all the
	  // other flags, this is rarely what is required.
	  int flags = 0;
	  if ((flags = fcntl(iSockFd,getFlag,0)) < 0){
	     throw SocketOptionException(std::string("FcntlOption set (fcntl(fd,F_GETFL,0)) failed : ") + strerror(errno));
	  }
          
	  value_ == true ? flags |= option : flags &= ~option;
	 
	  if (fcntl(iSockFd,setFlag,flags) < 0){
	     throw SocketOptionException(std::string("FcntlOption set (fcntl(fd,F_SETFL,flags)) failed : ") + strerror(errno));
	  }
       }
       
       void get(const int iSockFd)
       {
	  int flags = 0;
	  if ((flags = fcntl(iSockFd,getFlag,0)) < 0){
	     throw SocketOptionException(std::string("FcntlOption fcntl (F_GETFL) failed : ") + strerror(errno));
	  }
	  value_ = flags & option;
       }

       void setValue(bool iValue) { value_ = iValue; }
       bool getValue()            { return value_;   }

    private:
       bool value_;
};

} // end namespace SKT 

#include "sockets/socketOption.Linux"
#include "sockets/socketOption.SunOS"

#endif 
