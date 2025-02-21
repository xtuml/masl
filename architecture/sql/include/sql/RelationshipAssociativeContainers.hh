/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_RelationshipAssociativeContainers_HH
#define Sql_RelationshipAssociativeContainers_HH

#include <set>

#include "Iterator.hh"
#include "RelationshipContainers.hh"
#include "Util.hh"

namespace SQL {

// ***********************************************************************
//! @Brief Class to handle an object linked to the many end of an associative
//! relationship.
//!
//! The MASL language supports several types of multi-valued associative
//! relationships. These are one-to-many and many-to-one. The
//! OneAssociativeRelationshipContainer class provides the functionality
//! required to manage the one side of these kinds of relationship definition.
//!
// ***********************************************************************
template <int relNo, class T, class A>
class OneAssociativeRelationshipContainer {
  public:
    enum { EMPTY_LINK = 0 };

    typedef OneRelationshipType Multipicity;

    typedef typename PsObject_Traits<T>::PsObject PsObject;
    typedef typename PsObject_Traits<T>::PsObjectPtr PsObjectPtr;
    typedef typename PsObject_Traits<T>::PsObjectIdSet PsObjectIdSet;
    typedef typename PsObject_Traits<T>::PsObjectPtrSet PsObjectPtrSet;

    typedef typename PsObject_Traits<A>::PsObject AssocPsObject;
    typedef typename PsObject_Traits<A>::PsObjectPtr AssocPsObjectPtr;
    typedef typename PsObject_Traits<A>::PsObjectPtrSet AssocPsObjectPtrSet;

    typedef ::SWA::IdType NavigatedType;
    typedef SWA::Set<::SWA::IdType> NavigatedSetType;

  public:
    OneAssociativeRelationshipContainer()
        : related(EMPTY_LINK), associative(EMPTY_LINK) {}

    ~OneAssociativeRelationshipContainer() {}

    // ***********************************************************************
    //! Remove the currently linked object, and set the link
    //! to an empty link value.
    // ***********************************************************************
    void clear() {
        related = EMPTY_LINK;
        associative = EMPTY_LINK;
    }

    // ***********************************************************************
    //! link the specified object.
    //!
    //! throws SqlException if object already takes part in link
    //!
    //! @param obj The object instance to form a link with.
    // ***********************************************************************
    void link(const PsObjectPtr &relatedObj, const AssocPsObjectPtr &assocObj);

    // ***********************************************************************
    //! link the specified object.
    //!
    //! throws SqlException if object already takes part in link
    //!
    //! @param objId The architecture Id of the object to form a link with.
    // ***********************************************************************
    void
    link(const ::SWA::IdType &relatedObjId,
         const ::SWA::IdType &assocObjId); // used to form links from database

    // ***********************************************************************
    //! unlink the specified object from the relationship.
    //!
    //! throws SqlException if object does not already take part in link
    //!
    //! @param obj The object instance to unlink.
    // ***********************************************************************
    void unlink(const PsObjectPtr &relatedObj,
                const AssocPsObjectPtr &assocObj);

    // ***********************************************************************
    //! unlink the specified object from the relationship.
    //!
    //! throws SqlException if object does not already take part in link
    //!
    //! @param obj The object instance to unlink.
    // ***********************************************************************
    void unlink(const ::SWA::IdType &relatedObjId,
                const ::SWA::IdType &assocObjId);

    // ***********************************************************************
    //! @return true if the specified object takes part in the relationship
    //! @param obj The object instance to check.
    // ***********************************************************************
    bool hasRelatedLink(const PsObjectPtr &relatedObj) const {
        return related == relatedObj->getArchitectureId();
    }

    // ***********************************************************************
    //! @return true if the specified object takes part in the relationship
    //! @param obj The object instance to check.
    // ***********************************************************************
    bool hasAssocLink(const AssocPsObjectPtr &assocObj) const {
        return associative == assocObj->getArchitectureId();
    }

    // ***********************************************************************
    //! @return true if a link exists
    // ***********************************************************************
    bool isLinked() const { return related != EMPTY_LINK; }

    // ***********************************************************************
    //! @return the number of relationship links
    // ***********************************************************************
    ::std::size_t linkCount() const { return (isLinked() ? 1 : 0); }

    // ***********************************************************************
    //! @return the architecture Id of the object taking part in the
    //! relationship
    // ***********************************************************************
    const NavigatedType &navigateRelated() const { return related; }

    // ***********************************************************************
    //! @return the architecture Id of the object taking part in the
    //! relationship
    // ***********************************************************************
    const NavigatedType &navigateAssoc() const { return associative; }

    // ***********************************************************************
    //! Append the architecture Id of the object taking part in the relationship
    //! to the specified set. If the link is empty the do nothing
    //! @param objectSet The container to insert the architecture id of the
    //! currently linked object.
    // ***********************************************************************
    void navigateRelatedWithSet(NavigatedSetType &objectSet) const {
        if (related > 0) {
            objectSet.insert(related);
        }
    }

