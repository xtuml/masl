/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.sql.sqlite;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.translate.sql.main.PreparedStatement;

import java.util.List;

public class SqliteObjectPreparedStatement implements PreparedStatement {

    private final SqliteObjectToTableTranslator tableTranslator;
    private final PreparedStatement.PreparedStatementType statementType;

    private String preparedStatement;

    public SqliteObjectPreparedStatement(final SqliteObjectToTableTranslator tableTranslator,
                                         final PreparedStatement.PreparedStatementType statementType) {
        this.tableTranslator = tableTranslator;
        this.statementType = statementType;
        formStatement();
    }

    @Override
    public Class getClassType() {
        return SqliteDatabase.preparedStatement;
    }

    @Override
    public String getStatement() {
        return preparedStatement;
    }

    @Override
    public Expression prepare(final Expression statementExpr) {
        return new Function("prepare").asFunctionCall(statementExpr, false);
    }

    @Override
    public Expression execute(final Expression statementExpr, final List<Expression> arguments) {
        return new Function("execute").asFunctionCall(statementExpr, false, arguments);
    }

    private void formStatement() {
        final List<String> columnList = tableTranslator.getColumnList();

        switch (statementType) {
            case DELETE:
                preparedStatement =
                        "DELETE  FROM " + tableTranslator.getTableName() + " WHERE " + columnList.get(0) + " = :1;";
                break;

            case INSERT:
                // Form the prepared insert statement
                final StringBuilder
                        insertStatement =
                        new StringBuilder("INSERT INTO " + tableTranslator.getTableName() + " VALUES(");
                for (int x = 0; x < columnList.size(); ++x) {
                    insertStatement.append(":" + (x + 1));
                    if (x != columnList.size() - 1) {
                        insertStatement.append(",");
                    }
                }
                insertStatement.append(");");
                preparedStatement = insertStatement.toString();
                break;

            case UPDATE:
                // Form the prepared update statement
                final StringBuilder
                        updateStatement =
                        new StringBuilder("UPDATE " + tableTranslator.getTableName() + " SET ");
                for (int x = 1; x < columnList.size(); ++x) {
                    updateStatement.append(columnList.get(x) + " = :" + (x + 1) + " ");
                    if (x != columnList.size() - 1) {
                        updateStatement.append(" , ");
                    }
                }
                updateStatement.append(" WHERE " + columnList.get(0) + " = :1;");
                preparedStatement = updateStatement.toString();
                break;

            default:
                throw new RuntimeException("");

        }
    }

}
