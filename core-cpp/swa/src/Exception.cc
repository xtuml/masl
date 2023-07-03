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

