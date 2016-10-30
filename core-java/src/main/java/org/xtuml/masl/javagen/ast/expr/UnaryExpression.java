//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;


public interface UnaryExpression
    extends Expression
{

  enum Operator
  {
    PLUS, MINUS, NOT, BITWISE_COMPLEMENT
  }

  Expression getExpression ();

  Expression setExpression ( Expression expression );

  Operator getOperator ();

  void setOperator ( Operator operator );

}
