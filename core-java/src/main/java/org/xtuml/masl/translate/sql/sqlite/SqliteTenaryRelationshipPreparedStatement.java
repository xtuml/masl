/*
 * Filename : SqliteTenaryRelationshipPreparedStatement.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.sqlite;

import org.xtuml.masl.translate.sql.main.PreparedStatement;


public class SqliteTenaryRelationshipPreparedStatement extends SqliteRelationshipPreparedStatement
{

  private final SqliteTenaryRelationshipToTableTranslator tableTranslator;

  public SqliteTenaryRelationshipPreparedStatement ( final SqliteTenaryRelationshipToTableTranslator tableTranslator,
                                                     final PreparedStatement.PreparedStatementType statementType )
  {
    super(statementType);
    this.tableTranslator = tableTranslator;
    this.statementType = statementType;
    formStatement();
  }


  private void formStatement ()
  {
    switch ( statementType )
    {
      case DELETE:
        preparedStatement = "DELETE FROM " + tableTranslator.getTableName() +
                               " WHERE " +
                                  tableTranslator.getLeftColumnName() + " = :1 AND " +
                                  tableTranslator.getRightColumnName() + "= :2 AND " +
                                  tableTranslator.getAssocColumnName() + "= :3;";
        break;

      case INSERT:
        preparedStatement = "INSERT INTO " + tableTranslator.getTableName() + " VALUES(:1,:2,:3);";
        break;

      default:
        throw new RuntimeException("RelationshipPreparedStatement could not support required statementType : " + statementType);
    }
  }

}
