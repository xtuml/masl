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
