/*
 * ================================================================ JCommon : a
 * general purpose, open source, class library for Java
 * ================================================================
 * 
 * Project Info: http://www.jrefinery.com/jcommon Project Lead: David Gilbert
 * (david.gilbert@jrefinery.com);
 * 
 * (C) Copyright 2000, 2001 Simba Management Limited and Contributors;
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 * 
 * ------------------ SortableTable.java ------------------ (C) Copyright 2000,
 * 2001 Simba Management Limited;
 * 
 * Original Author: David Gilbert (for Simba Management Limited);
 * Contributor(s): -;
 * 
 * Changes (from 26-Oct-2001) -------------------------- 26-Oct-2001 : Changed
 * package to com.jrefinery.ui.*;
 */

package com.jrefinery.ui;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * A simple extension of JTable that supports the use of a SortableTableModel.
 */
public class SortableTable extends org.xtuml.masl.inspector.gui.ToolTipTable {

    /** A listener for sorting; */
    SortableTableHeaderListener headerListener;

    /**
     * Standard constructor - builds a table for the specified model.
     */
    public SortableTable(final SortableTableModel model) {
        super(model);

        final SortButtonRenderer renderer = new SortButtonRenderer();
        final TableColumnModel columnModel = this.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setHeaderRenderer(renderer);
        }

        final JTableHeader header = this.getTableHeader();
        headerListener = new SortableTableHeaderListener(model, renderer);
        header.addMouseListener(headerListener);
        header.addMouseMotionListener(headerListener);
    }

    /**
     * Changes the model for the table. Takes care of updating the header listener
     * at the same time.
     */
    public void setSortableModel(final SortableTableModel model) {
        super.setModel(model);
        headerListener.setTableModel(model);
        final SortButtonRenderer renderer = new SortButtonRenderer();
        final TableColumnModel columnModel = this.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setHeaderRenderer(renderer);
        }
    }

}
