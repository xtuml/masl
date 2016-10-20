/*
 * Filename : SqliteBinaryOperator.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.sqlite;

import org.xtuml.masl.metamodel.expression.BinaryExpression;


/**
 * Define a enumeration that can provide the required mapping between the binary
 * operators used by MASL and their SQL equivalents. This will be used when a
 * MASL find Expression is being translated into a suitable SQL where clause.
 */
public enum SqliteBinaryOperator
{
                  AND(" AND "),
                  XOR(" XOR "),
                  OR(" OR "),
                  NOT_EQUAL(" != "),
                  EQUAL(" = "),
                  LESS_THAN(" < "),
                  GREATER_THAN(" > "),
                  LESS_THAN_OR_EQUAL(" <= "),
                  GREATER_THAN_OR_EQUAL(" >= ");

  private String operatorText;

  private SqliteBinaryOperator ( final String operatorText )
  {
    this.operatorText = operatorText;
  }

  @Override
  public String toString ()
  {
    return operatorText;
  }

  static String maslToSqlOperator ( final BinaryExpression.Operator operator )
  {
    switch ( operator )
    {
      case AND:
        return SqliteBinaryOperator.AND.toString();
      case XOR:
        return SqliteBinaryOperator.XOR.toString();
      case OR:
        return SqliteBinaryOperator.OR.toString();
      case NOT_EQUAL:
        return SqliteBinaryOperator.NOT_EQUAL.toString();
      case EQUAL:
        return SqliteBinaryOperator.EQUAL.toString();
      case LESS_THAN:
        return SqliteBinaryOperator.LESS_THAN.toString();
      case GREATER_THAN:
        return SqliteBinaryOperator.GREATER_THAN.toString();
      case LESS_THAN_OR_EQUAL:
        return SqliteBinaryOperator.LESS_THAN_OR_EQUAL.toString();
      case GREATER_THAN_OR_EQUAL:
        return SqliteBinaryOperator.GREATER_THAN_OR_EQUAL.toString();
      default:
        throw new AssertionError("unknown MASl to SQL binary operator Mapping : " + operator);
    }
  }
}
