//
// File: DeviceType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.common.Position;


public class DeviceType extends BuiltinType
{

  public static DeviceType create ( final Position position, final boolean anonymous )
  {
    return new DeviceType(position, anonymous);
  }

  private static final DeviceType ANON = new DeviceType(null, true);

  public static DeviceType createAnonymous ()
  {
    return ANON;
  }

  private DeviceType ( final Position position, final boolean anonymous )
  {
    super(position, "device", anonymous);
  }

  @Override
  public DeviceType getPrimitiveType ()
  {
    return this;
  }

  @Override
  public DeviceType getBasicType ()
  {
    return this;
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.DEVICE;
  }

}
