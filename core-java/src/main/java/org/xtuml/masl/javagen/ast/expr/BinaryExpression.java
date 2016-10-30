//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;


public interface BinaryExpression
    extends Expression
{

  enum Operator
  {
    MULTIPLY,
    DIVIDE,
    REMAINDER,
    ADD,
    SUBTRACT,
    LEFT_SHIFT,
    RIGHT_SHIFT,
    RIGHT_SHIFT_UNSIGNED,
    LESS_THAN,
    GREATER_THAN,
    LESS_THAN_OR_EQUAL_TO,
    GREATER_THAN_OR_EQUAL_TO,
    INSTANCEOF,
    EQUAL_TO,
    NOT_EQUAL_TO,
    BITWISE_AND,
    BITWISE_OR,
    BITWISE_XOR,
    AND,
    OR
  }

  Expression getLhs ();

  Operator getOperator ();

  Expression getRhs ();

  Expression setLhs ( Expression lhs );

  Expression setRhs ( Expression rhs );
}
