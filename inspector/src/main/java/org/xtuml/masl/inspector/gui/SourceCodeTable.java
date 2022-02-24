// 
// Filename : SourceCodeTable.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.xtuml.masl.inspector.BreakpointController;
import org.xtuml.masl.inspector.Preferences;
import org.xtuml.masl.inspector.processInterface.Capability;
import org.xtuml.masl.inspector.processInterface.SourcePosition;

class SourceCodeTable extends ToolTipTable {

    private final JPopupMenu popup = new JPopupMenu();

    protected final SourceCodeTableModel model;

    public SourceCodeTable(final SourceCodeTableModel model) {
        super(model);
        this.model = model;
        init();

    }

    private void init() {
        setIntercellSpacing(new Dimension(0, 0));
        setShowHorizontalLines(false);
        setShowVerticalLines(false);
        setTableHeader(null);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        addMouseListener(new MouseHandler());

        getColumnModel().getColumn(SourceCodeTableModel.INDICATOR_COL).setCellRenderer(new NoSelectTableCellRenderer() {

            {
                setForeground(java.awt.Color.red);
                setBackground(java.awt.Color.lightGray);
            }
        });

        getColumnModel().getColumn(SourceCodeTableModel.LINE_NO_COL).setCellRenderer(new NoSelectTableCellRenderer() {

            {
                setHorizontalAlignment(SwingConstants.RIGHT);
                setForeground(java.awt.Color.black);
                setBackground(java.awt.Color.lightGray);
            }
        });

        getColumnModel().getColumn(SourceCodeTableModel.CODE_COL).setCellRenderer(new DefaultTableCellRenderer() {

            @Override
            public void setFont(final Font font) {
                super.setFont(new Font(Preferences.getCodeFontName(), font.getStyle(), font.getSize()));
            }
        });

        if (Capability.SET_BREAKPOINT.isAvailable()) {
            popup.add(new AbstractAction("Set Breakpoint") {

                @Override
                public void actionPerformed(final ActionEvent action) {
                    final int[] rows = getSelectedRows();
                    for (final int row : rows) {
                        int line = row + 1;
                        if (line == model.getRowCount()) {
                            line = SourcePosition.LAST_LINE;
                        }
                        BreakpointController.getInstance().setBreakpoint(model.getSource().getSourcePosition(line));
                    }
                }
            });

            popup.add(new AbstractAction("Clear Breakpoint") {

                @Override
                public void actionPerformed(final ActionEvent action) {
                    final int[] rows = getSelectedRows();
                    for (final int row : rows) {
                        int line = row + 1;
                        if (line == model.getRowCount()) {
                            line = SourcePosition.LAST_LINE;
                        }
                        BreakpointController.getInstance().clearBreakpoint(model.getSource().getSourcePosition(line));
                    }
                }
            });

            popup.add(new AbstractAction("Remove Breakpoint") {

                @Override
                public void actionPerformed(final ActionEvent action) {
                    final int[] rows = getSelectedRows();
                    for (final int row : rows) {
                        int line = row + 1;
                        if (line == model.getRowCount()) {
                            line = SourcePosition.LAST_LINE;
                        }
                        BreakpointController.getInstance().removeBreakpoint(model.getSource().getSourcePosition(line));
                    }
                }
            });
        }
        if (model.getCurrentLine() != SourcePosition.NO_LINE) {
            scrollToCurrentLine();
        }

        model.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(final TableModelEvent e) {
                if (e.getColumn() == TableModelEvent.ALL_COLUMNS) {
                    setColumnWidths();
                }
            }
        });

        final Action toggleAction = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent action) {
                final int[] rows = getSelectedRows();
                for (final int row : rows) {
                    int line = row + 1;
                    if (line == model.getRowCount()) {
                        line = SourcePosition.LAST_LINE;
                    }
                    BreakpointController.getInstance().toggleBreakpoint(model.getSource().getSourcePosition(line));
                }
            }
        };
        registerKeyboardAction(toggleAction, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, Event.CTRL_MASK),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

    }

    @Override
    public void setFont(final Font font) {
        super.setFont(font);
        setRowHeight(getFontMetrics(font).getAscent() + getFontMetrics(font).getDescent() + getRowMargin());
    }

    public void setColumnWidths() {
        int totalWidth = 0;
        for (int i = 0; i < getModel().getColumnCount(); i++) {
            final int width = model.getPreferredColumnWidth(i, this);
            totalWidth += width;
            getColumnModel().getColumn(i).setMaxWidth(width);
            getColumnModel().getColumn(i).setPreferredWidth(width);
            getColumnModel().getColumn(i).setWidth(width);
        }
        final Dimension d = getPreferredScrollableViewportSize();
        d.width = totalWidth;
        setPreferredScrollableViewportSize(d);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        final Dimension vs = super.getPreferredScrollableViewportSize();
        final Dimension ps = getPreferredSize();
        return new Dimension(Math.max(ps.width, vs.width), Math.max(Math.min(ps.height, vs.height), 50));
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseClicked(final MouseEvent e) {
            if (Capability.SET_BREAKPOINT.isAvailable() && (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0
                    && e.getClickCount() > 1) {
                int line = getSelectedRows()[0] + 1;
                if (line == model.getRowCount()) {
                    line = SourcePosition.LAST_LINE;
                }
                BreakpointController.getInstance().toggleBreakpoint(model.getSource().getSourcePosition(line));
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

    public void scrollToCurrentLine() {
        if (model.getCurrentLine() != SourcePosition.NO_LINE) {
            scrollToLine(model.getCurrentLine());
        }
    }

    public void scrollToLine(final int line) {
        final Rectangle currentRow = getCellRect(line - 1, 0, true);
        final Rectangle aboveRow = getCellRect(Math.max(0, line - 1 - Preferences.getAboveContextLines()), 0, true);
        final Rectangle belowRow = getCellRect(
                Math.min(model.getRowCount() - 1, line - 1 + Preferences.getBelowContextLines()), 0, true);

        // Now scroll to make sure the context is visible.
        scrollRectToVisible(aboveRow.union(belowRow));

        // Now scroll to make sure the current line is really
        // visible, just in case the window is too small to
        // display the whole context.
        scrollRectToVisible(currentRow);
    }

}
