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

#include "SqliteStatementFormatter.hh"
#include "SqliteSql.hh"
#include "sqlite/Exception.hh"

namespace SQLITE {

// ***************************************************************
// ***************************************************************
SqliteDropStatementFormatter::SqliteDropStatementFormatter()
{

}

// ***************************************************************
// ***************************************************************
SqliteDropStatementFormatter::~SqliteDropStatementFormatter()
{

}
   
// ***************************************************************
// ***************************************************************
std::string SqliteDropStatementFormatter::getStatement() 
{ 
  return DROP_TABLE + " " + tableName_ + ";\n";
}

// ***************************************************************
// ***************************************************************
void SqliteDropStatementFormatter::addTableName(const std::string& tableName) 
{ 
  tableName_ = tableName; 
}

// ***************************************************************
// ***************************************************************
SqliteAbstractStatementFactory::SqliteAbstractStatementFactory()
{

}

// ***************************************************************
// ***************************************************************
SqliteAbstractStatementFactory::~SqliteAbstractStatementFactory()
{

}

// ***************************************************************
// ***************************************************************
boost::shared_ptr< ::SQL::DropStatementFormatter>   SqliteAbstractStatementFactory::createDropStatementFormatter()   
{ 
 return  boost::shared_ptr< ::SQL::DropStatementFormatter >(new SqliteDropStatementFormatter);
}

// ***************************************************************
// ***************************************************************
boost::shared_ptr< ::SQL::InsertStatementFormatter> SqliteAbstractStatementFactory::createInsertStatementFormatter() 
{ 
  throw SqliteException("SqliteAbstractStatementFactory::createInsertStatementFormatter : no implementation!!"); 
}

// ***************************************************************
// ***************************************************************
boost::shared_ptr< ::SQL::UpdateStatementFormatter> SqliteAbstractStatementFactory::createUpdateStatement() 
{ 
  throw SqliteException("SqliteAbstractStatementFactory::createUpdateStatement  : no implementation!!"); 
}

// ***************************************************************
// ***************************************************************
boost::shared_ptr< ::SQL::DeleteStatementFormatter> SqliteAbstractStatementFactory::createsDeleteStatement()
{ 
  throw SqliteException("SqliteAbstractStatementFactory::createsDeleteStatement : no implementation!!"); 
}

// ***************************************************************
// ***************************************************************
boost::shared_ptr< ::SQL::CreateStatementFormatter> SqliteAbstractStatementFactory::createCreateStatement() 
{ 
 throw SqliteException("SqliteAbstractStatementFactory::createCreateStatement  : no implementation!!"); }
} // end SQLITE namespace
