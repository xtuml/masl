//
// File: UnaryExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

public interface UnaryExpression
    extends Expression
{

  enum Operator
  {
    MINUS, PLUS, NOT, ABS
  }

  Expression getRhs ();

  Operator getOperator ();

}
