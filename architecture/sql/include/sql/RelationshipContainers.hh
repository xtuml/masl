/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_RelationshipContainers_HH
#define Sql_RelationshipContainers_HH

#include <algorithm>
#include <iterator>
#include <set>

#include "swa/ObjectPtr.hh"
#include "swa/types.hh"

#include "Exception.hh"
#include "Util.hh"

namespace SQL {

class OneRelationshipType {};
class ManyRelationshipType {};

// ***********************************************************************
//! @Brief Class to handle an object linked to the one end of a relationship.
//!
//! The MASL language supports several types of relationship. These are
//! one-to-one, one-to-many and many-to-one. The OneRelationshipContainer class
//! provides the functionality required to manage the one side of these
//! relationships.
//!
// ***********************************************************************
template <int relNo, class T> class OneRelationshipContainer {
  public:
    enum { EMPTY_LINK = 0 };

    typedef OneRelationshipType Multipicity;

    typedef typename PsObject_Traits<T>::PsObject PsObject;
    typedef typename PsObject_Traits<T>::PsObjectPtr PsObjectPtr;
    typedef typename PsObject_Traits<T>::PsObjectPtrSet PsObjectPtrSet;
    typedef typename PsObject_Traits<T>::PsObjectIdSet PsObjectIdSet;

    typedef ::SWA::IdType NavigatedType;
    typedef PsObjectIdSet NavigatedSetType;

  public:
    OneRelationshipContainer() : linked(EMPTY_LINK) {}

    ~OneRelationshipContainer() {}

    // ***********************************************************************
    //! Remove the currently linked object, and set the link
    //! to an empty link value.
    // ***********************************************************************
    void clear() { linked = EMPTY_LINK; }

    // ***********************************************************************
    //! link the specified object.
    //!
    //! throws SqlException if object already takes part in link
    //!
    //! @param objId The architecture Id of the object to form a link with.
    // ***********************************************************************
    void link(const ::SWA::IdType &objId); // used to form links from database

    // ***********************************************************************
    //! link the specified object.
    //!
    //! throws SqlException if object already takes part in link
    //!
    //! @param obj The object instance to form a link with.
    // ***********************************************************************
    void link(const PsObjectPtr &obj);

    // ***********************************************************************
    //! unlink the specified object from the relationship.
    //!
    //! throws SqlException if object does not already take part in link
    //!
    //! @param obj The object instance to unlink.
    // ***********************************************************************
    void unlink(const PsObjectPtr &obj);

    // ***********************************************************************
    //! @return true if the specified object takes part in the relationship
    //! @param obj The object instance to check.
    // ***********************************************************************
    bool hasLink(const PsObjectPtr &obj) const {
        return hasLink(obj->getArchitectureId());
    }

    // ***********************************************************************
    //! @return true if the specified object takes part in the relationship
    //! @param objId The architecture Id of the object instance to check.
    // ***********************************************************************
    bool hasLink(const ::SWA::IdType &objId) const { return linked == objId; }

    // ***********************************************************************
    //! @return true if a link exists
    // ***********************************************************************
    bool isLinked() const { return linked != EMPTY_LINK; }

    // ***********************************************************************
    //! @return the number of relationship links
    // ***********************************************************************
    ::std::size_t linkCount() const { return (isLinked() ? 1 : 0); }

    // ***********************************************************************
    //! @return the architecture Id of the object taking part in the
    //! relationship
    // ***********************************************************************
    const NavigatedType navigate() const { return linked; }

    // ***********************************************************************
    //! Append the architecture Id of the object taking part in the relationship
    //! to the specified set. If the link is empty the do nothing
    //! @param objectSet The container to insert the architecture id of the
    //! currently linked object.
    // ***********************************************************************
    void navigateWithSet(NavigatedSetType &objectSet) const {
        if (linked > 0) {
            objectSet.insert(linked);
        }
    }

    // ***********************************************************************
    //! Output the contents of the relationship container to the supplied stream
    // ***********************************************************************
    void display(std::ostream &ostr) { ostr << linked; }

  private:
    NavigatedType linked;
};

// ***********************************************************************
//! @Brief Class to handle an object linked to the many end of a relationship.
//!
//! The MASL language supports several types of multi-valued relationship. These
//! are one-to-many and many-to-one. The ManyRelationshipContainer class
//! provides the functionality required to manage the many side of these kinds
//! of relationship definition.
//!
// ***********************************************************************
template <int relNo, class T> class ManyRelationshipContainer {
  public:
    typedef ManyRelationshipType Multipicity;

    typedef typename PsObject_Traits<T>::PsObject PsObject;
    typedef typename PsObject_Traits<T>::PsObjectPtr PsObjectPtr;
    typedef typename PsObject_Traits<T>::PsObjectPtrSet PsObjectPtrSet;
    typedef typename PsObject_Traits<T>::PsObjectIdSet PsObjectIdSet;

    typedef SWA::Set<SWA::IdType> NavigatedType;
    typedef SWA::Set<SWA::IdType> NavigatedSetType;

    // std::set is used rather than the gnu hash_set due to
    // the excessive memory usage of the hash_set. The std::set
    // in this instance uses less memory and it not that much slower.
    typedef std::set<SWA::IdType> ContainerType;

  public:
    ManyRelationshipContainer() {}
    ~ManyRelationshipContainer() {}

    // ***********************************************************************
    //! Remove the currently linked objects
    // ***********************************************************************
    void clear() { linked.clear(); }

    // ***********************************************************************
    //! link the specified object.
    //!
    //! throws SqlException if object already takes part in link
    //!
    //! @param objId The architecture Id of the object to form a link with.
    // ***********************************************************************
    void link(const ::SWA::IdType &objId); // used to form links from database

    // ***********************************************************************
    //! link the specified object.
    //!
    //! throws SqlException if object already takes part in link
    //!
    //! @param obj The object instance to form a link with.
    // ***********************************************************************
    void link(const PsObjectPtr &obj);

    // ***********************************************************************
    //! unlink the specified object from the relationship.
    //!
    //! throws SqlException if object does not already take part in link
    //!
    //! @param obj The object instance to unlink.
    // ***********************************************************************
    void unlink(const PsObjectPtr &obj);

    // ***********************************************************************
    //! @return true if the specified object takes part in the relationship
    //! @param obj The object instance to check.
    // ***********************************************************************
    bool hasLink(const PsObjectPtr &obj) const {
        return hasLink(obj->getArchitectureId());
    }

    // ***********************************************************************
    //! @return true if the specified object takes part in the relationship
    //! @param objId The architecture Id of the object instance to check.
    // ***********************************************************************
    bool hasLink(const ::SWA::IdType &objId) const {
        return linked.count(objId) > 0;
    }

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
    const NavigatedType navigate() const {
        return NavigatedType(linked.begin(), linked.end());
    }

    // ***********************************************************************
    //! Append the architecture Id's of the objects taking part in the
    //! relationship to the specified set.
    //! @param objectSet The container to insert the architecture id of the
    //! currently linked object.
    // ***********************************************************************
    void navigateWithSet(NavigatedSetType &objectSet) const {
        objectSet.insert(linked.begin(), linked.end());
    }

    // ***********************************************************************
    //! Output the contents of the relationship container to the supplied stream
    // ***********************************************************************
    void display(std::ostream &ostr) {
        std::copy(linked.begin(), linked.end(),
                  std::ostream_iterator<int32_t>(ostr, ","));
    }

  private:
    ContainerType linked;
};

// ***********************************************************************
// ***********************************************************************
template <int relNo, class T>
void OneRelationshipContainer<relNo, T>::link(const PsObjectPtr &obj) {
    if (isLinked() == true) {
        std::ostringstream errorMsgStrm;
        errorMsgStrm << "link on one side of relationship R" << relNo;
        errorMsgStrm << " failed : object already participates in relationship";
        errorMsgStrm << " one  objectId (" << obj->getArchitectureId() << ")";
        throw SqlException(errorMsgStrm.str());
    }
    linked = obj->getArchitectureId();
}

// ***********************************************************************
// ***********************************************************************
template <int relNo, class T>
void OneRelationshipContainer<relNo, T>::link(const ::SWA::IdType &objId) {
    if (isLinked() == true) {
        std::ostringstream errorMsgStrm;
        errorMsgStrm << "link on one side of relationship R" << relNo;
        errorMsgStrm << " failed : object already participates in relationship";
        errorMsgStrm << " one  objectId (" << objId << ")";
        throw SqlException(errorMsgStrm.str());
    }
    linked = objId;
}

// ***********************************************************************
// ***********************************************************************
template <int relNo, class T>
void OneRelationshipContainer<relNo, T>::unlink(const PsObjectPtr &obj) {
    if (hasLink(obj) == false) {
        std::ostringstream errorMsgStrm;
        errorMsgStrm << "unlink on one side of relationship R" << relNo;
        errorMsgStrm << " failed : object already participates in relationship";
        errorMsgStrm << " one  objectId (" << obj->getArchitectureId() << ")";
        throw SqlException(errorMsgStrm.str());
    }
    linked = EMPTY_LINK;
}

// ***********************************************************************
// ***********************************************************************
template <int relNo, class T>
void ManyRelationshipContainer<relNo, T>::link(const PsObjectPtr &obj) {
    link(obj->getArchitectureId());
}

// ***********************************************************************
// ***********************************************************************
template <int relNo, class T>
void ManyRelationshipContainer<relNo, T>::link(const ::SWA::IdType &objId) {
    if (linked.find(objId) != linked.end()) {
        std::ostringstream errorMsgStrm;
        errorMsgStrm << "link on many side of relationship R" << relNo;
        errorMsgStrm << " failed : object already participates in relationship";
        errorMsgStrm << " many  objectId (" << objId << ")";
        throw SqlException(errorMsgStrm.str());
    }
    linked.insert(objId);
}

// ***********************************************************************
// ***********************************************************************
template <int relNo, class T>
void ManyRelationshipContainer<relNo, T>::unlink(const PsObjectPtr &obj) {
    typename ContainerType::iterator linkedObjItr =
        linked.find(obj->getArchitectureId());
    if (linkedObjItr == linked.end()) {
        std::ostringstream errorMsgStrm;
        errorMsgStrm << "unlink on many side of relationship R" << relNo;
        errorMsgStrm << " failed : object does not participate in relationship";
        errorMsgStrm << " many  objectId (" << obj->getArchitectureId() << ")";
        throw SqlException(errorMsgStrm.str());
    }
    linked.erase(linkedObjItr);
}

} // namespace SQL
#endif
