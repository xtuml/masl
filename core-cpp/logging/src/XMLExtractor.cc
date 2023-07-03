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

#include "XMLExtractor.hh"
#include "logging/Logging.hh"

#include <boost/lexical_cast.hpp>
#include <boost/algorithm/string/replace.hpp>

#include <iostream>
#include <stdexcept>

#include <unistd.h>
#include <sys/param.h>

#include "FilterChannel.hh"
#include <Poco/Logger.h>
#include <Poco/LoggingRegistry.h>
#include <Poco/ConsoleChannel.h>
#include <Poco/SyslogChannel.h>
#include <Poco/SplitterChannel.h>
#include <Poco/AsyncChannel.h>
#include <Poco/PatternFormatter.h>
#include <Poco/FormattingChannel.h>
#include <Poco/FileChannel.h>

#include <Poco/SAX/InputSource.h>
#include <Poco/DOM/DOMParser.h>
#include <Poco/DOM/Document.h>
#include <Poco/DOM/Element.h>
#include <Poco/DOM/Node.h>
#include <Poco/DOM/Text.h>
#include <Poco/DOM/Attr.h>
#include <Poco/DOM/AutoPtr.h>
#include <Poco/Exception.h>


namespace Logging
{

 XMLExtractor::XMLExtractor ( std::istream& source, const std::string& cmd, const std::string& name, const Lookup& params )
 {
    initSubsLookup(cmd,name,params);

    Poco::LoggingRegistry::defaultRegistry().registerChannel("stdout",new Poco::ConsoleChannel(std::cout));
    Poco::LoggingRegistry::defaultRegistry().registerChannel("stderr",new Poco::ConsoleChannel(std::cerr));

    Poco::XML::DOMParser parser;

    try
    {
      Poco::XML::InputSource xmlSource(source);

      Poco::XML::AutoPtr<Poco::XML::Document> document = parser.parse(&xmlSource);
    
      parseDefaultLogger(document->documentElement());  
      parseSyslogConfig(document->documentElement());  

      for ( const Poco::XML::Node* node = document->documentElement()->firstChild(); node != 0; node = node->nextSibling() )
      {
        if ( const Poco::XML::Element* element = dynamic_cast<const Poco::XML::Element*>(node) )
        {
          if ( element->tagName() == "pattern" )
          {
            parsePatternDef(element);
          }
          else if ( element->tagName() == "logger" )
          {
            parseLoggerDef(element);
          }
        }      
      } 
    }
    catch ( Poco::Exception& e )
    {
      std::cerr << e.displayText() << std::endl;
    } 


  }

  void XMLExtractor::parseDefaultLogger ( const Poco::XML::Element* parent )
  {
    if ( Poco::XML::Element* defaultElt = parent->getChildElement("default") )
    {
      if ( const Poco::XML::Attr* defaultName = defaultElt->getAttributeNode("name") )
      {
        Logger::getInstance().setDefaultLog(defaultName->getValue());
      }
    }
    else
    {
      if ( Poco::XML::Element* firstLogger = parent->getChildElement("logger") )
      {
        if ( Poco::XML::Element* loggerName = firstLogger->getChildElement("name") )
        {
          Logger::getInstance().setDefaultLog(loggerName->innerText());
        }
      }
    }
  }

  void XMLExtractor::parsePatternDef ( const Poco::XML::Element* element )
  {
    if ( const Poco::XML::Attr* patternId = element->getAttributeNode("id") )
    {
      std::string id = patternId->getValue();
      std::string pattern = parseSubstitutionText(element,true);

      Poco::LoggingRegistry::defaultRegistry().registerFormatter(id,new Poco::PatternFormatter(pattern));
    }

  }

  int XMLExtractor::parseLevel ( const std::string& level )
  {
    if ( level == "fatal" )       return Poco::Message::PRIO_FATAL;
    if ( level == "critical" )    return Poco::Message::PRIO_CRITICAL;
    if ( level == "error" )       return Poco::Message::PRIO_ERROR;
    if ( level == "warning" )     return Poco::Message::PRIO_WARNING;
    if ( level == "notice" )      return Poco::Message::PRIO_NOTICE;
    if ( level == "information" || level == "info" ) return Poco::Message::PRIO_INFORMATION;
    if ( level == "debug" )       return Poco::Message::PRIO_DEBUG;
    if ( level == "trace" )       return Poco::Message::PRIO_TRACE;
      
    throw std::runtime_error( "Invalid log level: " + level );    
  }

