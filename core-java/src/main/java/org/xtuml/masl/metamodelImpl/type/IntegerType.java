//
// File: IntegerType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.common.Position;


public class IntegerType extends NumericType
{

  public static IntegerType create ( final Position position, final boolean anonymous )
  {
    return new IntegerType(position, anonymous);
  }

  private static final IntegerType ANON = new IntegerType(null, true);

  public static IntegerType createAnonymous ()
  {
    return ANON;
  }

  private IntegerType ( final Position position, final boolean anonymous )
  {
    super(position, "long_integer", true, 64, anonymous);
  }

  @Override
  public IntegerType getPrimitiveType ()
  {
    return IntegerType.createAnonymous();
  }

  @Override
  public IntegerType getBasicType ()
  {
    return this;
  }

  @Override
  protected boolean isConvertibleFromRelaxation ( final BasicType rhs )
  {
    return rhs instanceof RealType || rhs.getDefinedType() instanceof EnumerateType;
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.INTEGER;
  }


}
