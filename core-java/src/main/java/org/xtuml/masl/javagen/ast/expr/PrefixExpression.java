//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

public interface PrefixExpression
    extends StatementExpression
{

  enum Operator
  {
    INCREMENT, DECREMENT
  }

  Operator getOperator ();

  Expression getExpression ();

  void setOperator ( Operator operator );

  Expression setExpression ( Expression expression );

}
