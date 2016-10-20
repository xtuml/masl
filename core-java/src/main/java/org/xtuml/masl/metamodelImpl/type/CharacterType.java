//
// File: CharacterType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.common.Position;


public class CharacterType extends BuiltinType
{

  public static CharacterType create ( final Position position, final boolean anonymous )
  {
    return new CharacterType(position, anonymous);
  }

  private static final CharacterType ANON = new CharacterType(null, true);

  public static CharacterType createAnonymous ()
  {
    return ANON;
  }

  private CharacterType ( final Position position, final boolean anonymous )
  {
    super(position, "character", anonymous);
  }

  @Override
  public WCharacterType getPrimitiveType ()
  {
    return WCharacterType.createAnonymous();
  }

  @Override
  public CharacterType getBasicType ()
  {
    return this;
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.CHARACTER;
  }

  @Override
  public boolean isCharacter ()
  {
    return true;
  }


}
