//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.expression.Expression;


public class DeltaConstraint extends TypeConstraint
    implements org.xtuml.masl.metamodel.type.DeltaConstraint
{

  private final Expression delta;

  public DeltaConstraint ( final Expression delta, final RangeConstraint range )
  {
    super(range.getRange());
    this.delta = delta;
  }

  @Override
  public Expression getDelta ()
  {
    return delta;
  }

  @Override
  public String toString ()
  {
    return "delta " + delta + " range " + range;
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof DeltaConstraint) )
    {
      return false;
    }
    else
    {
      final DeltaConstraint rhs = (DeltaConstraint)obj;

      return super.equals(obj) && delta.equals(rhs.delta);
    }
  }

  @Override
  public int hashCode ()
  {

    return super.hashCode() * 31 + delta.hashCode();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitDeltaConstraint(this, p);
  }

}
