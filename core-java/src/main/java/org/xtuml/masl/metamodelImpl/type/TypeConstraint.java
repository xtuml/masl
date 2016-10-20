//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.RangeExpression;


public abstract class TypeConstraint
    implements org.xtuml.masl.metamodel.type.TypeConstraint
{

  protected final Expression range;

  public TypeConstraint ( final Expression range )
  {
    this.range = range;
  }

  @Override
  public RangeExpression getRange ()
  {
    return (RangeExpression)range;
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof TypeConstraint) )
    {
      return false;
    }
    else
    {
      final TypeConstraint rhs = (TypeConstraint)obj;

      return range.equals(rhs.range);
    }
  }

  @Override
  public int hashCode ()
  {
    return range.hashCode();
  }
}
