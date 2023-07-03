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
    
     sockListener.establish(host,port);
     
     ClientSocket client;

  char buffer[255];

    errno=0;
    std::cout << "sigqueue max" << sysconf(_SC_SIGQUEUE_MAX) << std::endl;
    perror("sysconf");

          sigset_t sigs;
          siginfo_t info;
          sigemptyset(&sigs);
          for ( int i = SIGRTMIN; i <= SIGRTMAX; ++i )
          {
            sigaddset(&sigs,i);
          }
          sigaddset(&sigs,SIGUSR1);
          sigaddset(&sigs,SIGIO);
          sigaddset(&sigs,SIGPOLL);
          sigprocmask(SIG_BLOCK,&sigs,0);  

    sigval data;
    data.sival_int = 1;
    pid_t pid = getpid();
    while ( !sigqueue(pid,SIGUSR1,data) && data.sival_int < 1000000 ) data.sival_int++;
    
    std::cout << "sigmax" << data.sival_int << std::endl;

         while ( (sigwaitinfo(&sigs,&info)) != SIGTERM )

             std::cout << "Signal " << info.si_signo << 
                          " errno " << info.si_errno << 
                          " code "  << info.si_code << " pid " << info.si_pid <<
    " uid " << info.si_uid <<
    " val "  << info.si_value.sival_int << std::endl;;


     for(; ; ){
       ServerSocket::socket_descriptor descriptor = sockListener.acceptSocket();
       if (descriptor.isValid() == true){
          std::cout << "received socket connection " << descriptor.descriptor_ << std::endl;
          
	  client.setSocketDescriptor(descriptor);

        fcntl(descriptor.descriptor_,F_SETFL,O_RDWR | O_NONBLOCK | O_ASYNC);
        fcntl(descriptor.descriptor_,F_SETOWN,getpid());
        fcntl(descriptor.descriptor_,F_SETSIG,SIGRTMIN);
          
      int id = 0;

        int i;
         while ( (i = sigwaitinfo(&sigs,&info)) != SIGTERM )
         {
           if ( i < 0 ) 
           {
             perror("sigwaitinfo");
           }
           else
           {
             std::cout << "Signal " << i <<  
                          " signo " << info.si_signo << 
                          " errno " << info.si_errno << 
                          " code "  << info.si_code;


              if ( info.si_code > 0 )
              {  std::cout << 
                          " band "  << info.si_band << 
                          " fd "  << info.si_fd;
              }
              else
              {
                std::cout << "pid" << info.si_pid <<
                              "uid" << info.si_uid <<
                              " val "  << info.si_value.sival_int << std::endl;
              }

              int z = read(descriptor.descriptor_,&buffer,10);
              if ( z > 0 )
              {
                std::cout << "Read " << z << "bytes" << std::endl;
                sigval data;
                data.sival_int = ++id;
                sigqueue(getpid(),SIGUSR1,data);
              }
              else if ( z == 0 )
              {
                std::cout << "Nothing Read" << std::endl;
              }
              else
              {
                 perror("read");
              }
           }

         }












       }
       else{
          // Error processing. 
	  // The accept call returned a socket_descriptor with a -1 value. Therefore
	  // can use errno to determine the kind of error and the subsequent processing
	  // to undertake.
	  throw SKT::SocketIOException("socket accept failed",errno);
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
