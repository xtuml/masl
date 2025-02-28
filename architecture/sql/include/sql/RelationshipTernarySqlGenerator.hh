/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_RelationshipTernarySqlGenerator_HH
#define Sql_RelationshipTernarySqlGenerator_HH

#include <string>

#include "boost/tuple/tuple.hpp"
#include "swa/tuple_hash.hh"
#include <unordered_map>

#include "swa/ObjectPtr.hh"
#include "swa/types.hh"

#include "RelationshipSql.hh"
#include "Util.hh"

namespace SQL {

    // *****************************************************************
    //! @brief Provides interface to SQL implementation for specified Relationship
    //!
    //!
    // *****************************************************************
    template <class LhsRelContainer, class RhsRelContainer, class AssRelContainer>
    class RelationshipTernarySqlGenerator : public RelationshipSql {
      public:
        typedef typename LhsRelContainer::PsObjectPtr LhsPsObjectPtr;
        typedef typename RhsRelContainer::PsObjectPtr RhsPsObjectPtr;
        typedef typename LhsRelContainer::AssocPsObjectPtr AssPsObjectPtr;

        typedef LhsRelContainer LhsContainerType;
        typedef RhsRelContainer RhsContainerType;
        typedef AssRelContainer AssContainerType;

        typedef ::boost::tuple<LhsPsObjectPtr, RhsPsObjectPtr, AssPsObjectPtr> LinkedTernaryType;

        typedef std::unordered_map<::SWA::IdType, RhsContainerType> LhsLinkType;
        typedef std::unordered_map<::SWA::IdType, LhsContainerType> RhsLinkType;
        typedef std::unordered_map<::SWA::IdType, AssContainerType> AssLinkType;

      public:
        // *****************************************************************
        //! @brief
        //!
        //!
        // *****************************************************************
        class CachedTernaryContainerSet {
          public:
            CachedTernaryContainerSet(LhsLinkType &lhsLinks, RhsLinkType &rhsLinks, AssLinkType &assLinks)
                : lhsLinks(lhsLinks), rhsLinks(rhsLinks), assLinks(assLinks) {}

            ~CachedTernaryContainerSet() {}

            LhsLinkType &getLhsLinks() {
                return lhsLinks;
            }
            RhsLinkType &getRhsLinks() {
                return rhsLinks;
            }
            AssLinkType &getAssLinks() {
                return assLinks;
            }

          private:
            CachedTernaryContainerSet(const CachedTernaryContainerSet &rhs) {}
            CachedTernaryContainerSet &operator=(const CachedTernaryContainerSet &rhs);

          private:
            LhsLinkType &lhsLinks;
            RhsLinkType &rhsLinks;
            AssLinkType &assLinks;
        };

      public:
        RelationshipTernarySqlGenerator() {}
        virtual ~RelationshipTernarySqlGenerator() {}

        virtual void initialise() = 0;

        virtual const std::string &getTableName() const = 0;
        virtual const std::string getDomainName() const = 0;
        virtual const std::string getLhsColumnName() const = 0;
        virtual const std::string getRhsColumnName() const = 0;
        virtual const std::string getAssocColumnName() const = 0;
        virtual const std::string &getRelationshipName() const = 0;

        virtual ::SWA::IdType executeGetRowCount() const = 0;

        virtual void commitLink(const LinkedTernaryType &linkObjects) const = 0;
        virtual void commitUnlink(const LinkedTernaryType &unlinkObjects) const = 0;

        virtual void loadAll(CachedTernaryContainerSet &cachedTernaryContainers) const = 0;
        virtual void
        loadLhs(const ::SWA::IdType &rhsIdentity, CachedTernaryContainerSet &cachedTernaryContainers) const = 0;
        virtual void
        loadRhs(const ::SWA::IdType &lhsIdentity, CachedTernaryContainerSet &cachedTernaryContainers) const = 0;
        virtual void
        loadAss(const ::SWA::IdType &Assidentity, CachedTernaryContainerSet &cachedTernaryContainers) const = 0;

      private:
        RelationshipTernarySqlGenerator(const RelationshipTernarySqlGenerator &rhs);
        RelationshipTernarySqlGenerator &operator=(const RelationshipTernarySqlGenerator &rhs);
    };

} // end namespace SQL

#endif
