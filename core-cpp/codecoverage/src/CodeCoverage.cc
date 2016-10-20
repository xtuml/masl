//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "CodeCoverage.hh"
#include "swa/Stack.hh"
#include "swa/NameFormatter.hh"
#include "swa/Process.hh"
#include "swa/Timestamp.hh"
#include "swa/String.hh"
#include "swa/PluginRegistry.hh"
#include "metadata/MetaData.hh"
#include <iostream>
#include <fstream>
#include <Poco/XML/XML.h>
#include <Poco/DOM/Document.h>
#include <Poco/DOM/Element.h>
#include <Poco/DOM/Text.h>
#include <Poco/DOM/DOMWriter.h>
#include <Poco/DOM/Attr.h>
#include <Poco/XML/XMLWriter.h>
#include <Poco/DOM/AutoPtr.h>
#include <Poco/XML/XMLException.h>
#include <boost/lexical_cast.hpp>
#include <boost/bind.hpp>

namespace
{
  bool registered = CodeCoverage::CodeCoverage::getInstance().initialise();  

}

namespace CodeCoverage
{

  CodeCoverage::CodeCoverage()
    : active(true),
      startTime(SWA::Timestamp::now()),
      samplingTime(),
      processStack(SWA::Stack::getInstance())
      
  {
  }

  CodeCoverage& CodeCoverage::getInstance()
  {
    static CodeCoverage singleton;
    return singleton;
  }

  bool CodeCoverage::initialise()
  {
    SWA::PluginRegistry::getInstance().registerAction(getName(),"Print Report",boost::bind(&CodeCoverage::printReport,this));
    SWA::PluginRegistry::getInstance().registerPropertySetter(getName(),"Save Report to",boost::bind(&CodeCoverage::saveReport,this,_1));
    SWA::PluginRegistry::getInstance().registerAction(getName(),"Clear Statistics",boost::bind(&CodeCoverage::clearStats,this));
    SWA::PluginRegistry::getInstance().registerFlagSetter(getName(),"Active",boost::bind(&CodeCoverage::setActive,this,_1));
    SWA::PluginRegistry::getInstance().registerFlagGetter(getName(),"Active",boost::bind(&CodeCoverage::isActive,this));


    std::string finalReportFile = SWA::Process::getInstance().getName() + "_code_coverage." + boost::lexical_cast<std::string>(getpid()) + ".xml";
    SWA::Process::getInstance().registerShutdownListener(boost::bind(&CodeCoverage::saveReport,this,finalReportFile));

    registerMonitor();
    connectToMonitor();
    return true;
  }


  void CodeCoverage::startStatement()
  {
    if ( active )
    {
      timeStack.push_back(Time(StackFrame(processStack.top())));
    }
  }

  void CodeCoverage::endStatement()
  {
    if ( active )
    {
      statistics[timeStack.back().getFrame()] += timeStack.back();
      timeStack.pop_back();
    }
  }

  CodeCoverage::StackFrame::StackFrame ( const SWA::StackFrame& frame )
    : type(frame.getType()),
      domain(frame.getDomainId()), 
      object(frame.getObjectId()), 
      action(frame.getActionId()), 
      line(frame.getLine())
  {
  }

  CodeCoverage::StackFrame::StackFrame ( SWA::StackFrame::ActionType type, int domain, int object, int action, int line )
    : type(type),
      domain(domain), 
      object(object), 
      action(action), 
      line(line)
  {
  }

  std::ostream& operator<< ( std::ostream& stream, const CodeCoverage::StackFrame& rhs )
  {
    stream << SWA::NameFormatter::formatStackFrame(rhs.type,rhs.domain,rhs.object,rhs.action);
    if ( rhs.line > 0 ) stream << "-" << rhs.line;
    return stream;
  }

  using namespace Poco::XML;

