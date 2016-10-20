//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.utils.HashCode;


public final class SequenceType extends CollectionType
    implements org.xtuml.masl.metamodel.type.SequenceType
{

  private final Expression bound;

  public static SequenceType create ( final Position position,
                                      final BasicType containedType,
                                      final Expression bound,
                                      final boolean anonymous )
  {
    if ( containedType == null )
    {
      return null;
    }
    return new SequenceType(position, containedType, bound, anonymous);
  }

  public static SequenceType createAnonymous ( final BasicType containedType )
  {
    return new SequenceType(null, containedType, null, true);
  }

  private SequenceType ( final Position position, final BasicType containedType, final Expression bound, final boolean anonymous )
  {
    super(position, containedType, anonymous);
    this.bound = bound;
  }

  @Override
  public Expression getBound ()
  {
    return bound;
  }

  @Override
  public String toString ()
  {
    return (isAnonymousType() ? "anonymous " : "") + "sequence "
           + (bound == null ? "" : "(" + bound + ") ")
           + "of "
           + getContainedType();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof SequenceType) )
    {
      return false;
    }
    else
    {

      final SequenceType rhs = (SequenceType)obj;

      return collEquals(rhs) && ((bound == rhs.bound) || (bound != null && bound.equals(rhs.bound)));
    }
  }

  @Override
  public int hashCode ()
  {
    return HashCode.combineHashes(collHashCode(), (bound != null ? bound.hashCode() : 0));
  }

  @Override
  public SequenceType getBasicType ()
  {
    return new SequenceType(null, getContainedType().getBasicType(), bound, true);
  }

  @Override
  protected boolean isAssignableFromRelaxation ( final BasicType rhs )
  {
    return (rhs instanceof SequenceType && getContainedType().isAssignableFrom(rhs.getContainedType()))
           || (rhs instanceof RangeType && getContainedType().isAssignableFrom(rhs.getContainedType()));
  }

  @Override
  protected boolean isConvertibleFromRelaxation ( final BasicType rhs )
  {
    return (rhs instanceof SequenceType && getContainedType().isConvertibleFrom(rhs.getContainedType()))
           || (rhs instanceof RangeType && getContainedType().isConvertibleFrom(rhs.getContainedType()));
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.SEQUENCE;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitSequenceType(this, p);
  }


}
