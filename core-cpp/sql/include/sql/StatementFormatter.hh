//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   StatementFormatter.hh
//
//============================================================================//
#ifndef Sql_StatementFormatter_HH
#define Sql_StatementFormatter_HH

#include <string>
#include "boost/shared_ptr.hpp"

namespace SQL {

// ***************************************************************
// ***************************************************************
class WhereFormatter
{
   protected:
       WhereFormatter() {}

   public:
       enum UnaryConditionType  {NOT};
       enum BinaryConditionType {AND, OR};
       enum WhereOperatorType   {EQUALS, NOT_EQUALS};

   public:
      virtual ~WhereFormatter() {}
   
      virtual void addCondition (const std::string&      columnName, 
                                 const WhereOperatorType whereOperator, 
                                 const std::string&      columnValue) = 0;

      virtual void addUnaryCondition  (const UnaryConditionType  unaryCondition)  = 0;
      virtual void addBinaryCondition (const BinaryConditionType binaryCondition) = 0;

   private:
      WhereFormatter(const WhereFormatter& rhs);
      WhereFormatter& operator=(const WhereFormatter& rhs);
};

// ***************************************************************
// ***************************************************************
class StatementFormatter
{
   protected:
       StatementFormatter() {}

   public:
      virtual ~StatementFormatter() {}

      virtual std::string getStatement() = 0;
      
   private:
      StatementFormatter(const StatementFormatter& rhs);
      StatementFormatter& operator=(const StatementFormatter& rhs);
};

// ***************************************************************
// ***************************************************************
class DropStatementFormatter :  public StatementFormatter
{
   protected:
       DropStatementFormatter() {}

   public:
      virtual ~DropStatementFormatter() {}
   
      virtual void addTableName(const std::string& tableName) = 0;

   private:
      DropStatementFormatter(const DropStatementFormatter& rhs);
      DropStatementFormatter& operator=(const DropStatementFormatter& rhs);
};

// ***************************************************************
// ***************************************************************
class InsertStatementFormatter :  public StatementFormatter
{
   protected:
       InsertStatementFormatter() {}

   public:
      virtual ~InsertStatementFormatter() {}
   
      virtual void setTableName()  = 0;
      virtual void setColumnValue(const std::string& columnName, const std::string& columnValue) = 0;

   private:
      InsertStatementFormatter(const InsertStatementFormatter& rhs);
      InsertStatementFormatter& operator=(const InsertStatementFormatter& rhs);
};

// ***************************************************************
// ***************************************************************
class UpdateStatementFormatter :  public StatementFormatter
{
   protected:
       UpdateStatementFormatter() {}

   public:
      virtual ~UpdateStatementFormatter() {}
   
      virtual void setTableName()  = 0;
      virtual void setColumnValue(const std::string& columnName, const std::string& columnValue) = 0;
      virtual boost::shared_ptr<WhereFormatter> addWhereClause() = 0;

   private:
      UpdateStatementFormatter(const UpdateStatementFormatter& rhs);
      UpdateStatementFormatter& operator=(const UpdateStatementFormatter& rhs);
};

// ***************************************************************
// ***************************************************************
class DeleteStatementFormatter :  public StatementFormatter
{
   protected:
       DeleteStatementFormatter() {}

   public:
      virtual ~DeleteStatementFormatter() {}
   
      virtual void setTableName()  = 0;
      virtual boost::shared_ptr<WhereFormatter> addWhereClause() = 0;

   private:
      DeleteStatementFormatter(const DeleteStatementFormatter& rhs);
      DeleteStatementFormatter& operator=(const DeleteStatementFormatter& rhs);
};

// ***************************************************************
// ***************************************************************
class CreateStatementFormatter :  public StatementFormatter
{
   protected:
       CreateStatementFormatter() {}

   public:
      virtual ~CreateStatementFormatter() {}

   private:
      CreateStatementFormatter(const  CreateStatementFormatter& rhs);
      CreateStatementFormatter& operator=(const CreateStatementFormatter& rhs);
};

// ***************************************************************
// ***************************************************************
class AbstractStatementFactory
{
   protected:
       AbstractStatementFactory() {}

   public:
      virtual ~AbstractStatementFactory() {}

      virtual boost::shared_ptr<DropStatementFormatter>   createDropStatementFormatter()   = 0;
      virtual boost::shared_ptr<InsertStatementFormatter> createInsertStatementFormatter() = 0;
      virtual boost::shared_ptr<UpdateStatementFormatter> createUpdateStatement()  = 0;
      virtual boost::shared_ptr<DeleteStatementFormatter> createsDeleteStatement() = 0;
      virtual boost::shared_ptr<CreateStatementFormatter> createCreateStatement()  = 0;

   private:
      AbstractStatementFactory(const AbstractStatementFactory& rhs);
      AbstractStatementFactory& operator=(const AbstractStatementFactory& rhs);

};

}  // end SQL namespace

#endif
