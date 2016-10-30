//
// File: SplitExpression.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.BooleanType;
import org.xtuml.masl.metamodelImpl.type.DurationType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;
import org.xtuml.masl.metamodelImpl.type.TimestampType;
import org.xtuml.masl.utils.HashCode;


public class TimerFieldExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.TimerFieldExpression
{

  TimerFieldExpression ( final Position position, final Expression lhs, final String characteristic ) throws SemanticError
  {
    super(position);
    this.lhs = lhs;

    if ( lhs.getType().getBasicType().getActualType() == ActualType.TIMER )
    {
      field = Field.valueOf(characteristic);
    }
    else
    {
      throw new SemanticError(SemanticErrorCode.CharacteristicNotValid, position, characteristic, lhs.getType());
    }

  }


  private TimerFieldExpression ( final Position position, final Expression lhs, final Field field )
  {
    super(position);
    this.lhs = lhs;
    this.field = field;
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( obj != null )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj.getClass() == getClass() )
      {
        final TimerFieldExpression obj2 = ((TimerFieldExpression)obj);
        return lhs.equals(obj2.lhs) && field == obj2.field;
      }
      else
      {
        return false;
      }
    }
    return false;
  }

  @Override
  public Field getField ()
  {
    return field;
  }

  @Override
  public int getFindAttributeCount ()
  {
    return lhs.getFindAttributeCount();
  }

  @Override
  public Expression getFindSkeletonInner ()
  {
    return new TimerFieldExpression(getPosition(), lhs.getFindSkeleton(), field);
  }


  @Override
  public Expression getLhs ()
  {
    return lhs;
  }


  @Override
  public BasicType getType ()
  {
    switch ( field )
    {
      case delta:
        return DurationType.createAnonymous();
      case scheduled_at:
        return TimestampType.createAnonymous();
      case expired_at:
        return TimestampType.createAnonymous();
      case expired:
        return BooleanType.createAnonymous();
      case scheduled:
        return BooleanType.createAnonymous();
      case missed:
        return IntegerType.createAnonymous();
      default:
        assert false;
        return null;
    }
  }

  @Override
  public int hashCode ()
  {
    return HashCode.combineHashes(field.hashCode(), lhs.hashCode());
  }

  @Override
  public String toString ()
  {
    return lhs + "'" + field;
  }

  @Override
  protected List<Expression> getFindArgumentsInner ()
  {
    return new ArrayList<Expression>(lhs.getFindArguments());

  }

  @Override
  protected List<FindParameterExpression> getFindParametersInner ()
  {
    return new ArrayList<FindParameterExpression>(lhs.getConcreteFindParameters());

  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitTimerFieldExpression(this, p);
  }

  private final Expression lhs;
  private final Field      field;

  @Override
  public List<Expression> getChildExpressions ()
  {
    return Arrays.<Expression>asList(lhs);
  }

}
