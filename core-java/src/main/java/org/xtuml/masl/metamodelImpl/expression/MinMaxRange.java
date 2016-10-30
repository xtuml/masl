//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.type.RangeType;


public class MinMaxRange extends RangeExpression
    implements org.xtuml.masl.metamodel.expression.MinMaxRange
{

  private final Expression min;
  private final Expression max;
  private final RangeType  type;

  public MinMaxRange ( final Expression min, final Expression max )
  {
    super(min.getPosition());
    this.min = min;
    this.max = max;
    type = RangeType.createAnonymous(max.getType());
  }

  @Override
  public Expression getMin ()
  {
    return min;
  }

  @Override
  public Expression getMax ()
  {
    return max;
  }

  @Override
  public RangeType getType ()
  {
    return type;
  }

  @Override
  public String toString ()
  {
    return min + ".." + max;
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof MinMaxRange) )
    {
      return false;
    }
    else
    {
      final MinMaxRange rhs = (MinMaxRange)obj;

      return min.equals(rhs.min) && max.equals(rhs.max);
    }
  }

  @Override
  public int hashCode ()
  {

    return min.hashCode() * 31 + max.hashCode();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitMinMaxRange(this, p);
  }

}
