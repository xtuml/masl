// 
// Filename : SuperSubLineUIResource.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui.modelView;

import javax.swing.UIManager;


public class SuperSubLineUIResource
    implements javax.swing.plaf.UIResource
{

  private final java.awt.Shape arrowHead;

  public SuperSubLineUIResource ( final java.awt.Shape arrowHead )
  {
    this.arrowHead = arrowHead;
  }

  public java.awt.Shape getArrowHead ()
  {
    return arrowHead;
  }

  public static SuperSubLineUIResource getSupertypeLineHead ()
  {
    return (SuperSubLineUIResource)UIManager.get("SuperSub.super");
  }

  public static SuperSubLineUIResource getSubtypeLineHead ()
  {
    return (SuperSubLineUIResource)UIManager.get("SuperSub.sub");
  }
}
