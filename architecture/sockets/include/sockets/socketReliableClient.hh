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

#ifndef SOCKET_SocketReliableClientP__
#define SOCKET_SocketReliableClientP__

#include <string>

#include "sockets/socketLogging.hh"
#include "sockets/socketSocket.hh"

namespace SKT {

// ***********************************************
// ***********************************************
// ***********************************************
template <class P>
class SocketReliableClient : public Socket<P>
{
  public:
        typedef P  protocol_family_type;
	
	typedef typename Socket<P>::SocketDescriptor socket_descriptor;
	typedef typename P::socket_address_type      socket_address_type;
        typedef typename P::address_family_type      address_family_type;
  
  public:
       SocketReliableClient();
       SocketReliableClient(const socket_descriptor& iFd);
       SocketReliableClient(const std::string& iDestinationHost,
                            const int          iDestinationPort);

       // Use this constructor if the local interface and port number
       // need to be set to a known value (don't let kernal assign
       // an ephemeral port). Set iLocalInterface to "" to use INADDR_ANY 
       SocketReliableClient(const std::string& iDestinationHost,
                    const int          iDestinationPort,
		    const std::string& iLocalInterface,
                    const int          iLocalPort);
            
       virtual ~SocketReliableClient();
       
       void setConnected();

       // For a connected socket the bind system call does not need to be invoked as
       // all it will do is fill in the local-addr information. This is done
       // by the connect anyway.
       virtual void connect();
       virtual void connect(const std::string& iDestinationHost, const int iDestinationPort);
      
       virtual bool isConnected() { return connected_; }
              
       // Make read and write virtual so that if different functionality 
       // is required for these methods (i.e handling errors) the class 
       // can be extended from.
       virtual int write (const char *iBuffer, const int iSize);
       virtual int read  (char *oBuffer, const int iSize);
       
       void close();
       
   protected:
       void disconnectionDetected();
   
   protected:
       std::string destinationHost_;
       int         destinationPort_;

