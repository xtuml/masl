// 
// Filename : StackBrowser.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import org.xtuml.masl.inspector.Preferences;
import org.xtuml.masl.inspector.processInterface.LocalVarData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.ProcessStatusListener;
import org.xtuml.masl.inspector.processInterface.StackFrame;
import org.xtuml.masl.inspector.processInterface.WeakProcessStatusListener;

public class StackBrowser extends InspectorSubFrame implements ProcessStatusListener {

    private static int s_defaultFontSize = Preferences.getCodeFontSize();
    private static boolean s_sortLocalVars = Preferences.getSortLocalVars();

    static StackBrowser instance = null;

    public static void display() {
        if (instance == null) {
            instance = new StackBrowser();
        }
        instance.setState(java.awt.Frame.NORMAL);
        instance.toFront();
    }

    static void closeBrowser() {
        instance = null;
    }

    private final StackList stackList;
    private final StackSource stackSource;
    private final SourceCodeTableModel sourceModel;
    private final LocalVarTree varTree;
    private final LocalVarTreeModel varModel;
    private final EventQueueTableModel eventQueueModel;
    private final EventQueueTable eventTable;

    private final JSplitPane localVarSplit;
    private final JSplitPane eventQueueSplit;

    boolean displayLocalVars = true;

    StackBrowser() {
        super("Stack");

        stackList = new StackList();
        sourceModel = new SourceCodeTableModel();
        stackSource = new StackSource(stackList, sourceModel);
        varModel = new LocalVarTreeModel();
        varTree = new LocalVarTree(varModel);
        eventQueueModel = new EventQueueTableModel();
        eventTable = new EventQueueTable(eventQueueModel);

        stackList.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(final ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    updateLocalVars();
                    endLocalVarUpdate();
                } else if (event.getStateChange() == ItemEvent.DESELECTED) {
                    startLocalVarUpdate();
                }
            }
        });

        localVarSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        localVarSplit.setContinuousLayout(true);
        localVarSplit.setResizeWeight(0);
        localVarSplit.setTopComponent(new JScrollPane(stackList));
        localVarSplit.setBottomComponent(new JScrollPane(varTree));
        localVarSplit.setBorder(BorderFactory.createEmptyBorder());

        final JSplitPane codeStackSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        codeStackSplit.setContinuousLayout(true);
        codeStackSplit.setLeftComponent(localVarSplit);
        codeStackSplit.setRightComponent(new JScrollPane(stackSource));
        codeStackSplit.setResizeWeight(0);
        codeStackSplit.setBorder(BorderFactory.createEmptyBorder());

        eventQueueSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        eventQueueSplit.setContinuousLayout(true);
        eventQueueSplit.setResizeWeight(1);
        eventQueueSplit.setTopComponent(codeStackSplit);
        eventQueueSplit.setBottomComponent(new JScrollPane(eventTable));
        eventQueueSplit.setBorder(BorderFactory.createEmptyBorder());

        getContentPane().add(eventQueueSplit);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        final JToolBar toolbar = new JToolBar();
        final JPanel toolbarPanel = new JPanel(new ToolbarLayout());
        toolbar.add(toolbarPanel);

        toolbarPanel.add(ProcessConnectionGUI.getPauseComponent());
        toolbarPanel.add(ProcessConnectionGUI.getStepComponent());
        toolbarPanel.add(ProcessConnectionGUI.getSloMoComponent());
        toolbarPanel.add(ProcessConnectionGUI.getContinueComponent());
        getContentPane().add(toolbar, BorderLayout.NORTH);

        setFont(s_defaultFontSize);

        addMenus();
        pack();
        setSize(getPreferredSize());

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(final WindowEvent e) {
                closeBrowser();
            }
        });

        ProcessConnection.getConnection().addProcessStatusListener(new WeakProcessStatusListener(this));

        localVarSplit.setDividerLocation(localVarSplit.getUI().getDividerLocation(localVarSplit));
        codeStackSplit.setDividerLocation(codeStackSplit.getUI().getDividerLocation(codeStackSplit));
        eventQueueSplit.setDividerLocation(eventQueueSplit.getUI().getDividerLocation(eventQueueSplit));
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

        final JMenu viewMenu = new JMenu("View");
        mainMenu.add(viewMenu);

        final JMenuItem localVars = new JCheckBoxMenuItem(new DisplayLocalVarsAction("Display Local Variables"));
        localVars.setSelected(displayLocalVars);
        viewMenu.add(localVars);

        final JMenuItem sortLocalVars = new JCheckBoxMenuItem(new SortLocalVarsAction("Sort Local Variables"));
        sortLocalVars.setSelected(s_sortLocalVars);
        viewMenu.add(sortLocalVars);

        final JMenuItem eventQueue = new JCheckBoxMenuItem(new DisplayEventQueueAction("Display Event Queue"));
        eventQueue.setSelected(true);
        viewMenu.add(eventQueue);

        final JMenuItem refreshEventQueue = new JMenuItem(new RefreshEventQueueAction("Refresh Event Queue"));
        refreshEventQueue.setSelected(true);
        viewMenu.add(refreshEventQueue);

        final JMenuItem fontMenu = new JMenu("Font");
        viewMenu.add(fontMenu);

        final ButtonGroup group = new ButtonGroup();
        final FontChangeListener listener = new FontChangeListener();
        for (int f = 6; f < 15; f++) {
            final JRadioButtonMenuItem fontButton = new JRadioButtonMenuItem(f + "pt");

            group.add(fontButton);
            fontMenu.add(fontButton);
            fontButton.setActionCommand("" + f);
            fontButton.addActionListener(listener);
            if (f == s_defaultFontSize) {
                fontButton.setSelected(true);
            }
        }

    }

    @Override
    public void processStatusChanged(final org.xtuml.masl.inspector.processInterface.ProcessStatusEvent e) {
        if (e.getStatus() == ProcessConnection.RUNNING) {
            sourceModel.setStaleCurrentLine(true);
        }
    }

    public void setFont(final int size) {
        final Font font = new Font(Preferences.getFontName(), Font.PLAIN, size);
        stackSource.setFont(font);
        stackSource.setColumnWidths();
        setSize(getPreferredSize());
        pack();
        s_defaultFontSize = size;
    }

    private void startLocalVarUpdate() {
        varTree.updateStarted();
        varModel.setLocalVars(null);
    }

    private void updateLocalVars() {
        final Object[] selectedObjects = stackList.getSelectedObjects();
        if (selectedObjects != null && selectedObjects.length > 0 && selectedObjects[0] != null) {
            final StackFrame selected = (StackFrame) selectedObjects[0];

            if (selected != null) {
                if (displayLocalVars) {
                    LocalVarData[] vars = selected.getLocalVars();
                    if (s_sortLocalVars) {
                        // Need to make copy of array, as StackList caches the list and
                        // sorting it would sort the original
                        final LocalVarData[] sorted = new LocalVarData[vars.length];
                        System.arraycopy(vars, 0, sorted, 0, vars.length);
                        Arrays.sort(sorted, new Comparator<LocalVarData>() {

                            @Override
                            public int compare(final LocalVarData o1, final LocalVarData o2) {
                                return o1.getName().compareTo(o2.getName());
                            }
                        });
                        vars = sorted;
                    }

                    varModel.setLocalVars(vars);
                } else {
                    varModel.setLocalVars(null);
                }
            }
        }
    }

    private void endLocalVarUpdate() {
        varTree.updateComplete();
    }

    private class FontChangeListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {
            setFont((new Integer(e.getActionCommand())).intValue());
        }
    }

    private class DisplayLocalVarsAction extends ToggleAction {

        public DisplayLocalVarsAction(final String name) {
            super(name);
        }

        @Override
        public void togglePerformed(final boolean selected) throws java.rmi.RemoteException {
            displayLocalVars = selected;
            if (selected) {
                localVarSplit.setDividerLocation(localVarSplit.getLastDividerLocation());
            } else {
                localVarSplit.setDividerLocation(1.0);
            }
        }
    }

    private class SortLocalVarsAction extends ToggleAction {

        public SortLocalVarsAction(final String name) {
            super(name);
        }

        @Override
        public void togglePerformed(final boolean selected) throws java.rmi.RemoteException {
            s_sortLocalVars = selected;
            startLocalVarUpdate();
            updateLocalVars();
            endLocalVarUpdate();
        }
    }

    private class DisplayEventQueueAction extends ToggleAction {

        public DisplayEventQueueAction(final String name) {
            super(name);
        }

        @Override
        public void togglePerformed(final boolean selected) throws java.rmi.RemoteException {
            eventQueueModel.setAutoUpdate(selected);
            if (selected) {
                eventQueueModel.update();
                eventQueueSplit.setDividerLocation(eventQueueSplit.getLastDividerLocation());
            } else {
                eventQueueModel.clear();
                eventQueueSplit.setDividerLocation(1.0);
            }
        }
    }

    private class RefreshEventQueueAction extends javax.swing.AbstractAction {

        public RefreshEventQueueAction(final String name) {
            super(name);
        }

        @Override
        public void actionPerformed(final java.awt.event.ActionEvent e) {
            eventQueueModel.update();
        }

    }

}
