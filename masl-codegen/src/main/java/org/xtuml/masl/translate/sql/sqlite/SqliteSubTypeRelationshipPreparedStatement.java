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

import org.xtuml.masl.translate.sql.main.PreparedStatement;

public class SqliteSubTypeRelationshipPreparedStatement extends SqliteRelationshipPreparedStatement {

    private final SqliteSubTypeRelationshipToTableTranslator tableTranslator;

    public SqliteSubTypeRelationshipPreparedStatement(final SqliteSubTypeRelationshipToTableTranslator tableTranslator,
                                                      final PreparedStatement.PreparedStatementType statementType) {
        super(statementType);
        this.tableTranslator = tableTranslator;
        this.statementType = statementType;
        formStatement();
    }

    private void formStatement() {
        switch (statementType) {
            case DELETE:
                preparedStatement =
                        "DELETE FROM " +
                        tableTranslator.getTableName() +
                        " WHERE " +
                        tableTranslator.getTypeColumnName() +
                        " = " +
                        tableTranslator.getTypeColumnValue() +
                        " AND " +
                        tableTranslator.getLeftColumnName() +
                        " = :1 AND " +
                        tableTranslator.getRightColumnName() +
                        " = :2;";
                break;

            case INSERT:
                preparedStatement =
                        "INSERT INTO " +
                        tableTranslator.getTableName() +
                        " VALUES(:1,:2," +
                        tableTranslator.getTypeColumnValue() +
                        ");";
                break;

            default:
                throw new RuntimeException(
                        "SqliteSubTypeRelationshipPreparedStatement does not support required statementType : " +
                        statementType);
        }
    }
}
