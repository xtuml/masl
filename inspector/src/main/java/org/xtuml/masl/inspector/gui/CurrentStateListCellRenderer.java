// 
// Filename : CurrentStateListCellRenderer.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JList;

import org.xtuml.masl.inspector.processInterface.StateMetaData;


class CurrentStateListCellRenderer extends javax.swing.DefaultListCellRenderer
{

  private final Map<Integer, String> lookup = new HashMap<Integer, String>();

  public CurrentStateListCellRenderer ( final StateMetaData[] states )
  {
    super();
    for ( final StateMetaData state : states )
    {
      lookup.put(new Integer(state.getId()), state.getName());
    }
  }

  @Override
  public java.awt.Component getListCellRendererComponent ( final JList list,
                                                           final Object value,
                                                           final int index,
                                                           final boolean isSelected,
                                                           final boolean cellHasFocus )
  {
    final JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    label.setText(lookup.get(value));
    return label;
  }

}
