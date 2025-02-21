/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "swa/Exception.hh"
#include "swa/NameFormatter.hh"

#include <format>

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
      error += std::format("  #{}\t{}\n",depth--, NameFormatter::formatStackFrame(*it));
    }
    try {
      std::rethrow_if_nested(*this);
    } catch (const std::exception& e) {
      error += std::format("Caused by: {}",e.what());
    }
  }

}

