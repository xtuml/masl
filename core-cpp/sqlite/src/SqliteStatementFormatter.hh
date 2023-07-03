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

#include <string>
#include "sql/StatementFormatter.hh"

#include "boost/shared_ptr.hpp"

namespace SQLITE {

// ***************************************************************
// ***************************************************************
class SqliteDropStatementFormatter :  public ::SQL::DropStatementFormatter
{
   public: 
               SqliteDropStatementFormatter();
      virtual ~SqliteDropStatementFormatter();
   
      std::string getStatement();
      void addTableName(const std::string& tableName);

   private:
      SqliteDropStatementFormatter(const SqliteDropStatementFormatter& rhs);
      SqliteDropStatementFormatter& operator=(const SqliteDropStatementFormatter& rhs);

   private:
      std::string tableName_;
};

// ***************************************************************
// ***************************************************************
class SqliteAbstractStatementFactory : public ::SQL::AbstractStatementFactory
{
   public:
               SqliteAbstractStatementFactory();
      virtual ~SqliteAbstractStatementFactory();

      boost::shared_ptr< ::SQL::DropStatementFormatter>   createDropStatementFormatter();
      boost::shared_ptr< ::SQL::InsertStatementFormatter> createInsertStatementFormatter();
      boost::shared_ptr< ::SQL::UpdateStatementFormatter> createUpdateStatement();
      boost::shared_ptr< ::SQL::DeleteStatementFormatter> createsDeleteStatement();
      boost::shared_ptr< ::SQL::CreateStatementFormatter> createCreateStatement();

   private:
      SqliteAbstractStatementFactory(const SqliteAbstractStatementFactory& rhs);      
      SqliteAbstractStatementFactory& operator=(const SqliteAbstractStatementFactory& rhs);
};


}// end SQLITE namespace
