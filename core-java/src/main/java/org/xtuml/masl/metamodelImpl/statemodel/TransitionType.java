//
// File: TransitionType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.statemodel;

public enum TransitionType
{
  TO_STATE("", org.xtuml.masl.metamodel.statemodel.TransitionType.TO_STATE), CANNOT_HAPPEN("Cannot_Happen",
      org.xtuml.masl.metamodel.statemodel.TransitionType.CANNOT_HAPPEN), IGNORE("Ignore",
      org.xtuml.masl.metamodel.statemodel.TransitionType.IGNORE);


  private final String                                            text;
  private final org.xtuml.masl.metamodel.statemodel.TransitionType type;

  private TransitionType ( final String text, final org.xtuml.masl.metamodel.statemodel.TransitionType type )
  {
    this.text = text;
    this.type = type;
  }

  public org.xtuml.masl.metamodel.statemodel.TransitionType getType ()
  {
    return type;
  }

  @Override
  public String toString ()
  {
    return text;
  }
}
