//
// File: BinaryExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

public interface BinaryExpression
    extends Expression
{

  enum Operator
  {
    AND,
    XOR,
    OR,
    NOT_EQUAL,
    EQUAL,
    LESS_THAN,
    GREATER_THAN,
    LESS_THAN_OR_EQUAL,
    GREATER_THAN_OR_EQUAL,
    PLUS,
    MINUS,
    CONCATENATE,
    UNION,
    NOT_IN,
    TIMES,
    DIVIDE,
    MOD,
    POWER,
    REM,
    INTERSECTION,
    DISUNION
  }

  Operator getOperator ();

  Expression getLhs ();

  Expression getRhs ();

}
