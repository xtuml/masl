//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File: RelationshipSqlGenerator.hh
//
//============================================================================//
#ifndef Sql_RelationshipBinarySqlGenerator_HH
#define Sql_RelationshipBinarySqlGenerator_HH

#include <string>
#include <boost/unordered_map.hpp>

#include "swa/types.hh"
#include "swa/ObjectPtr.hh"

#include "Util.hh"
#include "RelationshipSql.hh"

namespace SQL {

// *****************************************************************
//! @brief 
//!
//! 
// *****************************************************************
template <class LhsRelContainer, class RhsRelContainer>
class RelationshipBinarySqlGenerator : public RelationshipSql
{
   public:
       typedef  typename LhsRelContainer::PsObjectPtr    LhsPsObjectPtr;
       typedef  typename RhsRelContainer::PsObjectPtr    RhsPsObjectPtr;
       typedef std::pair<LhsPsObjectPtr,RhsPsObjectPtr > LinkedPairType;

       typedef LhsRelContainer LhsContainerType;
       typedef RhsRelContainer RhsContainerType;

       typedef boost::unordered_map< ::SWA::IdType, RhsRelContainer> LhsToRhsContainerType;
       typedef boost::unordered_map< ::SWA::IdType, LhsRelContainer> RhsToLhsContainerType;

   public:
                RelationshipBinarySqlGenerator(){}
       virtual ~RelationshipBinarySqlGenerator(){}

       virtual void initialise() = 0;

       virtual const std::string& getTableName        () const = 0;
       virtual const std::string  getDomainName       () const = 0;
       virtual const std::string  getLhsColumnName    () const = 0;
       virtual const std::string  getRhsColumnName    () const = 0;
       virtual const std::string& getRelationshipName () const = 0;

       virtual ::SWA::IdType executeGetRowCount      () const = 0;

       virtual void commitLink   (const LinkedPairType& linkObjects)   const = 0;
       virtual void commitUnlink (const LinkedPairType& unlinkObjects) const = 0;

       virtual void loadAll (LhsToRhsContainerType& lhsToRhsLinkSet, RhsToLhsContainerType& rhsToLhsLinkSet) const = 0;
       virtual void loadLhs (const ::SWA::IdType& rhsIdentity, LhsToRhsContainerType& lhsToRhsLinkSet, RhsToLhsContainerType& rhsToLhsLinkSet) const = 0;
       virtual void loadRhs (const ::SWA::IdType& lhsIdentity, LhsToRhsContainerType& lhsToRhsLinkSet, RhsToLhsContainerType& rhsToLhsLinkSet) const = 0;

    private:
        RelationshipBinarySqlGenerator(const RelationshipBinarySqlGenerator& rhs);
        RelationshipBinarySqlGenerator& operator=(const RelationshipBinarySqlGenerator& rhs);
};

} // end namespace SQL

#endif
