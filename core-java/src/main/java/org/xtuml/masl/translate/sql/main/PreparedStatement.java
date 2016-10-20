/*
 * Filename : PreparedStatement.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.main;

import java.util.List;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.Expression;


public interface PreparedStatement
{

  public enum PreparedStatementType
  {
    CREATE,
                                      INSERT,
                                      UPDATE,
                                      DELETE
  }

  public Class getClassType ();

  public String getStatement ();

  public Expression prepare ( Expression statementExpr );

  public Expression execute ( Expression statementExpr, List<Expression> arguments );
}
