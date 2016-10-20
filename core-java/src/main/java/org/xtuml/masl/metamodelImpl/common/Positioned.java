//
// File: Positioned.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.common;


public abstract class Positioned
{

  protected Positioned ( final String position )
  {
    this.position = Position.getPosition(position);
  }

  protected Positioned ( final Positioned position )
  {
    this.position = position.getPosition();
  }

  protected Positioned ( final Position position )
  {
    this.position = position;
  }

  public Position getPosition ()
  {
    return position;
  }

  private final Position position;

}
