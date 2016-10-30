//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "asn1/BERDecoder.hh"
#include "asn1/BERDecode.hh"
#include "asn1/BERPrettyPrint.hh"

#include <iostream>
#include <fstream>
#include <sstream>
#include <iostream>

void decode ( const std::istream& stream )
{
  std::ostringstream input;
  input << stream.rdbuf();
  std::string str = input.str();

  std::string::const_iterator start = str.begin();
  std::string::const_iterator end = str.end();

  while ( start != end )
  {
    ASN1::BER::Decoder<std::string::const_iterator> decoder(start);
    std::cout << decoder << std::flush;
    start = decoder.end();
    // assume separator char between asn1 encodings
    if ( start != end ) ++start;
    std::cout <<"-------------------------------------------------------------------------------" << std::endl;
  }
}

int main(int argc, char** argv )
{
  std::vector<std::string> args (argv+1,argv+argc);

  std::ifstream infile;

  if ( args.size() == 0 )
  {
    decode(std::cin);  
  }

  for ( std::vector<std::string>::const_iterator it = args.begin(); it != args.end(); ++it )
  {
    if ( *it == "-" ) decode(std::cin);
    else
    {
      std::ifstream file(it->c_str());
      if ( !file )
      {
        std::cerr << *argv << ": File not found:  " << *it << std::endl;
        return 1;
      }
      decode(file);
    }
  }

}
