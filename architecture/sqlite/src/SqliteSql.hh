/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sqlite_Sql_HH
#define Sqlite_Sql_HH

namespace SQLITE {

namespace {
const std::string SPACE = " ";
const std::string SQL_TERMINATOR = ";";
const std::string NEW_LINE = "\n";
const std::string ALL_COLUMNS = "*";

const std::string SELECT = "SELECT";
const std::string DELETE = "DELETE";
const std::string WHERE = "WHERE";
const std::string FROM = "FROM";
const std::string DROP_TABLE = "DROP TABLE";
const std::string CREATE_DATABASE = "CREATE DATABASE";
const std::string DROP_DATABASE = "DROP DATABASE";

const std::string START_TRANSACTION = "BEGIN    TRANSACTION";
const std::string COMMIT_TRANSACTION = "COMMIT   TRANSACTION";
const std::string ABORT_TRANSACTION = "ROLLBACK TRANSACTION";
} // namespace

} // namespace SQLITE
#endif
