//
// Filename : InstanceTable.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.xtuml.masl.inspector.processInterface.Capability;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.TimerData;

class EventQueueTable extends AutoSizingTable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final JPopupMenu popup = new JPopupMenu();
    private JMenuItem cancelTimer;

    public EventQueueTable(final EventQueueTableModel model) {
        super(model);
        setAutoResizeMode(AUTO_RESIZE_OFF);

        init();
    }

    private void init() {
        addMouseListener(new MouseHandler());

        final JMenuItem displayDetail = new JMenuItem("Display Detail");
        popup.add(displayDetail);
        displayDetail.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent action) {
                for (final int row : getSelectedRows()) {
                    (new EditDataDialog(((EventQueueTableModel) model).getEventAt(row), false)).display();
                }
            }
        });

        cancelTimer = new JMenuItem("Cancel Timer");

        if (Capability.CANCEL_TIMER.isAvailable()) {
            popup.add(cancelTimer);
            cancelTimer.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent action) {
                    for (final int row : getSelectedRows()) {
                        final Object event = ((EventQueueTableModel) model).getEventAt(row);
                        if (event instanceof TimerData) {
                            try {
                                if (JOptionPane.showConfirmDialog(EventQueueTable.this,
                                        "Are you sure you wish to cancel this timer?", "Cancel Timer",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                                    ProcessConnection.getConnection().cancelTimer((TimerData) event);
                                    ((EventQueueTableModel) model).update();
                                }
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }

    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseClicked(final MouseEvent e) {
            if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0 && e.getClickCount() > 1) {
                (new EditDataDialog(((EventQueueTableModel) model).getEventAt(getSelectedRows()[0]), false)).display();
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
                    cancelTimer.setVisible(getSelectedRows().length == 1
                            && ((EventQueueTableModel) model).getEventAt(getSelectedRow()) instanceof TimerData);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            super.mousePressed(e);
        }
    }

}
