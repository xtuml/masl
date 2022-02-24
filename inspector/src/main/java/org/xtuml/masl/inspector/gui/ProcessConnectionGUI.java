// 
// Filename : ProcessConnectionGUI.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.Event;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import org.xtuml.masl.inspector.processInterface.Capability;
import org.xtuml.masl.inspector.processInterface.Plugin;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;

public class ProcessConnectionGUI {

    static private final ProcessConnection process = ProcessConnection.getConnection();

    static private JComponent invisibleOnCapability(final JComponent item, final Capability capability) {
        if (!capability.isAvailable()) {
            item.setVisible(false);
        }
        return item;
    }

    static private final Action traceLinesAction = new ToggleAction("Trace Lines") {

        @Override
        public void togglePerformed(final boolean selected) throws java.rmi.RemoteException {
            process.setTraceLines(selected);
        }
    };

    static private final Action traceBlocksAction = new ToggleAction("Trace Blocks") {

        @Override
        public void togglePerformed(final boolean selected) throws java.rmi.RemoteException {
            process.setTraceBlocks(selected);
        }
    };

    static private final Action traceExceptionsAction = new ToggleAction("Trace Exceptions") {

        @Override
        public void togglePerformed(final boolean selected) throws java.rmi.RemoteException {
            process.setTraceExceptions(selected);
        }
    };

    static private final Action traceEventsAction = new ToggleAction("Trace Events") {

        @Override
        public void togglePerformed(final boolean selected) throws java.rmi.RemoteException {
            process.setTraceEvents(selected);
        }
    };
    static private final Action stepLinesAction = new ToggleAction("Step Lines") {

        @Override
        public void togglePerformed(final boolean selected) throws java.rmi.RemoteException {
            process.setStepLines(selected);
        }
    };
    static private final Action stepBlocksAction = new ToggleAction("Step Blocks") {

        @Override
        public void togglePerformed(final boolean selected) throws java.rmi.RemoteException {
            process.setStepBlocks(selected);
        }
    };

    static private final Action stepExceptionsAction = new ToggleAction("Step Exceptions") {

        @Override
        public void togglePerformed(final boolean selected) throws java.rmi.RemoteException {
            process.setStepExceptions(selected);
        }
    };

    static private final Action stepEventsAction = new ToggleAction("Step Events") {

        @Override
        public void togglePerformed(final boolean selected) throws java.rmi.RemoteException {
            process.setStepEvents(selected);
        }
    };

    static private final Action catchConsoleAction = new ToggleAction("Catch Console") {

        @Override
        public void togglePerformed(final boolean selected) throws java.rmi.RemoteException {
            process.setCatchConsole(selected);
        }
    };

    static private final Action enableTimersAction = new ToggleAction("Enable Timers") {

        @Override
        public void togglePerformed(final boolean selected) throws java.rmi.RemoteException {
            process.setEnableTimers(selected);
        }
    };

    static public JMenuItem getTraceLinesMenuItem() {
        final JCheckBoxMenuItem item = new JCheckBoxMenuItem(traceLinesAction);
        invisibleOnCapability(item, Capability.TRACE_LINES);
        try {
            item.setSelected(process.getTraceLines());
        } catch (final java.rmi.RemoteException e) {
        }
        return item;
    }

    static public JMenuItem getTraceBlocksMenuItem() {
        final JCheckBoxMenuItem item = new JCheckBoxMenuItem(traceBlocksAction);
        invisibleOnCapability(item, Capability.TRACE_BLOCKS);
        try {
            item.setSelected(process.getTraceBlocks());
        } catch (final java.rmi.RemoteException e) {
        }
        return item;
    }

    static public JMenuItem getTraceExceptionsMenuItem() {
        final JCheckBoxMenuItem item = new JCheckBoxMenuItem(traceExceptionsAction);
        invisibleOnCapability(item, Capability.TRACE_EXCEPTIONS);
        try {
            item.setSelected(process.getTraceExceptions());
        } catch (final java.rmi.RemoteException e) {
        }
        return item;
    }

    static public JMenuItem getTraceEventsMenuItem() {
        final JCheckBoxMenuItem item = new JCheckBoxMenuItem(traceEventsAction);
        invisibleOnCapability(item, Capability.TRACE_EVENTS);
        try {
            item.setSelected(process.getTraceEvents());
        } catch (final java.rmi.RemoteException e) {
        }
        return item;
    }

    static public JMenuItem getStepLinesMenuItem() {
        final JCheckBoxMenuItem item = new JCheckBoxMenuItem(stepLinesAction);
        invisibleOnCapability(item, Capability.STEP_LINES);
        try {
            item.setSelected(process.getStepLines());
        } catch (final java.rmi.RemoteException e) {
        }
        return item;
    }

