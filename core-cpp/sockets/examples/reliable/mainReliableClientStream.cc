//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include <unistd.h>
#include <sstream>

#include <stdio.h>
#include <unistd.h>

#include "sockets/sockets.hh"

// ****************************************************************
// ****************************************************************
// ****************************************************************
void sigPipeHandler(int)
{
    std::cout << "sigPipeHandler" << std::endl;
}

//**************************************************************************
//**************************************************************************
//**************************************************************************
void usage() {
   std::cout << "mainReliableClient  : " << std::endl;
   std::cout << "   -h host [default : INADDR_ANY]" << std::endl;
   std::cout << "   -p port [default : 0]"          << std::endl;
}

int main (int argc, char *argv[])
{
  SKT::Logger::Instance().initialise();
  
  try {
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
      
     SKT::BufferedInternetReliableClient client(host,port);
     
     // install required signal handlers
     client.setSigPipeHandler(&sigPipeHandler);
  
     // set any socket options
     client.setOption(SKT::SoReuseAddr(SKT::enable));
     client.setOption(SKT::TcpNoDelay(SKT::enable));
     
     // Connect to the required destination host and port.
     while (client.isConnected() == false){
         std::cout << " client not connect " << std::endl;
	 client.connect();
	 sleep(2);
     }	 
     
     // echo data from the console to the connected socket.
     std::string inputLine;
     while (std::getline(std::cin,inputLine) ){
         if (inputLine.empty() == false){      // do not send empty lines
             client << inputLine << std::endl;
	     if (!client){
	        // client connection failed. Attempt a re-connect
                while (client.isConnected() == false){
                   std::cout << " client not connect " << std::endl;
	           client.connect();
	           sleep(2);
                }	 
	     }
         }
     }
  }
  catch(const SKT::SocketIOException& sioe){
     std::cout << "Caught unexpected SocketException : " << sioe.report() <<  " reason : " << sioe.reason() << std::endl;
  }
  catch(const SKT::SocketException& se){
     std::cout << "Caught unexpected SocketException : " << se.report() <<  std::endl;
  }
  catch(const std::exception& e){
     std::cout << "Caught unexpected C++ exception : " << e.what() << std::endl;
  }
  catch(...){
     std::cout << "Caught unexpected and unknown exception " << std::endl;
  }
  return 0;
}
