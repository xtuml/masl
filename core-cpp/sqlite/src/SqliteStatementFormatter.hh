//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   SqliteStatementFormatter.hh
//
//============================================================================//

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
