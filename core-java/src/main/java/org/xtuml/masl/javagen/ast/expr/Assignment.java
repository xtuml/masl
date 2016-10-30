//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;


public interface Assignment
    extends StatementExpression
{

  enum Operator
  {
    ASSIGN,
    ADD,
    MULTIPLY,
    REMAINDER,
    DIVIDE,
    SUBTRACT,
    LEFT_SHIFT,
    RIGHT_SHIFT,
    RIGHT_SHIFT_ZERO_EXTEND,
    BITWISE_AND,
    BITWISE_XOR,
    BITWISE_OR
  }

  Operator getOperator ();

  Expression getTarget ();

  Expression getSource ();

  Expression setTarget ( Expression target );

  Expression setSource ( Expression source );

  void setOperator ( Operator operator );
}
