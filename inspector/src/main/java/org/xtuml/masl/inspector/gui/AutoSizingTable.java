//
// Filename : AutoSizingTable.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.Dimension;

import javax.swing.table.TableColumn;

class AutoSizingTable extends ToolTipTable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    protected final AutoSizingTableModel model;
    private int minHeight = 100;

    public AutoSizingTable(final AutoSizingTableModel model) {
        super(model);
        this.model = model;
        init();
    }

    private void init() {
        minHeight = 100;
        setColumnWidths();
    }

    public void setColumnWidths() {
        int totalWidth = 0;
        for (int i = 0; i < getModel().getColumnCount(); i++) {
            final int width = model.getPreferredColumnWidth(i, this);
            totalWidth += width;
            final TableColumn col = getColumnModel().getColumn(i);
            col.setPreferredWidth(width);
            col.setWidth(width);
        }
        final Dimension d = getPreferredScrollableViewportSize();
        d.width = totalWidth;
        setPreferredScrollableViewportSize(d);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        final Dimension vs = super.getPreferredScrollableViewportSize();
        final Dimension ps = getPreferredSize();
        return new Dimension(Math.max(ps.width, vs.width), Math.max(Math.min(ps.height, vs.height), minHeight));
    }

    public void setMinHeight(final int height) {
        minHeight = height;
    }

}
