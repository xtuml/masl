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
#include <stdio.h>
#include <sys/signal.h>

#include "sockets/sockets.hh"
#include "sockets/socketSelector.hh"

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
void sigusr1_handler(int)
{

}

//**************************************************************************
//**************************************************************************
//**************************************************************************
int main (int argc, char *argv[])
{
  typedef SKT::Select<SKT::Wait<30,0> > WaitSelectType;
  typedef SKT::Select<SKT::NoWait>      NoWaitSelectType;
  typedef SKT::Select<SKT::WaitForever> WaitForeverSelectType;

  // check the types can be created 
  // without any compile errors.
  NoWaitSelectType select1;
  select1.addSenseRead(1);
  select1.addSenseRead(2);
  
  WaitSelectType select2;
  select1.addSenseRead(1);
  select1.addSenseRead(2);
 
  try{ 
      struct sigaction sa;
      sa.sa_handler = sigusr1_handler; 
      sa.sa_flags = SA_RESTART; 
      if(::sigaction(SIGUSR1, &sa, NULL) < 0){
     	 throw SKT::SocketException( std::string("Unable to install SIGUSR1 signal handler : ") + ::strerror(errno));
      }

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
	        std::cout << "mainSinkSelector - invalid argument" << std::endl;
		usage();
		exit(1);
		break;
	  }
      } // end while

      std::cout << "Using host : '" << host  << "'" << std::endl;
      std::cout << "Using port : '" << port  << " & " << port+1 << std::endl;
      
      SKT::Logger::Instance().initialise();
 
      SKT::InternetReliableClient client1(host,port);
      SKT::InternetReliableClient client2(host,port+1);

      client1.connect();
      client2.connect();
 
      WaitSelectType selector;
      selector.addSenseRead(client1.getSocketFd().descriptor_);
      selector.addSenseRead(client2.getSocketFd().descriptor_);

      std::cout << selector.senseReadList().size() << std::endl;
     
      for(;;){

          WaitSelectType::DescriptorContType readDescriptorList;
          WaitSelectType::DescriptorContType writeDescriptorList;
          selector.sense(readDescriptorList,writeDescriptorList);
     
          int bytesRead = 0; 
          for(std::size_t sDIndex = 0; sDIndex < readDescriptorList.size(); ++sDIndex){
              char buffer[256];
	      if (readDescriptorList[sDIndex] == client1.getSocketFd().descriptor_){
	          std::cout << "client 1 - ";
	          bytesRead = client1.read(buffer, 256); 
	      }
	      else{
	          std::cout << "client 2 - ";
	          bytesRead = client2.read(buffer, 256); 
	      }
	      
	      if (bytesRead != 0){
	         std::string text(buffer,bytesRead);
	         std::cout << "received : " << text << std::endl;
	      } 
	      else{
	        std::cout << "read zero bytes, connection closed !!!!" << std::endl;
	        throw SKT::SocketException("client read zero bytes.");
	      }
         }
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
