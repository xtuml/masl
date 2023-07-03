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

#include <Poco/SAX/DefaultHandler.h>
#include <Poco/SAX/SAXParser.h>
#include <Poco/SAX/Attributes.h>
#include <Poco/SAX/SAXException.h>
#include <Poco/MD5Engine.h>
#include <Poco/DigestStream.h>
#include <boost/lexical_cast.hpp>
#include <boost/algorithm/string/trim.hpp>
#include <string>
#include <iostream>
#include <iomanip>
#include <fstream>
#include <map>
#include <stdint.h>

class CoverageData
{
  public:
    CoverageData ( const std::string& fileName, const std::string& md5sum ) 
      : fileName(fileName),
        md5sum(md5sum),
        lineCounts()
    {
    }
    
    const std::string& getFileName() const { return fileName; }
    const std::string& getMd5sum() const { return md5sum; }

    void addLineCount ( int line, int count ) { lineCounts[line]+= count; }
    void addReal ( int line, uint64_t duration ) { real[line]+= duration; }
    void addUser ( int line, uint64_t duration ) { user[line]+= duration; }
    void addSystem ( int line, uint64_t duration ) { system[line]+= duration; }

    bool hasLine ( int line ) const {  return lineCounts.find(line) != lineCounts.end(); }
    int getLineCount ( int line ) const {  return lineCounts.find(line)->second; }
    const uint64_t getRealNanos ( int line ) const {  return real.find(line)->second; }
    const uint64_t getUserNanos ( int line ) const {  return user.find(line)->second; }
    const uint64_t getSystemNanos ( int line ) const {  return system.find(line)->second; }

  private:
    std::string fileName;
    std::string md5sum;
    std::map<int,int> lineCounts;
    std::map<int,uint64_t> real;
    std::map<int,uint64_t> user;
    std::map<int,uint64_t> system;

    
};

// DOMAIN is defined by math.h on rhel3u5
#ifdef DOMAIN
#undef DOMAIN
#endif


class Handler : public Poco::XML::DefaultHandler
{
  public:
    Handler() 
      : curDomain(),
        curObject(),
        curAction(),
        count(),
        real(),
        user(),
        system(),
        curLine()
    {
    }

    typedef std::map<std::string,CoverageData> Coverage;
    const Coverage& getCoverage() const { return coverage; }

    const std::string& getReportTime() const { return reportTime; }
    const std::string& getReportDuration() const { return reportDuration; }

  private:
    void characters ( const Poco::XML::XMLChar ch[], int start, int length )
    {
      std::string value(ch+start,ch+start+length);
      if ( curNode == COUNT )
      {
        count += value;
      }
      else if ( curNode == REAL )
      {
        real += value;
      }
      else if ( curNode == USER )
      {
        user += value;
      }
      else if ( curNode == USER )
      {
        system += value;
      }
      else if ( curNode == TIME )
      {
        reportTime += value;
      }
      else if ( curNode == DURATION )
      {
        reportDuration += value;
      }
    }

    void startElement ( const Poco::XML::XMLString& uri, const Poco::XML::XMLString& localName, const Poco::XML::XMLString& qname, const Poco::XML::Attributes& attributes )
    {
      curNode = localName;
      if ( localName == DOMAIN )
      {
        curDomain = attributes.getValue("",NAME) + "::";
      }
      else if ( localName == TERMINATOR )
      {
        curObject = attributes.getValue("",NAME) + "~>";
      }
      else if ( localName == OBJECT )
      {
        curObject = attributes.getValue("",NAME) + ".";
      }
      else if ( localName == SERVICE || localName == STATE )
      {
        std::string name = curDomain + curObject + attributes.getValue("",NAME);
        std::string filename = attributes.getValue("",FILENAME);
        std::string md5sum = attributes.getValue("",MD5SUM);

        curAction = coverage.find(name);
        if ( curAction == coverage.end() )
        {
          curAction = coverage.insert(Coverage::value_type(name,CoverageData(filename,md5sum))).first;
        }
        else
        {
          if ( curAction->second.getFileName() != filename || curAction->second.getMd5sum() != md5sum )
          {
             std::cerr << "Inconsistent XML - filenames or md5sums do not match for " << name << std::endl;
          }
        }            
        
      }
      else if ( localName == STATEMENT )
      {
        curLine = boost::lexical_cast<int>(attributes.getValue("",LINE));
        curAction->second.addLineCount(curLine,0);
      }
    } 

