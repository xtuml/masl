//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.utils.HashCode;


public final class RangeType extends CollectionType
{

  public static RangeType createAnonymous ( final BasicType containedType )
  {
    return new RangeType(null, containedType);
  }

  private RangeType ( final Position position, final BasicType containedType )
  {
    super(position, containedType, true);
  }

  @Override
  public String toString ()
  {
    return (isAnonymousType() ? "anonymous " : "") + "range of " + getContainedType();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof RangeType) )
    {
      return false;
    }
    else
    {

      final RangeType rhs = (RangeType)obj;

      return collEquals(rhs);
    }
  }

  @Override
  public int hashCode ()
  {
    return HashCode.combineHashes(collHashCode());
  }

  @Override
  public RangeType getBasicType ()
  {
    return new RangeType(null, getContainedType().getBasicType());
  }


  @Override
  public ActualType getActualType ()
  {
    return null;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    throw new IllegalStateException("Cannot visit RangeType");
  }


}
