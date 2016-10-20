// 
// Filename : ObjectDetailsPane.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.xtuml.masl.inspector.Preferences;


class ObjectDetailsPane extends JTabbedPane
{

  class TabHider
      implements ListDataListener
  {

    private final int       index;
    private final ListModel listModel;

    public TabHider ( final int index, final ListModel model )
    {
      this.index = index;
      this.listModel = model;
    }

    public void contentsChanged ( final ListDataEvent e )
    {
      setEnabledAt(index, listModel.getSize() > 0);
    }

    public void intervalAdded ( final ListDataEvent e )
    {
      setEnabledAt(index, listModel.getSize() > 0);
    }

    public void intervalRemoved ( final ListDataEvent e )
    {
      setEnabledAt(index, listModel.getSize() > 0);
    }

  }


  ObjectDetailsPane ( final ObjectList objectList )
  {
    if ( Preferences.getModellingMode() == Preferences.ModellingMode.UML )
    {
      addTab("Class Diagram", new JScrollPane(new org.xtuml.masl.inspector.gui.modelView.ObjectDisplay(objectList)));
    }
    else
    {
      addTab("Information Model", new JScrollPane(new org.xtuml.masl.inspector.gui.modelView.ObjectDisplay(objectList)));
    }
    objectList.getModel().addListDataListener(new TabHider(getTabCount() - 1, objectList.getModel()));
    setEnabledAt(getTabCount() - 1, objectList.getModel().getSize() > 0);

    final ObjectServiceList objectServiceList = new ObjectServiceList(objectList);
    addTab("Object Services", new JScrollPane(objectServiceList));
    objectServiceList.getModel().addListDataListener(new TabHider(getTabCount() - 1, objectServiceList.getModel()));
    setEnabledAt(getTabCount() - 1, objectServiceList.getModel().getSize() > 0);

    final InstanceServiceList instanceServiceList = new InstanceServiceList(objectList);
    addTab("Instance Services", new JScrollPane(instanceServiceList));
    instanceServiceList.getModel().addListDataListener(new TabHider(getTabCount() - 1, instanceServiceList.getModel()));
    setEnabledAt(getTabCount() - 1, instanceServiceList.getModel().getSize() > 0);

    final ObjectStateList objectStateList = new ObjectStateList(objectList);
    addTab("Object States", new JScrollPane(objectStateList));
    objectStateList.getModel().addListDataListener(new TabHider(getTabCount() - 1, objectStateList.getModel()));
    setEnabledAt(getTabCount() - 1, objectStateList.getModel().getSize() > 0);

    final ObjectEventList objectEventList = new ObjectEventList(objectList);
    addTab("Object Events", new JScrollPane(objectEventList));
    objectEventList.getModel().addListDataListener(new TabHider(getTabCount() - 1, objectEventList.getModel()));
    setEnabledAt(getTabCount() - 1, objectEventList.getModel().getSize() > 0);

    final AssignerStateList assignerStateList = new AssignerStateList(objectList);
    addTab("Assigner States", new JScrollPane(assignerStateList));
    assignerStateList.getModel().addListDataListener(new TabHider(getTabCount() - 1, assignerStateList.getModel()));
    setEnabledAt(getTabCount() - 1, assignerStateList.getModel().getSize() > 0);

    final AssignerEventList assignerEventList = new AssignerEventList(objectList);
    addTab("Assigner Events", new JScrollPane(assignerEventList));
    assignerEventList.getModel().addListDataListener(new TabHider(getTabCount() - 1, assignerEventList.getModel()));
    setEnabledAt(getTabCount() - 1, assignerEventList.getModel().getSize() > 0);
  }
}