    void endElement ( const Poco::XML::XMLString& uri, const Poco::XML::XMLString& localName, const Poco::XML::XMLString& qname )
    {
      if ( localName == DOMAIN )
      {
        curDomain.clear();
      }
      else if ( localName == TERMINATOR || localName == OBJECT )
      {
        curObject.clear();
      }
      else if ( localName == SERVICE || localName == STATE )
      {
        curAction = coverage.end();
      }
      else if ( localName == COUNT )
      {
        boost::trim(count);
        if ( count.size() > 0 ) curAction->second.addLineCount(curLine,boost::lexical_cast<int>(count));
        count.clear();
      }
      else if ( localName == REAL )
      {
        boost::trim(real);
        if ( real.size() > 0 ) curAction->second.addReal(curLine,boost::lexical_cast<int>(real));
        real.clear();
      }
      else if ( localName == USER )
      {
        boost::trim(user);
        if ( user.size() > 0 ) curAction->second.addUser(curLine,boost::lexical_cast<int>(user));
        user.clear();
      }
      else if ( localName == SYSTEM )
      {
        boost::trim(system);
        if ( system.size() > 0 ) curAction->second.addSystem(curLine,boost::lexical_cast<int>(system));
        system.clear();
      }
      else if ( localName == STATEMENT )
      {  
        curLine = 0;
      }
      else if ( localName == TIME )
      {
        boost::trim(reportTime);
      }
      else if ( localName == DURATION )
      {
        boost::trim(reportDuration);
      }
      curNode = "";
    } 


  private:
    std::string curNode;
    std::string curDomain;
    std::string curObject;
    Coverage::iterator curAction;

    std::string count;
    std::string real;
    std::string user;
    std::string system;
    int curLine;

    Coverage coverage;
    std::string reportTime;
    std::string reportDuration;

    // Element names
    const static std::string DOMAIN;
    const static std::string OBJECT;
    const static std::string TERMINATOR;
    const static std::string SERVICE;
    const static std::string STATE;
    const static std::string STATEMENT;
    const static std::string COUNT;
    const static std::string USER;
    const static std::string REAL;
    const static std::string SYSTEM;
    const static std::string TIME;
    const static std::string DURATION;

    // Attribute names
    const static std::string NAME;
    const static std::string LINE;
    const static std::string MD5SUM;
    const static std::string FILENAME;

};

const std::string Handler::DOMAIN = "domain";
const std::string Handler::OBJECT = "object";
const std::string Handler::TERMINATOR = "terminator";
const std::string Handler::SERVICE = "service";
const std::string Handler::STATE = "state";
const std::string Handler::STATEMENT = "statement";
const std::string Handler::COUNT = "count";
const std::string Handler::USER = "user";
const std::string Handler::REAL = "real";
const std::string Handler::SYSTEM = "system";
const std::string Handler::NAME = "name";
const std::string Handler::LINE = "line";
const std::string Handler::MD5SUM = "md5sum";
const std::string Handler::FILENAME = "filename";
const std::string Handler::TIME = "time";
const std::string Handler::DURATION = "duration";


class TextReport
{
  public:
    TextReport ( const Handler& handler );
    void reportLine ( std::ostream& stream, const CoverageData& data, int lineNo, const std::string& lineText );

    void write ( std::ostream& stream ) const;

  private: 
    std::ostringstream report;

};


