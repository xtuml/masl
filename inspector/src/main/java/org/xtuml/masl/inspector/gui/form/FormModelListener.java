//
// File: TableModelListener.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.inspector.gui.form;

import java.util.EventListener;


public interface FormModelListener
    extends EventListener
{

  void formChanged ( FormModelEvent e );

}
