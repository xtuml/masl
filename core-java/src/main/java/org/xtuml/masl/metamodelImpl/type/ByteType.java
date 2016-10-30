//
// File: ByteType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.common.Position;


public class ByteType extends NumericType
{

  public static ByteType create ( final Position position, final boolean anonymous )
  {
    return new ByteType(position, anonymous);
  }

  private static final ByteType ANON = new ByteType(null, true);

  public static ByteType createAnonymous ()
  {
    return ANON;
  }

  private ByteType ( final Position position, final boolean anonymous )
  {
    super(position, "byte", false, 8, anonymous);
  }

  @Override
  public IntegerType getPrimitiveType ()
  {
    return IntegerType.createAnonymous();
  }

  @Override
  public ByteType getBasicType ()
  {
    return this;
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.BYTE;
  }


}
