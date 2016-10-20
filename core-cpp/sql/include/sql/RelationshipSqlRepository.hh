//============================================================================//
// UK Crown Copyright (c) 2009. All rights reserved.
//
// File: RelationshipSql.hh
//
//============================================================================//
#ifndef Sql_RelationshipSqlRepository_HH
#define Sql_RelationshipSqlRepository_HH

#include <map>
#include <string>

namespace SQL {

// *****************************************************************
//! Define an repository that can be used to programatically determine
//! the SQL mapping that have been used for a MASl object. 
// *****************************************************************
class RelationshipSql;
class RelationshipSqlRepository
{
   public:
      static RelationshipSqlRepository& getInstance();

      bool registerRelationshipSql   (const RelationshipSql* const sql);
      void deregisterRelationshipSql (const RelationshipSql* const sql);

      const RelationshipSql& getRelationshipSql(const std::string& domainName, const std::string& relationshipName) const;

   private:
      RelationshipSqlRepository();
     ~RelationshipSqlRepository();

   private:
      RelationshipSqlRepository(const RelationshipSqlRepository& rhs);
      RelationshipSqlRepository& operator=(const RelationshipSqlRepository& rhs);

   private:
      std::map<std::string, const RelationshipSql* const> repository;
};

} // end namespace SQL

#endif