    // ***********************************************************************
    //! Append the architecture Id of the object taking part in the relationship
    //! to the specified set. If the link is empty the do nothing
    //! @param objectSet The container to insert the architecture id of the
    //! currently linked object.
    // ***********************************************************************
    void navigateAssocWithSet(NavigatedSetType &objectSet) const {
        if (associative > 0) {
            objectSet.insert(associative);
        }
    }

    // ***********************************************************************
    //! Output the contents of the relationship container to the supplied stream
    // ***********************************************************************
    void display(std::ostream &ostr) { ostr << related << "," << associative; }

  private:
    NavigatedType related;
    NavigatedType associative;
};

// ***********************************************************************
//! @Brief Class to handle an object linked to the many end of an associative
//! relationship.
//!
//! The MASL language supports several types of multi-valued relationship. These
//! are one-to-many and many-to-one. The ManyAssociativeRelationshipContainer
//! class provides the functionality required to manage the many side of these
//! kinds of associative relationship definition.
//!
// ***********************************************************************
template <int relNo, class T, class A>
class ManyAssociativeRelationshipContainer {
  public:
    typedef ManyRelationshipType Multipicity;

    typedef typename PsObject_Traits<T>::PsObject PsObject;
    typedef typename PsObject_Traits<T>::PsObjectPtr PsObjectPtr;
    typedef typename PsObject_Traits<T>::PsObjectIdSet PsObjectIdSet;
    typedef typename PsObject_Traits<T>::PsObjectPtrSet PsObjectPtrSet;

    typedef typename PsObject_Traits<A>::PsObject AssocPsObject;
    typedef typename PsObject_Traits<A>::PsObjectPtr AssocPsObjectPtr;
    typedef typename PsObject_Traits<A>::PsObjectPtrSet AssocPsObjectPtrSet;

    typedef SWA::Set<::SWA::IdType> NavigatedType;
    typedef SWA::Set<::SWA::IdType> NavigatedSetType;

    typedef std::set<std::pair<::SWA::IdType, ::SWA::IdType>> Container;

    typedef PairFirstIterator<typename Container::const_iterator>
        RelatedIterator;
    typedef PairSecondIterator<typename Container::const_iterator>
        AssocIterator;

  public:
    ManyAssociativeRelationshipContainer() {}
    ~ManyAssociativeRelationshipContainer() {}

    // ***********************************************************************
    //! Remove the currently linked objects
    // ***********************************************************************
    void clear() { linked.clear(); }

    // ***********************************************************************
    //! link the specified object.
    //!
    //! throws SqlException if object already takes part in link
    //!
    //! @param obj The object instance to form a link with.
    // ***********************************************************************
    void link(const PsObjectPtr &relatedObj, const AssocPsObjectPtr &assocObj);

    // ***********************************************************************
    //! link the specified object.
    //!
    //! throws SqlException if object already takes part in link
    //!
    //! @param objId The architecture Id of the object to form a link with.
    // ***********************************************************************
    void
    link(const ::SWA::IdType &relatedObjId,
         const ::SWA::IdType &assocObjId); // used to form links from database

    // ***********************************************************************
    //! unlink the specified object from the relationship.
    //!
    //! throws SqlException if object does not already take part in link
    //!
    //! @param obj The object instance to unlink.
    // ***********************************************************************
    void unlink(const PsObjectPtr &relatedObj,
                const AssocPsObjectPtr &assocObj);

    // ***********************************************************************
    //! @return true if a link exists
    // ***********************************************************************
    bool isLinked() const { return linked.size() > 0; }

    // ***********************************************************************
    //! @return the number of relationship links
    // ***********************************************************************
    ::std::size_t linkCount() const { return linked.size(); }

    // ***********************************************************************
    //! @return the architecture Id's of the objects taking part in the
    //! relationship
    // ***********************************************************************
    const NavigatedType navigateRelated() const {
        return NavigatedType(relatedBegin(), relatedEnd());
    }

    // ***********************************************************************
    //! @return the architecture Id's of the objects taking part in the
    //! relationship
    // ***********************************************************************
    const NavigatedType navigateAssoc() const {
        return NavigatedType(assocBegin(), assocEnd());
    }

    // ***********************************************************************
    //! Append the architecture Id's of the objects taking part in the
    //! relationship to the specified set.
    //! @param objectSet The container to insert the architecture id of the
    //! currently linked object.
    // ***********************************************************************
    void navigateRelatedWithSet(NavigatedSetType &objectSet) const {
        objectSet += NavigatedSetType(relatedBegin(), relatedEnd());
    }

    // ***********************************************************************
    //! Append the architecture Id's of the objects taking part in the
    //! relationship to the specified set.
    //! @param objectSet The container to insert the architecture id of the
    //! currently linked object.
    // ***********************************************************************
    void navigateAssocWithSet(NavigatedSetType &objectSet) const {
        objectSet += NavigatedSetType(assocBegin(), assocEnd());
    }

