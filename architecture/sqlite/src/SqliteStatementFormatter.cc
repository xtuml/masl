/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "SqliteStatementFormatter.hh"
#include "SqliteSql.hh"
#include "sqlite/Exception.hh"

namespace SQLITE {

// ***************************************************************
// ***************************************************************
SqliteDropStatementFormatter::SqliteDropStatementFormatter() {}

// ***************************************************************
// ***************************************************************
SqliteDropStatementFormatter::~SqliteDropStatementFormatter() {}

// ***************************************************************
// ***************************************************************
std::string SqliteDropStatementFormatter::getStatement() {
    return DROP_TABLE + " " + tableName_ + ";\n";
}

// ***************************************************************
// ***************************************************************
void SqliteDropStatementFormatter::addTableName(const std::string &tableName) {
    tableName_ = tableName;
}

// ***************************************************************
// ***************************************************************
SqliteAbstractStatementFactory::SqliteAbstractStatementFactory() {}

// ***************************************************************
// ***************************************************************
SqliteAbstractStatementFactory::~SqliteAbstractStatementFactory() {}

// ***************************************************************
// ***************************************************************
std::shared_ptr<::SQL::DropStatementFormatter>
SqliteAbstractStatementFactory::createDropStatementFormatter() {
    return std::shared_ptr<::SQL::DropStatementFormatter>(
        new SqliteDropStatementFormatter);
}

// ***************************************************************
// ***************************************************************
std::shared_ptr<::SQL::InsertStatementFormatter>
SqliteAbstractStatementFactory::createInsertStatementFormatter() {
    throw SqliteException(
        "SqliteAbstractStatementFactory::createInsertStatementFormatter : no "
        "implementation!!");
}

// ***************************************************************
// ***************************************************************
std::shared_ptr<::SQL::UpdateStatementFormatter>
SqliteAbstractStatementFactory::createUpdateStatement() {
    throw SqliteException("SqliteAbstractStatementFactory::"
                          "createUpdateStatement  : no implementation!!");
}

// ***************************************************************
// ***************************************************************
std::shared_ptr<::SQL::DeleteStatementFormatter>
SqliteAbstractStatementFactory::createsDeleteStatement() {
    throw SqliteException("SqliteAbstractStatementFactory::"
                          "createsDeleteStatement : no implementation!!");
}

// ***************************************************************
// ***************************************************************
std::shared_ptr<::SQL::CreateStatementFormatter>
SqliteAbstractStatementFactory::createCreateStatement() {
    throw SqliteException("SqliteAbstractStatementFactory::"
                          "createCreateStatement  : no implementation!!");
}
} // namespace SQLITE
