/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_ObjectSql_HH
#define Sql_ObjectSql_HH

#include <string>

namespace SQL {

// *****************************************************************
//! \brief
//! Define an interface that provides the basic sql details for
//! an object that is being stored in a SQL database.
//!
// *****************************************************************
class ObjectSql {
  public:
    virtual ~ObjectSql() {}

    virtual const std::string &getTableName() const = 0;
    virtual const std::string &getObjectName() const = 0;
    virtual const std::string getDomainName() const = 0;
    virtual const std::string
    getColumnName(const std::string &attribute) const = 0;

  protected:
    ObjectSql() {}

  private:
    ObjectSql(const ObjectSql &rhs);
    ObjectSql &operator=(const ObjectSql &rhs);
};

} // end namespace SQL

#endif
