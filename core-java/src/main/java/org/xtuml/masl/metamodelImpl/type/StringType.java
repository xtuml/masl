//
// File: StringType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.common.Position;


public class StringType extends BuiltinType
{

  public static StringType create ( final Position position, final boolean anonymous )
  {
    return new StringType(position, anonymous);
  }

  private static final StringType ANON = new StringType(null, true);

  public static StringType createAnonymous ()
  {
    return ANON;
  }

  private StringType ( final Position position, final boolean anonymous )
  {
    super(position, "string", anonymous);
  }

  @Override
  public SequenceType getPrimitiveType ()
  {
    return SequenceType.createAnonymous(getContainedType());
  }

  @Override
  public StringType getBasicType ()
  {
    return this;
  }

  @Override
  public BasicType getContainedType ()
  {
    return isAnonymousType() ? CharacterType.createAnonymous() : CharacterType.create(null, false);
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.STRING;
  }

  @Override
  public boolean isString ()
  {
    return true;
  }

}
