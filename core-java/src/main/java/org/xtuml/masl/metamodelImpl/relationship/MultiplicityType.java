//
// File: MultiplicityType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.relationship;

public enum MultiplicityType
{
  ONE("one", org.xtuml.masl.metamodel.relationship.MultiplicityType.ONE), MANY("many",
      org.xtuml.masl.metamodel.relationship.MultiplicityType.MANY);

  private MultiplicityType ( final String text, final org.xtuml.masl.metamodel.relationship.MultiplicityType type )
  {
    this.text = text;
    this.type = type;
  }

  @Override
  public String toString ()
  {
    return text;
  }

  public org.xtuml.masl.metamodel.relationship.MultiplicityType getType ()
  {
    return type;
  }

  private final String                                                text;
  private final org.xtuml.masl.metamodel.relationship.MultiplicityType type;
}
