//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef Logging_XMLExtractor_HH
#define Logging_XMLExtractor_HH

#include <istream>
#include <string>
#include <map>
#include <Poco/DOM/Element.h>
#include <Poco/Channel.h>

namespace Logging
{

  class XMLExtractor
  {
    public:
      typedef std::map<std::string,std::string> Lookup;

      XMLExtractor ( std::istream& source, const std::string& cmd, const std::string& name, const Lookup& params );


    private:
      void parseDefaultLogger ( const Poco::XML::Element* element );
      void parseSyslogConfig  ( const Poco::XML::Element* element );
      void parsePatternDef    ( const Poco::XML::Element* element );
      void parseLoggerDef     ( const Poco::XML::Element* element );

      std::string parseSubstitutionText ( const Poco::XML::Element* parent, bool isPattern );

      int parseLevel ( const std::string& level );
      int getMinLevel ( const Poco::XML::Element* parent );
      int getMaxLevel ( const Poco::XML::Element* parent );
      int getFacility ( const Poco::XML::Element* parent );
      int getOptions  ( const Poco::XML::Element* parent );

      Poco::Channel* getChildChannels     ( const Poco::XML::Element* parent );

      Poco::Channel* getFileChannel       ( const Poco::XML::Element* element );
      Poco::Channel* getLoggerChannel     ( const Poco::XML::Element* element );
      Poco::Channel* getAsyncChannel      ( const Poco::XML::Element* element );
      Poco::Channel* getFilterChannel     ( const Poco::XML::Element* element );
      Poco::Channel* getFormattingChannel ( const Poco::XML::Element* element );

      void initSubsLookup(const std::string& cmd, const std::string& name, const Lookup& params);

    private:
      Lookup subsAtt;
      Lookup subsLookup;
      Lookup patternSubsLookup;
  
  };

}
#endif