  void CodeCoverage::addLineXML ( AutoPtr<Element> parent, StackFrame frame ) const
  {
    AutoPtr<Element> lineElement = parent->ownerDocument()->createElement("statement");
    parent->appendChild(lineElement);
    lineElement->setAttribute("line",boost::lexical_cast<std::string>(frame.getLine()));

    Statistics::const_iterator statIt = statistics.find(frame);

    if ( statIt != statistics.end() )
    {
      AutoPtr<Element> countElement = parent->ownerDocument()->createElement("count");
      lineElement->appendChild(countElement);
      AutoPtr<Text> countText = parent->ownerDocument()->createTextNode(boost::lexical_cast<std::string>(statIt->second.getCount()));
      countElement->appendChild(countText);

      AutoPtr<Element> realElement = parent->ownerDocument()->createElement("real");
      lineElement->appendChild(realElement);
      AutoPtr<Text> realText = parent->ownerDocument()->createTextNode(boost::lexical_cast<std::string>(statIt->second.getReal().nanos()));
      realElement->appendChild(realText);

      AutoPtr<Element> userElement = parent->ownerDocument()->createElement("user");
      lineElement->appendChild(userElement);
      AutoPtr<Text> userText = parent->ownerDocument()->createTextNode(boost::lexical_cast<std::string>(statIt->second.getUser().nanos()));
      userElement->appendChild(userText);

      AutoPtr<Element> systemElement = parent->ownerDocument()->createElement("system");
      lineElement->appendChild(systemElement);
      AutoPtr<Text> systemText = parent->ownerDocument()->createTextNode(boost::lexical_cast<std::string>(statIt->second.getSystem().nanos()));
      systemElement->appendChild(systemText);
    }

  } 

  void CodeCoverage::addServiceXML ( AutoPtr<Element> parent, const SWA::ServiceMetaData& service, SWA::StackFrame::ActionType type, int domain, int object ) const
  {
    AutoPtr<Element> serviceElement = parent->ownerDocument()->createElement("service");
    parent->appendChild(serviceElement);
    serviceElement->setAttribute("name",service.getName());
    serviceElement->setAttribute("filename",service.getFileName());
    serviceElement->setAttribute("md5sum",service.getFileHash());

    for ( std::vector<int>::const_iterator it = service.getLines().begin(); it != service.getLines().end(); ++it )
    {
      addLineXML ( serviceElement, StackFrame ( type, domain, object, service.getId(), *it) );
    }
  }

  void CodeCoverage::addStateXML ( AutoPtr<Element> parent, const SWA::StateMetaData& state, int domain, int object ) const
  {
    AutoPtr<Element> stateElement = parent->ownerDocument()->createElement("state");
    parent->appendChild(stateElement);
    stateElement->setAttribute("name",state.getName());
    stateElement->setAttribute("filename",state.getFileName());
    stateElement->setAttribute("md5sum",state.getFileHash());
    for ( std::vector<int>::const_iterator it = state.getLines().begin(); it != state.getLines().end(); ++it )
    {
      addLineXML ( stateElement, StackFrame ( SWA::StackFrame::StateAction, domain, object, state.getId(), *it) );
    }
  }