  int XMLExtractor::getMinLevel ( const Poco::XML::Element* parent )
  {
    if ( Poco::XML::Element* levelElt = parent->getChildElement("minlevel") )
    {
      return parseLevel(levelElt->innerText());
    }
    return Poco::Message::PRIO_TRACE;
  }

  int XMLExtractor::getMaxLevel ( const Poco::XML::Element* parent )
  {
    if ( Poco::XML::Element* levelElt = parent->getChildElement("maxlevel") )
    {
      return parseLevel(levelElt->innerText());
    }
    return Poco::Message::PRIO_FATAL;
  }


  void XMLExtractor::initSubsLookup( const std::string& cmd, const std::string& name, const Lookup& params )
  {
    char hostname[MAXHOSTNAMELEN+1];
    if ( gethostname(hostname, MAXHOSTNAMELEN+1) ) throw std::runtime_error("Could not get hostname");

    subsLookup["cmd"] = cmd;
    subsLookup["name"] = name;
    subsLookup["pid"] = boost::lexical_cast<std::string>(getpid());
    subsLookup["host"] = hostname;

    subsAtt          ["param"                 ] = "name";
    for ( Lookup::const_iterator it = params.begin(); it != params.end(); ++it )
    {
      subsLookup["param/" + it->first] = it->second;
    }

    patternSubsLookup = subsLookup;

    patternSubsLookup["message"               ] = "%t";
    patternSubsLookup["logger"                ] = "%s";

    subsAtt          ["priority"               ] = "format"; 
    patternSubsLookup["priority"               ] = "%p";     
    patternSubsLookup["priority/full"          ] = "%p";     
    patternSubsLookup["priority/abbreviated"   ] = "%q"; 
    patternSubsLookup["priority/number"        ] = "%l"; 

    subsAtt          ["timestamp"             ] = "decimal";
    patternSubsLookup["timestamp"             ] = "%Y-%m-%dT%H:%M:%S.%F%z";
    patternSubsLookup["timestamp/micros"      ] = "%Y-%m-%dT%H:%M:%S.%F%z";
    patternSubsLookup["timestamp/millis"      ] = "%Y-%m-%dT%H:%M:%S.%i%z";
    patternSubsLookup["timestamp/tenths"      ] = "%Y-%m-%dT%H:%M:%S.%c%z";
    patternSubsLookup["timestamp/none"        ] = "%Y-%m-%dT%H:%M:%S%z";

    patternSubsLookup["year"                  ] = "%Y";

    subsAtt          ["month"                 ] = "pad";
    patternSubsLookup["month"                 ] = "%m";
    patternSubsLookup["month/zero"            ] = "%m";
    patternSubsLookup["month/none"            ] = "%n";
    patternSubsLookup["month/space"           ] = "%o";

    subsAtt          ["monthname"             ] = "format";
    patternSubsLookup["monthname"             ] = "%B";
    patternSubsLookup["monthname/full"        ] = "%B";
    patternSubsLookup["monthname/abbreviated" ] = "%b";

    subsAtt          ["day"                   ] = "pad";
    patternSubsLookup["day"                   ] = "%d";
    patternSubsLookup["day/zero"              ] = "%d";
    patternSubsLookup["day/none"              ] = "%e";
    patternSubsLookup["day/space"             ] = "%f";

    subsAtt          ["dayname"               ] = "format"; 
    patternSubsLookup["dayname"               ] = "%W";     
    patternSubsLookup["dayname/full"          ] = "%W";     
    patternSubsLookup["dayname/abbreviated"   ] = "%w"; 

    subsAtt          ["hour"                  ] = "clock"; 
    patternSubsLookup["hour"                  ] = "%H";
    patternSubsLookup["hour/24"               ] = "%H";
    patternSubsLookup["hour/12"               ] = "%h";
    
    subsAtt          ["am-pm"                 ] = "case"; 
    patternSubsLookup["am-pm"                 ] = "%A";
    patternSubsLookup["am-pm/upper"           ] = "%A";
    patternSubsLookup["am-pm/lower"           ] = "%a";

    patternSubsLookup["minute"                ] = "%M";

    subsAtt          ["second"                ] = "decimal";
    patternSubsLookup["second"                ] = "%S.%F";
    patternSubsLookup["second/micros"         ] = "%S.%F";
    patternSubsLookup["second/millis"         ] = "%S.%i";
    patternSubsLookup["second/tenths"         ] = "%S.%c";
    patternSubsLookup["second/none"           ] = "%S";
    
    subsAtt          ["timezone"              ] = "format";
    patternSubsLookup["timezone"              ] = "%z";
    patternSubsLookup["timezone/iso8601"      ] = "%z";
    patternSubsLookup["timezone/rfc"          ] = "%Z";


  }


