//
// Filename : ToolTipTable.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class ToolTipTable extends JTable {

    private static class ToolTipHeader extends JTableHeader {

        public ToolTipHeader(final TableColumnModel model) {
            super(model);
        }

        @Override
        public String getToolTipText(final MouseEvent e) {
            final Point p = new Point(e.getX(), e.getY());
            final int col = getTable().columnAtPoint(p);

            if (col == -1) {
                // Mouse not over a column header
                return null;
            }

            final String fullname = getTable().getColumnName(col);

            final int actualWidth = getHeaderRect(col).width;

            TableCellRenderer renderer = getTable().getColumnModel().getColumn(col).getHeaderRenderer();
            if (renderer == null) {
                renderer = getDefaultRenderer();
            }

            final Component cell = renderer.getTableCellRendererComponent(getTable(), fullname, false, false, -1, col);
            final int preferedWidth = cell.getPreferredSize().width;

            return (actualWidth < preferedWidth) ? fullname : null;

        }

        @Override
        public Point getToolTipLocation(final MouseEvent e) {
            if (getToolTipText(e) != null) {
                final Point p = new Point(e.getX(), e.getY());
                final int col = getTable().columnAtPoint(p);

                if (col == -1) {
                    // Mouse not over a column header
                    return null;
                }

                final Rectangle rect = getHeaderRect(col);
                return new Point(rect.x, rect.y);
            } else {
                return null;
            }
        }

    }

    public ToolTipTable(final TableModel model) {
        super(model);
        setTableHeader(new ToolTipHeader(getTableHeader().getColumnModel()));
    }

    @Override
    public String getToolTipText(final MouseEvent e) {
        final Point p = new Point(e.getX(), e.getY());
        final int row = rowAtPoint(p);
        final int col = columnAtPoint(p);
        final Object value = getValueAt(row, col);

        final int actualWidth = getCellRect(row, col, false).width;

        final Component cell = getCellRenderer(row, col).getTableCellRendererComponent(this, value, false, false, row,
                col);
        final int preferedWidth = cell.getPreferredSize().width;

        return (actualWidth < preferedWidth) ? value.toString() : null;
    }

    @Override
    public Point getToolTipLocation(final MouseEvent e) {
        if (getToolTipText(e) != null) {
            final Point p = new Point(e.getX(), e.getY());
            final int row = rowAtPoint(p);
            final int col = columnAtPoint(p);

            final Rectangle rect = getCellRect(row, col, false);
            return new Point(rect.x, rect.y);
        } else {
            return null;
        }
    }

}
