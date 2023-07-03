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

#ifndef SWA_Exception_HH
#define SWA_Exception_HH

#include <unistd.h>
#include <string>
#include <sstream>
#include <exception>

#include "boost/tuple/tuple.hpp"
#include "boost/tuple/tuple_io.hpp"

#include "Stack.hh"

namespace SWA
{
  class ExceptionStackFrame
  {
    public:
      ExceptionStackFrame ( const StackFrame& source );

      StackFrame::ActionType getType() const { return type; }
      int getLine() const { return line; }
      int getDomainId() const { return domainId; }
      int getObjectId() const { return objectId; }
      int getActionId() const { return actionId; }

    private:
      StackFrame::ActionType type;
      int domainId;
      int objectId;
      int actionId;
      int line;

  };

  // *****************************************************************
  // *****************************************************************
  template <class T>
  inline std::string streamTuple(const T& tuple)
  {
      std::ostringstream textStream;
      textStream << ::boost::tuples::set_open(' ') << ::boost::tuples::set_close(' ') << ::boost::tuples::set_delimiter(' ') << tuple;
      return textStream.str();
  }

  // *****************************************************************
  // *****************************************************************
  class Exception : public std::exception
  {
    private:
      class ExceptionStack
      {
        public:
          ExceptionStack()
          {
            const std::vector<StackFrame>& stack = Stack::getInstance().getStackFrames();
            frames.insert ( frames.begin(),stack.begin(), stack.end() );
          }
          const std::vector<ExceptionStackFrame>& getFrames() const { return frames; }

        private:
          std::vector<ExceptionStackFrame> frames;
      };

    public:
      Exception () : error(),stackAdded(false) { abortException(); ProcessMonitor::getInstance().exceptionRaised(error); }
      Exception ( const std::string& error ) : error(error),stackAdded(false) {  abortException(); ProcessMonitor::getInstance().exceptionRaised(error);}

      template <class T>
      Exception (const std::string& exception, const T& tuple) : error(exception + streamTuple(tuple)),stackAdded(false) { abortException(); ProcessMonitor::getInstance().exceptionRaised(error);}

      virtual ~Exception() throw() {}

      virtual const char* what() const throw() { if (!stackAdded ) addStack(); return error.c_str(); }

      void addStack() const;

    private:
        void abortException() { if (getenv("MASL_ABORT_ON_EXCEPTION") != 0) { abort(); } }

    private:
      mutable std::string error;
      mutable bool stackAdded;
      ExceptionStack stack;
  };




}

#endif
