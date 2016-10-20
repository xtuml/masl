//
// File: TimestampType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.common.Position;


public class TimestampType extends BuiltinType
{

  public static TimestampType create ( final Position position, final boolean anonymous )
  {
    return new TimestampType(position, anonymous);
  }

  private static final TimestampType ANON = new TimestampType(null, true);

  public static TimestampType createAnonymous ()
  {
    return ANON;
  }

  private TimestampType ( final Position position, final boolean anonymous )
  {
    super(position, "timestamp", anonymous);
  }

  @Override
  public TimestampType getPrimitiveType ()
  {
    return ANON;
  }

  @Override
  public TimestampType getBasicType ()
  {
    return this;
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.TIMESTAMP;
  }
}
