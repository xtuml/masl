/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_ObjectPtr_HH
#define SWA_ObjectPtr_HH

#include "ProgramError.hh"
#include "types.hh"
#include <iostream>

#include "boost/functional/hash.hpp"
#include "boost/tuple/tuple.hpp"
#include <nlohmann/json.hpp>

namespace SWA {

enum NullObjectPtr { Null };

template <class T> class ObjectPtr {
  public:
    typedef T pointee;

    ObjectPtr() : ptr(0) {}
    ObjectPtr(NullObjectPtr) : ptr(0) {}

    template <class T2> explicit ObjectPtr(T2 *ptr) : ptr(ptr) {}

    ObjectPtr &operator=(NullObjectPtr) {
        this->ptr = 0;
        return *this;
    }

    template <class T2> ObjectPtr &operator=(T2 *ptr) {
        this->ptr = ptr;
        return *this;
    }

    template <class T2> ObjectPtr(const ObjectPtr<T2> &rhs) : ptr(rhs.get()) {}

    template <class T2> ObjectPtr &operator=(const ObjectPtr<T2> &rhs) {
        this->ptr = rhs.get();
        return *this;
    }

    template <class T2> ObjectPtr<T2> downcast() const {
        T2 *castObj = dynamic_cast<T2 *>(ptr);
        if (castObj == 0 && ptr != 0) {
            throw ProgramError("dynamic_cast failed");
        }
        return ObjectPtr<T2>(castObj);
    }

    T *operator->() const {
        check();
        return ptr;
    }
    T &operator*() const {
        check();
        return *ptr;
    }

    T *get() const { return ptr; }
    T *getChecked() const {
        checkNull();
        return ptr;
    }

    T &deref() const {
        checkNull();
        return *ptr;
    }

    friend bool operator==(const ObjectPtr<T> &cp, NullObjectPtr) {
        return !cp;
    }
    friend bool operator==(NullObjectPtr, const ObjectPtr<T> &cp) {
        return !cp;
    }
    friend bool operator==(const ObjectPtr<T> &cp, const T *p) {
        return cp == ObjectPtr<T>(p);
    }
    friend bool operator==(const T *p, const ObjectPtr<T> &cp) {
        return cp == ObjectPtr<T>(p);
    }
    friend bool operator==(const ObjectPtr<T> &cp1, const ObjectPtr<T> &cp2) {
        if (!cp1)
            return !cp2;
        if (!cp2)
            return false;
        return cp1.ptr->getArchitectureId() == cp2.ptr->getArchitectureId();
    }

    friend bool operator!=(const ObjectPtr<T> &cp, NullObjectPtr) { return cp; }
    friend bool operator!=(NullObjectPtr, const ObjectPtr<T> &cp) { return cp; }
    friend bool operator!=(const ObjectPtr<T> &cp, const T *p) {
        return !(cp == p);
    }
    friend bool operator!=(const T *p, const ObjectPtr<T> &cp) {
        return !(p == cp);
    }
    friend bool operator!=(const ObjectPtr<T> &cp1, const ObjectPtr<T> &cp2) {
        return !(cp1 == cp2);
    }

    // We need a sort order for sets and bags, but it
    // doesn't matter what it is. Sort on architecture id,
    // with null pointers before anything else. Use the
    // atual pointer, rather than the is deleted flag,
    // otherwise the sort order may change under our feet,
    // which will cause all sorts of problems.
    friend bool operator<(const ObjectPtr<T> &cp1, const ObjectPtr<T> &cp2) {
        if (!cp1.ptr)
            return cp2.ptr;
        if (!cp2.ptr)
            return false;
        return cp1.ptr->getArchitectureId() < cp2.ptr->getArchitectureId();
    }

    // If either pointer is null, returns a null pointer (or
    // empty set) of the destination type, otherwise returns
    // the instance pointer (or set) containing all the
    // objects that are correlated using the supplied
    // pointers, using the supplied correlation function.
    // This is necessary so that correlation with a null
    // pointer does not throw an exception.
    template <class ResultType, class SrcObj, class RhsObj>
    ResultType correlate(ResultType (SrcObj::*correlator)(ObjectPtr<RhsObj>)
                             const,
                         ObjectPtr<RhsObj> rhs) const {
        return (ptr && rhs) ? (ptr->*correlator)(rhs) : ResultType();
    }

    template <class ResultType, class SrcObj, class RhsObj>
    ResultType correlate(ResultType (SrcObj::*correlator)(ObjectPtr<RhsObj>)
                             const,
                         RhsObj *rhs) const {
        return correlate(correlator, ObjectPtr<RhsObj>(rhs));
    }

    void deleteInstance() const {
        if (!isNull() && !isDeleted())
            ptr->deleteInstance();
    }

    friend void to_json(nlohmann::json &json, const SWA::ObjectPtr<T> &v) {
        json = v.ptr->getArchitectureId();
    }

    friend void from_json(const nlohmann::json &json, SWA::ObjectPtr<T> &v) {}

    // Conversion to bool - See C++ Templates, Vandevoorde &
    // Josuttis. Section 20.2.8 Implicit Conversions, p392.
  private:
    struct BoolConversionSupport {
        int dummy;
    };

  public:
    operator int BoolConversionSupport::*() const {
        return isNull() ? 0 : &BoolConversionSupport::dummy;
    }

    bool isNull() const { return !ptr; }
    bool isDeleted() const { return ptr->isDeleted(); }

  private:
    void check() const {
        checkNull();
        if (isDeleted())
            throw ProgramError("Deleted instance handle");
    }
    void checkNull() const {
        if (isNull())
            throw ProgramError("Null instance handle");
    }

  private:
    T *ptr;
};

template <class T> std::size_t hash_value(const ObjectPtr<T> &p) {
    return p ? boost::hash<SWA::IdType>()(p.get()->getArchitectureId()) : -1;
}



template <> class ObjectPtr<void> {
  public:
    typedef void pointee;

    ObjectPtr() : ptr(0), archId(0) {}
    ObjectPtr(NullObjectPtr) : ptr(0), archId(0) {}

    template <class T2>
    explicit ObjectPtr(T2 *objPtr)
        : ptr(objPtr), archId(objPtr ? objPtr->getArchitectureId() : 0) {}

    template <class T2> ObjectPtr &operator=(T2 *objPtr) {
        this->ptr = objPtr;
        this->archId = objPtr ? objPtr->getArchitectureId() : 0;
        return *this;
    }

    ObjectPtr &operator=(NullObjectPtr) {
        this->ptr = 0;
        this->archId = 0;
        return *this;
    }

    template <class T2>
    ObjectPtr(const ObjectPtr<T2> &objPtr)
        : ptr(objPtr.get()),
          archId(objPtr.get() ? objPtr->getArchitectureId() : 0) {}

    template <class T2> ObjectPtr &operator=(const ObjectPtr<T2> &rhs) {
        this->ptr = rhs.get();
        this->archId = rhs.get() ? rhs->getArchitectureId() : 0;
        return *this;
    }

    template <class T2> ObjectPtr<T2> downcast() {
        return ObjectPtr<T2>(dynamic_cast<T2 *>(ptr));
    }

    void *operator->() const {
        check();
        return ptr;
    }

    void *get() const { return ptr; }
    SWA::IdType getArchitectureId() const { return archId; }

    friend bool operator==(const ObjectPtr &cp, NullObjectPtr) { return !cp; }
    friend bool operator==(NullObjectPtr, const ObjectPtr &cp) { return !cp; }

    friend bool operator!=(const ObjectPtr &cp, NullObjectPtr) { return cp; }
    friend bool operator!=(NullObjectPtr, const ObjectPtr &cp) { return cp; }

    friend void to_json(nlohmann::json &json, const SWA::ObjectPtr<void> &v) {
        json = nullptr;
    }

    friend void from_json(const nlohmann::json &json, SWA::ObjectPtr<void> &v) {
    }

    // Conversion to bool - See C++ Templates, Vandevoorde &
    // Josuttis. Section 20.2.8 Implicit Conversions, p392.
  private:
    struct BoolConversionSupport {
        int dummy;
    };

  public:
    operator int BoolConversionSupport::*() const {
        return this->ptr ? &BoolConversionSupport::dummy : 0;
    }

  private:
    void check() const {
        if (!ptr)
            throw ProgramError("Null instance handle");
    }

    void *ptr;
    SWA::IdType archId;
};

template <class T>
std::ostream &operator<<(std::ostream &str, const ObjectPtr<T> &obj) {
    if (obj) {
        str << obj.deref();
    } else {
        str << "<null instance handle>";
    }
    return str;
}

//********************************************************************************
//********************************************************************************
//! When using the Sequence and Set containers, the addition of ObjectPtr's in
//! to these containers needs to check for null. If it is null then the object
//! should not be added to the container. The insert functionality of these
//! containers includes instances of the NullCheck helper class to aid with the
//! null determination. The default implementation (for non ObjectPtr types)
//! returns a false result while a specialisation for ObjectPtr types actually
//! undertakes the required null check.
//********************************************************************************
//********************************************************************************
template <class NT> struct NullCheck {
    static bool isNull(const NT &element) { return false; }
};

template <class NT> struct NullCheck<SWA::ObjectPtr<NT>> {
    static bool isNull(const SWA::ObjectPtr<NT> &element) {
        return element.get() == 0;
    }
};

} // namespace SWA

template<typename T>
struct std::hash<SWA::ObjectPtr<T>> {
    std::size_t operator()(const SWA::ObjectPtr<T> &p) const noexcept {
        return p ? std::hash<SWA::IdType>{}(p.get()->getArchitectureId()) : -1;
    }
};


#endif
