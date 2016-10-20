//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;


public class SetType extends CollectionType
    implements org.xtuml.masl.metamodel.type.SetType
{

  public static SetType create ( final Position position, final BasicType containedType, final boolean anonymous )
  {
    if ( containedType == null )
    {
      return null;
    }

    return new SetType(position, containedType, anonymous);
  }

  public static SetType createAnonymous ( final BasicType containedType )
  {
    return new SetType(null, containedType, true);
  }

  private SetType ( final Position position, final BasicType containedType, final boolean anonymous )
  {
    super(position, containedType, anonymous);
  }

  @Override
  public String toString ()
  {
    return (isAnonymousType() ? "anonymous " : "") + "set of " + getContainedType();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof SetType) )
    {
      return false;
    }
    else
    {

      final SetType rhs = (SetType)obj;

      return collEquals(rhs);
    }
  }

  @Override
  public int hashCode ()
  {
    return collHashCode();
  }

  @Override
  public SetType getBasicType ()
  {
    return new SetType(null, getContainedType().getBasicType(), true);
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.SET;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitSetType(this, p);
  }


}
