// 
// Filename : BreakpointTable.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.xtuml.masl.inspector.BreakpointController;

class BreakpointTable extends ToolTipTable {

    private final JPopupMenu popup = new JPopupMenu();

    private final BreakpointTableModel model;

    public BreakpointTable() {
        super(new BreakpointTableModel());
        this.model = (BreakpointTableModel) getModel();
        init();
    }

    private void init() {
        addMouseListener(new MouseHandler());

        getColumnModel().getColumn(BreakpointTableModel.LINE_COL).setCellRenderer(new DefaultTableCellRenderer() {

            {
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
        });

        popup.add(new AbstractAction("Set Breakpoint") {

            @Override
            public void actionPerformed(final ActionEvent action) {
                final int[] rows = getSelectedRows();
                for (final int row : rows) {
                    BreakpointController.getInstance().setBreakpoint(model.getPosition(row));
                }
            }
        });

        popup.add(new AbstractAction("Clear Breakpoint") {

            @Override
            public void actionPerformed(final ActionEvent action) {
                final int[] rows = getSelectedRows();
                for (final int row : rows) {
                    BreakpointController.getInstance().clearBreakpoint(model.getPosition(row));
                }
            }
        });

        popup.add(new AbstractAction("Remove Breakpoint") {

            @Override
            public void actionPerformed(final ActionEvent action) {
                final int[] rows = getSelectedRows();
                for (int i = rows.length - 1; i >= 0; i--) {
                    BreakpointController.getInstance().removeBreakpoint(model.getPosition(rows[i]));
                }
            }
        });

        setColumnWidths();
    }

    public void setColumnWidths() {
        int totalWidth = 0;
        for (int i = 0; i < getModel().getColumnCount(); i++) {
            final int width = model.getPreferredColumnWidth(i, this);
            totalWidth += width;
            final TableColumn col = getColumnModel().getColumn(i);
            if (i != BreakpointTableModel.NAME_COL) {
                col.setMaxWidth(width);
                col.setMinWidth(width);
            }
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
        return new Dimension(Math.max(ps.width, vs.width), Math.max(Math.min(ps.height, vs.height), 100));
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseClicked(final MouseEvent e) {
            if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0 && e.getClickCount() > 1) {
                BreakpointController.getInstance().toggleBreakpoint(model.getPosition(getSelectedRows()[0]));
            }
            super.mouseClicked(e);
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            if (e.isPopupTrigger()) {
                final int row = rowAtPoint(new Point(e.getX(), e.getY()));
                if (!isRowSelected(row)) {
                    setRowSelectionInterval(row, row);
                }

                if (getSelectedRow() >= 0) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            super.mousePressed(e);
        }
    }

}
