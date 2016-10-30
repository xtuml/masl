//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File: ObjectColumnDefinition.hh
//
//============================================================================//
#ifndef Sql_StatementGenerator_HH
#define Sql_StatementGenerator_HH

namespace SQL {

class Criteria;
// *****************************************************************
//! \brief 
//!
//! 
// *****************************************************************
template <class T>
class SQLGenerator
{
   protected:
       virtual ~StatementGenerator();

       virtual const std::string getTableName  () const = 0;
       virtual const std::string getObjectName () const = 0;

       virtual void getRowCount      (std::string& oStatement);
       virtual void getMaxIdentifier (std::string& oStatement);

       virtual void update (const T& object, std::string& oStatement) const;
       virtual void insert (const T& object, std::string& oStatement) const;
       virtual void remove (const T& object, std::string& oStatement) const;

       virtual void selectOne          (std::string& oStatement);
       virtual void selectAll          (std::string& oStatement);

       virtual void select             (const Criteria& criteria,      std::string& oStatement);
       virtual void selectOne          (const unsigned int identifier, std::string& oStatement);
       virtual void selectAllWithLimit (const unsigned int rowCount,   std::string& oStatement);

    private:
        SQLGenerator(const SQLGenerator& rhs);
        SQLGenerator& operator=(const SQLGenerator& rhs);

   protected:
        SQLGenerator();
};

} // end namespace SQL

#endif
