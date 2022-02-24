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
 * -------------------------------- SortableTableHeaderListener.java
 * -------------------------------- (C) Copyright 2000, 2001 Nabuo Tamemasa and
 * Contributors;
 *
 * Original Author: Nabuo Tamemasa; Contributor(s): David Gilbert (for Simba
 * Management Limited);
 *
 * Changes (from 26-Oct-2001) -------------------------- 26-Oct-2001 : Changed
 * package to com.jrefinery.ui.*;
 */

package com.jrefinery.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.table.JTableHeader;

/**
 * Captures mouse clicks on a table header, with the intention of triggering a
 * sort. Adapted from code by Nabuo Tamemasa posted on http://www.codeguru.com.
 */
public class SortableTableHeaderListener implements MouseListener, MouseMotionListener {

    /** A reference to the table model. */
    private SortableTableModel model;

    /** The header renderer. */
    private final SortButtonRenderer renderer;

    /**
     * The index of the column that is sorted - used to determine the state of the
     * renderer.
     */
    private int sortColumnIndex;

    /**
     * Standard constructor.
     *
     * @param model    ;
     * @param renderer ;
     */
    public SortableTableHeaderListener(final SortableTableModel model, final SortButtonRenderer renderer) {
        this.model = model;
        this.renderer = renderer;
    }

    /**
     * Sets the table model for the listener.
     */
    public void setTableModel(final SortableTableModel model) {
        this.model = model;
    }

    /**
     * Handle a mouse press event - if the user is NOT resizing a column and NOT
     * dragging a column then give visual feedback that the column header has been
     * pressed.
     */
    @Override
    public void mousePressed(final MouseEvent e) {

        final JTableHeader header = (JTableHeader) e.getComponent();

        if (header.getResizingColumn() == null) { // resizing takes precedence over sorting
            if (header.getDraggedDistance() < 1) { // dragging also takes precedence over sorting
                final int columnIndex = header.columnAtPoint(e.getPoint());
                final int modelColumnIndex = header.getTable().convertColumnIndexToModel(columnIndex);
                if (model.isSortable(modelColumnIndex)) {
                    sortColumnIndex = header.getTable().convertColumnIndexToModel(columnIndex);
                    renderer.setPressedColumn(sortColumnIndex);
                    header.repaint();
                    if (header.getTable().isEditing()) {
                        header.getTable().getCellEditor().stopCellEditing();
                    }
                } else {
                    sortColumnIndex = -1;
                }
            }
        }

    }

    /**
     * If the user is dragging or resizing, then we clear the sort column.
     */
    @Override
    public void mouseDragged(final MouseEvent e) {

        final JTableHeader header = (JTableHeader) e.getComponent();

        if ((header.getDraggedDistance() > 0) || (header.getResizingColumn() != null)) {
            renderer.setPressedColumn(-1);
            sortColumnIndex = -1;
        }
    }

    /**
     * This event is ignored (not required).
     */
    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    /**
     * This event is ignored (not required).
     */

    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    /**
     * This event is ignored (not required).
     */
    @Override
    public void mouseMoved(final MouseEvent e) {
    }

    /**
     * This event is ignored (not required).
     */
    @Override
    public void mouseExited(final MouseEvent e) {
    }

    /**
     * When the user releases the mouse button, we attempt to sort the table.
     */
    @Override
    public void mouseReleased(final MouseEvent e) {

        final JTableHeader header = (JTableHeader) e.getComponent();

        if (header.getResizingColumn() == null) { // resizing the column takes precedence over sorting
            if (sortColumnIndex != -1) {
                final SortableTableModel model = (SortableTableModel) (header.getTable().getModel());
                final boolean ascending = !model.getAscending();
                model.setAscending(ascending);
                model.sortByColumn(sortColumnIndex, ascending);
                renderer.setPressedColumn(-1); // clear
                header.repaint();
            }
        }
    }

}
