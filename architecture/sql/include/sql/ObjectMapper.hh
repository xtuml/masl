/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_ObjectMapper_HH
#define Sql_ObjectMapper_HH

#include <vector>

#include <functional>
#include <memory>

#include "swa/ObjectPtr.hh"
#include "swa/Set.hh"

#include "CacheStrategy.hh"
#include "Criteria.hh"
#include "Exception.hh"
#include "ObjectMapperItr.hh"
#include "ObjectMapperUnitOfWork.hh"
#include "ObjectSqlGenerator.hh"
#include "ObjectSqlRepository.hh"
#include "ResourceMonitor.hh"
#include "ResourceMonitorObserver.hh"
#include "Util.hh"
#include "WriteOnChangeEnabler.hh"

namespace SQL {

    // *****************************************************************
    //! \brief
    //!
    //!
    // *****************************************************************
    template <class ObjectBase, class ObjectDerived>
    class ObjectMapper : public ResourceMonitorObserver {
      public:
        typedef typename PsObject_Traits<ObjectBase>::PsObject PsBaseObject;
        typedef typename PsObject_Traits<ObjectBase>::PsObjectPtr PsBaseObjectPtr;
        typedef typename PsObject_Traits<ObjectBase>::PsObjectPtrSwaSet PsBaseObjectPtrSwaSet;

        typedef typename PsObject_Traits<ObjectDerived>::PsObject PsObject;
        typedef typename PsObject_Traits<ObjectDerived>::PsObjectPtr PsObjectPtr;
        typedef typename PsObject_Traits<ObjectDerived>::PsObjectIdSet PsObjectIdSet;
        typedef typename PsObject_Traits<ObjectDerived>::PsObjectPtrSet PsObjectPtrSet;

        typedef typename PsObject_Traits<ObjectDerived>::PsSharedPtr PsSharedPtr;
        typedef typename PsObject_Traits<ObjectDerived>::PsCachedPtrSet PsCachedPtrSet;
        typedef typename PsObject_Traits<ObjectDerived>::PsCachedPtrMap PsCachedPtrMap;

        // ********************************************************************
        //! Undertake the required initialisation for this class. Should only
        //! be done once.
        //! \return true for the successfull initialisation of this instance
        // ********************************************************************
        bool initialise();

        // ********************************************************************
        //! \return the number of objects
        // ********************************************************************
        ::std::size_t size() const;

        // ********************************************************************
        //! \return the next available unique architecute id.
        // ********************************************************************
        ::SWA::IdType getNextArchId();

        // ********************************************************************
        //! \return the name of the MASL object supported bythis mapper
        // ********************************************************************
        std::string getObjectName() const {
            return sqlGenerator->getObjectName();
        }

        // ********************************************************************
        //! \return the name of the SQL Table by this mapper.
        // ********************************************************************
        std::string getTableName() const {
            return sqlGenerator->getTableName();
        }

        // ********************************************************************
        //! Mark the specified Object as having been modified and therefore
        //! requiring changes to be applied to the database on the next commit.
        //! \param  identity the unique identifer for the modified object
        // ********************************************************************
        void markAsDirty(const ::SWA::IdType identity);

        // ********************************************************************
        //! To be efficient, changes to individual objects will not be written to
        //! the database straight away. The modifications will be stored until a
        //! point just before the next database commit. Rather than using this
        //! behaviour, this method can be used to change the behaviour so that the
        //! default batch operation is replaced with an implementation that
        //! undertakes a database write on each modification. This method can be
        //! called at any time, to turn on and off the batch behaviour.
        //!
        //! \param enable false for batch change or true for single change operation
        // ********************************************************************
        void writeOnChange(const bool enable);

        // ********************************************************************
        //! \return the object corresponding to the specified identifier.
        // ********************************************************************
        PsObjectPtr getInstance(const ::SWA::IdType architectureId);

        // ********************************************************************
        //! remove the object corresponding to the specified identifier.
        // ********************************************************************
        virtual void deleteInstance(const PsBaseObjectPtr object);

        // ********************************************************************
        //! \return Any one of the possible objects, or null if zero population.
        // ********************************************************************
        PsObjectPtr findOne();

        // ********************************************************************
        //! \return a set of all the object instances.
        // ********************************************************************
        void findAll(::SWA::Set<PsBaseObjectPtr> &objectSet);