  std::string XMLExtractor::parseSubstitutionText ( const Poco::XML::Element* parent, bool isPattern )
  {
    std::string result;
    for ( const Poco::XML::Node* node = parent->firstChild(); node != 0; node = node->nextSibling() )
    {
      if ( const Poco::XML::Text* text = dynamic_cast<const Poco::XML::Text*>(node) )
      {
        std::string inner = text->innerText();
        if ( isPattern ) boost::algorithm::replace_all(inner,"%","%%");
        result += text->innerText();
      }
      else if ( const Poco::XML::Element* element = dynamic_cast<const Poco::XML::Element*>(node) )
      {

        const Lookup& lookup = isPattern?patternSubsLookup:subsLookup;
        std::string tag = element->tagName();

        Lookup::const_iterator found = lookup.end();

        if ( subsAtt.count(tag) )
        {
          if ( const Poco::XML::Attr* att = element->getAttributeNode(subsAtt[tag]) )
          {
             found = lookup.find(tag + "/" + att->getValue());
          }
        }
        
        if ( found == lookup.end() )
        {
          found = lookup.find(tag);
        }

        if ( found != lookup.end() )
        {
          result += found->second;
        }
        else if ( tag == "env" )
        {
          if ( const Poco::XML::Attr* envName = element->getAttributeNode("name") )
          {
            const char* envValue = getenv(envName->getValue().c_str());
            if ( envValue )
            {
              result+= envValue;
            }
            else if ( const Poco::XML::Attr* defaultValue = element->getAttributeNode("default") )
            {
              result += defaultValue->getValue();
            }
          }
        }
      }      
    } 
    return result;

  }

  void XMLExtractor::parseLoggerDef ( const Poco::XML::Element* root )
  {
    if ( Poco::XML::Element* nameElt = root->getChildElement("name") )
    {
      Poco::Logger& logger = Poco::Logger::get(nameElt->innerText());

      if ( Poco::XML::Element* levelElt = root->getChildElement("level") )
      {
        logger.setLevel(parseLevel(levelElt->innerText()));
      }
      Poco::Channel* childChannels = getChildChannels(root);   
      if ( childChannels )
      {
        logger.setChannel(childChannels);
      }
    }

  }

  Poco::Channel* XMLExtractor::getFileChannel ( const Poco::XML::Element* element )
  {
    Poco::FileChannel* fileChannel = 0;
    if ( const Poco::XML::Element* pathElt = element->getChildElement("path") )
    {
      fileChannel = new Poco::FileChannel(parseSubstitutionText(pathElt,false));
    }

    if ( const Poco::XML::Element* archiveElt = element->getChildElement("archive") )
    {
      if ( const Poco::XML::Element* rotateElt = archiveElt->getChildElement("rotate") )
      {
        if ( const Poco::XML::Element* timedElt = rotateElt->getChildElement("timed") )
        {
          fileChannel->setProperty(Poco::FileChannel::PROP_ROTATION,timedElt->innerText());
        }

        if ( const Poco::XML::Element* periodicElt = rotateElt->getChildElement("periodic") )
        {
          std::string period = periodicElt->innerText() + " ";
          if ( const Poco::XML::Attr* units = periodicElt->getAttributeNode("units") )
          {
            fileChannel->setProperty(Poco::FileChannel::PROP_ROTATION,periodicElt->innerText() + " " + units->getValue());
          }
        }

        if ( const Poco::XML::Element* periodicElt = rotateElt->getChildElement("size") )
        {
          if ( const Poco::XML::Attr* units = periodicElt->getAttributeNode("units") )
          {
            if ( units->getValue() == "KiB" )
            {
              fileChannel->setProperty(Poco::FileChannel::PROP_ROTATION,periodicElt->innerText() + "K" );
            }
            else if ( units->getValue() == "MiB" )
            {
              fileChannel->setProperty(Poco::FileChannel::PROP_ROTATION,periodicElt->innerText() + "M" );
            }
            else
            {
              fileChannel->setProperty(Poco::FileChannel::PROP_ROTATION,periodicElt->innerText());
            }
          }
        }

        if ( const Poco::XML::Element* extensionElt = archiveElt->getChildElement("extension") )
        {
          fileChannel->setProperty(Poco::FileChannel::PROP_ARCHIVE,extensionElt->innerText());
        }

        if ( const Poco::XML::Element* compressElt = archiveElt->getChildElement("compress") )
        {
          fileChannel->setProperty(Poco::FileChannel::PROP_COMPRESS,compressElt->innerText());
        }

        if ( const Poco::XML::Element* purgeElt = archiveElt->getChildElement("purge") )
        {
          if ( const Poco::XML::Attr* units = purgeElt->getAttributeNode("units") )
          {
            if ( units->getValue() == "count" )
            {
              fileChannel->setProperty(Poco::FileChannel::PROP_PURGECOUNT,purgeElt->innerText());
            }
            else
            {
              fileChannel->setProperty(Poco::FileChannel::PROP_PURGEAGE,purgeElt->innerText() + " " + units->getValue());
            }
          }
        }
      }
    }
    return fileChannel;
  }

