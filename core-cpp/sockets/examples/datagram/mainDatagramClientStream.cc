//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include <string>
#include <iostream>

#include <stdio.h>
#include <unistd.h>

#include "sockets/sockets.hh"

//**************************************************************************
//**************************************************************************
//**************************************************************************
void usage() {
   std::cout << "mainDatagramClientStream : " << std::endl;
   std::cout << "   -h host [default : INADDR_ANY]" << std::endl;
   std::cout << "   -p port [default : 0]"          << std::endl;
}

int main (int argc, char *argv[])
{
  usage();
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
	        std::cout << "mainDatagramClientStream - invalid argument" << std::endl;
		usage();
		exit(1);
		break;
	  }
     }// end while
     std::cout << "Using host : '" << host  << "'" << std::endl;
     std::cout << "Using port : '" << port  << "'" << std::endl;

     SKT::BufferedInternetDatagramClientStream client;
     client.connect(host,port);
     
     std::string line;
     while (std::getline(std::cin, line)){
	    if (line.empty() == false){         // Do not want to send a message of zero bytes 
	        client << line << std::endl;    // as this is used to detect connection termination.
	    
	       // A datagram socket may still remain connected even though the 
	       // receiving side is not available. Therefore clear the error condition
	       // of the stream and keep attempting to send.
	       client.clear();
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
