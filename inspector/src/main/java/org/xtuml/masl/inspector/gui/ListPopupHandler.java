//
// Filename : ListPopupHandler.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class ListPopupHandler extends MouseAdapter {

    private final JPopupMenu popup;

    public ListPopupHandler(final JPopupMenu popup) {
        this.popup = popup;
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            handlePopup(e);
        } else if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0 && e.getClickCount() > 1) {
            handleDoubleClick(e);
        }
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            handlePopup(e);
        }
        super.mouseReleased(e);
    }

    private void handlePopup(final MouseEvent e) {
        final JList list = (JList) e.getComponent();

        final int index = list.locationToIndex(new Point(e.getX(), e.getY()));
        if (index >= 0) {
            if (!list.isSelectedIndex(index)) {
                list.setSelectedIndex(index);
            }

            if (popup != null) {
                popup.show(list, e.getX(), e.getY());
            }
        }
    }

    private void handleDoubleClick(final MouseEvent e) {
        if (popup != null) {
            for (int i = 0; i < popup.getComponentCount(); i++) {
                try {
                    final JMenuItem item = (JMenuItem) popup.getComponent(i);
                    if (item.isEnabled() && item.isVisible()) {
                        item.doClick();
                        break;
                    }
                } catch (final ClassCastException ex) {/* ignore */
                }
            }
        }

    }
}
