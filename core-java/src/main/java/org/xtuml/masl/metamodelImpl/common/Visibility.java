//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.common;


public enum Visibility
{

  PUBLIC("public", org.xtuml.masl.metamodel.common.Visibility.PUBLIC), PRIVATE("private",
      org.xtuml.masl.metamodel.common.Visibility.PRIVATE);

  private final String                                    text;
  private final org.xtuml.masl.metamodel.common.Visibility visibility;

  private Visibility ( final String text, final org.xtuml.masl.metamodel.common.Visibility visibility )
  {
    this.text = text;
    this.visibility = visibility;
  }

  public org.xtuml.masl.metamodel.common.Visibility getVisibility ()
  {
    return visibility;
  }

  @Override
  public String toString ()
  {
    return text;
  }
}