void TextReport::reportLine ( std::ostream& stream, const CoverageData& data, int lineNo, const std::string& lineText )
{
  if ( data.hasLine(lineNo) )
  {
    int count = data.getLineCount(lineNo);

    if ( count )
    {
      stream << std::setw(8) << count << " : ";
      int64_t realMicros = data.getRealNanos(lineNo)/1000;
      int64_t userMicros = data.getUserNanos(lineNo)/1000;
      int64_t systemMicros = data.getSystemNanos(lineNo)/1000;
      stream << std::fixed << std::setprecision(3);
      if ( realMicros > 0 )
      {
        stream << std::setw(9) << realMicros/1.0e3/count   << "/" << std::setw(9) << realMicros/1.0e3  << " : ";
      }
      else
      {
        stream << std::setw(9+1+9) << " " << " : ";
      }
      if ( userMicros > 0 )
      {
        stream << std::setw(9) << userMicros/1.0e3/count   << "/" << std::setw(9) << userMicros/1.0e3 << " : ";
      }
      else
      {
        stream << std::setw(9+1+9) << " " << " : ";
      }
      if ( systemMicros > 0 )
      {
        stream << std::setw(9) << systemMicros/1.0e3/count   << "/" << std::setw(9) << systemMicros/1.0e3 << " : ";
      }
      else
      {
        stream << std::setw(9+1+9) << " " << " : ";
      }
    }
    else
    {
      stream << std::setw(8) << "########" << " : ";
      stream << std::setw(9+1+9) << " " << " : ";
      stream << std::setw(9+1+9) << " " << " : ";
      stream << std::setw(9+1+9) << " " << " : ";
    }
  }
  else
  {
    stream << std::setw(8) << "        " << " : ";
    stream << std::setw(9+1+9) << " " << " : ";
    stream << std::setw(9+1+9) << " " << " : ";
    stream << std::setw(9+1+9) << " " << " : ";
  }

  stream << std::setw(5) << lineNo << " : ";
  stream << lineText << "\n";

  
}


TextReport::TextReport( const Handler& handler )
{
  report << "Generated at " << handler.getReportTime() << " sampled over " << boost::lexical_cast<uint64_t>(handler.getReportDuration())/1e9 << " seconds\n";

  const Handler::Coverage& coverage = handler.getCoverage();

  for ( Handler::Coverage::const_iterator it = coverage.begin(), end = coverage.end(); it != end; ++it )
  {
    std::ifstream file ( it->second.getFileName().c_str() );

    Poco::MD5Engine md5summer;
    Poco::DigestInputStream md5Stream(md5summer,file);

    std::string lineText;
    int lineNo = 1;

    int executedLines = 0;
    int significantLines = 0;

    std::ostringstream fileDetail;

    while ( std::getline(md5Stream,lineText) )
    {
      reportLine(fileDetail,it->second,lineNo,lineText);
      if ( it->second.hasLine(lineNo) )
      {
        if ( it->second.getLineCount(lineNo) )
        {
          ++executedLines;
        }
        ++significantLines;
      }
      ++lineNo;
    }
    if ( significantLines )
    {
      double percentage = executedLines * 100.0 / significantLines;
      report << it->first << " : " << std::setprecision(4) << percentage << "%\n";
      report << std::setw(8) << "Count" << " : ";
      report << std::setw(9+1+9) << "Ave/Tot Real (ms)" << " : ";
      report << std::setw(9+1+9) << "Ave/Tot User (ms)" << " : ";
      report << std::setw(9+1+9) << "Ave/Tot System (ms)" << " : ";
      report << std::setw(5) << "Line" << " :\n";

      std::string md5sum = md5summer.digestToHex(md5summer.digest());
      if ( it->second.getMd5sum() != md5sum )
      {
        report << "!!!!!!! File content does not match the one used for coverage analysis !!!!!!\n";
        report << "!!!!!!! Coverage md5 = " << it->second.getMd5sum() <<  "!!!!!!\n";
        report << "!!!!!!! " << it->second.getFileName() << " md5 = " << md5sum <<  "!!!!!!\n";
      }
      report << fileDetail.str();
      report << std::string(80,'-') << "\n";
    }

  }

}

void TextReport::write ( std::ostream& stream ) const
{
  stream << report.str() << std::flush;
}



int main( int argc, char** argv)
{
  std::vector<std::string> xmlFiles (argv+1,argv+argc);

  Handler handler;
  
  Poco::XML::SAXParser parser;
  parser.setContentHandler(&handler);


  for ( std::vector<std::string>::const_iterator it = xmlFiles.begin(), end = xmlFiles.end(); it != end; ++it )
  {
    try
    {
      parser.parse(*it);
    }
    catch ( const Poco::XML::SAXParseException& e )
    {
      std::cerr << *it << " : Error : " << e.message() << std::endl;
    } 
  }

  TextReport report ( handler );
  report.write(std::cout);

}


        
