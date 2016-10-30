// 
// Filename : InspectorFrame.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

public class InspectorFrame extends javax.swing.JFrame
{

  public InspectorFrame ( final String title )
  {
    super(title);
    setIconImage(getToolkit().getImage(ClassLoader.getSystemResource("icons/inspector.gif")));
  }


  public InspectorFrame ()
  {
    super();
    setIconImage(getToolkit().getImage(ClassLoader.getSystemResource("icons/inspector.gif")));
  }
}
