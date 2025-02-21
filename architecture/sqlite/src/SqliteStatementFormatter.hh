/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "sql/StatementFormatter.hh"
#include <string>

#include <memory>

namespace SQLITE {

// ***************************************************************
// ***************************************************************
class SqliteDropStatementFormatter : public ::SQL::DropStatementFormatter {
  public:
    SqliteDropStatementFormatter();
    virtual ~SqliteDropStatementFormatter();

    std::string getStatement();
    void addTableName(const std::string &tableName);

  private:
    SqliteDropStatementFormatter(const SqliteDropStatementFormatter &rhs);
    SqliteDropStatementFormatter &
    operator=(const SqliteDropStatementFormatter &rhs);

  private:
    std::string tableName_;
};

// ***************************************************************
// ***************************************************************
class SqliteAbstractStatementFactory : public ::SQL::AbstractStatementFactory {
  public:
    SqliteAbstractStatementFactory();
    virtual ~SqliteAbstractStatementFactory();

    std::shared_ptr<::SQL::DropStatementFormatter>
    createDropStatementFormatter();
    std::shared_ptr<::SQL::InsertStatementFormatter>
    createInsertStatementFormatter();
    std::shared_ptr<::SQL::UpdateStatementFormatter> createUpdateStatement();
    std::shared_ptr<::SQL::DeleteStatementFormatter> createsDeleteStatement();
    std::shared_ptr<::SQL::CreateStatementFormatter> createCreateStatement();

  private:
    SqliteAbstractStatementFactory(const SqliteAbstractStatementFactory &rhs);
    SqliteAbstractStatementFactory &
    operator=(const SqliteAbstractStatementFactory &rhs);
};

} // namespace SQLITE
