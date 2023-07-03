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

#include <iostream>
#include <iomanip>

#include <stdio.h>
#include <unistd.h>

#include "sockets/sockets.hh"

//**************************************************************************
//**************************************************************************
//**************************************************************************
void usage() {
   std::cout << "mainReliableListener  : " << std::endl;
   std::cout << "   -h host [default   : INADDR_ANY]" << std::endl;
   std::cout << "   -p port [default   : 0]"          << std::endl;
}

int main (int argc, char *argv[])
{
  SKT::Logger::Instance().initialise();
  
  try{ 
     std::string host("");
     int port  = 0;
    
     int c = 0;
     opterr = 0; // stop getopt writing to stderr
     while ((c = getopt(argc,argv,"p:h:")) != EOF){
          switch(c) {
	     case 'p' :
	          port = SKT::stringToValue<int>(optarg);
		  break;
	     
	     case 'h' :
	          host = optarg;
		  break;
	    	     
	     case '?' :
	        std::cout << "mainDatagramClient - invalid argument" << std::endl;
		usage();
		exit(1);
		break;
	  }
     } // end while

     std::cout << "Using host : '" << host  << "'" << std::endl;
     std::cout << "Using port : '" << port  << "'" << std::endl;
 
     typedef SKT::InternetReliableListener       ServerSocket;
     typedef SKT::BufferedInternetReliableClient ClientSocket;
    
     ServerSocket sockListener;
  
     SKT::SoReuseAddr reuseAddr;
     SKT::TcpNoDelay  tcpNoDelay;
     
     sockListener.getOption(reuseAddr);
     sockListener.getOption(tcpNoDelay);
     
     std::cout << "reuseAddr  : " << reuseAddr.getValue()  << std::endl;
     std::cout << "tcpNoDelay : " << tcpNoDelay.getValue() << std::endl;
     
     reuseAddr.setValue (SKT::enable);
     tcpNoDelay.setValue(SKT::enable);
     
     sockListener.setOption(reuseAddr);
     sockListener.setOption(tcpNoDelay);
     
     std::cout << "reuseAddr  : " << reuseAddr.getValue() << std::endl;
     std::cout << "tcpNoDelay : " << tcpNoDelay.getValue() << std::endl;
    
     SKT::FdONonBlock fdONonBlock;
     sockListener.getOption(fdONonBlock);
     std::cout << "fdONonBlock  : " << fdONonBlock.getValue() << std::endl;
     
     fdONonBlock.setValue(SKT::enable);
     sockListener.setOption(fdONonBlock);
     
     sockListener.getOption(fdONonBlock);
     std::cout << "fdONonBlock  : " << fdONonBlock.getValue() << std::endl;
     
     sockListener.establish(host,port);
  
     timespec tval;
     tval.tv_sec  = 0;
     tval.tv_nsec = 100000000; // 0.1 secs
     for(; ; ){
       ServerSocket::socket_descriptor descriptor = sockListener.acceptSocket();
       if (descriptor.isValid() == true){
          std::cout << "received socket connection " << descriptor.descriptor_ << std::endl;
          
	  ClientSocket client(descriptor);
	  std::string line;
	  while (client >> std::setw(10) >> line){
	     std::cout << "line :  '" << line << "'" << std::endl;
	  }
       }
       else{
         // socket accept() failed. As this socket has been configured for
	 // non-blocking IO it will return -1 and set errno to EAGAIN, as 
	 // there are no pending connections. If this is not the reported
	 // error then something serious has happended
	 if (errno != EAGAIN){
	     throw SKT::SocketIOException("Non-blocking socket accept failed",errno);
	 }
       }
       nanosleep(&tval,NULL);
     }
  }
  catch(const SKT::SocketIOException& sioe){
     std::cout << "Caught unexpected SocketException : " << sioe.report() <<  " reason : " << sioe.reason() << std::endl;
  }
  catch(const SKT::SocketException& se){
     std::cout << "Caught unexpected SocketException : " << se.report() << std::endl;
  }
  catch(const std::exception& e){
     std::cout << "Caught unexpected C++ exception : " << e.what() << std::endl;
  }
  catch(...){
     std::cout << "Caught unexpected and unknown exception " << std::endl;
  }
  
  return 0;
}
