//
// File: AnyInstanceType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.common.Position;


public class AnyInstanceType extends BuiltinType
{

  @Override
  public ActualType getActualType ()
  {
    return ActualType.ANY_INSTANCE;
  }

  public static AnyInstanceType create ( final Position position, final boolean anonymous )
  {
    return new AnyInstanceType(position, anonymous);
  }

  private static final AnyInstanceType ANON = new AnyInstanceType(null, true);

  public static AnyInstanceType createAnonymous ()
  {
    return ANON;
  }

  private AnyInstanceType ( final Position position, final boolean anonymous )
  {
    super(position, "instance", anonymous);
  }

  @Override
  protected boolean isAssignableFromRelaxation ( final BasicType rhs )
  {
    return rhs.getPrimitiveType() instanceof InstanceType;
  }

  @Override
  protected boolean isConvertibleFromRelaxation ( final BasicType rhs )
  {
    return rhs.getPrimitiveType() instanceof InstanceType;
  }

  @Override
  public AnyInstanceType getPrimitiveType ()
  {
    return this;
  }

  @Override
  public AnyInstanceType getBasicType ()
  {
    return this;
  }

  @Override
  public boolean equals ( final Object rhs )
  {
    return rhs instanceof AnyInstanceType;
  }

}
