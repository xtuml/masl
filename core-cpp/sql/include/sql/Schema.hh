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

#ifndef Sql_Schema_HH
#define Sql_Schema_HH

#include <map>
#include <string>

namespace SQL {

// *****************************************************************
//! \brief A class to hold the schema to be used by the database.
//!
//! This is a singleton class that is dynamically populated with
//! the SQL required to create source tables and enable them to
//! be dropped.  
// *****************************************************************
class Database; 
class Schema 
{
   public:
       typedef std::map<std::string, std::string> TableDefinitionType;

   public:

       // *****************************************************************
       //! Static accessor method used to return the current schema Object. 
       //! Creates the Schema object if it has not already been created.
       //! @return The active Schema object.
       // *****************************************************************
       static Schema& singleton();

       // *****************************************************************
       //! @return the list of table definitions.
       // *****************************************************************
       const TableDefinitionType&  getTableDefinitions() const { return tableDefinitions_; }

       // *****************************************************************
       //! This method is used to register each table definition to be used 
       //! by the schema for the current set of current components. 
       //! 
       //! \param iTableName       the name of the soruec table.
       //! \param iTableDefinition the definition of the table.
       // *****************************************************************
       bool registerTable(const std::string& iTableName, const std::string& iTableDefinition);

       // *****************************************************************
       //! This method is used to deregister the create table statement 
       //! for the specified table. (Used mainly in the test suite to reset
       //! the schema class between tests)
       //!
       //! \param iTableName       the name of the soruec table.
       //! \param iTableDefinition the definition of the table.
       // *****************************************************************
       bool deregisterTable(const std::string& iTableName);

       // *****************************************************************
       //! \returns a string containing all the create table statements
       // *****************************************************************
       std::string getSchema();
 
       // *****************************************************************
       //! \returns a string containing all the drop table statements
       // *****************************************************************
       std::string dropSchema();


    private:
         Schema();
        ~Schema();
         
         // Prevent copy and assignment
         Schema(const Schema& rhs);
         Schema& operator=(const Schema& rhs);

    private:
        TableDefinitionType  tableDefinitions_;
};

}  // end namespace SQL

#endif
