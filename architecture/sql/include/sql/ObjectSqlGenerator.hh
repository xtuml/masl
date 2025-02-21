/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_ObjectSqlGenerator_HH
#define Sql_ObjectSqlGenerator_HH

#include <map>
#include <set>
#include <stdint.h>
#include <string>

#include "swa/ObjectPtr.hh"
#include "swa/Set.hh"
#include "swa/types.hh"

#include "ObjectSql.hh"
#include "Util.hh"

namespace SQL {
class Criteria;

// *****************************************************************
//! \brief
//!
//!
// *****************************************************************
template <class Base, class Derived>
class ObjectSqlGenerator : public ObjectSql {
  public:
    typedef typename PsObject_Traits<Derived>::PsCachedPtrMap CacheType;
    typedef typename PsObject_Traits<Derived>::PsObject PsObject;
    typedef typename PsObject_Traits<Derived>::PsObjectPtr PsObjectPtr;

    typedef typename PsObject_Traits<Base>::PsObjectPtr PsBaseObjectPtr;
    typedef
        typename PsObject_Traits<Base>::PsObjectPtrSwaSet PsBaseObjectPtrSwaSet;

    virtual ~ObjectSqlGenerator() {}

    virtual void initialise() = 0;

    virtual void executeGetMaxColumnValue(const ::std::string &attribute,
                                          int32_t &value) const = 0;
    virtual void executeGetMaxColumnValue(const ::std::string &attribute,
                                          int64_t &value) const = 0;

    virtual const std::string getDomainName() const = 0;
    virtual const std::string &getTableName() const = 0;
    virtual const std::string &getObjectName() const = 0;
    virtual const std::string
    getColumnName(const std::string &masl_attribute) const = 0;

    virtual ::SWA::IdType executeGetRowCount() const = 0;
    virtual ::SWA::IdType executeGetMaxIdentifier() const = 0;

    virtual void executeUpdate(const PsObjectPtr &object) const = 0;
    virtual void executeInsert(const PsObjectPtr &object) const = 0;
    virtual void executeRemove(const PsObjectPtr &object) const = 0;
    virtual void executeRemoveId(const ::SWA::IdType object) const = 0;

    virtual void executeSelect(CacheType &cache,
                               const Criteria &criteria) const = 0;
    virtual void executeSelect(CacheType &cache, const Criteria &criteria,
                               PsBaseObjectPtrSwaSet &objects) const = 0;

  private:
    ObjectSqlGenerator(const ObjectSqlGenerator &rhs);
    ObjectSqlGenerator &operator=(const ObjectSqlGenerator &rhs);

  protected:
    ObjectSqlGenerator() {}
};

} // namespace SQL

#endif
