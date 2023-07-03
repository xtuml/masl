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

#include <sstream>
#include "sql/Util.hh"
#include "sql/Schema.hh"
#include "sql/AssignerStateImpl.hh"
#include "sql/AssignerStateFactory.hh"

#include "sqlite/Database.hh"
#include "sqlite/Resultset.hh"
#include "sqlite/Exception.hh"
#include "AssignerStateMapper.hh"

#include "boost/shared_ptr.hpp"

//*****************************************************************************
//*****************************************************************************
namespace 
{
  const ::std::string createTableStatment ( )
  {
    return "CREATE TABLE ASSIGNER_STATES(object_key TEXT, current_state TEXT, PRIMARY KEY (object_key));\n";
  }

  bool registerSchema      = ::SQL::Schema::singleton().registerTable( "ASSIGNER_STATES", createTableStatment() );
  bool registerAssignerIml = ::SQL::AssignerStateFactory::singleton().registerImpl(boost::shared_ptr< ::SQL::AssignerStateImpl>(new ::SQLITE::AssignerStateMapper()));
}

namespace SQLITE {

//*****************************************************************************
//*****************************************************************************
AssignerStateMapper::AssignerStateMapper()
{
  
}

//*****************************************************************************
//*****************************************************************************
AssignerStateMapper::~AssignerStateMapper()
{

}

//*****************************************************************************
//*****************************************************************************
std::vector< std::pair<std::string,int32_t> >  AssignerStateMapper::initialise()
{
   std::vector< std::pair<std::string,int32_t> > assignerStates;
   std::string query("SELECT object_key,current_state FROM ASSIGNER_STATES;");
   ResultSet assignerStateResult;
   if (Database::singleton().executeQuery(query,assignerStateResult) == true){
       if (assignerStateResult.getColumns() == 2){
           for(ResultSet::RowType x = 0; x < assignerStateResult.getRows(); ++x){
             const std::string& keyValue   = assignerStateResult.getRow(x)[0];
             const int32_t      stateValue = ::SQL::stringToValue<int32_t>(assignerStateResult.getRow(x)[1]);
             assignerStates.push_back(std::make_pair(keyValue,stateValue));
           }
       }
       else{
         std::string errMsg("Failed to initialise assigner states : incorrect column count");
         throw SqliteException(errMsg);
        }
   }
   else{
     std::string errMsg("Failed to initialise assigner states due to database error : ");
     errMsg += Database::singleton().getCurrentError();
     throw SqliteException(errMsg);
   }
   return assignerStates;
}

//*****************************************************************************
//*****************************************************************************
void AssignerStateMapper::updateState(const std::string& objectKey, const int32_t currentState)
{
    std::ostringstream statement;
    statement << "UPDATE ASSIGNER_STATES ";
    statement << " SET ";
    statement << "   current_state = '" << currentState << "'";
    statement << " WHERE  ";
    statement << "   object_key    = '" << objectKey    << "'";
    statement << ";";
    executeStatement(statement.str());
}

//*****************************************************************************
//*****************************************************************************
void AssignerStateMapper::insertState(const std::string& objectKey, const int32_t currentState)
{
     std::ostringstream statement;
     statement << "INSERT INTO ASSIGNER_STATES VALUES('"
               << objectKey    << "','"
               << currentState << "');";
     executeStatement(statement.str());
}

//*****************************************************************************
//*****************************************************************************
void AssignerStateMapper::executeStatement(const std::string& statement)
{
   if (Database::singleton().executeStatement(statement) == false){
       std::string errMsg("Failed to execute statement  : ");
       errMsg += Database::singleton().getCurrentError();
       throw SqliteException(errMsg);
   }
}

} // end namespace SQLITE
