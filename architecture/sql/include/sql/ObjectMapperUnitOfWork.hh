/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_ObjectMapperUnitOfWork_HH
#define Sql_ObjectMapperUnitOfWork_HH

#include <iostream>
#include <set>
#include <string>
#include <vector>

#include "Database.hh"
#include "DatabaseFactory.hh"
#include "DatabaseUnitOfWork.hh"
#include "Exception.hh"
#include "UnitOfWorkObserver.hh"
#include "Util.hh"

namespace SQL {

    // *****************************************************************
    //! \brief
    //!
    //!
    // *****************************************************************
    template <class T>
    class ObjectMapperUnitOfWork : public UnitOfWorkObserver {
      public:
        typedef typename PsObject_Traits<typename T::PsObject>::PsObject PsObject;
        typedef typename PsObject_Traits<typename T::PsObject>::PsObjectPtr PsObjectPtr;
        typedef typename PsObject_Traits<typename T::PsObject>::PsObjectIdSet PsObjectIdSet;
        typedef typename PsObject_Traits<typename T::PsObject>::PsObjectPtrSet PsObjectPtrSet;

      public:
        ObjectMapperUnitOfWork(T &parent);
        virtual ~ObjectMapperUnitOfWork();

        void initialise();

        void registerInsert(const PsObjectPtr &obj);
        void registerDelete(const PsObjectPtr &obj);
        void registerUpdate(const PsObjectPtr &obj);

        void insertStatement(const std::string &statement);
        void removeStatement(const std::string &statement);
        void updateStatement(const std::string &statement);

        // UnitOfWorkObserver
        void flush(UnitOfWorkContext &context);
        void committed(UnitOfWorkContext &context);
        void startTransaction(UnitOfWorkContext &context);
        void commitTransaction(UnitOfWorkContext &context);
        void abortTransaction(UnitOfWorkContext &context);

        bool isDirty() {
            return unitOfWorkDirty == true;
        }

        // flush any dirty object changes to the database. These
        // will still not be committed until the database unit of
        // work commits.
        void flush();

      private:
        void clear();
        void primeParentForChanges();

      private:
        T &parentMapper;

        PsObjectPtrSet insertSet;
        PsObjectPtrSet updateSet;

        std::vector<SWA::IdType> deleteSet;

        std::string insertStatements;
        std::string deleteStatements;
        std::string updateStatements;

        bool unitOfWorkDirty;
    };

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    ObjectMapperUnitOfWork<T>::ObjectMapperUnitOfWork(T &parent)
        : parentMapper(parent), unitOfWorkDirty(false) {}

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    ObjectMapperUnitOfWork<T>::~ObjectMapperUnitOfWork() {
        clear();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    void ObjectMapperUnitOfWork<T>::initialise() {}

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    void ObjectMapperUnitOfWork<T>::insertStatement(const std::string &statement) {
        primeParentForChanges();
        insertStatements += statement;
        insertStatements += "\n";
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    void ObjectMapperUnitOfWork<T>::removeStatement(const std::string &statement) {
        primeParentForChanges();
        deleteStatements += statement;
        deleteStatements += "\n";
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    void ObjectMapperUnitOfWork<T>::updateStatement(const std::string &statement) {
        primeParentForChanges();
        updateStatements += statement;
        updateStatements += "\n";
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    void ObjectMapperUnitOfWork<T>::flush() {
        // Force the main unit of work instance to flush any pending
        // changes held by this mapper, but only if the mapper contains
        // dirty objects.
        if (isDirty()) {
            DatabaseFactory::singleton().getImpl().getCurrentUnitOfWork().flushObserver(this);
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    void ObjectMapperUnitOfWork<T>::primeParentForChanges() {
        // This method is called before this mapper is due to become
        // dirty, i.e. the data controlled by the mapper has been modified.
        // The Master Unit Of Work needs to be informed of this so it can
        // commit the changes at the required time.
        if (isDirty() == false) {
            DatabaseFactory::singleton().getImpl().getCurrentUnitOfWork().registerDirtyObserver(this);
            unitOfWorkDirty = true;
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    void ObjectMapperUnitOfWork<T>::registerInsert(const PsObjectPtr &obj) {
        primeParentForChanges();
        insertSet.insert(obj);
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    void ObjectMapperUnitOfWork<T>::registerUpdate(const PsObjectPtr &obj) {
        // If an object has been inserted within the current transaction then
        // it will have been marked as dirty during its construction and
        // modifications to the object will not cause this method to be invoked as
        // the object has already been marked as dirty. So can just insert object
        // into the update list.
        primeParentForChanges();
        updateSet.insert(obj);
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    void ObjectMapperUnitOfWork<T>::registerDelete(const PsObjectPtr &obj) {
        primeParentForChanges();
        typename PsObjectPtrSet::iterator insertObjItr = insertSet.find(obj);
        if (insertObjItr != insertSet.end()) {
            insertSet.erase(insertObjItr);
            // Object created and deleted in the same transaction
            // so is essentially a no-op. Do not need to therefore
            // change the database as it was just a transient object.
        } else {
            typename PsObjectPtrSet::iterator updateObjItr = updateSet.find(obj);
            if (updateObjItr != updateSet.end()) {
                updateSet.erase(updateObjItr);
            }
            deleteSet.push_back(obj->getArchitectureId());
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    void ObjectMapperUnitOfWork<T>::flush(UnitOfWorkContext &context) {
        commitTransaction(context);

        std::for_each(insertSet.begin(), insertSet.end(), [](auto &&objptr) {
            objptr.get()->markAsClean();
        });
        std::for_each(updateSet.begin(), updateSet.end(), [](auto &&objptr) {
            objptr.get()->markAsClean();
        });
        clear();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    void ObjectMapperUnitOfWork<T>::startTransaction(UnitOfWorkContext &context) {
        if (isDirty()) {
            throw SqlException("ObjectMapperUnitOfWork<T>::start_transaction : "
                               "mapper is not clean");
        }
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    void ObjectMapperUnitOfWork<T>::commitTransaction(UnitOfWorkContext &context) {
        parentMapper.commitInsert(insertSet, context.getStatements());
        context.getStatements() += insertStatements;

        parentMapper.commitUpdate(updateSet, context.getStatements());
        context.getStatements() += updateStatements;

        parentMapper.commitDelete(deleteSet, context.getStatements());
        context.getStatements() += deleteStatements;
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    void ObjectMapperUnitOfWork<T>::abortTransaction(UnitOfWorkContext &context) {
        // Clear the cache if any of the containers are non empty.
        parentMapper.abort();
        clear();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    void ObjectMapperUnitOfWork<T>::committed(UnitOfWorkContext &context) {
        parentMapper.committed();
        std::for_each(insertSet.begin(), insertSet.end(), [](auto &&objptr) {
            objptr.get()->markAsClean();
        });
        std::for_each(updateSet.begin(), updateSet.end(), [](auto &&objptr) {
            objptr.get()->markAsClean();
        });
        clear();
    }

    // ***********************************************************************
    // ***********************************************************************
    template <class T>
    void ObjectMapperUnitOfWork<T>::clear() {
        insertSet.clear();
        updateSet.clear();
        deleteSet.clear();

        insertStatements.clear();
        deleteStatements.clear();
        updateStatements.clear();
        unitOfWorkDirty = false;
    }

} // namespace SQL

#endif
