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

#ifndef Events_TerminatorServiceContext_HH
#define Events_TerminatorServiceContext_HH

#include <string>
#include <vector>

namespace SWA {
class StackFrame;
class DomainMetaData;
}

namespace EVENTS {

class ParameterContext;
class TerminatorServiceContext
{
   public:
      TerminatorServiceContext(const ::SWA::StackFrame& frame, const std::size_t frameLevel, const ::SWA::DomainMetaData& domain);
     ~TerminatorServiceContext( );

      int getDomainId   () const ;
      int getServiceId  () const ;

      const bool        isMulti()            const;
      const std::string getDomainName()      const;
      const std::string getKeyLetters()      const;

      const std::string getServiceName()     const;
      const std::string getStackFrameLevel() const;

   private:
      const SWA::StackFrame&     frame;
      const std::size_t          frameLevel;
      const SWA::DomainMetaData& domain;
};

} // end EVENTS namespace

#endif
