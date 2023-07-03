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

#ifndef Sqlite_AssignerStateMapper_HH
#define Sqlite_AssignerStateMapper_HH

#include <map>
#include <string>
#include <stdint.h>

#include "sql/AssignerStateImpl.hh"

namespace SQLITE {

// ********************************************************************
//! @brief Class to store the states associated with assigner state models
//! 
//! The assigner state models have one state model instance per class type 
//! rather than per class instance. Therefore the current state of an assigner
//! state model is stored and accessed via the generated population classes.
//! Rather than creating a seperate table for each assigner state model which
//! would contain a single row detailing the current state information, all the
//! information for every assigner state model within a process is stored in a 
//! single table that uses the object name for the assigner key and a textual 
//! representation of the current state.
// ********************************************************************
class AssignerStateMapper : public ::SQL::AssignerStateImpl
{
   public:
       AssignerStateMapper();
       virtual ~AssignerStateMapper();

   private:        

       // ********************************************************************
       //! Initialise the Assigner State Mapper and return the current set of 
       //! assigner state values from the database.
       //!
       //! throws SqliteException on database access error
       //!
       // ********************************************************************
       std::vector< std::pair<std::string,int32_t> > initialise();

       // ********************************************************************
       //! Executes an SQL UPDATE statement to store the key value pair in the 
       //! assigner state table.
       //!
       //! throws SqliteException on database access error
       //!
       // ********************************************************************
       void updateState(const std::string& objectKey, const int32_t currentState);

       // ********************************************************************
       //! Executes an SQL INSERT statement to store the key value pair in the 
       //! assigner state table.
       //!
       //! throws SqliteException on database access error
       //!
       // ********************************************************************
       void insertState(const std::string& objectKey, const int32_t currentState);

   private:
       void executeStatement(const std::string& statement);

   private:
       AssignerStateMapper(const AssignerStateMapper& rhs);
       AssignerStateMapper& operator=(const AssignerStateMapper& rhs);

};



} // end namespace SQLITE

#endif
