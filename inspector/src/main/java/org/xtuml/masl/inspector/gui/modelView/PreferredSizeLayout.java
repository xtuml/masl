// 
// Filename : PreferredSizeLayout.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui.modelView;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JComponent;


public class PreferredSizeLayout
    implements LayoutManager, java.io.Serializable
{

  public void addLayoutComponent ( final String name, final Component comp )
  {
  }

  public void removeLayoutComponent ( final Component comp )
  {
  }

  public Dimension minimumLayoutSize ( final Container target )
  {
    return ((JComponent)target).getMinimumSize();
  }

  public Dimension preferredLayoutSize ( final Container target )
  {
    return ((JComponent)target).getPreferredSize();
  }

  public void layoutContainer ( final Container parent )
  {
    for ( int i = 0; i < parent.getComponentCount(); i++ )
    {
      final Component m = parent.getComponent(i);
      if ( m.isVisible() )
      {
        final Dimension d = m.getPreferredSize();
        m.setSize(d.width, d.height);
      }
    }

  }

}
