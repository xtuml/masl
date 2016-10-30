//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
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
