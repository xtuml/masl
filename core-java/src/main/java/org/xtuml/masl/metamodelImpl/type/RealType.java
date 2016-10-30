//
// File: RealType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.common.Position;


public class RealType extends NumericType
{

  public static RealType create ( final Position position, final boolean anonymous )
  {
    return new RealType(position, anonymous);
  }

  private static final RealType ANON = new RealType(null, true);

  public static RealType createAnonymous ()
  {
    return ANON;
  }

  private RealType ( final Position position, final boolean anonymous )
  {
    super(position, "real", anonymous);
  }

  @Override
  public RealType getPrimitiveType ()
  {
    return this;
  }

  @Override
  public RealType getBasicType ()
  {
    return this;
  }

  @Override
  protected boolean isAssignableFromRelaxation ( final BasicType rhs )
  {
    return rhs.isAnonymousType() && rhs instanceof IntegerType;
  }

  @Override
  protected boolean isConvertibleFromRelaxation ( final BasicType rhs )
  {
    return rhs instanceof IntegerType;
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.REAL;
  }

}
