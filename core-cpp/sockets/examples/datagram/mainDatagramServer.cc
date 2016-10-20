//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include <iostream>
#include <stdio.h>
#include <unistd.h>

#include "sockets/sockets.hh"

//**************************************************************************
//**************************************************************************
//**************************************************************************
void usage() {
   std::cout << "mainDatagramServer  : "            << std::endl;
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
	        std::cout << "mainDatagramServer - invalid argument" << std::endl;
		usage();
		exit(1);
		break;
	  }
     } // end while
     std::cout << "Using host : '" << host  << "'" << std::endl;
     std::cout << "Using port : '" << port  << "'" << std::endl;
     SKT::InternetDatagramServer server(host,port);
     
     const int bufferSize= 1024;
     char buffer[bufferSize];
     for(ssize_t bytesRead = 0; bytesRead != -1;){
         bytesRead = server.recvfromSocket(buffer,bufferSize,0);
	 if (bytesRead > 0){
	     std::cout << std::string(buffer, bytesRead) << std::endl;
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
