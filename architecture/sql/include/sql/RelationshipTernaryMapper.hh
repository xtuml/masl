/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_RelationshipTernaryMapper_HH
#define Sql_RelationshipTernaryMapper_HH

#include <sstream>
#include <string>

#include <functional>
#include <memory>
#include <unordered_map>

#include "swa/ObjectPtr.hh"
#include "swa/tuple_hash.hh"
#include "swa/types.hh"

#include "Exception.hh"
#include "Iterator.hh"
#include "RelationshipAssociativeContainers.hh"
#include "RelationshipAtomicity.hh"
#include "RelationshipContainers.hh"
#include "RelationshipSqlRepository.hh"
#include "RelationshipTernaryMapperUnitOfWork.hh"
#include "RelationshipTernarySqlGenerator.hh"
#include "ResourceMonitor.hh"
#include "ResourceMonitorObserver.hh"
#include "Util.hh"
#include "WriteOnChangeEnabler.hh"

namespace SQL {
    // ***********************************************************************
    //! @brief Generic class to handle o-to-m and m-to-o relationship
    //! specifications.
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    class RelationshipTernaryMapper : public ResourceMonitorObserver {
      public:
        enum { identity = rel };

        typedef typename LhsRelContainer::PsObjectPtr LhsPsObjectPtr;
        typedef typename RhsRelContainer::PsObjectPtr RhsPsObjectPtr;
        typedef typename LhsRelContainer::AssocPsObjectPtr AssPsObjectPtr;

        typedef typename LhsRelContainer::PsObjectPtrSet LhsPsObjectPtrSet;
        typedef typename RhsRelContainer::PsObjectPtrSet RhsPsObjectPtrSet;
        typedef typename LhsRelContainer::AssocPsObjectPtrSet AssPsObjectPtrSet;

        typedef typename LhsRelContainer::NavigatedType NavigatedLhsType;
        typedef typename RhsRelContainer::NavigatedType NavigatedRhsType;
        typedef typename AssRelContainer::NavigatedType NavigatedAssType;
        typedef typename LhsRelContainer::NavigatedSetType NavigatedSetType;

        typedef RelationshipTernarySqlGenerator<LhsRelContainer, RhsRelContainer, AssRelContainer> RelSqlGeneratorType;
        typedef RelationshipTernaryMapperUnitOfWork<RelationshipTernaryMapper> UnitOfWorkType;

      public:
        static RelationshipTernaryMapper &singleton();

        void initialise(const ::std::shared_ptr<RelSqlGeneratorType> &generator);
        bool isInitialised() const;

        void setToLoadOnDemand(bool isEnabled);

        void linkFromLhsToRhs(const LhsPsObjectPtr &lhsObj, const RhsPsObjectPtr &rhsObj, const AssPsObjectPtr &assObj);
        void linkFromRhsToLhs(const RhsPsObjectPtr &rhsObj, const LhsPsObjectPtr &lhsObj, const AssPsObjectPtr &assObj);

        void
        unlinkFromLhsToRhs(const LhsPsObjectPtr &lhsObj, const RhsPsObjectPtr &rhsObj, const AssPsObjectPtr &assObj);
        void
        unlinkFromRhsToLhs(const RhsPsObjectPtr &rhsObj, const LhsPsObjectPtr &lhsObj, const AssPsObjectPtr &assObj);

        NavigatedAssType correlateFromLhsToRhs(const LhsPsObjectPtr &lhsObj, const RhsPsObjectPtr &rhsObj);
        NavigatedAssType correlateFromRhsToLhs(const RhsPsObjectPtr &rhsObj, const LhsPsObjectPtr &lhsObj);

        ::std::size_t countFromLhsToRhs(const LhsPsObjectPtr &lhsObj);
        ::std::size_t countFromLhsToAss(const LhsPsObjectPtr &lhsObj);

        ::std::size_t countFromRhsToLhs(const RhsPsObjectPtr &rhsObj);
        ::std::size_t countFromRhsToAss(const RhsPsObjectPtr &rhsObj);

        ::std::size_t countFromAssToLhs(const AssPsObjectPtr &assObj);
        ::std::size_t countFromAssToRhs(const AssPsObjectPtr &assObj);

        void abortLinks();
        void commitLinks(const typename UnitOfWorkType::LinkHashSetType &objects);
        void commitUnlinks(const typename UnitOfWorkType::UnLinkHashSetType &objects);

        void objectDeletedLhs(const LhsPsObjectPtr &lhsObj);
        void objectDeletedRhs(const RhsPsObjectPtr &rhsObj);
        void objectDeletedAss(const AssPsObjectPtr &assObj);

        // Check for dangling relationships before an object is deleted.
        bool hasLinksLhs(const LhsPsObjectPtr &lhsObj);
        bool hasLinksRhs(const RhsPsObjectPtr &rhsObj);
        bool hasLinksAss(const AssPsObjectPtr &assObj);

        const NavigatedRhsType navigateFromLhsToRhs(const LhsPsObjectPtr &lhsObj);
        NavigatedSetType navigateFromLhsToRhs(const LhsPsObjectPtrSet &lhsObjSet);

        const NavigatedLhsType navigateFromRhsToLhs(const RhsPsObjectPtr &rhsObj);
        NavigatedSetType navigateFromRhsToLhs(const RhsPsObjectPtrSet &rhsObjSet);

        const NavigatedAssType navigateFromAssToRhs(const AssPsObjectPtr &assObj);
        NavigatedSetType navigateFromAssToRhs(const AssPsObjectPtrSet &assObjSet);

        const NavigatedAssType navigateFromAssToLhs(const AssPsObjectPtr &assObj);
        NavigatedSetType navigateFromAssToLhs(const AssPsObjectPtrSet &assObjSet);

        const NavigatedLhsType navigateFromRhsToAss(const RhsPsObjectPtr &rhsObj);
        NavigatedSetType navigateFromRhsToAss(const RhsPsObjectPtrSet &rhsObjSet);

        const NavigatedRhsType navigateFromLhsToAss(const LhsPsObjectPtr &lhsObj);
        NavigatedSetType navigateFromLhsToAss(const LhsPsObjectPtrSet &lhsObjSet);

        void forceFlush();

        // resource monitor interface
        void report(ResourceMonitorContext &context);
        void compact(ResourceMonitorContext &context);
        void release(ResourceMonitorContext &context);

      private:
        RelationshipTernaryMapper();
        ~RelationshipTernaryMapper();

      private:
        void loadAll();
        void clearAll();
        void flush();

        void loadLhsToRhsLinks(const ::SWA::IdType &lhsObjId);
        void loadRhsToLhsLinks(const ::SWA::IdType &rhsObjId);
        void loadAssociativeLinks(const ::SWA::IdType &assObjId);

        void displayAllLinks(const std::string &location);

        template <class T>
        void displayContainer(const std::string &label, T &container);

        template <class Tuple>
        void reportError(const Tuple &parameters);

      private:
        RelationshipTernaryMapper(const RelationshipTernaryMapper &rhs);
        RelationshipTernaryMapper &operator=(const RelationshipTernaryMapper &rhs);

        NavigatedAssType correlate(const NavigatedAssType &lhsAssObjId, const NavigatedAssType &rhsAssObjId);
        NavigatedAssType correlate(const NavigatedAssType &lhsAssObjId, const NavigatedSetType &rhsAssObjIdSet);
        NavigatedAssType correlate(const NavigatedSetType &lhsAssObjIdSet, const NavigatedAssType &rhsAssObjId);
        NavigatedAssType correlate(const NavigatedSetType &lhsAssObjIdSet, const NavigatedSetType &rhsAssObjIdSet);

      private:
        bool allLoaded;
        bool loadOnDemand;
        bool writeOnChange;
        UnitOfWorkType unitOfWork;
        ::std::shared_ptr<RelSqlGeneratorType> relSqlGenerator;

        typedef std::unordered_map<::SWA::IdType, RhsRelContainer> LhsLinksType;
        typedef std::unordered_map<::SWA::IdType, LhsRelContainer> RhsLinksType;
        typedef std::unordered_map<::SWA::IdType, AssRelContainer> AssLinksType;

        LhsLinksType lhsLinks;
        RhsLinksType rhsLinks;
        AssLinksType assLinks;

        NavigatedRhsType emptyRhsLinks;
        NavigatedLhsType emptyLhsLinks;
        NavigatedAssType emptyAssLinks;

        typename RelSqlGeneratorType::CachedTernaryContainerSet cachedContainerHandle;
    };

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        RelationshipTernaryMapper()
        : allLoaded(false),
          loadOnDemand(false),
          writeOnChange(false),
          unitOfWork(*this),
          cachedContainerHandle(lhsLinks, rhsLinks, assLinks) {}

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        ~RelationshipTernaryMapper() {}

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC> &
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::singleton() {
        static RelationshipTernaryMapper instance;
        return instance;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::displayAllLinks(
        const std::string &location
    ) {
        std::cout << "R" << rel << " :: " << location << std::endl;
        displayContainer("lhsLinks", lhsLinks);
        displayContainer("rhsLinks", rhsLinks);
        displayContainer("assLinks", assLinks);
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    template <class T>
    void
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::displayContainer(
        const std::string &label, T &container
    ) {
        typename T::iterator containerItr = container.begin();
        typename T::iterator containerEnd = container.end();
        for (; containerItr != containerEnd; ++containerItr) {
            std::cout << label << " " << containerItr->first << " :: ";
            containerItr->second.display(std::cout);
            std::cout << std::endl;
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::initialise(
        const ::std::shared_ptr<RelSqlGeneratorType> &generator
    ) {
        relSqlGenerator = generator;
        unitOfWork.initialise();
        relSqlGenerator->initialise();
        RelationshipSqlRepository::getInstance().registerRelationshipSql(relSqlGenerator.get());

        writeOnChange = WriteOnChangeEnabler(relSqlGenerator->getTableName()).isEnabled();

        // Always cache any persisted object links. This is the default operation,
        // This class will still function correctly if the cache is loaded on
        // demand.
        loadAll();

        // output the contents that have been loaded.
        // displayAllLinks("initialise");
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::forceFlush() {
        unitOfWork.flush();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    bool RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::isInitialised(
    ) const {
        return relSqlGenerator.get() != 0;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::setToLoadOnDemand(
        bool isEnabled
    ) {
        loadOnDemand = isEnabled;
        if (loadOnDemand == true) {
            clearAll();
        } else {
            loadAll();
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::linkFromLhsToRhs(
        const LhsPsObjectPtr &lhsObj, const RhsPsObjectPtr &rhsObj, const AssPsObjectPtr &assObj
    ) {
        const ::SWA::IdType &lhsObjId = lhsObj.getChecked()->getArchitectureId();
        const ::SWA::IdType &rhsObjId = rhsObj.getChecked()->getArchitectureId();
        const ::SWA::IdType &assObjId = assObj.getChecked()->getArchitectureId();

        // The link operation for all the link containers needs to remain
        // consistent in the face of any kind of exception. Therefore if
        // one of the link operations fails, any that have been successful
        // need to be rolled back.

        // Check that the required link data is loaded before
        // undertaking any kind of processing on the cache.
        loadLhsToRhsLinks(lhsObjId);
        loadRhsToLhsLinks(rhsObjId);
        loadAssociativeLinks(assObjId);

        RelationshipAtomicity<LhsLinksType, RhsPsObjectPtr, AssPsObjectPtr, LinkPolicy> atomicLhsLinks(
            lhsLinks, lhsObjId, rhsObj, assObj
        );
        RelationshipAtomicity<RhsLinksType, LhsPsObjectPtr, AssPsObjectPtr, LinkPolicy> atomicRhsLinks(
            rhsLinks, rhsObjId, lhsObj, assObj
        );
        RelationshipAtomicity<AssLinksType, LhsPsObjectPtr, RhsPsObjectPtr, LinkPolicy> atomicAssLinks(
            assLinks, assObjId, lhsObj, rhsObj
        );

        atomicLhsLinks.completed();
        atomicRhsLinks.completed();
        atomicAssLinks.completed();

        unitOfWork.registerLink(lhsObj, rhsObj, assObj);
        flush();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::linkFromRhsToLhs(
        const RhsPsObjectPtr &rhsObj, const LhsPsObjectPtr &lhsObj, const AssPsObjectPtr &assObj
    ) {
        linkFromLhsToRhs(lhsObj, rhsObj, assObj);
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::unlinkFromLhsToRhs(
        const LhsPsObjectPtr &lhsObj, const RhsPsObjectPtr &rhsObj, const AssPsObjectPtr &assObj
    ) {
        const ::SWA::IdType &lhsObjId = lhsObj.getChecked()->getArchitectureId();
        const ::SWA::IdType &rhsObjId = rhsObj.getChecked()->getArchitectureId();
        const ::SWA::IdType &assObjId = assObj.getChecked()->getArchitectureId();

        // Check that the required link data is loaded before
        // undertaking any kind of processing on the cache.
        loadLhsToRhsLinks(lhsObjId);
        loadRhsToLhsLinks(rhsObjId);
        loadAssociativeLinks(assObjId);

        RelationshipAtomicity<LhsLinksType, RhsPsObjectPtr, AssPsObjectPtr, UnlinkPolicy> atomicLhsUnlinks(
            lhsLinks, lhsObjId, rhsObj, assObj
        );
        RelationshipAtomicity<RhsLinksType, LhsPsObjectPtr, AssPsObjectPtr, UnlinkPolicy> atomicRhsUnlinks(
            rhsLinks, rhsObjId, lhsObj, assObj
        );
        RelationshipAtomicity<AssLinksType, LhsPsObjectPtr, RhsPsObjectPtr, UnlinkPolicy> atomicAssUnlinks(
            assLinks, assObjId, lhsObj, rhsObj
        );

        atomicLhsUnlinks.completed();
        atomicRhsUnlinks.completed();
        atomicAssUnlinks.completed();

        unitOfWork.registerUnLink(lhsObj, rhsObj, assObj);
        flush();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::unlinkFromRhsToLhs(
        const RhsPsObjectPtr &rhsObj, const LhsPsObjectPtr &lhsObj, const AssPsObjectPtr &assObj
    ) {
        unlinkFromLhsToRhs(lhsObj, rhsObj, assObj);
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedAssType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
            correlateFromLhsToRhs(const LhsPsObjectPtr &lhsObj, const RhsPsObjectPtr &rhsObj) {
        // Check that the required link data is loaded before
        // undertaking any kind of processing on the cache.

        const NavigatedRhsType lhsToAssObjs = navigateFromLhsToAss(lhsObj);
        const NavigatedLhsType rhsToAssObjs = navigateFromRhsToAss(rhsObj);
        return correlate(lhsToAssObjs, rhsToAssObjs);
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedAssType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
            correlateFromRhsToLhs(const RhsPsObjectPtr &rhsObj, const LhsPsObjectPtr &lhsObj) {
        return correlateFromLhsToRhs(lhsObj, rhsObj);
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    ::std::size_t
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::countFromLhsToRhs(
        const LhsPsObjectPtr &lhsObj
    ) {
        loadLhsToRhsLinks(lhsObj.getChecked()->getArchitectureId());
        typename LhsLinksType::iterator rhsLinksItr = lhsLinks.find(lhsObj.getChecked()->getArchitectureId());
        return rhsLinksItr != lhsLinks.end() ? rhsLinksItr->second.linkCount() : 0;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    ::std::size_t
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::countFromLhsToAss(
        const LhsPsObjectPtr &lhsObj
    ) {
        loadLhsToRhsLinks(lhsObj.getChecked()->getArchitectureId());
        typename LhsLinksType::iterator assLinksItr = lhsLinks.find(lhsObj.getChecked()->getArchitectureId());
        return assLinksItr != lhsLinks.end() ? assLinksItr->second.linkCount() : 0;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    ::std::size_t
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::countFromRhsToLhs(
        const RhsPsObjectPtr &rhsObj
    ) {
        loadRhsToLhsLinks(rhsObj.getChecked()->getArchitectureId());
        typename RhsLinksType::iterator rhsLinksItr = rhsLinks.find(rhsObj.getChecked()->getArchitectureId());
        return rhsLinksItr != rhsLinks.end() ? rhsLinksItr->second.linkCount() : 0;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    ::std::size_t
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::countFromRhsToAss(
        const RhsPsObjectPtr &rhsObj
    ) {
        loadRhsToLhsLinks(rhsObj.getChecked()->getArchitectureId());
        typename RhsLinksType::iterator rhsLinksItr = rhsLinks.find(rhsObj.getChecked()->getArchitectureId());
        return rhsLinksItr != rhsLinks.end() ? rhsLinksItr->second.linkCount() : 0;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    ::std::size_t
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::countFromAssToLhs(
        const AssPsObjectPtr &assObj
    ) {
        loadAssociativeLinks(assObj.getChecked()->getArchitectureId());
        typename AssLinksType::iterator assLinksItr = assLinks.find(assObj.getChecked()->getArchitectureId());
        return assLinksItr != assLinks.end() ? assLinksItr->second.linkCount() : 0;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    ::std::size_t
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::countFromAssToRhs(
        const AssPsObjectPtr &assObj
    ) {
        loadAssociativeLinks(assObj.getChecked()->getArchitectureId());
        typename AssLinksType::iterator assLinksItr = assLinks.find(assObj.getChecked()->getArchitectureId());
        return assLinksItr != assLinks.end() ? assLinksItr->second.linkCount() : 0;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::abortLinks() {
        // The set of cached links will contain some cached values
        // that have not been committed to the database. Therefore
        // clear the cache and repopulate as required.
        clearAll();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::commitLinks(
        const typename UnitOfWorkType::LinkHashSetType &objects
    ) {
        std::for_each(objects.begin(), objects.end(), [&](const auto &obj) {
            relSqlGenerator->commitLink(obj);
        });
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::commitUnlinks(
        const typename UnitOfWorkType::UnLinkHashSetType &objects
    ) {
        std::for_each(objects.begin(), objects.end(), [&](const auto &obj) {
            relSqlGenerator->commitUnlink(obj);
        });
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::objectDeletedLhs(
        const LhsPsObjectPtr &lhsObj
    ) {
        const ::SWA::IdType architectureId = lhsObj.getChecked()->getArchitectureId();

        // The specified object is being deleted
        if (hasLinksLhs(lhsObj) == true) {
            std::ostringstream errorMsgStrm;
            errorMsgStrm << " Found dangling relationship(s) in lhs object of "
                            "associative relationship R"
                         << identity;
            errorMsgStrm << " : lhs  objectId (" << lhsObj.getChecked()->getArchitectureId() << ")";
            throw SqlException(errorMsgStrm.str());
        }
        lhsLinks.erase(architectureId);
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::objectDeletedRhs(
        const RhsPsObjectPtr &rhsObj
    ) {
        const ::SWA::IdType architectureId = rhsObj.getChecked()->getArchitectureId();

        // The specified object is being deleted
        if (hasLinksRhs(rhsObj) == true) {
            std::ostringstream errorMsgStrm;
            errorMsgStrm << " Found dangling relationship(s) in rhs object of "
                            "associative relationship R"
                         << identity;
            errorMsgStrm << " : rhs  objectId (" << architectureId << ")";
            throw SqlException(errorMsgStrm.str());
        }
        rhsLinks.erase(architectureId);
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::objectDeletedAss(
        const AssPsObjectPtr &assObj
    ) {
        const ::SWA::IdType architectureId = assObj.getChecked()->getArchitectureId();

        // The specified object is being deleted
        if (hasLinksAss(assObj) == true) {
            std::ostringstream errorMsgStrm;
            errorMsgStrm << " Found dangling relationship(s) in assoc object of "
                            "associative relationship R"
                         << identity;
            errorMsgStrm << " : associative  objectId (" << architectureId << ")";
            throw SqlException(errorMsgStrm.str());
        }
        assLinks.erase(architectureId);
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    bool RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::hasLinksLhs(
        const LhsPsObjectPtr &lhsObj
    ) {
        const ::SWA::IdType architectureId = lhsObj.getChecked()->getArchitectureId();

        loadLhsToRhsLinks(architectureId);

        typename LhsLinksType::iterator lhsLinksItr = lhsLinks.find(architectureId);
        bool hasLinks = lhsLinksItr != lhsLinks.end() && lhsLinksItr->second.isLinked();
        return hasLinks;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    bool RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::hasLinksRhs(
        const RhsPsObjectPtr &rhsObj
    ) {
        const ::SWA::IdType architectureId = rhsObj.getChecked()->getArchitectureId();

        loadRhsToLhsLinks(architectureId);

        typename RhsLinksType::iterator rhsLinksItr = rhsLinks.find(architectureId);
        bool hasLinks = rhsLinksItr != rhsLinks.end() && rhsLinksItr->second.isLinked();
        return hasLinks;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    bool RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::hasLinksAss(
        const AssPsObjectPtr &assObj
    ) {
        const ::SWA::IdType architectureId = assObj.getChecked()->getArchitectureId();

        loadAssociativeLinks(architectureId);

        typename AssLinksType::iterator assLinksItr = assLinks.find(architectureId);
        bool hasLinks = assLinksItr != assLinks.end() && assLinksItr->second.isLinked();
        return hasLinks;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    const typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedRhsType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
            navigateFromLhsToRhs(const LhsPsObjectPtr &lhsObj) {
        const ::SWA::IdType architectureId = lhsObj.getChecked()->getArchitectureId();

        // Check that the required link data is loaded before
        // undertaking any kind of processing on the cache.
        loadLhsToRhsLinks(architectureId);

        typename LhsLinksType::iterator lhsLinksItr = lhsLinks.find(architectureId);
        if (lhsLinksItr == lhsLinks.end()) {
            return emptyRhsLinks;
        }
        return lhsLinksItr->second.navigateRelated();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedSetType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
            navigateFromLhsToRhs(const LhsPsObjectPtrSet &lhsObjSet) {
        NavigatedSetType objectSet;
        typename LhsPsObjectPtrSet::const_iterator objItr = lhsObjSet.begin();
        typename LhsPsObjectPtrSet::const_iterator endItr = lhsObjSet.end();
        for (; objItr != endItr; ++objItr) {
            const LhsPsObjectPtr &lhsObjPtr = *objItr;
            loadLhsToRhsLinks(lhsObjPtr.getChecked()->getArchitectureId());
            typename LhsLinksType::iterator lhsLinkItr = lhsLinks.find(lhsObjPtr.getChecked()->getArchitectureId());
            if (lhsLinkItr != lhsLinks.end()) {
                lhsLinkItr->second.navigateRelatedWithSet(objectSet);
            }
        }
        objectSet.forceUnique();
        return objectSet;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    const typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedLhsType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
            navigateFromRhsToLhs(const RhsPsObjectPtr &rhsObj) {
        const ::SWA::IdType architectureId = rhsObj.getChecked()->getArchitectureId();

        // Check that the required link data is loaded before
        // undertaking any kind of processing on the cache.
        loadRhsToLhsLinks(architectureId);

        // Do not check the conditionality here as conditionality
        // only needs to be consistent at the end of a transaction
        typename RhsLinksType::iterator rhsLinksItr = rhsLinks.find(architectureId);
        if (rhsLinksItr == rhsLinks.end()) {
            return emptyLhsLinks;
        }
        return rhsLinksItr->second.navigateRelated();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedSetType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
            navigateFromRhsToLhs(const RhsPsObjectPtrSet &rhsObjSet) {
        NavigatedSetType objectSet;
        typename RhsPsObjectPtrSet::const_iterator objItr = rhsObjSet.begin();
        typename RhsPsObjectPtrSet::const_iterator endItr = rhsObjSet.end();
        for (; objItr != endItr; ++objItr) {
            const RhsPsObjectPtr &rhsObjPtr = *objItr;
            loadRhsToLhsLinks(rhsObjPtr.getChecked()->getArchitectureId());
            typename RhsLinksType::iterator rhsLinkItr = rhsLinks.find(rhsObjPtr.getChecked()->getArchitectureId());
            if (rhsLinkItr != rhsLinks.end()) {
                rhsLinkItr->second.navigateRelatedWithSet(objectSet);
            }
        }
        objectSet.forceUnique();
        return objectSet;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    const typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedAssType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
            navigateFromAssToRhs(const AssPsObjectPtr &assObj) {
        const ::SWA::IdType architectureId = assObj.getChecked()->getArchitectureId();

        // Check that the required link data is loaded before
        // undertaking any kind of processing on the cache.
        loadAssociativeLinks(architectureId);

        // Do not check the conditionality here as conditionality
        // only needs to be consistent at the end of a transaction
        typename AssLinksType::iterator assLinksItr = assLinks.find(architectureId);
        if (assLinksItr == assLinks.end()) {
            return emptyAssLinks;
        }
        return assLinksItr->second.navigateAssoc();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedSetType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
            navigateFromAssToRhs(const AssPsObjectPtrSet &assObjSet) {
        NavigatedSetType objectSet;
        typename AssPsObjectPtrSet::const_iterator objItr = assObjSet.begin();
        typename AssPsObjectPtrSet::const_iterator endItr = assObjSet.end();
        for (; objItr != endItr; ++objItr) {
            const AssPsObjectPtr &assObjPtr = *objItr;
            loadAssociativeLinks(assObjPtr.getChecked()->getArchitectureId());
            typename AssLinksType::iterator assLinkItr = assLinks.find(assObjPtr.getChecked()->getArchitectureId());
            if (assLinkItr != assLinks.end()) {
                assLinkItr->second.navigateAssocWithSet(objectSet);
            }
        }
        objectSet.forceUnique();
        return objectSet;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    const typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedAssType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
            navigateFromAssToLhs(const AssPsObjectPtr &assObj) {
        const ::SWA::IdType architectureId = assObj.getChecked()->getArchitectureId();

        // Check that the required link data is loaded before
        // undertaking any kind of processing on the cache.
        loadAssociativeLinks(architectureId);

        // Do not check the conditionality here as conditionality
        // only needs to be consistent at the end of a transaction
        typename AssLinksType::iterator assLinksItr = assLinks.find(architectureId);
        if (assLinksItr == assLinks.end()) {
            return emptyAssLinks;
        }
        return assLinksItr->second.navigateRelated();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedSetType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
            navigateFromAssToLhs(const AssPsObjectPtrSet &assObjSet) {
        NavigatedSetType objectSet;
        typename AssPsObjectPtrSet::const_iterator objItr = assObjSet.begin();
        typename AssPsObjectPtrSet::const_iterator endItr = assObjSet.end();
        for (; objItr != endItr; ++objItr) {
            const AssPsObjectPtr &assObjPtr = *objItr;
            loadAssociativeLinks(assObjPtr.getChecked()->getArchitectureId());
            typename AssLinksType::iterator assLinkItr = assLinks.find(assObjPtr.getChecked()->getArchitectureId());
            if (assLinkItr != assLinks.end()) {
                assLinkItr->second.navigateRelatedWithSet(objectSet);
            }
        }
        objectSet.forceUnique();
        return objectSet;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    const typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedLhsType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
            navigateFromRhsToAss(const RhsPsObjectPtr &rhsObj) {
        const ::SWA::IdType architectureId = rhsObj.getChecked()->getArchitectureId();

        // Check that the required link data is loaded before
        // undertaking any kind of processing on the cache.
        loadRhsToLhsLinks(architectureId);

        // Do not check the conditionality here as conditionality
        // only needs to be consistent at the end of a transaction
        typename RhsLinksType::iterator rhsLinksItr = rhsLinks.find(architectureId);
        if (rhsLinksItr == rhsLinks.end()) {
            return emptyLhsLinks;
        }
        return rhsLinksItr->second.navigateAssoc();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedSetType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
            navigateFromRhsToAss(const RhsPsObjectPtrSet &rhsObjSet) {
        NavigatedSetType objectSet;
        typename RhsPsObjectPtrSet::const_iterator objItr = rhsObjSet.begin();
        typename RhsPsObjectPtrSet::const_iterator endItr = rhsObjSet.end();
        for (; objItr != endItr; ++objItr) {
            const RhsPsObjectPtr &rhsObjPtr = *objItr;
            loadRhsToLhsLinks(rhsObjPtr.getChecked()->getArchitectureId());
            typename RhsLinksType::iterator rhsLinkItr = rhsLinks.find(rhsObjPtr.getChecked()->getArchitectureId());
            if (rhsLinkItr != rhsLinks.end()) {
                rhsLinkItr->second.navigateAssocWithSet(objectSet);
            }
        }
        objectSet.forceUnique();
        return objectSet;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    const typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedRhsType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
            navigateFromLhsToAss(const LhsPsObjectPtr &lhsObj) {
        const ::SWA::IdType architectureId = lhsObj.getChecked()->getArchitectureId();

        // Check that the required link data is loaded before
        // undertaking any kind of processing on the cache.
        loadLhsToRhsLinks(architectureId);

        // Do not check the conditionality here as conditionality
        // only needs to be consistent at the end of a transaction
        typename LhsLinksType::iterator lhsLinksItr = lhsLinks.find(architectureId);
        if (lhsLinksItr == lhsLinks.end()) {
            return emptyRhsLinks;
        }
        return lhsLinksItr->second.navigateAssoc();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedSetType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
            navigateFromLhsToAss(const LhsPsObjectPtrSet &lhsObjSet) {
        NavigatedSetType objectSet;
        typename LhsPsObjectPtrSet::const_iterator objItr = lhsObjSet.begin();
        typename LhsPsObjectPtrSet::const_iterator endItr = lhsObjSet.end();
        for (; objItr != endItr; ++objItr) {
            const LhsPsObjectPtr &lhsObjPtr = *objItr;
            loadLhsToRhsLinks(lhsObjPtr.getChecked()->getArchitectureId());
            typename LhsLinksType::iterator lhsLinkItr = lhsLinks.find(lhsObjPtr.getChecked()->getArchitectureId());
            if (lhsLinkItr != lhsLinks.end()) {
                lhsLinkItr->second.navigateAssocWithSet(objectSet);
            }
        }
        objectSet.forceUnique();
        return objectSet;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::report(
        ResourceMonitorContext &context
    ) {}

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::compact(
        ResourceMonitorContext &context
    ) {}

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::release(
        ResourceMonitorContext &context
    ) {}

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::loadAll() {
        relSqlGenerator->loadAll(cachedContainerHandle);
        allLoaded = true;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::clearAll() {
        allLoaded = false;
        lhsLinks.clear();
        rhsLinks.clear();
        assLinks.clear();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::loadLhsToRhsLinks(
        const ::SWA::IdType &lhsObjId
    ) {
        // Load the Rhs side of a relationship. Before attempting load
        // check that the the specified lhs object has not already been
        // loaded and cached.
        if (allLoaded == false) {
            relSqlGenerator->loadRhs(lhsObjId, cachedContainerHandle);
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void
    RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::loadRhsToLhsLinks(
        const ::SWA::IdType &rhsObjectId
    ) {
        if (allLoaded == false) {
            relSqlGenerator->loadLhs(rhsObjectId, cachedContainerHandle);
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        loadAssociativeLinks(const ::SWA::IdType &assObjId) {
        if (allLoaded == false) {
            relSqlGenerator->loadAss(assObjId, cachedContainerHandle);
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedAssType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::correlate(
            const NavigatedAssType &lhsAssObjId, const NavigatedAssType &rhsAssObjId
        ) {
        return lhsAssObjId == rhsAssObjId ? lhsAssObjId : 0;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedAssType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::correlate(
            const NavigatedAssType &lhsAssObjId, const NavigatedSetType &rhsAssObjIdSet
        ) {
        return rhsAssObjIdSet.find(lhsAssObjId) != rhsAssObjIdSet.end() ? lhsAssObjId : 0;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedAssType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::correlate(
            const NavigatedSetType &lhsAssObjIdSet, const NavigatedAssType &rhsAssObjId
        ) {
        return lhsAssObjIdSet.find(rhsAssObjId) != lhsAssObjIdSet.end() ? rhsAssObjId : 0;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    typename RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::
        NavigatedAssType
        RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::correlate(
            const NavigatedSetType &lhsAssObjIdSet, const NavigatedSetType &rhsAssObjIdSet
        ) {
        NavigatedSetType intersectedSet;
        std::set_intersection(
            lhsAssObjIdSet.begin(),
            lhsAssObjIdSet.end(),
            rhsAssObjIdSet.begin(),
            rhsAssObjIdSet.end(),
            associativeInserter(intersectedSet)
        );
        return intersectedSet.size() == 1 ? (*intersectedSet.begin()) : 0;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <int rel, class LhsRelContainer, class RhsRelContainer, class AssRelContainer, bool OneC, bool ManyC>
    void RelationshipTernaryMapper<rel, LhsRelContainer, RhsRelContainer, AssRelContainer, OneC, ManyC>::flush() {
        if (writeOnChange == true) {
            unitOfWork.flush();
        }
    }

} // namespace SQL
#endif