  void CodeCoverage::writeReport( std::ostream& stream ) const
  {
    AutoPtr<Document> xmlDocument = new Document;

    AutoPtr<Element> processElement = xmlDocument->createElement("process");
    processElement->setAttribute("name",SWA::Process::getInstance().getName());
    xmlDocument->appendChild(processElement);

    AutoPtr<Element> timeElement = xmlDocument->createElement("time");
    processElement->appendChild(timeElement);
    AutoPtr<Text> timeText = xmlDocument->createTextNode(SWA::Timestamp::now().format_iso_ymdhms(SWA::Timestamp::Second,9,true).s_str());
    timeElement->appendChild(timeText);

    AutoPtr<Element> durationElement = xmlDocument->createElement("duration");
    processElement->appendChild(durationElement);
    uint64_t totalTime = samplingTime.nanos();
    if ( active ) 
    {
      totalTime += (SWA::Timestamp::now() - startTime).nanos();
    }
    AutoPtr<Text> durationText = xmlDocument->createTextNode(boost::lexical_cast<std::string>(totalTime));
    durationElement->appendChild(durationText);


    const SWA::Process::DomainList& domains = SWA::Process::getInstance().getDomains();

    const SWA::ProcessMetaData& process = SWA::ProcessMetaData::getProcess();

    for ( SWA::Process::DomainList::const_iterator domIt = domains.begin(), 
          end = domains.end();
          domIt != end; ++domIt )
    {
      AutoPtr<Element> domainElement = xmlDocument->createElement("domain");
      domainElement->setAttribute("name",domIt->getName());
      processElement->appendChild(domainElement);

      const SWA::DomainMetaData& domain = process.getDomain(domIt->getId());

      const std::vector<SWA::ServiceMetaData>& services = domain.getServices();
      for ( std::vector<SWA::ServiceMetaData>::const_iterator srvIt = services.begin(); srvIt != services.end(); ++srvIt )
      {
        addServiceXML ( domainElement, *srvIt, SWA::StackFrame::DomainService, domain.getId(), -1 );
      }

      const std::vector<SWA::TerminatorMetaData>& terms = domain.getTerminators();
      for ( std::vector<SWA::TerminatorMetaData>::const_iterator termIt = terms.begin(); termIt != terms.end(); ++termIt )
      {
        AutoPtr<Element> termElement = xmlDocument->createElement("terminator");
        termElement->setAttribute("name",termIt->getName());
        domainElement->appendChild(termElement);

        const std::vector<SWA::ServiceMetaData>& services = termIt->getServices();
        for ( std::vector<SWA::ServiceMetaData>::const_iterator srvIt = services.begin(); srvIt != services.end(); ++srvIt )
        {
          addServiceXML ( termElement, *srvIt, SWA::StackFrame::TerminatorService, domain.getId(), termIt->getId() );
        }
      }


      const std::vector<SWA::ObjectMetaData>& objects = domain.getObjects();
      for ( std::vector<SWA::ObjectMetaData>::const_iterator objIt = objects.begin(); objIt != objects.end(); ++objIt )
      {
        AutoPtr<Element> objectElement = xmlDocument->createElement("object");
        objectElement->setAttribute("name",objIt->getName());
        domainElement->appendChild(objectElement);

        const std::vector<SWA::ServiceMetaData>& services = objIt->getServices();
        for ( std::vector<SWA::ServiceMetaData>::const_iterator srvIt = services.begin(); srvIt != services.end(); ++srvIt )
        {
          addServiceXML ( objectElement, *srvIt, SWA::StackFrame::ObjectService, domain.getId(), objIt->getId() );
        }

        const std::vector<SWA::StateMetaData>& states = objIt->getStates();
        for ( std::vector<SWA::StateMetaData>::const_iterator stIt = states.begin(); stIt != states.end(); ++stIt )
        {
          addStateXML ( objectElement, *stIt, domain.getId(), objIt->getId() );
        }

      }

    }

    try
    {
      DOMWriter writer;
      writer.setNewLine("\n");
      writer.setOptions(XMLWriter::PRETTY_PRINT);


      writer.writeNode(stream, xmlDocument);
    }
    catch ( const XMLException& e )
    {
      std::cerr << e.displayText() << std::endl;
    }
  }

  void CodeCoverage::printReport() const
  {
    writeReport(std::cout);
  }

  void CodeCoverage::saveReport ( const std::string& filename ) const
  {
    std::ofstream file (filename.c_str());
    if ( file )
    {
      writeReport(file);
      std::cout << "Code Coverage report saved to " + filename << std::endl;
    }
    else
    {
      std::cout << "Failed to save Code Coverage report to " + filename << std::endl;
    }
  }

  void CodeCoverage::setActive(bool flag)
  {
    if ( active && !flag )
    {
      samplingTime += SWA::Timestamp::now() - startTime;
    }
    else if ( !active && flag )
    {
      startTime = SWA::Timestamp::now();
    }
    active = flag;
  }

  CodeCoverage::~CodeCoverage()
  {
  }

  bool CodeCoverage::StackFrame::operator< ( const StackFrame& rhs ) const
  {

    return ( type    < rhs.type    || ( type == rhs.type && 
           ( domain  < rhs.domain  || ( domain == rhs.domain && 
           ( object  < rhs.object  || ( object == rhs.object && 
           ( action  < rhs.action  || ( action == rhs.action &&
           ( line    < rhs.line ) ) ) ) ) ) ) ) );
  }

  bool CodeCoverage::StackFrame::operator== ( const StackFrame& rhs ) const
  {
    return type == rhs.type &&
           domain == rhs.domain && 
           object == rhs.object && 
           action == rhs.action &&
           line == rhs.line;
  }


  CodeCoverage::Time::Time( const StackFrame& frame )
    : real(SWA::Duration::real()),
      user(SWA::Duration::user()),
      system(SWA::Duration::system()),
      frame(frame)
  {
  }
  
  CodeCoverage::Statistic::Statistic()
    : count(),
      real(),
      user(),
      system()
  {
  }
  
  void CodeCoverage::Statistic::operator+= ( const Time& startTime )
  {
    ++count;
    real   += SWA::Duration::real()   - startTime.getReal();
    user   += SWA::Duration::user()   - startTime.getUser();
    system += SWA::Duration::system() - startTime.getSystem();
  }
}
