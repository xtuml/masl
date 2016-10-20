//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   SqliteCriteriaImpl.cc
//
//============================================================================//

#include "sql/Util.hh"

#include "SqliteCriteria.hh"
#include "sqlite/Exception.hh"

namespace SQLITE {

// ***********************************************************************
// ***********************************************************************
SqliteCriteriaImpl::SqliteCriteriaImpl():
   column_ (),
   where_  (),
   limit_  (),
   from_   ()
{

}

// ***********************************************************************
// ***********************************************************************
SqliteCriteriaImpl::~SqliteCriteriaImpl()
{

}

// ***********************************************************************
// ***********************************************************************
bool SqliteCriteriaImpl::empty() const
{
  return where_.empty();
}

// ***********************************************************************
// ***********************************************************************
void SqliteCriteriaImpl::setLimit (const int32_t limit)
{
   if (limit < 0){
       throw SqliteException("SqliteCriteriaImpl::setLimit : negative limit value");
   }
   limit_ = ::SQL::valueToString(limit);
}

// ***********************************************************************
// ***********************************************************************
void SqliteCriteriaImpl::addFromClause (const std::string& tableName)
{
    if (!from_.empty()){
         from_ += ", ";
    }
    from_ += tableName;
    from_ += " ";
}

// ***********************************************************************
// ***********************************************************************
void SqliteCriteriaImpl::addWhereClause (const std::string& where)
{
    where_ += where;
    where_ += " ";
}

// ***********************************************************************
// ***********************************************************************
void SqliteCriteriaImpl::addColumn (const std::string& columnName)
{
   if (columnName.find(" ")  != std::string::npos ||
       columnName.find(",")  != std::string::npos ||
       columnName.find("\t") != std::string::npos ){
       throw SqliteException("SqliteCriteriaImpl::addColumn : only one column name allowed");
   }

   if (!column_.empty()){
       column_ +=  ",";
   }
   column_ += columnName;
}

// ***********************************************************************
// ***********************************************************************
void SqliteCriteriaImpl::addAllColumn ()
{
    addColumn("*");
}

// ***********************************************************************
// ***********************************************************************
void SqliteCriteriaImpl::addAllColumns (const std::string& table)
{
   addColumn(table + ".*");
}

// ***********************************************************************
// ***********************************************************************
std::string SqliteCriteriaImpl::selectStatement() const
{
    std::string statement ("SELECT ");
    statement += column_;
    statement += " FROM ";
    statement += from_;

    if (!where_.empty()){
      statement += " WHERE ";
      statement +=   where_;
    }

    if (!limit_.empty()){
        statement += " LIMIT ";
        statement += limit_;
    }
    statement += ";";
    return statement;
}

// ***********************************************************************
// ***********************************************************************
SqliteCloneableCriteria::SqliteCloneableCriteria() 
{

}

// ***********************************************************************
// ***********************************************************************
SqliteCloneableCriteria::~SqliteCloneableCriteria() 
{

}

// ***********************************************************************
// ***********************************************************************
boost::shared_ptr< ::SQL::CriteriaImpl> SqliteCloneableCriteria::clone() const 
{ 
 return boost::shared_ptr< ::SQL::CriteriaImpl>(new SqliteCriteriaImpl()); 
}

namespace 
{
   bool critieraImplReg = ::SQL::CriteriaFactory::singleton().registerImpl( boost::shared_ptr< ::SQL::CloneableCriteria>( new SqliteCloneableCriteria) );
}

} // end namespace SQL


