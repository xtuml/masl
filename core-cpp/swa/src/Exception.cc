//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "swa/Exception.hh"
#include "swa/NameFormatter.hh"

#include "boost/lexical_cast.hpp"
namespace SWA
{
  ExceptionStackFrame::ExceptionStackFrame ( const StackFrame& source )
        : type(source.getType()),
          domainId(source.getDomainId()),
          objectId(source.getObjectId()),
          actionId(source.getActionId()),
          line(source.getLine())
  {
  }



  void Exception::addStack() const
  {
    stackAdded = true;
    error+= "\n  Stack:\n";
    int depth = stack.getFrames().size();
    for ( std::vector<ExceptionStackFrame>::const_reverse_iterator it = stack.getFrames().rbegin(), end = stack.getFrames().rend(); it != end; ++it )
    {
      error += "  #" + boost::lexical_cast<std::string>(depth--) + "\t" + NameFormatter::formatStackFrame(*it) + "\n";
    }
    try {
      std::rethrow_if_nested(*this);
    } catch (const std::exception& e) {
      error += "Caused by: " + std::string(e.what());
    }
  }

}

