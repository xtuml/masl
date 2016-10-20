// 
// Filename : CurrentStateRenderer.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.util.HashMap;
import java.util.Map;

import org.xtuml.masl.inspector.processInterface.StateMetaData;


class CurrentStateRenderer extends javax.swing.table.DefaultTableCellRenderer
{

  private final Map<Integer, String> lookup = new HashMap<Integer, String>();

  public CurrentStateRenderer ( final StateMetaData[] states )
  {
    super();
    for ( final StateMetaData state : states )
    {
      lookup.put(new Integer(state.getId()), state.getName());
    }
  }

  @Override
  public void setValue ( final Object value )
  {
    setText(lookup.get(value));
  }
}