        // ********************************************************************
        //! Find the object with the specified unique architecture id
        //! \return The object with the specified unqiue architecture Id
        // ********************************************************************
        PsObjectPtr find(const ::SWA::IdType identity);

        // ********************************************************************
        //!  Find the objects with the specified unique architecture ids
        //! \return The objects with the specified unqiue architecture Ids
        // ********************************************************************
        PsObjectPtrSet find(const PsObjectIdSet &identitySet);

        // ********************************************************************
        //! Force all dirty objects to have their modifications applied to the
        //! database. Required to make sure database is upto date with modifications
        //! before a select opertaion is performed on the associated table.
        // ********************************************************************
        void forceFlush();

        // ********************************************************************
        //! Each object mapper instance can be associated with one of many
        //! different kinds of caching strategy's via the configuration of
        //! the CacheStrategyFactory. The functionality of these strategy's
        //! can be configured so that only a limited number (or zero) of the
        //! application objects will be cached; perhaps to reduce memory footprint.
        //! If this is the case then the first level caching provided by the
        //! population class for finds using preferred ids has to be disabled as
        //! the complete object population is not cached.
        //!
        //! @returns true if the caching strategy allows all objects to be cached.
        // ********************************************************************
        bool fullCachingEnabled() const;

        // Interfaces required by the templated objectMapperUnitOfWork class.
        void committed();
        void abort();
        void commitUpdate(const PsObjectPtrSet &objects, std::string &sql);
        void commitInsert(const PsObjectPtrSet &objects, std::string &sql);
        void commitDelete(const std::vector<SWA::IdType> &objects, std::string &sql);

        // resource monitor interface
        void report(ResourceMonitorContext &context);
        void compact(ResourceMonitorContext &context);
        void release(ResourceMonitorContext &context);

        void selectOne(::std::function<bool(PsObject *)> &predicate, PsBaseObjectPtr &object);
        void selectOne(Criteria &sqlSelector, PsBaseObjectPtr &object);

        void selectAll(::std::function<bool(PsObject *)> &predicate, PsBaseObjectPtrSwaSet &objectSet);
        void selectAll(Criteria &sqlSelector, PsBaseObjectPtrSwaSet &objectSet);

        void selectOneFromCache(
            const std::string &predicateDebug, ::std::function<bool(PsObject *)> &predicate, PsBaseObjectPtr &objectSet
        );
        void selectAllFromCache(
            const std::string &predicateDebug,
            ::std::function<bool(PsObject *)> &predicate,
            PsBaseObjectPtrSwaSet &objectSet
        );

      protected:
        ObjectMapper(const std::shared_ptr<ObjectSqlGenerator<PsBaseObject, PsObject>> &sqlGenerator);
        virtual ~ObjectMapper();

        void flushCache();
        virtual bool doPostInit();
        bool allowInMemoryFind();

        void load(const ::SWA::IdType instanceCount);
        void loadAll();
        void loadSet(const PsObjectIdSet &identity);
        void loadSingle(const ::SWA::IdType architectureId);

        typename PsCachedPtrMap::iterator locate(const ::SWA::IdType &identity);

      protected:
        PsCachedPtrMap cache;
        std::vector<PsSharedPtr> deletedCache;

        bool writeOnModify;
        bool allLoaded;

        ::SWA::IdType objectCount;
        ::SWA::IdType currentArchIdentifier;

        std::shared_ptr<ObjectSqlGenerator<PsBaseObject, PsObject>> sqlGenerator;

