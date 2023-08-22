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

#include <cerrno>
#include <cctype>
#include <string>
#include <sstream>
#include <fstream>
#include <algorithm>
#include <stdexcept>
#include <iostream>

#include "sql/Util.hh"
#include "sql/Schema.hh"
#include "sql/Database.hh"
#include "sql/Exception.hh"
#include "sql/DatabaseFactory.hh"
#include "sql/StatementFormatter.hh"

#include "swa/Process.hh"
#include "swa/CommandLine.hh"

namespace SQL {

namespace {
  const std::string SCHEMA_DUMP        ("-schema-dump");
  const std::string SCHEMA_PREINCLUDE  ("-schema-preinclude");
  const std::string SCHEMA_POSTINCLUDE ("-schema-postinclude");

  bool registerCommandLine()
  {       
      SWA::Process::getInstance().getCommandLine().registerOption (SWA::NamedOption(SCHEMA_DUMP,       "dump schema to console",false));
      SWA::Process::getInstance().getCommandLine().registerOption (SWA::NamedOption(SCHEMA_PREINCLUDE, "sql schema file",false,"include sql before ooa schema",true) );
      SWA::Process::getInstance().getCommandLine().registerOption (SWA::NamedOption(SCHEMA_POSTINCLUDE,"sql schema file",false,"include sql after ooa schema",true) );
      return true;
  }
  bool registerCmdLine = registerCommandLine();

  void loadSchemaFile(const std::string& fileName, std::string& schema)
  {
     std::ifstream iFileStream(fileName.c_str());
     if (iFileStream){
         std::ostringstream contentStream;
         contentStream << iFileStream.rdbuf();
         schema += contentStream.str();
     }
     else{
       std::cout << "Schema formation failed to include file " << fileName << " : " << strerror(errno) << std::endl;
     }
  }
}

// ***********************************************************************
// ***********************************************************************
// Define a function Object that can be used by the Schema class to loop around 
// all the registered table definitions to form a string that contains all the create
// table sql statements.
template <class T>
class SchemaFormation
{
    public:
       SchemaFormation(std::string& schema):schema_(schema) {}
       SchemaFormation() {}
      
       void operator()(const typename T::value_type value) { schema_ += value.second; }

    private:
      std::string& schema_;
};

// ***********************************************************************
// ***********************************************************************
// Define a function Object that can be used by the Schema class to loop around 
// all the registered table definitions to form a string that contains all the drop
// table sql statements.
template <class T>
class DropFormation
{
    public:
       DropFormation(const boost::shared_ptr<DropStatementFormatter>& dropFormatter, std::string& drop):drop_(drop),dropFormatter_(dropFormatter) {}
       DropFormation() {}
      
       void operator()(const typename T::value_type value) 
       { 
          dropFormatter_->addTableName(value.first);
          drop_ += dropFormatter_->getStatement(); 
       }

    private:
      std::string& drop_;
      const boost::shared_ptr<DropStatementFormatter>& dropFormatter_;
};

// ***********************************************************************
// ***********************************************************************
// Define a helper function that can undertake the type deduction and return 
// the required SchemaFormation object, based on the type of the container key.
template <class C>
SchemaFormation<C> FormSchema(const C& container, std::string& schema)
{
   return SchemaFormation<C>(schema);
}

// ***********************************************************************
// ***********************************************************************
// Define a helper function that can undertake the type deduction and return 
// the required DropFormation object, based on the type of the container key.
template <class C>
DropFormation<C> DropSchema(const boost::shared_ptr<DropStatementFormatter>& dropFormatter, const C& container, std::string& dropSchema)
{
   return DropFormation<C>(dropFormatter, dropSchema);
}

// ***********************************************************************
// ***********************************************************************
Schema& Schema::singleton()
{
    static Schema instance;
    return instance;
}

// ***********************************************************************
// ***********************************************************************
Schema::Schema() {}

// ***********************************************************************
// ***********************************************************************
Schema::~Schema(){}


// ***********************************************************************
// ***********************************************************************
bool Schema::registerTable(const std::string& iTableName, const std::string& iTableDefinition)
{
   if (std::find_if(tableDefinitions_.begin(),tableDefinitions_.end(),matchKey(tableDefinitions_,iTableName)) != tableDefinitions_.end()){
       throw SqlException(std::string("Schema::registerTable table name already exists : ") + iTableName);
   }
   tableDefinitions_[iTableName] = iTableDefinition;
   return true;
}

// ***********************************************************************
// ***********************************************************************
bool Schema::deregisterTable(const std::string& iTableName)
{
   TableDefinitionType::iterator tableItr = tableDefinitions_.find(iTableName);
   if (tableItr != tableDefinitions_.end()){
       tableDefinitions_.erase(tableItr);
   }
   return true;
}

// ***********************************************************************
// ***********************************************************************
std::string Schema::getSchema()
{
    std::string schema;
    if (SWA::Process::getInstance().getCommandLine().optionPresent(SCHEMA_PREINCLUDE)){
        std::string fileName(SWA::Process::getInstance().getCommandLine().getOption(SCHEMA_PREINCLUDE));
        loadSchemaFile(fileName,schema);
    }

    std::for_each(tableDefinitions_.begin(),tableDefinitions_.end(),FormSchema(tableDefinitions_,schema));

    if (SWA::Process::getInstance().getCommandLine().optionPresent(SCHEMA_POSTINCLUDE)){
        std::string fileName(SWA::Process::getInstance().getCommandLine().getOption(SCHEMA_POSTINCLUDE));
        loadSchemaFile(fileName,schema);
    }


    if (SWA::Process::getInstance().getCommandLine().optionPresent(SCHEMA_DUMP)){
        std::cout << "Schema definition ..." << std::endl;
        std::cout << schema << std::endl;
    }
    return schema;
}

// ***********************************************************************
// ***********************************************************************
std::string Schema::dropSchema()
{
   boost::shared_ptr<AbstractStatementFactory> statementFactory  = DatabaseFactory::singleton().getImpl().getStatementFormatter();
   boost::shared_ptr<DropStatementFormatter>   dropStmtFormatter = statementFactory->createDropStatementFormatter();

   std::string dropSchema;
   std::for_each(tableDefinitions_.begin(),tableDefinitions_.end(),DropSchema(dropStmtFormatter,tableDefinitions_,dropSchema));
   return dropSchema;
}

}