    static public JMenuItem getStepBlocksMenuItem() {
        final JCheckBoxMenuItem item = new JCheckBoxMenuItem(stepBlocksAction);
        invisibleOnCapability(item, Capability.STEP_BLOCKS);
        try {
            item.setSelected(process.getStepBlocks());
        } catch (final java.rmi.RemoteException e) {
        }
        return item;
    }

    static public JMenuItem getStepExceptionsMenuItem() {
        final JCheckBoxMenuItem item = new JCheckBoxMenuItem(stepExceptionsAction);
        invisibleOnCapability(item, Capability.STEP_EXCEPTIONS);
        try {
            item.setSelected(process.getStepExceptions());
        } catch (final java.rmi.RemoteException e) {
        }
        return item;
    }

    static public JMenuItem getStepEventsMenuItem() {
        final JCheckBoxMenuItem item = new JCheckBoxMenuItem(stepEventsAction);
        invisibleOnCapability(item, Capability.STEP_EVENTS);
        try {
            item.setSelected(process.getStepEvents());
        } catch (final java.rmi.RemoteException e) {
        }
        return item;
    }

    static public JMenuItem getCatchConsoleMenuItem() {
        final JCheckBoxMenuItem item = new JCheckBoxMenuItem(catchConsoleAction);
        invisibleOnCapability(item, Capability.CATCH_CONSOLE);
        try {
            item.setSelected(process.getCatchConsole());
        } catch (final java.rmi.RemoteException e) {
        }

        return item;
    }

    static public JMenuItem getEnableTimersMenuItem() {
        final JCheckBoxMenuItem item = new JCheckBoxMenuItem(enableTimersAction);
        invisibleOnCapability(item, Capability.ENABLE_TIMERS);
        try {
            item.setSelected(process.getEnableTimers());
        } catch (final java.rmi.RemoteException e) {
        }
        return item;
    }

