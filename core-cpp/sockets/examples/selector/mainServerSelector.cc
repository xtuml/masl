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
   std::cout << "   -p port "                         << std::endl;
}

//**************************************************************************
//**************************************************************************
//**************************************************************************
void sigpipe_handler(int) { }

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

     std::cout << "Using host : '" << host << "'" << std::endl;
     std::cout << "Using port : "  << port << " & " << port+1  << std::endl;
 
     typedef SKT::InternetReliableListener ServerSocket;
     typedef SKT::InternetReliableClient   ClientSocket;
    
     ServerSocket sockListener1;
     ServerSocket sockListener2;
  
     sockListener1.setOption(SKT::SoReuseAddr(SKT::enable));
     sockListener1.setOption(SKT::TcpNoDelay (SKT::enable));
     sockListener1.setOption(SKT::FdONonBlock(SKT::enable));

     sockListener2.setOption(SKT::SoReuseAddr(SKT::enable));
     sockListener2.setOption(SKT::TcpNoDelay (SKT::enable));
     sockListener2.setOption(SKT::FdONonBlock(SKT::enable));
         
     sockListener1.establish(host,port);
     sockListener2.establish(host,port+1);
  
     for(; ; ){
       ServerSocket::socket_descriptor descriptor1 = sockListener1.acceptSocket();
       ServerSocket::socket_descriptor descriptor2 = sockListener2.acceptSocket();
       
       if (descriptor1.isValid() &&  descriptor2.isValid()){
          std::cout << "received socket connection : " << descriptor1.descriptor_ << std::endl;
          std::cout << "received socket connection : " << descriptor2.descriptor_ << std::endl;
          
          ClientSocket client1(descriptor1);
          client1.setSigPipeHandler(sigpipe_handler);
	  
          ClientSocket client2(descriptor2);
          client2.setSigPipeHandler(sigpipe_handler);
	  
	  for(; ;){
	     int bytesWrite1 = client1.write("123456789",9);
	     int bytesWrite2 = client2.write("abcdefghi",9);
	     sleep(1);
	     if (bytesWrite1 < 0 || bytesWrite2 < 0){
	        std::cout << "write opertaion failed - connection closed!!!" << std::endl;
		break;
	     }
	  }
       }
      sleep(10);
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
