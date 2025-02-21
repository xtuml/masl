/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include <algorithm>
#include <fstream>
#include <functional>
#include <iostream>

#include <unistd.h>

#include "sql/DatabaseFactory.hh"
#include "sql/Schema.hh"
#include "sql/Util.hh"

#include "SqliteSql.hh"
#include "SqliteStatementFormatter.hh"
#include "sqlite/BlobData.hh"
#include "sqlite/Database.hh"
#include "sqlite/Exception.hh"
#include "sqlite/Resultset.hh"
#include "sqlite/SqlMonitor.hh"

#include "swa/CommandLine.hh"
#include "swa/Process.hh"

#include <format>

namespace SQLITE {

namespace {
bool registerInitRoutine();
bool init = registerInitRoutine();

// *****************************************************************
// *****************************************************************
void initialiseDatabase() {
    const std::string &name =
        SWA::Process::getInstance().getCommandLine().getOption("-db");
    const bool isColdStart = SWA::Process::getInstance().coldStart();
    Database::singleton().initialise(name, isColdStart);
}

// *****************************************************************
// *****************************************************************
bool registerInitRoutine() {
    SWA::Process::getInstance().registerStartupListener(&initialiseDatabase);
    SWA::Process::getInstance().getCommandLine().registerOption(
        SWA::NamedOption("-db", "Database", true, "name of database", true));
    return true;
}

// *****************************************************************
// *****************************************************************
bool registerImpl =
    ::SQL::DatabaseFactory::singleton().registerImpl(&Database::singleton());
} // namespace

// *****************************************************************
// *****************************************************************
Database::ScopedFinalise::ScopedFinalise(const char *const loc,
                                         sqlite3_stmt *stmt)
    : location(loc), ppStmt(stmt) {}

// *****************************************************************
// *****************************************************************
Database::ScopedFinalise::~ScopedFinalise() {
    if (ppStmt != 0) {
        if (Database::finaliseCompile(location, ppStmt) == true) {
            ppStmt = 0;
        } else {
            ::std::string errorMessage;
            errorMessage += location;
            errorMessage += " - failed to finalise database :";
            errorMessage += sqlite3_errmsg(
                ::SQLITE::Database::singleton().getDatabaseImpl());
            std::cout << errorMessage << std::endl;
        }
    }
}

// *****************************************************************
// *****************************************************************
Database &Database::singleton() {
    static Database instance;
    return instance;
}

// *****************************************************************
// *****************************************************************
Database::Database() : database_(0) {}

// *****************************************************************
// *****************************************************************
Database::~Database() { shutdown(); }

// *****************************************************************
// *****************************************************************
const std::string &Database::getDbName() { return dbName_; }

// *****************************************************************
// *****************************************************************
const std::string &Database::getCurrentError() { return error_; }

int busy_handler(void *, int count) {
    // Arbitrary 1ms delay. Seems to take about 1ms to try the
    // lock anyway (making the retry interval about 2ms), so not
    // much point going shorter and cpu usage is almost zero
    // with a 1ms delay.
    static const timespec delay = {0, 1000000};

    // Print message first time in
    if (!count)
        std::cout << "Waiting for database lock...\n" << std::flush;

    // wait a bit before trying again
    nanosleep(&delay, 0);

    // retry indefinitely
    return 1;
}

// *****************************************************************
// *****************************************************************
void Database::initialise(const std::string &iName, const bool iIsColdStart) {
    // Check the input parameters
    if (iName.empty()) {
        throw SqliteException(
            "Database::initialise - detected empty database name");
    }

    dbName_ = iName;
    bool opened = openMemoryBasedDb() || openDiskBasedDb(iIsColdStart);

    if (opened == false) {
        throw SqliteException(::boost::make_tuple(
            "Database::initialise - invalid database name ", dbName_));
    }

    sqlite3_busy_handler(database_, &busy_handler, 0);
}

// *****************************************************************
// *****************************************************************
bool Database::openDiskBasedDb(const bool iIsColdStart) {
    bool isDiskDb = false;
    if (dbName_ != ":memory:") {
        isDiskDb = true;
        if (iIsColdStart == true) {
            unlink(dbName_.c_str());
        }

        // This method can be called multiple times, therefore make sure any
        // open database is closed before attemtping to initialise the new one.
        close();

        int errorCode = sqlite3_open(dbName_.c_str(), &database_);
        if (errorCode != SQLITE_OK) {
            throw SqliteException(
                ::boost::make_tuple("Failed to open SQLITE database :", dbName_,
                                    "-", sqlite3_errmsg(database_)));
        }

        if (isEmpty()) {
            schema_ = ::SQL::Schema::singleton().getSchema();
            if (executeStatement(schema_) == false) {
                throw SqliteException(
                    ::boost::make_tuple("Failed to create schema for", dbName_,
                                        ":", sqlite3_errmsg(database_)));
            }
        } else {
            validateSchema(::SQL::Schema::singleton().getTableDefinitions());
            schema_ = ::SQL::Schema::singleton().getSchema();
        }
    }
    return isDiskDb;
}

// *****************************************************************
// *****************************************************************
bool Database::openMemoryBasedDb() {
    bool isMemoryDb = false;
    if (dbName_ == ":memory:") {
        // The database will be an in-memory database so do not need to worry
        // about cold or warm starts. just open the database and load in the
        // required schema.
        isMemoryDb = true;
        int errorCode = sqlite3_open(dbName_.c_str(), &database_);
        if (errorCode != SQLITE_OK) {
            throw SqliteException(::boost::make_tuple(
                "Failed to open SQLITE in memory database :",
                sqlite3_errmsg(database_)));
        }
        schema_ = ::SQL::Schema::singleton().getSchema();
        if (executeStatement(schema_) == false) {
            throw SqliteException(::boost::make_tuple(
                "Failed to create schema for in memory database :",
                sqlite3_errmsg(database_)));
        }
    }
    return isMemoryDb;
}

// *****************************************************************
// *****************************************************************
std::shared_ptr<::SQL::AbstractStatementFactory>
Database::getStatementFormatter() {
    return std::shared_ptr<::SQL::AbstractStatementFactory>(
        new SqliteAbstractStatementFactory);
}

// *****************************************************************
// *****************************************************************
void Database::close() {
    if (database_ != 0) {
        sqlite3_close(database_);
        database_ = 0;
        unitOfWork.clearObservers();
    }
}

// *****************************************************************
// *****************************************************************
void Database::destroy() {
    close();
    unlink(dbName_.c_str());
}

// *****************************************************************
// *****************************************************************
void Database::shutdown() { close(); }

// *****************************************************************
// *****************************************************************
void Database::abortTransaction() {
    check("Database::abortTransaction");
    if (executeStatement(ABORT_TRANSACTION + SQL_TERMINATOR) == false) {
        // When a transaction is being aborted, a problem in the runtime code
        // has occurred, probably due to an exception. Rather than raising an
        // other exception, just log any failure to the console.
        std::cout << "Database::abortTransaction failed to abort transaction : "
                  << getCurrentError() << std::endl;
    }

    // Notify the registered containers
    notifyAbort();
}

// *****************************************************************
// *****************************************************************
void Database::committingTransaction() {
    check("Database::committingTransaction");
    // Notify the registered containers.
    notifyCommit();
}

// *****************************************************************
// *****************************************************************
void Database::commitTransaction() {
    check("Database::commitTransaction");
    if (executeStatement(COMMIT_TRANSACTION + SQL_TERMINATOR) == false) {
        throw SqliteException(
            ::boost::make_tuple("Failed to commit transaction for database",
                                dbName_, ":", getCurrentError()));
    }
}

// *****************************************************************
// *****************************************************************
void Database::startTransaction(const std::string &iName) {
    check("Database::start_transaction");
    if (executeStatement(START_TRANSACTION + SQL_TERMINATOR) == false) {
        throw SqliteException(::boost::make_tuple(
            "Failed to start transaction for database", dbName_, ":", iName));
    }

    // Make sure all the containers start
    // the transaction from clean
    notifyStart();
}

// **********************************************************
// **********************************************************
void Database::notifyCommit() { unitOfWork.commitTransaction(); }

// **********************************************************
// **********************************************************
void Database::notifyStart() { unitOfWork.startTransaction(); }

// **********************************************************
// **********************************************************
void Database::notifyAbort() { unitOfWork.abortTransaction(); }

// *****************************************************************
// *****************************************************************
bool Database::executeQuery(const std::string &iQuery, ResultSet &oResult) {
    check("Database::executeQuery");
    bool queryResult = false;
    sqlite3_stmt *ppStmt;
    int compile_result =
        sqlite3_prepare(database_, iQuery.c_str(), -1, &ppStmt, NULL);
    if (compile_result == SQLITE_OK) {

        int columnCount = sqlite3_column_count(ppStmt);
        oResult.setColumns(columnCount);
        for (int column = 0; column < columnCount; ++column) {
            oResult.addColumnName(column, sqlite3_column_name(ppStmt, column));
        }

        ResultSet::EntryContainerType currentRow(columnCount);
        int step_result = sqlite3_step(ppStmt);
        while (step_result == SQLITE_ROW) {
            for (int columnIndex = 0; columnIndex < columnCount;
                 ++columnIndex) {
                int columnType = sqlite3_column_type(ppStmt, columnIndex);
                switch (columnType) {
                case SQLITE_FLOAT: {
                    double doubleEntry =
                        sqlite3_column_double(ppStmt, columnIndex);
                    std::string doubleValue =
                        std::format("{}",doubleEntry);
                    currentRow[columnIndex] = doubleValue;
                } break;
                case SQLITE_BLOB: {
                    int32_t column2Bytes =
                        sqlite3_column_bytes(ppStmt, columnIndex);
                    const char *column2Blob = reinterpret_cast<const char *>(
                        sqlite3_column_blob(ppStmt, columnIndex));
                    currentRow[columnIndex] =
                        std::string(column2Blob, column2Blob + column2Bytes);
                } break;

                default:
                    const unsigned char *entry =
                        sqlite3_column_text(ppStmt, columnIndex);
                    if (entry != NULL) {
                        currentRow[columnIndex] =
                            reinterpret_cast<const char *>(entry);
                    } else {
                        currentRow[columnIndex] = "NULL";
                    }
                    break;
                };
            }
            oResult.appendRow(currentRow);
            step_result = sqlite3_step(ppStmt);
        }

        int finalise_result = sqlite3_finalize(ppStmt);
        if (finalise_result == SQLITE_OK) {
            queryResult = true;
        } else {
            // ERROR - Failed to finalise database
            std::string errorMsg = "Failed to finalise database - ";
            errorMsg += sqlite3_errmsg(database_);
            reportError(errorMsg);
        }
    } else {
        // ERROR - Failed to compile sql
        std::string errorMsg = "Failed to compile sql statement ";
        errorMsg += iQuery;
        errorMsg += " - ";
        errorMsg += sqlite3_errmsg(database_);
        reportError(errorMsg);
    }

    SqlQueryMonitor(iQuery, oResult, queryResult);
    return queryResult;
}
// *****************************************************************
// *****************************************************************
bool Database::executeQuery(const std::string &iQuery, BlobData &blobData) {
    check("Database::executeQuery");

    sqlite3_stmt *ppStmt = 0;
    ScopedFinalise finaliser("Database::executeQuery", ppStmt);
    bool queryResult = false;
    int32_t compile_result =
        sqlite3_prepare(getDatabaseImpl(), iQuery.c_str(), -1, &ppStmt, 0);
    if (compile_result == SQLITE_OK) {
        const int columnCount = sqlite3_column_count(ppStmt);
        if (columnCount == 1) {
            while (sqlite3_step(ppStmt) == SQLITE_ROW) {
                int32_t column2Bytes = sqlite3_column_bytes(ppStmt, 0);
                const char *column2Blob = reinterpret_cast<const char *>(
                    sqlite3_column_blob(ppStmt, 0));
                blobData.append(column2Blob, column2Bytes);
            }
            queryResult = true;
        } else {
            // ERROR - More than one column
            std::string errorMsg = "Failed to execute query  for blob data";
            errorMsg += iQuery;
            errorMsg += " - ";
            errorMsg += " only one table column name allowed in query";
            reportError(errorMsg);
        }
    } else {
        // ERROR - Failed to compile sql
        std::string errorMsg = "Failed to compile sql statement ";
        errorMsg += iQuery;
        errorMsg += " - ";
        errorMsg += sqlite3_errmsg(database_);
        reportError(errorMsg);
    }
    SqlQueryMonitor queryMonitor(iQuery);
    return queryResult;
}

// *****************************************************************
// *****************************************************************
bool Database::executeStatement(const std::string &iStatement) {
    check("Database::executeStatement");
    bool result = false;
    char *errMsg = 0;
    if (sqlite3_exec(database_, iStatement.c_str(), NULL, NULL, &errMsg) ==
        SQLITE_OK) {
        result = true;
    } else {
        // exec error
        reportError(errMsg);
        sqlite3_free(errMsg);
        result = false;
    }

    SqlStatementMonitor(iStatement, result);
    return result;
}

// ***********************************************************************
// ***********************************************************************
bool Database::isEmpty() {
    std::string schemaQuery(
        "SELECT count(*) FROM SQLITE_MASTER WHERE type='table';");
    ResultSet masterSchemaResult;
    if (executeQuery(schemaQuery, masterSchemaResult) == false) {
        throw SqliteException(std::string("Schema::hasSchema : failed to "
                                          "execute query on master table - ") +
                              getCurrentError());
    }
    return masterSchemaResult.getRow(0)[0] == "0";
}

void Database::validateSchema(
    const ::SQL::Schema::TableDefinitionType &tableDefinitions) {
    std::string schemaQuery(
        "SELECT name, sql FROM SQLITE_MASTER WHERE type='table';");
    ResultSet masterSchemaResult;
    if (executeQuery(schemaQuery, masterSchemaResult) == false) {
        throw SqliteException(std::string("Schema::validateSchema : failed to "
                                          "execute query on master table - ") +
                              getCurrentError());
    }

    if (masterSchemaResult.getColumns() != 2) {
        throw SqliteException(::boost::make_tuple(
            "Schema::validateSchema : failed due to unexpected master schema "
            "column count",
            "expected", 2, "found", masterSchemaResult.getColumns()));
    }

    for (::SQL::Schema::TableDefinitionType::const_iterator tableItr =
             tableDefinitions.begin();
         tableItr != tableDefinitions.end(); ++tableItr) {
        bool tableFound = false;
        for (ResultSet::RowType rowIndex = 0;
             rowIndex < masterSchemaResult.getRows(); ++rowIndex) {
            const ResultSet::EntryContainerType &currentRow =
                masterSchemaResult.getRow(rowIndex);
            const std::string &tableName = currentRow[0];
            if (tableItr->first == tableName) {
                tableFound = true;
                const std::string &createStatement = currentRow[1];
                std::string normalisedSchema(normalise(createStatement));
                std::string normalisedApplication(normalise(tableItr->second));
                if (normalisedSchema != normalisedApplication) {
                    std::ostringstream message;
                    message << "Schema::validateSchema : failed because "
                               "database schema definition "
                            << tableName;
                    message << " differs from the application schema\n";
                    message << " schema      table definition : '"
                            << normalisedSchema << "'\n";
                    message << " application table definition : '"
                            << normalisedApplication << "'\n";
                    throw SqliteException(message.str());
                }
            }
        }

        if (!tableFound) {
            std::ostringstream message;
            message << "Schema::validateSchema : failed because table "
                    << tableItr->second;
            message << " was not found in the database schema\n";
            throw SqliteException(message.str());
        }
    }
}

// *****************************************************************
// *****************************************************************
std::string Database::normalise(const std::string &sql) const {
    std::string normalisedSql(sql);
    std::transform(normalisedSql.begin(), normalisedSql.end(),
                   normalisedSql.begin(), toupper);

    // remove all the white space by copying all
    // elements in to a container of tokens
    std::vector<std::string> tokens;
    std::istringstream inputTokenStream(normalisedSql);
    std::copy(std::istream_iterator<std::string>(inputTokenStream),
              std::istream_iterator<std::string>(), std::back_inserter(tokens));

    // reform all the tokens into a single string with a single
    // white space seperator between each token.
    std::ostringstream outputTokenStream;
    std::copy(tokens.begin(), tokens.end(),
              std::ostream_iterator<std::string>(outputTokenStream, " "));
    normalisedSql = outputTokenStream.str();

    // remove trailing white space as a result of reform using ostream above.
    normalisedSql.resize(normalisedSql.size() - 1);

    // add statement terminator if not already present.
    if (*normalisedSql.rbegin() != ';') {
        normalisedSql += ';';
    }
    return normalisedSql;
}

// *****************************************************************
// *****************************************************************
sqlite3 *Database::getDatabaseImpl() {
    if (database_ == 0) {
        throw SqliteException(
            "Database::getDatabaseImpl - failed due to null implementation");
    }
    return database_;
}

// *****************************************************************
// *****************************************************************
void Database::dropTables() {
    const std::string dropTableStatements =
        ::SQL::Schema::singleton().dropSchema();
    executeStatement(dropTableStatements);
}

// *****************************************************************
// *****************************************************************
void Database::check(const char *const iWithin) {
    if (database_ == 0) {
        throw SqliteException(
            "Database::check - failed due to null implementation");
    }
}

// *****************************************************************
// *****************************************************************
void Database::reportError(const std::string &message) {
    error_ = "database Failed ";
    error_ += dbName_;
    error_ += " : ";
    error_ += message;
}

// *****************************************************************
// *****************************************************************
bool Database::finaliseCompile(const char *const location,
                               sqlite3_stmt *ppStmt) {
    int32_t finalise_result = sqlite3_finalize(ppStmt);
    return finalise_result == SQLITE_OK;
}

// *****************************************************************
// *****************************************************************
void Database::checkColumnCount(const char *const location,
                                const int32_t actual, const int32_t expected,
                                const std::string &query) {
    if (expected != actual) {
        throw ::SQLITE::SqliteException(
            ::boost::make_tuple(location, "- column count mismatch : expected",
                                expected, "found", actual, query));
    }
}

// *****************************************************************
// *****************************************************************
void Database::checkCompile(const char *const location,
                            const int32_t compile_result,
                            const std::string &query) {
    if (compile_result != SQLITE_OK) {
        throw ::SQLITE::SqliteException(::boost::make_tuple(
            location, "- failed to compile statement :",
            sqlite3_errmsg(::SQLITE::Database::singleton().getDatabaseImpl()),
            "(", query, ")"));
    }
}

} // namespace SQLITE
