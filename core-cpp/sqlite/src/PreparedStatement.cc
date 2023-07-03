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

#include <deque>
#include <sstream>
#include <iostream>

#include "sql/Util.hh"

#include "sqlite/Database.hh"
#include "sqlite/Exception.hh"
#include "sqlite/SqlMonitor.hh"
#include "sqlite/PreparedStatement.hh"

#include "swa/Timestamp.hh"
#include "swa/Duration.hh"
#include "swa/String.hh"
#include "sqlite/BlobData.hh"

#include "boost/lexical_cast.hpp"

namespace {

// Performance profiling using vtune has shown a bottle neck in the 
// getPositionIndex method due to the lexical conversions that are 
// being undertaken for each PreparedStatement. Therefore have created
// a static lookup table as the number of positional indexes is restricted
// by the PreparedStatement interface (currently 70)
// *****************************************************************
// *****************************************************************

const char* const lookupTable[::SQLITE::PreparedStatement::MAX_PARAMETER_COUNT] = { ":0", ":1",  ":2", ":3", ":4", ":5", ":6", ":7", ":8", ":9",":10",
                                                                                          ":11",":12",":13",":14",":15",":16",":17",":18",":19",":20",
                                                                                          ":21",":22",":23",":24",":25",":26",":27",":28",":29",":30",
                                                                                          ":31",":32",":33",":34",":35",":36",":37",":38",":39",":40",
                                                                                          ":41",":42",":43",":44",":45",":46",":47",":48",":49",":50",
                                                                                          ":51",":52",":53",":54",":55",":56",":57",":58",":59",":60",
                                                                                          ":61",":62",":63",":64",":65",":66",":67",":68",":69"};


class IndexLookUp
{
    public:
      // *****************************************************************
      // *****************************************************************
      static IndexLookUp& singleton()
      {
         static IndexLookUp instance;
         return instance;
      }

      // *****************************************************************
      // *****************************************************************
      const char* const lookUpPositionIndex(const int32_t index)
      {
         if(index > ::SQLITE::PreparedStatement::MAX_PARAMETER_COUNT){
            throw ::SQLITE::SqliteException("PreparedStatement loadLookUpIndex failed : index is out of range - " + boost::lexical_cast<std::string>(index));
         }
         return ::lookupTable[index];
      }

    private:
        // *****************************************************************
        // *****************************************************************
        IndexLookUp(){}

        // *****************************************************************
        // *****************************************************************
       ~IndexLookUp() {}
};

}

namespace SQLITE {

// *****************************************************************
// *****************************************************************
class PreparedStatement::PreparedStatementImpl
{
   public:
     PreparedStatementImpl(const std::string& sql):
         preparedStatement(0),
         statement(sql){ }

     void prepare()
     {
        if (preparedStatement != 0){
            // The statement is being prepared again, perhaps against a new
            // database. Therefore release resources before preparing again.
            sqlite3_finalize(preparedStatement); 
        }

        sqlite3* databaseImpl = Database::singleton().getDatabaseImpl();
        if (sqlite3_prepare(databaseImpl,statement.c_str(),statement.size(),&preparedStatement,0) != SQLITE_OK){
            throw SqliteException(::boost::make_tuple("PreparedStatement::PreparedStatementImpl : prepare failed for statement",statement,"-",sqlite3_errmsg(databaseImpl)));
        }
     }

     void execute()
     {       
       if(SqlPreparedStatementMonitor::isEnabled()){
          SqlPreparedStatementMonitor monitor(statement,bindValues,true);
          bindValues.clear();
       }

       if (preparedStatement == 0){
           throw SqliteException(::boost::make_tuple("PreparedStatementImpl::execute : statement has not been prepared - ",statement));
       }

       if (sqlite3_step(preparedStatement) != SQLITE_DONE){
           sqlite3_reset(preparedStatement);  // need to reset prepared statement to  get hold of the sqlite error .
           sqlite3* databaseImpl = Database::singleton().getDatabaseImpl();
           if (sqlite3_errcode(databaseImpl) != SQLITE_SCHEMA){
               throw SqliteException(::boost::make_tuple("PreparedStatementImpl::execute : statement execution failed -",statement,":",sqlite3_errmsg(databaseImpl)));
           }
           if(SqlPreparedStatementMonitor::isEnabled()){
              std::cout << "prepared statement : sqlite schema change detected for statement " << statement << " : re-preparing" << std::endl;
           }
           prepare();
           throw SqliteSchemaException(std::string("A schema change caused prepared statement to fail for statement : ") + statement);
       }
       else{
         sqlite3_reset(preparedStatement);
       }
     }