       std::string localInterface_;
       int         localPort_;
       bool        performBind_;
       bool        connected_;
};


// ***********************************************
// ***********************************************
// ***********************************************
template <class P>
SocketReliableClient<P>::SocketReliableClient():
   Socket<P>(),  
   performBind_(false),
   connected_(false)
{
   Logger::trace  ("SocketReliableClient<P>::SocketReliableClient","Address  Family",  address_family_type::getName());
   Logger::trace  ("SocketReliableClient<P>::SocketReliableClient","Protocol Family",  protocol_family_type::getName());
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P>
SocketReliableClient<P>::SocketReliableClient(const socket_descriptor& iFd):
       Socket<P>(iFd),
       performBind_(false),
       connected_(true)
{
     Logger::trace     ("SocketReliableClient<P>::SocketReliableClient","Address  Family",  address_family_type::getName());
     Logger::trace     ("SocketReliableClient<P>::SocketReliableClient","Protocol Family",  protocol_family_type::getName());
     Logger::trace<int>("SocketReliableClient<P>::SocketReliableClient", "iFd.descriptor_",iFd.descriptor_);
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P>
SocketReliableClient<P>::SocketReliableClient(const std::string& iDestinationHost,
                                              const int          iDestinationPort):
     destinationHost_(iDestinationHost),
     destinationPort_(iDestinationPort),
     performBind_(false),
     connected_(false)
{
     Logger::trace     ("SocketReliableClient<P>::SocketReliableClient","Address  Family",  address_family_type::getName());
     Logger::trace     ("SocketReliableClient<P>::SocketReliableClient","Protocol Family",  protocol_family_type::getName());
     Logger::trace     ("SocketReliableClient<P>::SocketReliableClient", "iDestinationHost",iDestinationHost);
     Logger::trace<int>("SocketReliableClient<P>::SocketReliableClient", "iDestinationPort",iDestinationPort);
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P>
SocketReliableClient<P>::SocketReliableClient(const std::string& iDestinationHost,
                              const int iDestinationPort,
			      const std::string& iLocalInterface,
                              const int iLocalPort):
     destinationHost_(iDestinationHost),
     destinationPort_(iDestinationPort),
     localInterface_(iLocalInterface),
     localPort_(iLocalPort),
     performBind_(true),
     connected_(false)
{
     Logger::trace     ("SocketReliableClient<P>::SocketReliableClient", "Address  Family", address_family_type::getName());
     Logger::trace     ("SocketReliableClient<P>::SocketReliableClient", "Protocol Family", protocol_family_type::getName());
     Logger::trace     ("SocketReliableClient<P>::SocketReliableClient", "iDestinationHost",iDestinationHost);
     Logger::trace<int>("SocketReliableClient<P>::SocketReliableClient", "iDestinationPort",iDestinationPort);
     Logger::trace     ("SocketReliableClient<P>::SocketReliableClient", "iLocalInterface", iLocalInterface);
     Logger::trace<int>("SocketReliableClient<P>::SocketReliableClient", "iLocalPort",      iLocalPort);   
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P>
SocketReliableClient<P>::~SocketReliableClient()
{
   Logger::trace ("SocketReliableClient<P>::~SocketReliableClient", "DESTRUCTOR");
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P>
void SocketReliableClient<P>::close()
{
  Logger::trace("SocketReliableClient,P>::close","current fd : ", this->socketD_.descriptor_);
  connected_ = false;
  Socket<P>::close(); 
  Socket<P>::socket();
  Logger::trace("SocketReliableClient,P>::close","new fd     : ", this->socketD_.descriptor_);
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P>
void SocketReliableClient<P>::setConnected()
{
  connected_ = true;
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P>
void SocketReliableClient<P>::connect()
{
   Logger::trace ("SocketReliableClient<P>::connect");
   if (connected_ == false){
       if (destinationHost_.empty() == false){
           address_family_type::populate(this->sockAddr_,destinationHost_,destinationPort_);
           if (performBind_ == true){
               // Bind the client to a known local 
               // interface address and port number.
               Logger::trace     ("SocketReliableClient<P>::connect",      "performing bind ");
               Logger::trace     ("SocketReliableClient<P>::SocketReliableClient", "localInterface_",localInterface_);
               Logger::trace<int>("SocketReliableClient<P>::SocketReliableClient", "localPort_",localPort_);
       
               typename protocol_family_type::socket_address_type localAddr;
               address_family_type::populate(localAddr,localInterface_,localPort_);
               protocol_family_type::bindSocket(this->socketD_.descriptor_,localAddr);
          }
          try {
             protocol_family_type::connectSocket(this->socketD_.descriptor_,this->sockAddr_);
             connected_ = true;
          } 
          catch(SKT::SocketException& ioe){
              Logger::trace ("SocketReliableClient<P>::connect", std::string("connect failed :") + ioe.report());
              // Stevens Unix Network programming, Vol 1, page 101. If connect fails 
              // must close the socket as it will no longer be usable.
              close();
          }
      }
      else{
        Logger::trace ("SocketReliableClient<P>::connect", "destination host has not been set");
	throw SocketException("destination host has not been set");
      }  
   }
   else{
     Logger::trace ("SocketReliableClient<P>::connect", "already connected!!!");
   }  
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P>
void SocketReliableClient<P>::connect(const std::string& iDestinationHost, const int iDestinationPort)
{
   if (connected_ == false){
       destinationHost_ = iDestinationHost;
       destinationPort_ = iDestinationPort;
       connect();
   } 
   else{
     Logger::trace ("SocketReliableClient<P>::connect(..)", "already connected!!!");
   }  
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P>
void SocketReliableClient<P>::disconnectionDetected()
{
   connected_ = false;
   close();
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P>
int SocketReliableClient<P>::write (const char *iBuffer, const int iSize)
{
  Logger::trace <int>("SocketReliableClient<P>::write","iSize",iSize);
  int   bytewritten = 0;
  int   bytesLeft   = iSize;
  
  while (bytesLeft > 0){
     Logger::trace <int>("SocketReliableClient<P>::write","bytesLeft",bytesLeft);
     bytewritten = ::write(this->socketD_.descriptor_,iBuffer,bytesLeft);
     if (bytewritten < 0){
	 if (errno == EINTR){
	    continue;
	 }
	 else if (errno == EAGAIN){ 
	      // Non-blocking  I/O  has  been  selected  using 
	      // O_NONBLOCK and the write would block.
	      break; 
	 }   
	 else{
	     if (errno == EPIPE){
	        disconnectionDetected();
	     }
	     throw SocketIOException("SocketReliableClient::writeDevice : write failed", errno); 
	 }    
     }
     bytesLeft -= bytewritten;
     iBuffer   += bytewritten;
  }
  return (iSize-bytesLeft);
}

// ***********************************************
// ***********************************************
// ***********************************************
template <class P>
int SocketReliableClient<P>::read (char *oBuffer, const int iSize)
{
  Logger::trace ("SocketReliableClient<P>::read");
  int bytesRead = 0;
  bool interrupted = false;
  do
  {  
     interrupted = false;
     bytesRead = ::read(this->socketD_.descriptor_,oBuffer,iSize);
     if (bytesRead < 0){
         if (errno == EINTR){
	     interrupted = true;
	     continue;
	 }
	 
	 if (errno != EAGAIN) { 
	     throw SocketIOException("SocketReliableClient::readDevice : read failed",errno);
	 }  
      }
  } while (interrupted == true);
  
  if (bytesRead == 0){
      disconnectionDetected();
  }

  Logger::trace <int>("SocketReliableClient<P>::read","bytesRead",bytesRead);
  return bytesRead;
}

} // end namespace SKT 

#endif 
