//
// Filename : AutoSizingTableModel.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

abstract class AutoSizingTableModel extends javax.swing.table.AbstractTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    abstract public int getPreferredColumnWidth(int col, javax.swing.JTable parent);
}