    static private final Action runScheduleAction = new AbstractAction("Run Test Schedule...") {

        @Override
        public void actionPerformed(final ActionEvent e) {
            final java.io.File file = TestScheduleChooser.getFile();
            if (file != null) {
                try {
                    process.runTestSchedule(file);
                } catch (final java.rmi.RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        }

    };

    static public JMenuItem getRunScheduleMenuItem() {
        return (JMenuItem) invisibleOnCapability(new JMenuItem(runScheduleAction), Capability.RUN_TEST_SCHEDULE);
    }

    static private final Action showBreakpointsAction = new AbstractAction("Show Breakpoints") {

        @Override
        public void actionPerformed(final ActionEvent e) {
            BreakpointBrowser.display();
        }

    };

    static private final Action showStackAction = new AbstractAction("Show Stack") {

        @Override
        public void actionPerformed(final ActionEvent e) {
            StackBrowser.display();
        }

    };

    static public JMenuItem getShowBreakpointsMenuItem() {
        return (JMenuItem) invisibleOnCapability(new JMenuItem(showBreakpointsAction), Capability.SET_BREAKPOINT);
    }

    static public JMenuItem getShowStackMenuItem() {
        return (JMenuItem) invisibleOnCapability(new JMenuItem(showStackAction), Capability.GET_STACK);
    }

    static private final Action getPluginFlagAction(final String pluginName, final Plugin.Flag flag,
            final JMenuItem item) {
        return new ToggleAction(flag.getName()) {

            @Override
            public void togglePerformed(final boolean selected) throws java.rmi.RemoteException {
                process.setPluginFlag(pluginName, flag.getName(), selected);
                try {
                    if (flag.isReadable()) {
                        item.setSelected(process.getPluginFlag(pluginName, flag.getName()));
                    } else {
                        item.setSelected(false);
                    }
                } catch (final java.rmi.RemoteException e) {
                }

            }
        };
    }

    static public JMenuItem getPluginFlagMenuItem(final String pluginName, final Plugin.Flag flag) {
        final JCheckBoxMenuItem item = new JCheckBoxMenuItem();
        item.setAction(getPluginFlagAction(pluginName, flag, item));
        item.setEnabled(flag.isWriteable());
        try {
            if (flag.isReadable()) {
                item.setSelected(process.getPluginFlag(pluginName, flag.getName()));
            } else {
                item.setSelected(false);
            }
        } catch (final java.rmi.RemoteException e) {
        }
        return item;
    }

    static private final Action getPluginPropertyAction(final String pluginName, final Plugin.Property property,
            final JTextComponent text) {
        return new AbstractAction(property.getName()) {

            @Override
            public void actionPerformed(final ActionEvent event) {
                try {
                    process.setPluginProperty(pluginName, property.getName(), text.getText());
                    try {
                        if (property.isReadable()) {
                            text.setText(process.getPluginProperty(pluginName, property.getName()));
                        }
                    } catch (final java.rmi.RemoteException e) {
                    }
                } catch (final java.rmi.RemoteException e) {
                }
            }
        };
    }

    static public void addPluginPropertyMenuItem(final JMenu parent, final String pluginName,
            final Plugin.Property property) {
        final JTextComponent text = new JTextField();
        text.setEnabled(property.isWriteable());
        try {
            if (property.isReadable()) {
                text.setText(process.getPluginProperty(pluginName, property.getName()));
            }
        } catch (final java.rmi.RemoteException e) {
        }

        parent.addSeparator();
        final JMenuItem item = new JMenuItem(getPluginPropertyAction(pluginName, property, text));
        item.setEnabled(property.isWriteable());
        parent.add(item);
        parent.add(text);

    }

    static private final Action getPluginInvokeAction(final String pluginName, final String actionName) {
        return new AbstractAction(actionName) {

            @Override
            public void actionPerformed(final ActionEvent event) {
                try {
                    process.invokePluginAction(pluginName, actionName);
                } catch (final java.rmi.RemoteException e) {
                }
            }
        };
    }

    static public JMenuItem getPluginActionMenuItem(final String pluginName, final String actionName) {
        return new JMenuItem(getPluginInvokeAction(pluginName, actionName));
    }

    static private final Action continueAction = new AbstractAction(null,
            new ImageIcon(ClassLoader.getSystemResource("icons/play.gif"))) {

        @Override
        public void actionPerformed(final java.awt.event.ActionEvent e) {
            try {
                process.continueExecution();
            } catch (final java.rmi.RemoteException ex) {
                ex.printStackTrace();
            }
        }
    };

    static private final Action pauseAction = new AbstractAction(null,
            new ImageIcon(ClassLoader.getSystemResource("icons/pause.gif"))) {

        @Override
        public void actionPerformed(final java.awt.event.ActionEvent e) {
            try {
                process.pauseExecution();
            } catch (final java.rmi.RemoteException ex) {
                ex.printStackTrace();
            }
        }
    };

    static private final Action stepAction = new AbstractAction(null,
            new ImageIcon(ClassLoader.getSystemResource("icons/step.gif"))) {

        @Override
        public void actionPerformed(final java.awt.event.ActionEvent e) {
            try {
                process.stepExecution();
            } catch (final java.rmi.RemoteException ex) {
                ex.printStackTrace();
            }
        }
    };

    static private final Action slomoAction = new AbstractAction(null,
            new ImageIcon(ClassLoader.getSystemResource("icons/slomo.gif"))) {

        @Override
        public void actionPerformed(final java.awt.event.ActionEvent e) {
            try {
                process.slomoExecution();
            } catch (final java.rmi.RemoteException ex) {
                ex.printStackTrace();
            }
        }
    };

    static public JComponent getContinueComponent() {
        final JButton button = new JButton(continueAction);
        button.setMargin(new java.awt.Insets(1, 1, 1, 1));
        button.setRequestFocusEnabled(false);

        button.registerKeyboardAction(continueAction,
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, Event.CTRL_MASK),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        return invisibleOnCapability(button, Capability.CONTINUE);
    }

    static public JComponent getPauseComponent() {
        final JButton button = new JButton(pauseAction);
        button.setMargin(new java.awt.Insets(1, 1, 1, 1));
        button.setRequestFocusEnabled(false);

        button.registerKeyboardAction(pauseAction,
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, Event.CTRL_MASK),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        return invisibleOnCapability(button, Capability.PAUSE);
    }

    static public JComponent getStepComponent() {
        final JButton button = new JButton(stepAction);
        button.setMargin(new java.awt.Insets(1, 1, 1, 1));
        button.setRequestFocusEnabled(false);

        button.registerKeyboardAction(stepAction, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, Event.CTRL_MASK),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        return invisibleOnCapability(button, Capability.STEP);
    }

    static public JComponent getSloMoComponent() {
        final JButton button = new JButton(slomoAction);
        button.setMargin(new java.awt.Insets(1, 1, 1, 1));
        button.setRequestFocusEnabled(false);

        button.registerKeyboardAction(slomoAction,
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, Event.CTRL_MASK),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        return invisibleOnCapability(button, Capability.SLOMO);
    }

}
