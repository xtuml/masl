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

#ifndef SWA_Event_HH
#define SWA_Event_HH

#include "types.hh"
#include "Parameter.hh"

#include <vector>

namespace SWA
{
  class Event
  {
    public:
      Event();
      virtual ~Event();

      void setSource ( int objectId, IdType instanceId );
      void setDest ( IdType instanceId );

      bool getHasDest() const { return hasDest; }
      IdType getDestInstanceId() const { return destInstanceId; }

      bool getHasSource() const { return hasSource; }
      int getSourceObjectId() const { return sourceObjectId; }
      IdType getSourceInstanceId() const { return sourceInstanceId; }

      virtual int getDomainId() const = 0;
      virtual int getObjectId() const = 0;
      virtual int getEventId() const = 0;

      virtual void invoke() const = 0;
      virtual std::vector<Parameter> getParameters() const { return parameters; }

   protected:
      template<class T>
      void addParam(const T& param ) { parameters.push_back(Parameter(param)); }

    private:
      bool hasDest;
      IdType destInstanceId;

      bool hasSource;
      int sourceObjectId;
      IdType sourceInstanceId;

      std::vector<Parameter> parameters;


      // Prevent copy, otherwise Parameter pointers will be invalid
      Event ( const Event& );
      Event& operator= ( const Event& );

  };
}

#endif
