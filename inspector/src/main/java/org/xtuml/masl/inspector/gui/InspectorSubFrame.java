// 
// Filename : InspectorSubFrame.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

public class InspectorSubFrame extends javax.swing.JFrame
{

  public InspectorSubFrame ( final String title )
  {
    super(title);
    setIconImage(getToolkit().getImage(ClassLoader.getSystemResource("icons/inspector.gif")));

  }

  public void addToWindowMenu ()
  {
    WindowMenu.getInstance().addWindow(this);
  }


  public InspectorSubFrame ()
  {
    this("");
  }
}
