/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_RelationshipTernaryMapperUnitOfWork_HH
#define Sql_RelationshipTernaryMapperUnitOfWork_HH

#include <string>
#include <utility>

#include "DatabaseFactory.hh"
#include "UnitOfWorkObserver.hh"
#include "Util.hh"
#include "boost/unordered_set.hpp"

#include "boost/tuple/tuple.hpp"
#include "boost/tuple/tuple_comparison.hpp"
#include "swa/tuple_hash.hh"

namespace SQL {

template <class T>
class RelationshipTernaryMapperUnitOfWork : public UnitOfWorkObserver {
  public:
    typedef typename T::LhsPsObjectPtr LhsPsObjectPtr;
    typedef typename T::RhsPsObjectPtr RhsPsObjectPtr;
    typedef typename T::AssPsObjectPtr AssPsObjectPtr;

    typedef ::boost::tuple<LhsPsObjectPtr, RhsPsObjectPtr, AssPsObjectPtr>
        LinkedTernaryType;

    typedef typename std::unordered_set<LinkedTernaryType,boost::hash<LinkedTernaryType>> LinkHashSetType;
    typedef typename std::unordered_set<LinkedTernaryType,boost::hash<LinkedTernaryType>> UnLinkHashSetType;

  public:
    RelationshipTernaryMapperUnitOfWork(T &parent);
    ~RelationshipTernaryMapperUnitOfWork();

    void initialise();

    void registerLink(const LhsPsObjectPtr &lhsObj,
                      const RhsPsObjectPtr &rhsObj,
                      const AssPsObjectPtr &assoObj);
    void registerUnLink(const LhsPsObjectPtr &lhsObj,
                        const RhsPsObjectPtr &rhsObj,
                        const AssPsObjectPtr &assoObj);

    void linkStatement(const std::string &statement);
    void unlinkStatement(const std::string &statement);

    // UnitOfWorkObserver
    void flush(UnitOfWorkContext &context);
    void committed(UnitOfWorkContext &context);
    void startTransaction(UnitOfWorkContext &context);
    void abortTransaction(UnitOfWorkContext &context);
    void commitTransaction(UnitOfWorkContext &context);

    // flush any dirty object changes to the database. These
    // will still not be committed until the database unit of
    // work commits.
    void flush();

  private:
    void clear();
    void primeParentForChanges();
    bool isDirty();

  private:
    T &parent;
    LinkHashSetType linkSet;
    UnLinkHashSetType unlinkSet;

    std::string linkStatements;
    std::string unlinkStatements;
    bool unitOfWorkDirty;
};

// ***********************************************************************
// ***********************************************************************
template <class T>
RelationshipTernaryMapperUnitOfWork<T>::RelationshipTernaryMapperUnitOfWork(
    T &parent)
    : parent(parent), unitOfWorkDirty(false) {}

// ***********************************************************************
// ***********************************************************************
template <class T>
RelationshipTernaryMapperUnitOfWork<T>::~RelationshipTernaryMapperUnitOfWork() {
    clear();
}

// ***********************************************************************
// ***********************************************************************
template <class T> void RelationshipTernaryMapperUnitOfWork<T>::initialise() {}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipTernaryMapperUnitOfWork<T>::linkStatement(
    const std::string &statement) {
    primeParentForChanges();
    linkStatements += statement;
    linkStatements += "\n";
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipTernaryMapperUnitOfWork<T>::unlinkStatement(
    const std::string &statement) {
    primeParentForChanges();
    unlinkStatements += statement;
    unlinkStatements += "\n";
}

// ***********************************************************************
// ***********************************************************************
template <class T> bool RelationshipTernaryMapperUnitOfWork<T>::isDirty() {
    return unitOfWorkDirty;
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipTernaryMapperUnitOfWork<T>::primeParentForChanges() {
    // This method is called before this mapper is due to become
    // dirty, i.e. the data controlled by the mapper has been modified.
    // The Master Unit Of Work needs to be informed of this so it can
    // commit the changes at the required time.
    if (isDirty() == false) {
        DatabaseFactory::singleton()
            .getImpl()
            .getCurrentUnitOfWork()
            .registerDirtyObserver(this);
        unitOfWorkDirty = true;
    }
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipTernaryMapperUnitOfWork<T>::registerLink(
    const LhsPsObjectPtr &lhsObj, const RhsPsObjectPtr &rhsObj,
    const AssPsObjectPtr &assoObj) {
    primeParentForChanges();
    typename UnLinkHashSetType::value_type TernaryValue(lhsObj, rhsObj, assoObj);
    if (unlinkSet.find(TernaryValue) == unlinkSet.end()) {
        linkSet.insert(::boost::make_tuple(lhsObj, rhsObj, assoObj));
    } else {
        // A unlink between the same three objects on the this relationship
        // was already been undertaken during the current transaction, therefore
        // a link between the three objects was already found. This link
        // operation essentially nullifies the orginal unlink as the database
        // already contains the required information
        unlinkSet.erase(TernaryValue);
    }
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipTernaryMapperUnitOfWork<T>::registerUnLink(
    const LhsPsObjectPtr &lhsObj, const RhsPsObjectPtr &rhsObj,
    const AssPsObjectPtr &assoObj) {
    primeParentForChanges();
    typename UnLinkHashSetType::value_type TernaryValue(lhsObj, rhsObj, assoObj);
    if (linkSet.find(TernaryValue) == linkSet.end()) {
        unlinkSet.insert(TernaryValue);
    } else {
        // A link between the same three objects on the this relationship
        // was already undertaken during the current transaction. This
        // unlink essentially nullifies the orginal link.
        linkSet.erase(TernaryValue);
    }
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipTernaryMapperUnitOfWork<T>::startTransaction(
    UnitOfWorkContext &context) {
    if (isDirty() == true) {
        throw SqlException("RelationshipTernaryMapperUnitOfWork<T>::start_"
                           "transaction : mapper is not clean");
    }
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipTernaryMapperUnitOfWork<T>::commitTransaction(
    UnitOfWorkContext &context) {
    parent.commitLinks(linkSet);
    context.getStatements() += linkStatements;

    parent.commitUnlinks(unlinkSet);
    context.getStatements() += unlinkStatements;
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipTernaryMapperUnitOfWork<T>::abortTransaction(
    UnitOfWorkContext &context) {
    // Clear the cache if any of the containers are non empty.
    parent.abortLinks();
    clear();
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipTernaryMapperUnitOfWork<T>::flush(UnitOfWorkContext &context) {
    commitTransaction(context);
    clear();
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipTernaryMapperUnitOfWork<T>::committed(
    UnitOfWorkContext &context) {
    clear();
}

// ***********************************************************************
// ***********************************************************************
template <class T> void RelationshipTernaryMapperUnitOfWork<T>::flush() {
    // Force the main unit of work instance to flush any pending
    // changes held by this mapper, but only if the mapper contains
    // dirty objects.
    if (isDirty() == true) {
        DatabaseFactory::singleton()
            .getImpl()
            .getCurrentUnitOfWork()
            .flushObserver(this);
    }
}

// ***********************************************************************
// ***********************************************************************
template <class T> void RelationshipTernaryMapperUnitOfWork<T>::clear() {
    linkSet.clear();
    unlinkSet.clear();
    linkStatements.clear();
    unlinkStatements.clear();
    unitOfWorkDirty = false;
}

} // namespace SQL

#endif
