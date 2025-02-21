/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sqlite_SqliteCriteria_HH
#define Sqlite_SqliteCriteria_HH

#include <string>

#include "sql/Criteria.hh"
#include "sql/CriteriaFactory.hh"

#include <memory>

namespace SQLITE {

// *****************************************************************
//! @brief Sqlite Critera implementation class
//!
//! This class provides the sqlite implementation for the CritieraImpl
//! class that is part of the sql interface. It is used to form very simple
//! SQL selection  critiera for query statements. It provides the minimum
//! possible to support the generated Sql/Sqlite implementation.
// *****************************************************************
class SqliteCriteriaImpl : public ::SQL::CriteriaImpl {
  public:
    SqliteCriteriaImpl();
    virtual ~SqliteCriteriaImpl();

    // *******************************************************
    //! Sets the maximum number of rows that may be returned by
    //! the formed query.
    //!
    //! @param limit the number of rows to limit result set to
    // *******************************************************
    void setLimit(const int32_t limit);

    // *******************************************************
    //! Sets the table column name that will be returned by the
    //! query.
    //!
    //! @columnName name of table column to return
    // *******************************************************
    void addColumn(const std::string &columnName);

    // *******************************************************
    //! Adds the table column name to the FROM section of the query
    //!
    //! @tableName name of table
    // *******************************************************
    void addFromClause(const std::string &tableName);

    // *******************************************************
    //! Set the where clause for the query.
    //!
    //! @where a fully formed SQL where clause.
    // *******************************************************
    void addWhereClause(const std::string &where);

    // *******************************************************
    //! @return whether the where clause is currently empty
    // *******************************************************
    bool empty() const;

    // *******************************************************
    //! Sets the table column to be all the columns within the
    //! specified table
    // *******************************************************
    void addTableColumns(const std::string &column);

    // *******************************************************
    //! Sets the table column name to the ALL COLUMN symbol
    // *******************************************************
    void addAllColumn();

    // *******************************************************
    //! Sets the table column to be all the columns within the
    //! specified table
    // *******************************************************
    void addAllColumns(const std::string &table);

    // *******************************************************
    //! Return the fully formed select statement that has been
    //! constructed using the current instance.
    //!
    //! @return the fully formed select statement
    // *******************************************************
    std::string selectStatement() const;

  private:
    SqliteCriteriaImpl(const SqliteCriteriaImpl &rhs);
    SqliteCriteriaImpl &operator=(const SqliteCriteriaImpl &rhs);

  private:
    std::string column_;
    std::string where_;
    std::string limit_;
    std::string from_;
};

// *****************************************************************
//! @brief prototype pattern class then enables a factory to clone instances.
//!
//! Implement the CloneableCriteria interface so that the Criteria factory
//! can create new instances of the Sqlite Criteria implementation.
// *****************************************************************
class SqliteCloneableCriteria : public ::SQL::CloneableCriteria {
  public:
    SqliteCloneableCriteria();
    virtual ~SqliteCloneableCriteria();

    std::shared_ptr<::SQL::CriteriaImpl> clone() const;

  private:
    SqliteCloneableCriteria(const SqliteCloneableCriteria &rhs);
    SqliteCloneableCriteria &operator=(const SqliteCloneableCriteria &rhs);
};

} // namespace SQLITE

#endif
