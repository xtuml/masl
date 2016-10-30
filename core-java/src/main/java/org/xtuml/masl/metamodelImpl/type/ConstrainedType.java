//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.expression.Expression;


public class ConstrainedType extends FullTypeDefinition
    implements org.xtuml.masl.metamodel.type.ConstrainedType
{

  private final BasicType      fullType;
  private final TypeConstraint constraint;

  public ConstrainedType ( final BasicType fullType, final TypeConstraint constraint )
  {
    super(fullType.getPosition());
    this.fullType = fullType;
    this.constraint = constraint;
  }

  @Override
  public BasicType getFullType ()
  {
    return fullType;
  }

  @Override
  public TypeConstraint getConstraint ()
  {
    return constraint;
  }

  @Override
  public String toString ()
  {
    return fullType + " " + constraint;
  }

  @Override
  public Expression getMinValue ()
  {
    return constraint.getRange().getMin();
  }

  @Override
  public Expression getMaxValue ()
  {
    return constraint.getRange().getMax();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof ConstrainedType) )
    {
      return false;
    }
    else
    {
      final ConstrainedType rhs = (ConstrainedType)obj;

      return fullType.equals(rhs.fullType) && constraint.equals(rhs.constraint);
    }
  }

  @Override
  public int hashCode ()
  {
    return fullType.hashCode() * 31 + constraint.hashCode();
  }

  @Override
  public BasicType getPrimitiveType ()
  {
    return fullType.getPrimitiveType();
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.CONSTRAINED;
  }

  @Override
  public void checkCanBePublic ()
  {
    fullType.checkCanBePublic();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitConstrainedType(this, p);
  }

}
