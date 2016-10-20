//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "Logging.hh"

#include <map>
#include <fstream>

int main ( int argc, char** argv )
{
  std::ifstream file(argv[1]);
  if ( file )
  {
    std::map<std::string,std::string> params;
    params["a"] = "AAA";
    params["bb"] = "BBB";
    Logging::Logger::getInstance().loadXMLConfiguration(file, argv[0], "MyName", params );

    Logging::fatal       ( "test fatal" );
    Logging::critical    ( "test critical" );
    Logging::error       ( "test error" );
    Logging::warning     ( "test warning" );
    Logging::notice      ( "test notice" );
    Logging::information ( "test information" );
    Logging::debug       ( "test debug" );
    Logging::trace       ( "test trace" );

  }

  sleep(1);

}
