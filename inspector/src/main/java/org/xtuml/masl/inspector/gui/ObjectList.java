// 
// Filename : ObjectList.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.xtuml.masl.inspector.processInterface.DomainMetaData;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;


public class ObjectList extends DependentObjectList<ObjectMetaData, DomainMetaData>
{

  public ObjectList ( final DomainPicker domainPicker )
  {
    super(new DependentObjectListModel<ObjectMetaData, DomainMetaData>(domainPicker)
        {

          @Override
          protected ObjectMetaData[] getDependentValues ( final DomainMetaData domain )
          {
            final ObjectMetaData[] objects = domain.getObjects();
            Arrays.sort(objects);
            return objects;
          }
        });

    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setVisibleRowCount(10);

    addMouseListener(new ListPopupHandler(getPopup()));

  }

  private JPopupMenu getPopup ()
  {
    final JPopupMenu popup = new JPopupMenu();
    final JMenuItem showAllItem = new JMenuItem("Display all instances...");
    showAllItem.addActionListener(new ActionListener()
      {

        public void actionPerformed ( final ActionEvent action )
        {
          InstanceViewer.display((ObjectMetaData)getSelectedValue());
        }
      });
    popup.add(showAllItem);

    popup.addPopupMenuListener(new InstanceCounter(showAllItem, popup));
    return popup;
  }


  private class InstanceCounter
      implements PopupMenuListener
  {

    private final JMenuItem  displayItem;
    private final JPopupMenu popup;

    public InstanceCounter ( final JMenuItem displayItem, final JPopupMenu popup )
    {
      this.displayItem = displayItem;
      this.popup = popup;
    }

    public void popupMenuWillBecomeVisible ( final PopupMenuEvent e )
    {
      displayItem.setText("Display all instances...");
      final Thread counter = new Thread()
        {

          @Override
          public void run ()
          {
            try
            {
              final int count = ProcessConnection.getConnection().getInstanceCount((ObjectMetaData)getSelectedValue());
              displayItem.setText("Display all " + count + " instances...");
              displayItem.setSize(displayItem.getPreferredSize());
              popup.pack();
            }
            catch ( final java.rmi.RemoteException re )
            {
            }
          }
        };

      counter.start();
    }

    public void popupMenuWillBecomeInvisible ( final PopupMenuEvent e )
    {
    }

    public void popupMenuCanceled ( final PopupMenuEvent e )
    {
    }
  }


  @Override
  public Dimension getPreferredScrollableViewportSize ()
  {
    final Dimension d = super.getPreferredScrollableViewportSize();

    if ( getModel().getSize() == 0 )
    {
      d.width = 0;
    }
    d.width = Math.max(d.width, 100);

    return d;
  }


}
