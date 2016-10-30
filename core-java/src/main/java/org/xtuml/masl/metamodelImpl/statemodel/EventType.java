//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.statemodel;

public enum EventType
{
  NORMAL("", EventDeclaration.Type.NORMAL), CREATION("creation", EventDeclaration.Type.CREATION), ASSIGNER("assigner",
      EventDeclaration.Type.ASSIGNER);

  private final String                text;
  private final EventDeclaration.Type type;

  private EventType ( final String text, final EventDeclaration.Type type )
  {
    this.text = text;
    this.type = type;
  }

  public EventDeclaration.Type getType ()
  {
    return type;
  }

  @Override
  public String toString ()
  {
    return text;
  }
}
