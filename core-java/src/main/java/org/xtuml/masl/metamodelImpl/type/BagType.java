//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;


public final class BagType extends CollectionType
    implements org.xtuml.masl.metamodel.type.BagType
{

  public static BagType create ( final Position position, final BasicType containedType, final boolean anonymous )
  {
    if ( containedType == null )
    {
      return null;
    }

    return new BagType(position, containedType, anonymous);
  }

  public static BagType createAnonymous ( final BasicType containedType )
  {
    return new BagType(null, containedType, true);
  }


  private BagType ( final Position position, final BasicType containedType, final boolean anonymous )
  {
    super(position, containedType, anonymous);
  }

  @Override
  public String toString ()
  {
    return (isAnonymousType() ? "anonymous " : "") + "bag of " + getContainedType();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof BagType) )
    {
      return false;
    }
    else
    {
      final BagType rhs = (BagType)obj;

      return collEquals(rhs);
    }
  }

  @Override
  public int hashCode ()
  {
    return collHashCode();
  }

  @Override
  public BagType getBasicType ()
  {
    return new BagType(null, getContainedType().getBasicType(), true);
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.BAG;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitBagType(this, p);
  }


}
