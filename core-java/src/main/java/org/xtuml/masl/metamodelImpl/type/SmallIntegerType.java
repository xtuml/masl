//
// File: IntegerType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.common.Position;


public class SmallIntegerType extends NumericType
{

  public static SmallIntegerType create ( final Position position, final boolean anonymous )
  {
    return new SmallIntegerType(position, anonymous);
  }

  private static final SmallIntegerType ANON = new SmallIntegerType(null, true);

  public static SmallIntegerType createAnonymous ()
  {
    return ANON;
  }

  private SmallIntegerType ( final Position position, final boolean anonymous )
  {
    super(position, "integer", true, 32, anonymous);
  }

  @Override
  public IntegerType getPrimitiveType ()
  {
    return IntegerType.createAnonymous();
  }

  @Override
  public SmallIntegerType getBasicType ()
  {
    return this;
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.SMALL_INTEGER;
  }


}
