//
// Filename : DomainDetailsPane.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

class DomainDetailsPane extends JTabbedPane {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    class TabHider implements ListDataListener {

        private final int index;
        private final ListModel listModel;

        public TabHider(final int index, final ListModel model) {
            this.index = index;
            this.listModel = model;
        }

        @Override
        public void contentsChanged(final ListDataEvent e) {
            setEnabledAt(index, listModel.getSize() > 0);
        }

        @Override
        public void intervalAdded(final ListDataEvent e) {
            setEnabledAt(index, listModel.getSize() > 0);
        }

        @Override
        public void intervalRemoved(final ListDataEvent e) {
            setEnabledAt(index, listModel.getSize() > 0);
        }

    }

    DomainDetailsPane(final DomainPicker domainPicker) {
        final ObjectList objectList = new ObjectList(domainPicker);
        final JScrollPane objectScroller = new JScrollPane(objectList);

        final JSplitPane objectSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        addTab("Objects", objectSplit);
        objectList.getModel().addListDataListener(new TabHider(getTabCount() - 1, objectList.getModel()));
        setEnabledAt(getTabCount() - 1, objectList.getModel().getSize() > 0);

        objectSplit.setLeftComponent(objectScroller);
        final ObjectDetailsPane objectDetailsPane = new ObjectDetailsPane(objectList);

        objectSplit.setRightComponent(objectDetailsPane);
        objectSplit.setContinuousLayout(true);
        objectSplit.setDividerLocation(objectScroller.getPreferredSize().width + 1);
        objectSplit.setResizeWeight(0);

        objectList.getModel().addListDataListener(new ListDataListener() {

            @Override
            public void contentsChanged(final ListDataEvent e) {
                objectSplit.setDividerLocation(objectScroller.getPreferredSize().width + 1);
            }

            @Override
            public void intervalAdded(final ListDataEvent e) {
                objectSplit.setDividerLocation(objectScroller.getPreferredSize().width + 1);
            }

            @Override
            public void intervalRemoved(final ListDataEvent e) {
                objectSplit.setDividerLocation(objectScroller.getPreferredSize().width + 1);
            }

        });

        final TerminatorList terminatorList = new TerminatorList(domainPicker);

        final JSplitPane terminatorSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        final JScrollPane terminatorScroller = new JScrollPane(terminatorList);

        addTab("Terminators", terminatorSplit);
        terminatorList.getModel().addListDataListener(new TabHider(getTabCount() - 1, terminatorList.getModel()));
        setEnabledAt(getTabCount() - 1, terminatorList.getModel().getSize() > 0);

        terminatorSplit.setLeftComponent(terminatorScroller);
        final TerminatorDetailsPane terminatorDetailsPane = new TerminatorDetailsPane(terminatorList);

        terminatorSplit.setRightComponent(terminatorDetailsPane);
        terminatorSplit.setContinuousLayout(true);
        terminatorSplit.setDividerLocation(terminatorScroller.getPreferredSize().width + 1);
        terminatorSplit.setResizeWeight(0);

        terminatorList.getModel().addListDataListener(new ListDataListener() {

            @Override
            public void contentsChanged(final ListDataEvent e) {
                terminatorSplit.setDividerLocation(terminatorScroller.getPreferredSize().width + 1);
            }

            @Override
            public void intervalAdded(final ListDataEvent e) {
                terminatorSplit.setDividerLocation(terminatorScroller.getPreferredSize().width + 1);
            }

            @Override
            public void intervalRemoved(final ListDataEvent e) {
                terminatorSplit.setDividerLocation(terminatorScroller.getPreferredSize().width + 1);
            }

        });

        final DomainScenarioList scenarioList = new DomainScenarioList(domainPicker);
        addTab("Scenarios", new JScrollPane(scenarioList));
        scenarioList.getModel().addListDataListener(new TabHider(getTabCount() - 1, scenarioList.getModel()));
        setEnabledAt(getTabCount() - 1, scenarioList.getModel().getSize() > 0);

        final DomainExternalList externalList = new DomainExternalList(domainPicker);
        addTab("Externals", new JScrollPane(externalList));
        externalList.getModel().addListDataListener(new TabHider(getTabCount() - 1, externalList.getModel()));
        setEnabledAt(getTabCount() - 1, externalList.getModel().getSize() > 0);

        final DomainServiceList serviceList = new DomainServiceList(domainPicker);
        addTab("Services", new JScrollPane(serviceList));
        serviceList.getModel().addListDataListener(new TabHider(getTabCount() - 1, serviceList.getModel()));
        setEnabledAt(getTabCount() - 1, serviceList.getModel().getSize() > 0);

    }
}