  Poco::Channel* XMLExtractor::getFormattingChannel ( const Poco::XML::Element* element )
  {
    Poco::FormattingChannel* formattingChannel = 0;

    if ( const Poco::XML::Element* pattElt = element->getChildElement("pattern") )
    {
      Poco::Formatter* patternFormatter = 0;
      if ( const Poco::XML::Attr* refName = pattElt->getAttributeNode("ref") )
      {
        patternFormatter = Poco::LoggingRegistry::defaultRegistry().formatterForName(refName->getValue());
      }
      else
      {
        patternFormatter = new Poco::PatternFormatter(parseSubstitutionText(pattElt,true));
      }
      formattingChannel = new Poco::FormattingChannel(patternFormatter,getChildChannels(element));
    }
    return formattingChannel;
  }

  Poco::Channel* XMLExtractor::getAsyncChannel ( const Poco::XML::Element* element )
  {
    return new Poco::AsyncChannel ( getChildChannels ( element ) );
  }

  Poco::Channel* XMLExtractor::getFilterChannel ( const Poco::XML::Element* element )
  {
    return new FilterChannel ( PriorityFilter(getMinLevel(element),getMaxLevel(element)), getChildChannels(element) );

  }

  Poco::Channel* XMLExtractor::getLoggerChannel ( const Poco::XML::Element* element )
  {
    Poco::Logger* loggerChannel = 0;

    if ( const Poco::XML::Attr* refName = element->getAttributeNode("ref") )
    {
      loggerChannel = Poco::Logger::has ( refName->getValue() );
    }

    return loggerChannel;
  }


  Poco::Channel* XMLExtractor::getChildChannels ( const Poco::XML::Element* parent )
  {
    Poco::Channel* result = 0;
    Poco::SplitterChannel* splitter = 0;
    for ( const Poco::XML::Node* node = parent->firstChild(); node != 0; node = node->nextSibling() )
    {
      if ( const Poco::XML::Element* element = dynamic_cast<const Poco::XML::Element*>(node) )
      {
        Poco::Channel* curChannel = 0;

        if ( element->tagName() == "stderr" )
        {
          curChannel = Poco::LoggingRegistry::defaultRegistry().channelForName("stderr");
        }
        else if ( element->tagName() == "stdout" )
        {
          curChannel = Poco::LoggingRegistry::defaultRegistry().channelForName("stdout");
        } 
        else if ( element->tagName() == "syslog" )
        {
          curChannel = Poco::LoggingRegistry::defaultRegistry().channelForName("syslog");
        } 
        else if ( element->tagName() == "async" )
        {
          curChannel = getAsyncChannel(element);
        } 
        else if ( element->tagName() == "filter" )
        {
          curChannel = getFilterChannel(element);
        } 
        else if ( element->tagName() == "logger" )
        {
          curChannel = getLoggerChannel(element);
        }
        else if ( element->tagName() == "format" )
        {
          curChannel = getFormattingChannel(element);
        }
        else if ( element->tagName() == "file" )
        {
          curChannel = getFileChannel(element);
        }
      
        if ( curChannel )
        {
          if ( splitter )
          {
            splitter->addChannel(curChannel);
          }
          else if ( result )
          {
            splitter = new Poco::SplitterChannel();
            splitter->addChannel(result);
            splitter->addChannel(curChannel);
            result = splitter;
          }
          else
          {
            result = curChannel;
          }
        }
      }
    }
    return result;
  }

