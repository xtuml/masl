//
// Filename : WindowMenu.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class WindowMenu extends JMenu {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static WindowMenu instance = new WindowMenu();

    public static WindowMenu getInstance() {
        return instance;
    }

    private final int xOffset = 25;
    private final int yOffset = 25;

    private JFrame parentFrame;
    private java.awt.Point nextLocation = null;

    public WindowMenu() {
        super("Window");

        super.add(new JMenuItem(new AbstractAction("Arrange") {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                arrangeWindows();
            }
        }));

        super.addSeparator();
    }

    private void resetLocation() {
        nextLocation = null;
    }

    private Point getNextLocation(final Dimension size) {
        if (parentFrame == null) {
            Component comp = this;
            while (parentFrame == null) {
                comp = comp.getParent();
                if (comp instanceof JFrame) {
                    parentFrame = (JFrame) comp;
                }
            }
        }
        if (nextLocation == null) {
            nextLocation = new java.awt.Point(parentFrame.getX() + xOffset * 2, parentFrame.getY() + yOffset * 2);
        } else {
            nextLocation.x += xOffset;
            nextLocation.y += yOffset;
        }
        final Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        // getMaximumWindowBounds is supposed to return total screen size, across
        // all screens, but it doesn't seem to, so mutiply width by number of
        // screens.
        final GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        nextLocation.x = nextLocation.x % (screen.width * screens.length);
        nextLocation.y = nextLocation.y % screen.height;

        if (nextLocation.x + size.getWidth() > screen.width * screens.length) {
            nextLocation.x = 0;
        }
        if (nextLocation.y + size.getHeight() > screen.height) {
            nextLocation.y = 0;
        }

        return nextLocation;
    }

    public void arrangeWindows() {
        resetLocation();
        for (int i = 2; i < getItemCount(); i++) {
            final JFrame window = (JFrame) getItem(i).getClientProperty("window");
            if (window.getState() == java.awt.Frame.NORMAL) {
                window.setLocation(getNextLocation(window.getSize()));
                window.setVisible(true);
            }
        }
    }

    public void addWindow(final JFrame window) {
        window.setLocation(getNextLocation(window.getSize()));

        final JMenuItem item = new JMenuItem(new AbstractAction(window.getTitle()) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                window.setState(java.awt.Frame.NORMAL);
                window.toFront();
            }
        });

        item.putClientProperty("window", window);

        add(item);

        window.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(final WindowEvent e) {
                remove(item);
            }
        });

        window.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            @Override
            public void propertyChange(final java.beans.PropertyChangeEvent event) {
                if (event.getPropertyName().equals("title")) {
                    item.setText((String) event.getNewValue());
                }
            }
        });
    }
}