        ObjectMapperUnitOfWork<ObjectMapper> unitOfWorkMap;
        std::shared_ptr<CacheStrategy> cacheStrategy;
    };

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    ObjectMapper<ObjectBase, ObjectDerived>::ObjectMapper(
        const std::shared_ptr<ObjectSqlGenerator<PsBaseObject, PsObject>> &sqlGenerator
    )
        : writeOnModify(false),
          allLoaded(false),
          objectCount(0),
          currentArchIdentifier(0),
          sqlGenerator(sqlGenerator),
          unitOfWorkMap(*this),
          cacheStrategy(CacheStrategyFactory::singleton().getStrategy(
              sqlGenerator->getDomainName(), sqlGenerator->getObjectName()
          )) {}

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    ObjectMapper<ObjectBase, ObjectDerived>::~ObjectMapper() {
        cache.clear();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    ::std::size_t ObjectMapper<ObjectBase, ObjectDerived>::size() const {
        return objectCount;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    bool ObjectMapper<ObjectBase, ObjectDerived>::fullCachingEnabled() const {
        // The objects cached in this object mapper might also be cached by the
        // population class as part of the find optimisation that has been applied
        // to finds on preferred attributes. If the installed strategy limits the
        // number of the object instances cached then the caching in the population
        // class has to be disabled as it will not contain the full population.
        return cacheStrategy->allowFullCaching(objectCount);
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    ::SWA::IdType ObjectMapper<ObjectBase, ObjectDerived>::getNextArchId() {
        ++objectCount;
        return ++currentArchIdentifier;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    bool ObjectMapper<ObjectBase, ObjectDerived>::initialise() {
        sqlGenerator->initialise();
        ObjectSqlRepository::getInstance().registerObjectSql(sqlGenerator.get());

        currentArchIdentifier = sqlGenerator->executeGetMaxIdentifier();
        objectCount = sqlGenerator->executeGetRowCount();
        unitOfWorkMap.initialise();

        if (objectCount == 0) {
            allLoaded = true;
        } else {
            load(cacheStrategy->getOperationalCount(objectCount));
        }

        writeOnModify = WriteOnChangeEnabler(sqlGenerator->getTableName()).isEnabled();
        ResourceMonitor::singleton().registerActiveResource(this);
        return doPostInit();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    bool ObjectMapper<ObjectBase, ObjectDerived>::doPostInit() {
        return true;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::markAsDirty(const ::SWA::IdType identity) {
        // For an object to be marked as dirty it must already exist in the cache
        typename PsCachedPtrMap::iterator objectItr = locate(identity);
        unitOfWorkMap.registerUpdate(PsObjectPtr(objectItr->second.get()));
        flushCache();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::writeOnChange(const bool enable) {
        writeOnModify = true;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    typename ObjectMapper<ObjectBase, ObjectDerived>::PsObjectPtr
    ObjectMapper<ObjectBase, ObjectDerived>::getInstance(const ::SWA::IdType architectureId) {
        loadSingle(architectureId);

        PsObjectPtr obj;
        typename PsCachedPtrMap::iterator cacheItr = cache.find(architectureId);
        if (cacheItr != cache.end()) {
            obj = PsObjectPtr(cacheItr->second.get());
        }

        return obj;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::deleteInstance(const PsBaseObjectPtr object) {
        // For an object to be deleted it must already have a handle in the cache.
        ::SWA::IdType architectureId = object->getArchitectureId();
        typename PsCachedPtrMap::iterator cacheItr = locate(architectureId);
        unitOfWorkMap.registerDelete(PsObjectPtr(cacheItr->second.get()));

        // Hold on to the pointer to the cached object as it may have been used
        // by other mappers (i.e. relationshipUnitOfWorkMapper during link/unlink
        // requests) during the current transaction. Once the transaction has been
        // committed then this set of delete objects can be cleared.
        deletedCache.push_back(cacheItr->second);

        cache.erase(cacheItr);
        --objectCount;
        flushCache();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    typename ObjectMapper<ObjectBase, ObjectDerived>::PsObjectPtr ObjectMapper<ObjectBase, ObjectDerived>::findOne() {
        // If there are objects available, but the cache
        // is empty then load some into the cache.
        if (objectCount != 0 && cache.empty() == true) {
            // If the cacheStrategy has a zero init load count, then as a find one
            // is being undertaken make sure one object is loaded.
            load(
                (cacheStrategy->getOperationalCount(objectCount) == 0 ? 1
                                                                      : cacheStrategy->getOperationalCount(objectCount))
            );
        }
        return cache.begin() == cache.end() ? PsObjectPtr() : PsObjectPtr((*cache.begin()).second.get());
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::findAll(::SWA::Set<PsBaseObjectPtr> &objectSet) {
        loadAll();
        objectSet.reserve(cache.size());
        std::copy(cache.begin(), cache.end(), MapToSetLoader(cache, objectSet));
        objectSet.forceUnique();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    typename ObjectMapper<ObjectBase, ObjectDerived>::PsObjectPtr
    ObjectMapper<ObjectBase, ObjectDerived>::find(const ::SWA::IdType architectureId) {
        // This method is called when a one-to-one relationship has been navigated
        // if no relationship actually exists then the specified architecture id
        // will be zero valued. Detect this condition and return a null object.
        return getInstance(architectureId);
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    typename ObjectMapper<ObjectBase, ObjectDerived>::PsObjectPtrSet
    ObjectMapper<ObjectBase, ObjectDerived>::find(const PsObjectIdSet &identitySet) {
        loadSet(identitySet);
        PsObjectPtrSet objectSet(identitySet.size());
        std::copy(identitySet.begin(), identitySet.end(), MapToSetKeyComparator(cache, objectSet));
        return objectSet;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    bool ObjectMapper<ObjectBase, ObjectDerived>::allowInMemoryFind() {
        bool allowFind = false;
        if (allLoaded == true) {
            // If a findAll has already occurred within the current transaction
            // then as all the objects have been serialised from the database
            // it might prove more efficient to undertake the find on the cached
            // object set. Check if the caching stratergy allows linear finds
            // and if so return a true result.
            allowFind = cacheStrategy->allowLinearFind(objectCount);
        } else {
            if (cacheStrategy->allowFullCaching(objectCount) && cacheStrategy->allowLinearFind(objectCount)) {
                loadAll();
                allowFind = true;
            }
        }
        return allowFind;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::forceFlush() {
        unitOfWorkMap.flush();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::load(const ::SWA::IdType instanceCount) {
        if ((instanceCount > 0) && (instanceCount > cache.size())) {
            Criteria criteria;
            criteria.addAllColumn();
            criteria.addFromClause(sqlGenerator->getTableName());
            criteria.setLimit(instanceCount);
            sqlGenerator->executeSelect(cache, criteria);
            if (objectCount == cache.size()) {
                allLoaded = true;
            }
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::loadAll() {
        if (allLoaded == false) {
            unitOfWorkMap.flush();
            Criteria criteria;
            criteria.addAllColumn();
            criteria.addFromClause(sqlGenerator->getTableName());
            sqlGenerator->executeSelect(cache, criteria);
            allLoaded = true;
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::loadSingle(const ::SWA::IdType architectureId) {
        if (allLoaded == false) {
            if (cache.find(architectureId) == cache.end()) {
                Criteria criteria;
                criteria.addAllColumn();
                criteria.addFromClause(sqlGenerator->getTableName());
                std::string whereCondition =
                    sqlGenerator->getColumnName("architecture_id") + " = " + valueToString(architectureId);
                criteria.addWhereClause(whereCondition);
                sqlGenerator->executeSelect(cache, criteria);
            }
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::loadSet(const PsObjectIdSet &identitySet) {
        if (allLoaded == false) {
            PsObjectIdSet missingSet;
            typename PsObjectIdSet::const_iterator idItr = identitySet.begin();
            typename PsObjectIdSet::const_iterator idEnd = identitySet.end();
            for (; idItr != idEnd; ++idItr) {
                ::SWA::IdType unqiueKey = *idItr;
                if (std::find_if(
                        cache.begin(),
                        cache.end(),
                        AssociativeKeyAccessor<typename PsCachedPtrMap::value_type>(unqiueKey)
                    ) == cache.end()) {
                    missingSet += unqiueKey;
                }
            }

            if (!missingSet.empty()) {
                // The request could not be satisfied by the cached objects,
                // therefore depending on the number of unsatisfied requests
                // either query for the missing set or just load the whole
                // instance population.
                if (fullCachingEnabled()) {
                    loadAll();
                } else {

                    std::ostringstream whereInCondition;
                    whereInCondition << " architecture_id IN (";
                    for (typename PsObjectIdSet::iterator missingItr = missingSet.begin();
                         missingItr != missingSet.end();
                         ++missingItr) {
                        if (missingItr != missingSet.begin()) {
                            whereInCondition << ", ";
                        }
                        whereInCondition << (*missingItr);
                    }
                    whereInCondition << ")";

                    Criteria missingObjectCritera;
                    missingObjectCritera.addAllColumn();
                    missingObjectCritera.addFromClause(sqlGenerator->getTableName());
                    missingObjectCritera.addWhereClause(whereInCondition.str());
                    sqlGenerator->executeSelect(cache, missingObjectCritera);
                }
            }
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::flushCache() {
        // If the write on modify has been set then flush any pending changes
        // directly to the database. This can be useful for debugging the SQL
        // that is being actioned.
        if (writeOnModify == true) {
            unitOfWorkMap.flush();
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    typename ObjectMapper<ObjectBase, ObjectDerived>::PsCachedPtrMap::iterator
    ObjectMapper<ObjectBase, ObjectDerived>::locate(const ::SWA::IdType &identity) {
        typename PsCachedPtrMap::iterator locatedObjItr = cache.find(identity);
        if (locatedObjItr == cache.end()) {
            throw SqlException(::boost::make_tuple(
                "ObjectMapper::locate failed for",
                sqlGenerator->getDomainName(),
                sqlGenerator->getObjectName(),
                "with identify",
                identity
            ));
        }
        return locatedObjItr;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::report(ResourceMonitorContext &context) {
        if (context.getReportStream() != 0) {
            std::ostream *reportStrm = context.getReportStream();
            (*reportStrm) << "ObjectMapper[" << sqlGenerator->getTableName() << "]" << std::endl;
            (*reportStrm) << "  cache  size           : " << cache.size() << std::endl;
            (*reportStrm) << "  deleted cache size    : " << deletedCache.size() << std::endl;
            (*reportStrm) << "  object size           : " << objectCount << std::endl;
            (*reportStrm) << "  allLoaded             : " << allLoaded << std::endl;
            (*reportStrm) << "  currentArchIdentifier : " << currentArchIdentifier << std::endl;
            (*reportStrm) << "ObjectStrategy [" << cacheStrategy->getName() << "]" << std::endl;
            (*reportStrm) << "   allowFullCaching    : " << cacheStrategy->allowFullCaching(objectCount) << std::endl;
            (*reportStrm) << "   allowLinearFind     : " << cacheStrategy->allowLinearFind(objectCount) << std::endl;
            (*reportStrm) << "   getOperationalCount : " << cacheStrategy->getOperationalCount(objectCount)
                          << std::endl;
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::compact(ResourceMonitorContext &context) {
        // The resource monitor calls the list of registered observers when the
        // current transaction has been committed (is called from within the
        // DatabaseUnitOfWork class. As the transaction has been committed
        // no references to object instances should exist outside of this container
        // Therefore the cache can be safely modified, in this case reduced in size
        // to the number allowed by the registered stratergy implementation.

        if (!cacheStrategy->allowFullCaching(objectCount)) {
            if (cacheStrategy->getOperationalCount(objectCount) != 0) {
                if (cache.size() > cacheStrategy->getOperationalCount(objectCount)) {
                    typename PsCachedPtrMap::iterator cacheStartItr = cache.begin();
                    typename PsCachedPtrMap::iterator cacheEndItr = cache.begin();
                    unsigned int purgeCount = cache.size() - cacheStrategy->getOperationalCount(objectCount);
                    std::advance(cacheEndItr, purgeCount);
                    cache.erase(cacheStartItr, cacheEndItr);
                    allLoaded = false;
                }
            } else {
                cache.clear();
                allLoaded = false;
            }
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::release(ResourceMonitorContext &context) {
        cache.clear();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::selectOne(
        ::std::function<bool(PsObject *)> &predicate, PsBaseObjectPtr &object
    ) {
        if (allowInMemoryFind()) {
            std::find_if(cache.begin(), cache.end(), singleObjectSelector(cache, object, predicate));
        } else {
            throw SqlException(::boost::make_tuple(
                "ObjectMapper::selectOne failed for",
                sqlGenerator->getDomainName(),
                sqlGenerator->getObjectName(),
                "in memory find not allowed"
            ));
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::selectOne(Criteria &sqlSelector, PsBaseObjectPtr &object) {
        // As the find is not being done using the current transient population
        // need to write any pending changes into the database, so the select
        // can act upon the correct data set.
        unitOfWorkMap.flush();
        PsBaseObjectPtrSwaSet objectSet;
        sqlGenerator->executeSelect(cache, sqlSelector, objectSet);
        if (objectSet.size() != 0) {
            object = (*objectSet.begin());
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::selectAll(
        ::std::function<bool(PsObject *)> &predicate, PsBaseObjectPtrSwaSet &objectSet
    ) {
        if (allowInMemoryFind()) {
            std::for_each(cache.begin(), cache.end(), objectMapperSelector(cache, objectSet, predicate));
            objectSet.forceUnique();
        } else {
            throw SqlException(::boost::make_tuple(
                "ObjectMapper::selectAll failed for",
                sqlGenerator->getDomainName(),
                sqlGenerator->getObjectName(),
                "in memory find not allowed"
            ));
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::selectAll(Criteria &sqlSelector, PsBaseObjectPtrSwaSet &objectSet) {
        // As the find is not being done using the current transient population
        // need to write any pending changes into the database, so the select
        // can act upon the correct data set.
        unitOfWorkMap.flush();
        sqlGenerator->executeSelect(cache, sqlSelector, objectSet);
        objectSet.forceUnique();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::selectOneFromCache(
        const std::string &predicateDebug, ::std::function<bool(PsObject *)> &predicate, PsBaseObjectPtr &object
    ) {
        // For Objects that contain attributes with complex types. Any finds
        // undertaken that require access to any fields of the complex data cannot
        // be undertaken by an SQL query on the database, as complex types are
        // stored as ASN.1 encoded binary blobs. Therefore the only way the find can
        // be satisfied is to load all the data into transient memory and undertake
        // a linear search. This might go against the caching stratergy employed by
        // the object.

        // Only allow error to be reported once per transaction.
        if (allLoaded == false && !allowInMemoryFind()) {
            // Cache is not supposed to undertake inmemory finds, but query contains
            // references to complex data, so have no choice but to cache all data
            // and undertake linear find on cache, so report warning.
            std::cout << "Warning  ObjectMapper::selectOneFromCache for table " << sqlGenerator->getTableName()
                      << " - using cache which CacheStrategy prohibits, for find "
                         "expression using complex type "
                      << predicateDebug << std::endl;
        }
        loadAll();
        std::find_if(cache.begin(), cache.end(), singleObjectSelector(cache, object, predicate));
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::selectAllFromCache(
        const std::string &predicateDebug,
        ::std::function<bool(PsObject *)> &predicate,
        PsBaseObjectPtrSwaSet &objectSet
    ) {
        // For Objects that contain attributes with complex types. Any finds
        // undertaken that require access to any fields of the complex data cannot
        // be undertaken by an SQL query on the database, as complex types are
        // stored as ASN.1 encoded binary blobs. Therefore the only way the find can
        // be satisfied is to load all the data into transient memory and undertake
        // a linear search. This might go against the caching stratergy employed by
        // the object.

        // Only allow error to be reported once per transaction
        // that causes all the objects to be loaded.
        if (allLoaded == false && !allowInMemoryFind()) {
            // Cache is not supposed to undertake inmemory finds, but query contains
            // references to complex data, so have no choice but to cache all data
            // and undertake linear find on cache, so report warning.
            std::cout << "Warning  ObjectMapper::selectAllFromCache for table " << sqlGenerator->getTableName()
                      << " - using cache which CacheStrategy prohibits, for find "
                         "expression using complex type"
                      << predicateDebug << std::endl;
        }
        loadAll();
        std::for_each(cache.begin(), cache.end(), objectMapperSelector(cache, objectSet, predicate));
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::committed() {
        deletedCache.clear();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::abort() {
        cache.clear();
        deletedCache.clear();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::commitUpdate(const PsObjectPtrSet &objects, std::string &sql) {
        std::for_each(objects.begin(), objects.end(), [&](const auto &obj) {
            sqlGenerator->executeUpdate(obj);
        });
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void ObjectMapper<ObjectBase, ObjectDerived>::commitInsert(const PsObjectPtrSet &objects, std::string &sql) {
        std::for_each(objects.begin(), objects.end(), [&](const auto &obj) {
            sqlGenerator->executeInsert(obj);
        });
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class ObjectBase, class ObjectDerived>
    void
    ObjectMapper<ObjectBase, ObjectDerived>::commitDelete(const std::vector<SWA::IdType> &objects, std::string &sql) {
        std::for_each(objects.begin(), objects.end(), [&](const auto &obj) {
            sqlGenerator->executeRemoveId(obj);
        });
    }

} // end namespace SQL

#endif
