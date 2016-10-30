/*
 * Filename : SqliteRelationshipPreparedStatement.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.sqlite;

import java.util.List;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.translate.sql.main.PreparedStatement;


public class SqliteRelationshipPreparedStatement
    implements PreparedStatement
{

  protected PreparedStatement.PreparedStatementType statementType;
  protected String                                  preparedStatement;

  public SqliteRelationshipPreparedStatement ( final PreparedStatement.PreparedStatementType statementType )
  {
    this.statementType = statementType;
  }

  @Override
  public Class getClassType ()
  {
    return SqliteDatabase.preparedStatement;
  }

  @Override
  public String getStatement ()
  {
    return preparedStatement;
  }

  @Override
  public Expression prepare ( final Expression statementExpr )
  {
    return new Function("prepare").asFunctionCall(statementExpr, false);
  }

  @Override
  public Expression execute ( final Expression statementExpr, final List<Expression> arguments )
  {
    return new Function("execute").asFunctionCall(statementExpr, false, arguments);
  }
}