    // ***********************************************************************
    //! Output the contents of the relationship container to the supplied stream
    // ***********************************************************************
    void display(std::ostream &ostr) {
        std::copy(linked.begin(), linked.end(),
                  std::ostream_iterator<Container::value_type>(ostr, ","));
    }

  private:
    RelatedIterator relatedBegin() const {
        return RelatedIterator(linked.begin());
    }
    RelatedIterator relatedEnd() const { return RelatedIterator(linked.end()); }

    AssocIterator assocBegin() const { return AssocIterator(linked.begin()); }
    AssocIterator assocEnd() const { return AssocIterator(linked.end()); }

  private:
    Container linked;
};

// ***********************************************************************
// ***********************************************************************
template <int relNo, class T, class A>
void OneAssociativeRelationshipContainer<relNo, T, A>::link(
    const PsObjectPtr &relatedObj, const AssocPsObjectPtr &assocObj) {
    link(relatedObj->getArchitectureId(), assocObj->getArchitectureId());
}

// ***********************************************************************
// ***********************************************************************
template <int relNo, class T, class A>
void OneAssociativeRelationshipContainer<relNo, T, A>::link(
    const ::SWA::IdType &relatedObjId, const ::SWA::IdType &assocObjId) {
    if (isLinked() == true) {
        std::ostringstream errorMsgStrm;
        errorMsgStrm << "link of associative relationship R" << relNo;
        errorMsgStrm << " failed : object already participates in relationship";
        errorMsgStrm << " related      objectId (" << relatedObjId << ")";
        errorMsgStrm << " associative  objectId (" << assocObjId << ")";
        throw SqlException(errorMsgStrm.str());
    }
    related = relatedObjId;
    associative = assocObjId;
}

// ***********************************************************************
// ***********************************************************************
template <int relNo, class T, class A>
void OneAssociativeRelationshipContainer<relNo, T, A>::unlink(
    const PsObjectPtr &relatedObj, const AssocPsObjectPtr &assocObj) {
    unlink(relatedObj->getArchitectureId(), assocObj->getArchitectureId());
}

// ***********************************************************************
// ***********************************************************************
template <int relNo, class T, class A>
void OneAssociativeRelationshipContainer<relNo, T, A>::unlink(
    const ::SWA::IdType &relatedObjId, const ::SWA::IdType &assocObjId) {
    if (related != relatedObjId || associative != assocObjId) {
        std::ostringstream errorMsgStrm;
        errorMsgStrm << "unlink of associative relationship R" << relNo;
        errorMsgStrm << " failed : objects do not participate in relationship";
        errorMsgStrm << " related     objectId (" << relatedObjId << ")";
        errorMsgStrm << " associative objectId (" << assocObjId << ")";
        throw SqlException(errorMsgStrm.str());
    }
    related = EMPTY_LINK;
    associative = EMPTY_LINK;
}

// ***********************************************************************
// ***********************************************************************
template <int relNo, class T, class A>
void ManyAssociativeRelationshipContainer<relNo, T, A>::link(
    const PsObjectPtr &relatedObj, const AssocPsObjectPtr &assocObj) {
    link(relatedObj->getArchitectureId(), assocObj->getArchitectureId());
}

// ***********************************************************************
// ***********************************************************************
template <int relNo, class T, class A>
void ManyAssociativeRelationshipContainer<relNo, T, A>::link(
    const ::SWA::IdType &relatedObjId, const ::SWA::IdType &assocObjId) {
    if (linked.find(std::make_pair(relatedObjId, assocObjId)) != linked.end()) {
        std::ostringstream errorMsgStrm;
        errorMsgStrm << "link of associative relationship R" << relNo;
        errorMsgStrm
            << " failed : related object already participates in relationship";
        errorMsgStrm << " related      objectId (" << relatedObjId << ")";
        errorMsgStrm << " associative  objectId (" << assocObjId << ")";
        throw SqlException(errorMsgStrm.str());
    }
    linked.insert(std::make_pair(relatedObjId, assocObjId));
}

// ***********************************************************************
// ***********************************************************************
template <int relNo, class T, class A>
void ManyAssociativeRelationshipContainer<relNo, T, A>::unlink(
    const PsObjectPtr &relatedObj, const AssocPsObjectPtr &assocObj) {
    typename Container::iterator relatedObjItr = linked.find(std::make_pair(
        relatedObj->getArchitectureId(), assocObj->getArchitectureId()));
    if (relatedObjItr != linked.end()) {
        linked.erase(relatedObjItr);
    } else {
        std::ostringstream errorMsgStrm;
        errorMsgStrm << "unlink of associative relationship R" << relNo;
        errorMsgStrm
            << " failed : object pair does not participate in relationship";
        errorMsgStrm << " related      objectId ("
                     << relatedObj->getArchitectureId() << ")";
        errorMsgStrm << " associative  objectId ("
                     << assocObj->getArchitectureId() << ")";
        throw SqlException(errorMsgStrm.str());
    }
}

} // end namespace SQL
#endif
