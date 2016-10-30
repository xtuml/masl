//
// File: Position.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.common;

import java.util.IdentityHashMap;


public abstract class Position
{

  public abstract String getText ();

  public abstract String getContext ();

  public abstract int getLineNumber ();

  @Override
  public String toString ()
  {
    return getText();
  }

  public static Position getPosition ( final String key )
  {
    return positionLookup.get(key);
  }

  public static void registerPosition ( final String key, final Position position )
  {
    positionLookup.put(key, position);
  }

  private static IdentityHashMap<String, Position> positionLookup = new IdentityHashMap<String, Position>();

  public static Position                           NO_POSITION    = null;
}
