/*
 * Filename : SqliteUnaryOperator.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.sqlite;

import org.xtuml.masl.metamodel.expression.UnaryExpression;


/**
 * Define a enumeration that can provide the required mapping between the unary
 * operators used by MASL and their SQL equivalents. This will be used when a
 * MASL find Expression is being translated into a suitable SQL where clause.
 */
public enum SqliteUnaryOperator
{
  NOT(" NOT ");

  private String operatorText;

  private SqliteUnaryOperator ( final String operatorText )
  {
    this.operatorText = operatorText;
  }

  @Override
  public String toString ()
  {
    return operatorText;
  }

  static String maslToSqlOperator ( final UnaryExpression.Operator operator )
  {
    switch ( operator )
    {
      case NOT:
        return SqliteUnaryOperator.NOT.toString();
      default:
        throw new AssertionError("unknown MASl to SQL unary operator Mapping : " + operator);
    }
  }

}