  void XMLExtractor::parseSyslogConfig ( const Poco::XML::Element* parent )
  {
    int facility = Poco::SyslogChannel::SYSLOG_USER;
    int options  = Poco::SyslogChannel::SYSLOG_CONS;
    std::string ident;

    if ( const Poco::XML::Element* configElt = parent->getChildElement("syslog") )
    {
      facility = getFacility(configElt);
      options  = getOptions(configElt);

      if ( const Poco::XML::Element* identElt = configElt->getChildElement("ident") )
      {
        ident = parseSubstitutionText(identElt,false);
      }
    }
    Poco::LoggingRegistry::defaultRegistry().registerChannel("syslog",new Poco::SyslogChannel(ident,facility,options));

  }

  int XMLExtractor::getOptions ( const Poco::XML::Element* parent )
  {
    int options = 0;

    if ( parent->getChildElement("console") )
    {
      options |= Poco::SyslogChannel::SYSLOG_CONS;
    }

    if ( parent->getChildElement("ndelay") )
    {
      options |= Poco::SyslogChannel::SYSLOG_NDELAY;
    }

    if ( parent->getChildElement("perror") )
    {
      options |= Poco::SyslogChannel::SYSLOG_PERROR;
    }

    if ( parent->getChildElement("pid") )
    {
      options |= Poco::SyslogChannel::SYSLOG_PID;
    }
    return options;
  }

  int XMLExtractor::getFacility ( const Poco::XML::Element* parent )
  {
    if ( Poco::XML::Element* facilityElt = parent->getChildElement("facility") )
    {
      std::string facility = facilityElt->innerText();

      if ( facility == "auth"     ) return Poco::SyslogChannel::SYSLOG_AUTH    ;
      if ( facility == "authpriv" ) return Poco::SyslogChannel::SYSLOG_AUTHPRIV;
      if ( facility == "cron"     ) return Poco::SyslogChannel::SYSLOG_CRON    ;
      if ( facility == "daemon"   ) return Poco::SyslogChannel::SYSLOG_DAEMON  ;
      if ( facility == "ftp"      ) return Poco::SyslogChannel::SYSLOG_FTP     ;
      if ( facility == "kern"     ) return Poco::SyslogChannel::SYSLOG_KERN    ;
      if ( facility == "local0"   ) return Poco::SyslogChannel::SYSLOG_LOCAL0  ;
      if ( facility == "local1"   ) return Poco::SyslogChannel::SYSLOG_LOCAL1  ;
      if ( facility == "local2"   ) return Poco::SyslogChannel::SYSLOG_LOCAL2  ;
      if ( facility == "local3"   ) return Poco::SyslogChannel::SYSLOG_LOCAL3  ;
      if ( facility == "local4"   ) return Poco::SyslogChannel::SYSLOG_LOCAL4  ;
      if ( facility == "local5"   ) return Poco::SyslogChannel::SYSLOG_LOCAL5  ;
      if ( facility == "local6"   ) return Poco::SyslogChannel::SYSLOG_LOCAL6  ;
      if ( facility == "local7"   ) return Poco::SyslogChannel::SYSLOG_LOCAL7  ;
      if ( facility == "lpr"      ) return Poco::SyslogChannel::SYSLOG_LPR     ;
      if ( facility == "mail"     ) return Poco::SyslogChannel::SYSLOG_MAIL    ;
      if ( facility == "news"     ) return Poco::SyslogChannel::SYSLOG_NEWS    ;
      if ( facility == "syslog"   ) return Poco::SyslogChannel::SYSLOG_SYSLOG  ;
      if ( facility == "user"     ) return Poco::SyslogChannel::SYSLOG_USER    ;
      if ( facility == "uucp"     ) return Poco::SyslogChannel::SYSLOG_UUCP    ;
    }
    return Poco::SyslogChannel::SYSLOG_USER;
  }


}
