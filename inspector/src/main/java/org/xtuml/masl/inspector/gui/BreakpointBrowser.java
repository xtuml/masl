// 
// Filename : BreakpointBrowser.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.xtuml.masl.inspector.BreakpointController;

public class BreakpointBrowser extends InspectorSubFrame {

    static BreakpointBrowser instance = null;

    public static void display() {
        if (instance == null) {
            instance = new BreakpointBrowser();
        }
        instance.setState(java.awt.Frame.NORMAL);
        instance.toFront();
    }

    static void closeBrowser() {
        instance = null;
    }

    BreakpointTable table;

    BreakpointBrowser() {
        super("Breakpoints");

        table = new BreakpointTable();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        getContentPane().add(new JScrollPane(table));
        addMenus();
        pack();
        setSize(getPreferredSize());

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(final WindowEvent e) {
                closeBrowser();
            }
        });
        setVisible(true);
        addToWindowMenu();
    }

    private void addMenus() {
        final JMenuBar mainMenu = new JMenuBar();
        setJMenuBar(mainMenu);

        final JMenu fileMenu = new JMenu("File");
        mainMenu.add(fileMenu);

        final JMenuItem exitItem = new JMenuItem(new FrameCloseAction(this));
        fileMenu.add(exitItem);

        final JMenu breakpointMenu = new JMenu("Breakpoints");
        mainMenu.add(breakpointMenu);

        breakpointMenu.add(new AbstractAction("Set All") {

            @Override
            public void actionPerformed(final ActionEvent action) {
                BreakpointController.getInstance().setAllBreakpoints();
            }
        });

        breakpointMenu.add(new AbstractAction("Clear All") {

            @Override
            public void actionPerformed(final ActionEvent action) {
                BreakpointController.getInstance().clearAllBreakpoints();
            }
        });

        breakpointMenu.add(new AbstractAction("Remove All") {

            @Override
            public void actionPerformed(final ActionEvent action) {
                BreakpointController.getInstance().removeAllBreakpoints();
            }
        });
    }

}
