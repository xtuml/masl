//
// File: BinaryLogicalExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.BooleanType;


public class BinaryLogicalExpression extends BinaryExpression
{

  public BinaryLogicalExpression ( Expression lhs, final OperatorRef operator, Expression rhs ) throws SemanticError
  {
    super(lhs.getPosition(), operator);

    rhs = rhs.resolve(lhs.getType());
    lhs = lhs.resolve(rhs.getType());

    setLhs(lhs);
    setRhs(rhs);

    if ( !BooleanType.createAnonymous().isAssignableFrom(getLhs()) )
    {
      throw new SemanticError(SemanticErrorCode.ExpectedBooleanCondition, getLhs().getPosition(), getLhs().getType());
    }

    if ( !BooleanType.createAnonymous().isAssignableFrom(getRhs()) )
    {
      throw new SemanticError(SemanticErrorCode.ExpectedBooleanCondition, getRhs().getPosition(), getRhs().getType());
    }

  }

  @Override
  public BooleanLiteral evaluate ()
  {
    final LiteralExpression lhsVal = getLhs().evaluate();
    final LiteralExpression rhsVal = getRhs().evaluate();

    if ( lhsVal instanceof BooleanLiteral && rhsVal instanceof BooleanLiteral )
    {
      final boolean lhsBool = ((BooleanLiteral)lhsVal).getValue();
      final boolean rhsBool = ((BooleanLiteral)rhsVal).getValue();
      switch ( getOperator() )
      {
        case AND:
          return new BooleanLiteral(lhsBool && rhsBool);
        case OR:
          return new BooleanLiteral(lhsBool || rhsBool);
        case XOR:
          return new BooleanLiteral(lhsBool ^ rhsBool);
        default:
          assert false : "Invalid logical operator " + getOperator();
      }
    }

    return null;
  }

  @Override
  public BasicType getType ()
  {
    return BooleanType.createAnonymous();
  }

}
