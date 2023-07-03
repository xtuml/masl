/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 * Classification: UK OFFICIAL
 * ----------------------------------------------------------------------------
 */

#ifndef Sqlite_Database_HH
#define Sqlite_Database_HH

#include <string>

#include "sqlite3.h"

#include "boost/shared_ptr.hpp"

#include "sql/Database.hh"
#include "sql/Schema.hh"


#include "BlobData.hh"

namespace SQLITE {

class ResultSet;

// *****************************************************************
//! @brief Abstraction of an Sqlite Database.
//!
//! A class that represents the actions and functionality
//! required of the sqlite database. It is implemented
//! as a singleton.
// *****************************************************************
class Database : public ::SQL::Database
{
  public:
     // *****************************************************************
     //! @brief use RAII to make sure finalise of database is executed
     // ****************************************************************
     class ScopedFinalise
     {
       public:
          ScopedFinalise(const char* const loc, sqlite3_stmt* stmt);
         ~ScopedFinalise();

       private:
           ScopedFinalise(const ScopedFinalise& rhs);
           ScopedFinalise& operator=(const ScopedFinalise& rhs);

       private:
           const char* const location;
           sqlite3_stmt*     ppStmt;
     };

  public:
     
     // *****************************************************************
     //! Static accessor method used to return the current database Object. 
     //! Creates the database object if it has not already been created.
     //! @return The active database object.
     // *****************************************************************
     static Database& singleton();
     
     // *****************************************************************
     //! This method should be called before any other non-static methods 
     //! of this class.
     //! 
     //! Use this method to initialise the database. This will create the
     //! required database or open an existing database. As part of the
     //! initialisation process the schema to be used will be obtained
     //! from the Schema class and if a cold start has be requested the
     //! schema (create table statements) will be executed by the database.
     //! At present on a warm start the schema used by the opened database
     //! is not compared with schema returned from the Schema class. Table
     //! Inconsistencies will therefore only be detected during execution of 
     //! SQL operations on the corresponding tables.
     //! 
     //! The sqlite database can be run as an inmemory RDBMS rather than
     //! being file based. To get this behaviour specify ':memory:' as the
     //! value for the iFileName parameter.
     //!
     //! On error will throw SqliteException
     //! 
     //! @see   Schema
     //! @param iName        the file name associated with the database. 
     //! @param iIsColdStart flag to indicate cold start of database.
     // *****************************************************************
     void initialise (const std::string& iFileName, const bool iIsColdStart);

     // *****************************************************************
     //! Provide access to the Sqlite database implementation structure
     //! On error will throw SqliteException.
     //! @return The Postgre Database implementation, will never return NULL.
     // *****************************************************************
     sqlite3* getDatabaseImpl();

     // ***************************************************************** 
     //! @return the file name being used by the database.
     // *****************************************************************
     const std::string& getDbName();

     // *****************************************************************
     //! Execute the specified SQL statement. 
     //! 
     //! @return the current error encountered by the database.
     // *****************************************************************
     const std::string& getCurrentError();
       
     // *****************************************************************
     //! Return a class that can be used to format the required SQL statements
     //! using SQLite SQL syntax.
     //!  
     //! @return An Sqlite SQL formatter.
     // *****************************************************************
     boost::shared_ptr< ::SQL::AbstractStatementFactory> getStatementFormatter();   

     // *****************************************************************
     //! close the opened database, is a no-op if a database is not open.  
     // *****************************************************************
     void close ();

     // *****************************************************************
     //! Destroys the database associated with the internally stored database
     //! name. Will open the database if it is not already open.
     //! 
     //! throws SqliteException on error
     // *****************************************************************
     void destroy ();

     // *****************************************************************
     //! shutdown any opened database, is a no-op if a database is not open.  
     // *****************************************************************
     void shutdown ();
     
     // *****************************************************************
     //! Aborts the current transaction. Any registered unitOfWorkObserver
     //! objects will be informed that the transaction has been aborted.
     //! 
     //! throws SqliteException on error
     // *****************************************************************
     void abortTransaction ();

