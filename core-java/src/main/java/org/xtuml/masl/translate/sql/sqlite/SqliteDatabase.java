/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.sql.sqlite;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.translate.main.Architecture;

public class SqliteDatabase {

    static Library sqlitelib = new Library("sqlite").inBuildSet(Architecture.buildSet);
    static final Namespace sqliteNamespace = new Namespace("SQLITE");

    static public final CodeFile sqlite3Inc = sqlitelib.createInterfaceHeader("sqlite3.h");
    static public final CodeFile exceptionInc = sqlitelib.createInterfaceHeader("sqlite/Exception.hh");
    static public final CodeFile databaseInc = sqlitelib.createInterfaceHeader("sqlite/Database.hh");
    static public final CodeFile resultSetInc = sqlitelib.createInterfaceHeader("sqlite/Resultset.hh");
    static public final CodeFile sqlMonitorInc = sqlitelib.createInterfaceHeader("sqlite/SqlMonitor.hh");
    static public final CodeFile
            sqlTimerMapperSqlInc =
            sqlitelib.createInterfaceHeader("sqlite/SqliteTimerMapperSql.hh");
    static public final CodeFile preparedStatInc = sqlitelib.createInterfaceHeader("sqlite/PreparedStatement.hh");
    static public final CodeFile blobInc = sqlitelib.createInterfaceHeader("sqlite/BlobData.hh");
    static public final CodeFile
            eventParamCodecsInc =
            sqlitelib.createInterfaceHeader("sqlite/EventParameterCodecs.hh");

    static final Class resultSetClass = new Class("ResultSet", sqliteNamespace, resultSetInc);
    static final Class sqlite3StmtClass = new Class("sqlite3_stmt", null, sqlite3Inc);
    static final Class sqliteExceptionClass = new Class("SqliteException", sqliteNamespace, exceptionInc);
    private static final Class sqliteDatabaseClass = new Class("Database", sqliteNamespace, databaseInc);
    static final Class sqlMonitorClass = new Class("SqlQueryMonitor", sqliteNamespace, sqlMonitorInc);
    static final Class
            sqlTimerMapperSqlClass =
            new Class("SqliteTimerMapperSql", sqliteNamespace, sqlTimerMapperSqlInc);
    public static final Class blobClass = new Class("BlobData", sqliteNamespace, blobInc);
    public final static Class preparedStatement = new Class("PreparedStatement", sqliteNamespace, preparedStatInc);
    public final static Expression
            eventParameterCodecs =
            new Class("EventParameterCodecs", sqliteNamespace, eventParamCodecsInc).callStaticFunction("getInstance");

    private SqliteDatabase() {

    }

    static ThrowStatement throwDatabaseException(final String error) {
        return new ThrowStatement(sqliteExceptionClass.callConstructor(Literal.createStringLiteral(error)));
    }

    static ThrowStatement throwDatabaseException(final Expression error) {
        return new ThrowStatement(sqliteExceptionClass.callConstructor(error));
    }

    public static Class getSqlitedatabaseclass() {
        return sqliteDatabaseClass;
    }

}
