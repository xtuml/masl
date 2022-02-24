//
// Filename : InstanceViewer.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.xtuml.masl.inspector.Preferences;
import org.xtuml.masl.inspector.processInterface.Capability;
import org.xtuml.masl.inspector.processInterface.EventMetaData;
import org.xtuml.masl.inspector.processInterface.EventMetaData.EventType;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.ObjectServiceMetaData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;

class InstanceViewer extends InspectorSubFrame implements TableModelListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JMenuBar mainMenu;
    private static int s_defaultFontSize = Preferences.getFontSize();
    private static boolean s_showInternalId = Preferences.getShowInternalId();

    private InstanceTable table;
    private final InstanceViewModel model;

    private final ObjectMetaData meta;

    private JPanel statusBar;
    private JProgressBar progressBar;
    private JLabel countLabel;
    private JLabel assignerLabel;

    private String key;

    static private Map<String, InstanceViewer> instanceViewers = new HashMap<String, InstanceViewer>();

    static public void display(final ObjectMetaData meta) {
        display(meta, null);
    }

    static public void display(final ObjectMetaData meta, final Object[] pks) {
        String key = meta.getDomain() + "::" + meta.getName();
        if (pks != null) {
            for (final Object pk : pks) {
                key += "." + pk;
            }
        }

        InstanceViewer viewer = instanceViewers.get(key);
        if (viewer == null) {
            viewer = new InstanceViewer(meta, pks);
            viewer.key = key;
            instanceViewers.put(key, viewer);
        } else {
            viewer.refresh();
        }
        viewer.setVisible(true);
        viewer = null;
    }

    @Override
    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        final Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        size.width = Math.min(size.width, screen.width);
        return size;
    }

    static public void display(final ObjectMetaData sourceMeta, final Object pk, final int relId) {
        final String key = sourceMeta.getDomain() + "::" + sourceMeta.getName() + "::" + pk + "::" + relId;

        InstanceViewer viewer = instanceViewers.get(key);
        if (viewer == null) {
            viewer = new InstanceViewer(sourceMeta, pk, relId);
            viewer.key = key;
            instanceViewers.put(key, viewer);
        } else {
            viewer.refresh();
        }
        viewer.setVisible(true);
    }

    static public void closeViewer(final InstanceViewer viewer) {
        instanceViewers.remove(viewer.key);
    }

    private InstanceViewer(final ObjectMetaData meta, final Object[] pks) {
        super();

        model = new InstanceViewModel(meta, pks);
        this.meta = model.getMetaObject();

        initGUI();
    }

    private InstanceViewer(final ObjectMetaData sourceMeta, final Object pk, final int relId) {
        super();

        model = new InstanceViewModel(sourceMeta, pk, relId);
        this.meta = model.getMetaObject();

        initGUI();
    }

    void initGUI() {
        model.addTableModelListener(this);

        table = new InstanceTable(model);
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

        addStatusBar();

        setFont(s_defaultFontSize);
        table.setColumnWidths();
        setTitle(model.getTitle());
        addMenus();
        pack();
        setSize(getPreferredSize());
        setVisible(true);
        addToWindowMenu();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(final WindowEvent e) {
                model.cancelLoad();
                closeViewer(InstanceViewer.this);
            }
        });

    }

    private void addMenus() {
        mainMenu = new JMenuBar();
        setJMenuBar(mainMenu);

        final JMenu fileMenu = new JMenu("File");
        mainMenu.add(fileMenu);

        final JMenuItem saveItem = new JMenuItem("Save Table...");
        final JMenuItem saveRowsItem = new JMenuItem("Save Rows...");
        final JMenuItem exitItem = new JMenuItem(new FrameCloseAction(this));
        saveItem.addActionListener(new SaveListener());
        saveRowsItem.addActionListener(new SaveRowsListener());
        fileMenu.add(saveItem);
        fileMenu.add(saveRowsItem);
        fileMenu.add(exitItem);

        final JMenu objectMenu = new JMenu("Object");
        mainMenu.add(objectMenu);

        final JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.addActionListener(new RefreshListener());
        refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
        objectMenu.add(refreshItem);

        final JMenuItem cancelItem = new JMenuItem("Cancel Load");
        cancelItem.addActionListener(new CancelLoadListener());
        cancelItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
        objectMenu.add(cancelItem);

        if (Capability.RUN_OBJECT_SERVICE.isAvailable()) {
            objectMenu.addSeparator();
            final ObjectServiceMetaData[] objectServices = meta.getObjectServices();
            Arrays.sort(objectServices);
            for (final ObjectServiceMetaData objectService : objectServices) {
                final JMenuItem item = new JMenuItem("Run " + objectService);
                objectMenu.add(item);

                item.addActionListener(new ObjectServiceInvoker(objectService));
            }
        }

        if (Capability.FIRE_EVENT.isAvailable()) {
            objectMenu.addSeparator();
            final EventMetaData[] events = meta.getInstanceEvents();
            Arrays.sort(events);
            for (final EventMetaData event : events) {
                if (event.getType() == EventType.Creation) {
                    final JMenuItem item = new JMenuItem("Fire " + event);
                    objectMenu.add(item);

                    item.addActionListener(new EventInvoker(event));
                }
            }
        }

        if (Capability.FIRE_EVENT.isAvailable() && meta.getAssignerStates().length > 0) {
            final JMenu assignerMenu = new JMenu("Assigner");
            mainMenu.add(assignerMenu);

            final EventMetaData[] assignerEvents = meta.getAssignerEvents();
            Arrays.sort(assignerEvents);
            for (final EventMetaData assignerEvent : assignerEvents) {
                final JMenuItem item = new JMenuItem("Fire " + assignerEvent);
                assignerMenu.add(item);

                item.addActionListener(new EventInvoker(assignerEvent));
            }
        }

        final JMenu viewMenu = new JMenu("View");
        mainMenu.add(viewMenu);

        final JCheckBoxMenuItem showId = new JCheckBoxMenuItem(new ShowInternalIdAction("Show Internal Id"));
        showId.setSelected(s_showInternalId);
        table.showInternalId(s_showInternalId);
        viewMenu.add(showId);

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

    public void setFont(final int size) {
        final Font font = new Font(Preferences.getFontName(), Font.PLAIN, size);
        table.setFont(font);
        s_defaultFontSize = size;
    }

    public void refresh() {
        model.refreshAll();
        if (assignerLabel != null) {
            try {
                final int as = ProcessConnection.getConnection().getAssignerState(meta);
                if (as < 0) {
                    assignerLabel.setText("Assigner State: " + meta.getAssignerStates()[as].getName());
                } else {
                    assignerLabel.setText("Assigner State:");
                }
            } catch (final java.rmi.RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addStatusBar() {
        statusBar = new JPanel();
        final GridBagLayout layout = new GridBagLayout();
        statusBar.setLayout(layout);

        final GridBagConstraints constraints = new GridBagConstraints();

        countLabel = new JLabel();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0;
        constraints.gridwidth = 1;
        layout.setConstraints(countLabel, constraints);
        countLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusBar.add(countLabel);

        if (meta.getAssignerStates().length > 0) {
            try {
                assignerLabel = new JLabel();
                final int as = ProcessConnection.getConnection().getAssignerState(meta);
                assignerLabel.setText("Assigner State: " + meta.getAssignerStates()[as].getName());
                constraints.weightx = 1;
                layout.setConstraints(assignerLabel, constraints);
                assignerLabel.setBorder(BorderFactory.createLoweredBevelBorder());
                statusBar.add(assignerLabel);
            } catch (final java.rmi.RemoteException ex) {
                ex.printStackTrace();
            }
        }

        final JLabel padding = new JLabel(" ");
        constraints.weightx = 1;
        layout.setConstraints(padding, constraints);
        padding.setBorder(BorderFactory.createLoweredBevelBorder());
        statusBar.add(padding);

        constraints.weightx = 0;
        final JPanel progressPanel = new JPanel(new ToolbarLayout());
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressPanel.add(progressBar);
        layout.setConstraints(progressPanel, constraints);
        statusBar.add(progressPanel);

        progressPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        progressBar.setBorderPainted(false);

        updateStatusBar();
        getContentPane().add(statusBar, BorderLayout.SOUTH);
    }

    public void updateStatusBar() {
        if (model.getInstanceCount() == -1) {
            progressBar.setIndeterminate(true);
            progressBar.setString("loading...");
            countLabel.setText(" " + model.getRowCount() + " instances ");
        } else if (model.getInstanceCount() == model.getRowCount()) {
            progressBar.setMaximum(model.getInstanceCount());
            progressBar.setValue(0);

            progressBar.setIndeterminate(false);
            progressBar.setString("");
            countLabel.setText(" " + model.getInstanceCount() + " instances ");
        } else {
            progressBar.setMaximum(model.getInstanceCount());
            progressBar.setValue(model.getRowCount());

            progressBar.setIndeterminate(false);
            progressBar.setString("loading...");
            countLabel.setText(" " + model.getInstanceCount() + " instances ");
        }
    }

    @Override
    public void tableChanged(final TableModelEvent e) {
        if (e.getType() == TableModelEvent.INSERT) {
            updateStatusBar();
        } else if (e.getType() == TableModelEvent.DELETE) {
            setTitle(model.getTitle());
            countLabel.setText(model.getRowCount() + " instances");
        }
    }

    private class FontChangeListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {
            setFont((Integer.valueOf(e.getActionCommand())).intValue());
        }
    }

    private class SaveListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {
            table.saveData();
        }
    }

    private class SaveRowsListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {
            table.saveSelectedData();
        }
    }

    private class RefreshListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {
            refresh();
        }
    }

    private class CancelLoadListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {
            model.cancelLoad();
        }
    }

    private class ShowInternalIdAction extends ToggleAction {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public ShowInternalIdAction(final String name) {
            super(name);
        }

        @Override
        public void togglePerformed(final boolean selected) throws java.rmi.RemoteException {
            s_showInternalId = selected;
            table.showInternalId(selected);
        }
    }

    private class ObjectServiceInvoker implements ActionListener {

        private final ObjectServiceMetaData service;

        public ObjectServiceInvoker(final ObjectServiceMetaData service) {
            this.service = service;
        }

        @Override
        public void actionPerformed(final ActionEvent action) {
            new InvokeObjectServiceDialog(service);
            // TODO add listener for return value
            /*
             * dialog.addInvokeListener( new InvokeDialog.InvokeListener(){ invoked() {
             * refresh(); } });
             */
        }
    }

    private class EventInvoker implements ActionListener {

        private final EventMetaData event;

        public EventInvoker(final EventMetaData event) {
            this.event = event;
        }

        @Override
        public void actionPerformed(final ActionEvent action) {
            new FireEventDialog(event);
        }
    }

}
