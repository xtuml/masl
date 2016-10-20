// 
// Filename : ToggleAction.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Icon;


public abstract class ToggleAction extends AbstractAction
{

  public ToggleAction ( final String name )
  {
    super(name);
  }

  public ToggleAction ( final String name, final Icon icon )
  {
    super(name, icon);
  }

  public void actionPerformed ( final ActionEvent event )
  {
    try
    {
      togglePerformed(((AbstractButton)event.getSource()).isSelected());
    }
    catch ( final Exception e )
    {
      ((AbstractButton)event.getSource()).setSelected(!((AbstractButton)event.getSource()).isSelected());
      e.printStackTrace();
    }
  }

  public abstract void togglePerformed ( boolean selected ) throws Exception;
}