     // *****************************************************************
     //! Notifies all registered parties to write their changes to the database.
     //! These changes will then get finally committed when the commitTransaction
     //! is called. 
     //! 
     //! throws SqliteException on error
     // *****************************************************************
     void committingTransaction ();

     // *****************************************************************
     //! commits the current transaction. Any registered unitOfWorkObserver
     //! objects will be informed that the current transaction is about to 
     //! be committed.
     //! 
     //! throws SqliteException on error
     // *****************************************************************
     void commitTransaction ();

     // *****************************************************************
     //! Starts a transaction with the specified name. Any registered unitOfWorkObserver
     //! objects will be informed of the start of a transaction. 
     //! 
     //! throws SqliteException on error
     // *****************************************************************
     void startTransaction (const std::string& iName);
     
     // *****************************************************************
     //! Execute the specified SQL query and return the results in the
     //! specified generic ResultSet object. This result set stores all
     //! the values as text fields and so can prove expensive to use due
     //! to the string to value and value to string conversions.
     //!
     //! @return true for success
     // *****************************************************************
     bool executeQuery (const std::string& iQuery, ResultSet& oResult);

     // *****************************************************************
     //! Execute the specified SQL query and return the results in the
     //! specified BlobData. This query method allows the data contained 
     //! in a blob column to be returned to the application. The query must 
     //! only contain a single column name.
     //! 
     //! @return true for success
     // *****************************************************************
     bool executeQuery (const std::string& iQuery, BlobData& oBlobData);

     // *****************************************************************
     //! Execute the specified SQL statement. 
     //! 
     //! @return true for success
     // *****************************************************************
     bool executeStatement (const std::string& iStatement);

     // *****************************************************************
     //! Check that the schema read from the database matchs the table
     //! definitions
     //! throws SqliteException if a table mis-match is detected.
     //! @return true for success
     // *****************************************************************
     void validateSchema (const ::SQL::Schema::TableDefinitionType& tableDefinitions);

     // *****************************************************************
     //! Normilise the specified sql statement or group of statements.
     //! @param sql the sql statement to normalise
     //! 
     //! @return normilises SQL statement(s)
     // *****************************************************************
     std::string normalise(const std::string& statement) const;

     // *****************************************************************
     //! Helper Method used to release any resources used by sqlite
     //! after a statement had been compiled. As this is used by the
     //! ScopedFinalise class above, it does not throw an exception
     //! on error, but returns an error code. This is so that the
     //! ScopedFinalise destructor does not emit any exceptions.
     //! 
     //! @param location information on calling method
     //! @param ppStmt  pointer to database resource
     //! 
     // *****************************************************************
     static bool finaliseCompile (const char* const location, sqlite3_stmt* ppStmt);

     // *****************************************************************
     //! Helper Method used to check the result of an Sqlite statement  
     //! compile.
     //! 
     //! @param location information on calling method
     //! @param compile_result the result code for the compile
     //! @param query the query that was compiled
     //! 
     //! throws SqliteException on error
     // *****************************************************************
     static void checkCompile(const char* const location, const int32_t compile_result, const std::string& query);

     // *****************************************************************
     //! Helper Method used to check the number of columns returned from 
     //! a query request match the number specified.
     //! 
     //! @param location information on calling method
     //! @param columnCount the the expected column count.
     //! 
     //! throws SqliteException on error
     // *****************************************************************
     static void checkColumnCount(const char* const location, const int32_t actual, const int32_t expected, const std::string& query);

  private:
     Database();
    ~Database();
     
     // Prebent copy and assignement
     Database(const Database& iRhs);
     Database& operator=(const Database& iRhs);

     void dropTables ();

     void check       (const char* const  iWithin);
     void reportError (const std::string& message);

     void notifyCommit();
     void notifyStart();
     void notifyAbort();

     bool openMemoryBasedDb();
     bool openDiskBasedDb (const bool iIsColdStart);

  private:
     std::string        schema_;
     std::string        dbName_;
     std::string        error_;
     sqlite3*           database_;
};

} // end namespace SQLITE

#endif
