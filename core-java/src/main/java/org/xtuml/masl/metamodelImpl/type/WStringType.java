//
// File: WStringType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.common.Position;


public class WStringType extends BuiltinType
{

  public static WStringType create ( final Position position, final boolean anonymous )
  {
    return new WStringType(position, anonymous);
  }

  private static final WStringType ANON = new WStringType(null, true);

  public static WStringType createAnonymous ()
  {
    return ANON;
  }

  private WStringType ( final Position position, final boolean anonymous )
  {
    super(position, "wstring", anonymous);
  }

  @Override
  public BasicType getContainedType ()
  {
    return isAnonymousType() ? WCharacterType.createAnonymous() : WCharacterType.create(null, false);
  }

  @Override
  public SequenceType getPrimitiveType ()
  {
    return SequenceType.createAnonymous(getContainedType());
  }

  @Override
  public WStringType getBasicType ()
  {
    return this;
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.WSTRING;
  }

  @Override
  public boolean isString ()
  {
    return true;
  }

}
