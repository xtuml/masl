//
// File: EventType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.common.Position;


public class EventType extends BuiltinType
{

  public static EventType create ( final Position position, final boolean anonymous )
  {
    return new EventType(position, anonymous);
  }

  private static final EventType ANON = new EventType(null, true);

  public static EventType createAnonymous ()
  {
    return ANON;
  }

  private EventType ( final Position position, final boolean anonymous )
  {
    super(position, "event", anonymous);
  }

  @Override
  public EventType getPrimitiveType ()
  {
    return this;
  }

  @Override
  public EventType getBasicType ()
  {
    return this;
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.EVENT;
  }

}
