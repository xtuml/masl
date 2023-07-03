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

#ifndef Sql_AssignerStateImpl_HH
#define Sql_AssignerStateImpl_HH

#include <string>
#include <vector>
#include <stdint.h>

namespace SQL {

class AssignerStateImpl
{
   protected:
       AssignerStateImpl(){}

   public:
      virtual ~AssignerStateImpl(){}

       // ********************************************************************
       //! Initialise the assigner state mapper. 
       //!
       //! throws SqlException on database access error
       //!
       // ********************************************************************
       virtual std::vector< std::pair<std::string,int32_t> > initialise() = 0;

       // ********************************************************************
       //! Executes an SQL UPDATE statement to store the key value pair in the 
       //! assigner state table.
       //!
       //! throws SqlException on database access error
       //!
       // ********************************************************************
       virtual void updateState(const std::string& objectKey, const int32_t currentState) = 0;

       // ********************************************************************
       //! Executes an SQL INSERT statement to store the key value pair in the 
       //! assigner state table.
       //!
       //! throws SqlException on database access error
       //!
       // ********************************************************************
       virtual void insertState(const std::string& objectKey, const int32_t currentState) = 0;

   private:
      AssignerStateImpl(const AssignerStateImpl& rhs);
      AssignerStateImpl& operator=(const AssignerStateImpl& rhs);
};


}  // end namespace SQL

#endif
