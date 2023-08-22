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

#ifndef Sql_RelationshipTenarySqlGenerator_HH
#define Sql_RelationshipTenarySqlGenerator_HH

#include <string>

#include "boost/tuple/tuple.hpp"
#include "boost/unordered_map.hpp"
#include "swa/tuple_hash.hh"

#include "swa/types.hh"
#include "swa/ObjectPtr.hh"

#include "Util.hh"
#include "RelationshipSql.hh"

namespace SQL {


// *****************************************************************
//! @brief Provides interface to SQL implementation for specified Relationship
//!
//! 
// *****************************************************************
template <class LhsRelContainer, class RhsRelContainer,class AssRelContainer>
class RelationshipTenarySqlGenerator : public RelationshipSql
{
   public:
       typedef  typename LhsRelContainer::PsObjectPtr      LhsPsObjectPtr;
       typedef  typename RhsRelContainer::PsObjectPtr      RhsPsObjectPtr;
       typedef  typename LhsRelContainer::AssocPsObjectPtr AssPsObjectPtr;

       typedef LhsRelContainer LhsContainerType;
       typedef RhsRelContainer RhsContainerType;
       typedef AssRelContainer AssContainerType;

       typedef ::boost::tuple<LhsPsObjectPtr,RhsPsObjectPtr,AssPsObjectPtr> LinkedTenaryType;

       typedef boost::unordered_map< ::SWA::IdType, RhsContainerType> LhsLinkType;
       typedef boost::unordered_map< ::SWA::IdType, LhsContainerType> RhsLinkType;
       typedef boost::unordered_map< ::SWA::IdType, AssContainerType> AssLinkType;

   public:

       // *****************************************************************
       //! @brief 
       //!
       //! 
       // *****************************************************************
       class CachedTenaryContainerSet
       {
           public:
              CachedTenaryContainerSet(LhsLinkType& lhsLinks, RhsLinkType& rhsLinks, AssLinkType& assLinks):
                     lhsLinks(lhsLinks),
                     rhsLinks(rhsLinks),
                     assLinks(assLinks){}

             ~CachedTenaryContainerSet(){}

              LhsLinkType& getLhsLinks() { return lhsLinks; }
              RhsLinkType& getRhsLinks() { return rhsLinks; }
              AssLinkType& getAssLinks() { return assLinks; }

           private:
             CachedTenaryContainerSet(const CachedTenaryContainerSet& rhs){}
             CachedTenaryContainerSet& operator=(const CachedTenaryContainerSet& rhs);

           private:
              LhsLinkType& lhsLinks;
              RhsLinkType& rhsLinks;
              AssLinkType& assLinks;
       };

   public:
                RelationshipTenarySqlGenerator(){}
       virtual ~RelationshipTenarySqlGenerator(){}

       virtual void initialise() = 0;

       virtual const std::string& getTableName        () const = 0;
       virtual const std::string  getDomainName       () const = 0;
       virtual const std::string  getLhsColumnName    () const = 0;
       virtual const std::string  getRhsColumnName    () const = 0;
       virtual const std::string  getAssocColumnName  () const = 0;
       virtual const std::string& getRelationshipName () const = 0;

       virtual ::SWA::IdType executeGetRowCount () const = 0;

       virtual void commitLink   (const LinkedTenaryType& linkObjects)   const = 0;
       virtual void commitUnlink (const LinkedTenaryType& unlinkObjects) const = 0;

       virtual void loadAll (CachedTenaryContainerSet& cachedTenaryContainers) const = 0;
       virtual void loadLhs (const ::SWA::IdType& rhsIdentity, CachedTenaryContainerSet& cachedTenaryContainers) const = 0;
       virtual void loadRhs (const ::SWA::IdType& lhsIdentity, CachedTenaryContainerSet& cachedTenaryContainers) const = 0;
       virtual void loadAss (const ::SWA::IdType& Assidentity, CachedTenaryContainerSet& cachedTenaryContainers) const = 0;

    private:
        RelationshipTenarySqlGenerator(const RelationshipTenarySqlGenerator& rhs);
        RelationshipTenarySqlGenerator& operator=(const RelationshipTenarySqlGenerator& rhs);
};

} // end namespace SQL

#endif