    ~PreparedStatementImpl()
     { 
        sqlite3_finalize(preparedStatement); 
     }

      template<class T>
      bool monitorBindValue(const int32_t position, const T& value)
      {
         if (!bindValues.empty()){
              bindValues = std::string(",") + bindValues;
         }
         bindValues = ::SQL::valueToString(value) + bindValues;
         return true;
      }

     sqlite3_stmt* preparedStatement;
     std::string   statement;
     std::string   bindValues;
};

// *****************************************************************
// *****************************************************************
PreparedStatement::PreparedStatement(const std::string& statement):
    impl(new PreparedStatementImpl(statement))
{

}

// *****************************************************************
// *****************************************************************
PreparedStatement::~PreparedStatement()
{

}

// *****************************************************************
// *****************************************************************
void PreparedStatement::prepare()
{
  impl->prepare(); 
}

// *****************************************************************
// *****************************************************************
void PreparedStatement::execute() const
{
   impl->execute();  
}

// *****************************************************************
// *****************************************************************
void PreparedStatement::execute(const int32_t& p1) const
{
  try {
      int parameterIndex = sqlite3_bind_parameter_index(impl->preparedStatement,":1");
      if (parameterIndex == 0){
          reportError("PreparedStatement::execute(int32_t) : failed to find parameter index");
      }

      if (sqlite3_bind_int(impl->preparedStatement,parameterIndex,p1) != SQLITE_OK){
          reportError("PreparedStatement::execute(int32_t) : bind failed");        
      } 
      SqlPreparedStatementMonitor::isEnabled() && impl->monitorBindValue(1,p1);
      execute(); 
  }
  catch(const SqliteSchemaException& sse){
      int parameterIndex = sqlite3_bind_parameter_index(impl->preparedStatement,":1");
      if (parameterIndex == 0){
          reportError("PreparedStatement::execute(int32_t) : failed to find parameter index");
      }

      if (sqlite3_bind_int(impl->preparedStatement,parameterIndex,p1) != SQLITE_OK){
          reportError("PreparedStatement::execute(int32_t) : bind failed");        
      } 
      SqlPreparedStatementMonitor::isEnabled() && impl->monitorBindValue(1,p1);
      execute(); 
  }
}

// *****************************************************************
// *****************************************************************
void PreparedStatement::execute(const uint32_t& p1) const
{
  try {
     int parameterIndex = sqlite3_bind_parameter_index(impl->preparedStatement,":1");
     if (parameterIndex == 0){
         reportError("PreparedStatement::execute(uint32_t) : failed to find parameter index");
     }

     if (sqlite3_bind_int(impl->preparedStatement,parameterIndex,p1) != SQLITE_OK){
         reportError("PreparedStatement::execute(uint32_t) : bind failed");        
     } 
     SqlPreparedStatementMonitor::isEnabled() && impl->monitorBindValue(1,p1);
     execute(); 
  }
  catch(const SqliteSchemaException& sse){
     int parameterIndex = sqlite3_bind_parameter_index(impl->preparedStatement,":1");
     if (parameterIndex == 0){
         reportError("PreparedStatement::execute(uint32_t) : failed to find parameter index");
     }

     if (sqlite3_bind_int(impl->preparedStatement,parameterIndex,p1) != SQLITE_OK){
         reportError("PreparedStatement::execute(uint32_t) : bind failed");        
     } 
     SqlPreparedStatementMonitor::isEnabled() && impl->monitorBindValue(1,p1);
     execute(); 

  }
}

// *****************************************************************
// *****************************************************************
void PreparedStatement::bind(const int32_t position, const int32_t value) const
{
  if (sqlite3_bind_int(impl->preparedStatement,getPositionIndex(position),value) != SQLITE_OK){
        reportError("PreparedStatement::bind(int32_t,int32_t) : execution failed");        
  }
  SqlPreparedStatementMonitor::isEnabled() && impl->monitorBindValue(position,value);
}

// *****************************************************************
// *****************************************************************
void PreparedStatement::bind(const int32_t position, const uint32_t value) const
{
  if (sqlite3_bind_int(impl->preparedStatement,getPositionIndex(position),value) != SQLITE_OK){
        reportError("PreparedStatement::bind(int32_t,uint32_t) : execution failed");        
  }
  SqlPreparedStatementMonitor::isEnabled() && impl->monitorBindValue(position,value);
}

// *****************************************************************
// *****************************************************************
void PreparedStatement::bind(const int32_t position, const int64_t value) const
{
   if (sqlite3_bind_int64(impl->preparedStatement,getPositionIndex(position),value) != SQLITE_OK){
       reportError("PreparedStatement::bind(int32_t,int64_t) : execution failed");        
   } 
   SqlPreparedStatementMonitor::isEnabled() && impl->monitorBindValue(position,value);
}

// *****************************************************************
// *****************************************************************
void PreparedStatement::bind(const int32_t position, const uint64_t value) const
{
   if (sqlite3_bind_int64(impl->preparedStatement,getPositionIndex(position),value) != SQLITE_OK){
       reportError("PreparedStatement::bind(int32_t,uint64_t) : execution failed");        
   } 
   SqlPreparedStatementMonitor::isEnabled() && impl->monitorBindValue(position,value);
}

// *****************************************************************
// *****************************************************************
void PreparedStatement::bind (const int32_t position, const double value) const
{
    if (sqlite3_bind_double(impl->preparedStatement,getPositionIndex(position),value) != SQLITE_OK){
       reportError("PreparedStatement::bind(int32_t,double) : execution failed");        
    } 
    SqlPreparedStatementMonitor::isEnabled() && impl->monitorBindValue(position,value);
}

// *****************************************************************
// *****************************************************************
void PreparedStatement::bind (const int32_t position, const std::string& value) const
{
   if (sqlite3_bind_text(impl->preparedStatement,getPositionIndex(position),value.c_str(),value.size(),SQLITE_TRANSIENT) != SQLITE_OK){  
       reportError("PreparedStatement::bind(int32_t,std::string) : execution failed");        
   }
   SqlPreparedStatementMonitor::isEnabled() && impl->monitorBindValue(position,value);
}

// *****************************************************************
// *****************************************************************
void PreparedStatement::bind (const int32_t position, const char* const value) const
{
    if (sqlite3_bind_text(impl->preparedStatement,getPositionIndex(position),value,strlen(value),SQLITE_TRANSIENT) != SQLITE_OK){
        reportError("PreparedStatement::bind(int32_t,char*) : execution failed");     
    } 
    SqlPreparedStatementMonitor::isEnabled() && impl->monitorBindValue(position,value);
}

// *****************************************************************
// *****************************************************************
void PreparedStatement::bind (const int32_t position, const BlobData& encodedBlob) const
{
    if (sqlite3_bind_blob(impl->preparedStatement,getPositionIndex(position),encodedBlob.data(),encodedBlob.size(),SQLITE_TRANSIENT) != SQLITE_OK){
        reportError("PreparedStatement::bind(int32_t,BlobData) : execution failed");     
    } 
    SqlPreparedStatementMonitor::isEnabled() && impl->monitorBindValue(position,std::string("[blob data]"));
}

// *****************************************************************
// *****************************************************************
void PreparedStatement::bind (const int32_t position, const SWA::Timestamp& value) const
{
    bind(position, value.nanosSinceEpoch());
}

// *****************************************************************
// *****************************************************************
void PreparedStatement::bind (const int32_t position, const SWA::Duration& value) const
{
    bind(position, value.nanos());
}

// *****************************************************************
// *****************************************************************
void PreparedStatement::bind (const int32_t position, const SWA::String& value) const
{
    bind(position, value.c_str());    
}

// *****************************************************************
// *****************************************************************
int32_t PreparedStatement::getPositionIndex(const int32_t position) const
{
   const char * const indexText = IndexLookUp::singleton().lookUpPositionIndex(position);
   int parameterIndex = sqlite3_bind_parameter_index(impl->preparedStatement,indexText);
   if (parameterIndex == 0){
       reportError("PreparedStatement call to sqlite3_bind_parameter_index : failed to find parameter index");
   }
   return parameterIndex;
}

// *****************************************************************
// *****************************************************************
void PreparedStatement::reportError(const std::string& message) const
{
   sqlite3* databaseImpl = Database::singleton().getDatabaseImpl();
   throw SqliteException(::boost::make_tuple(message,"-",sqlite3_errmsg(databaseImpl)));
}

} // end namepsace SQLITE



