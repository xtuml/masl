// 
// Filename : InstanceTable.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableCellRenderer;

import org.xtuml.masl.inspector.processInterface.Capability;
import org.xtuml.masl.inspector.processInterface.EventMetaData;
import org.xtuml.masl.inspector.processInterface.InstanceData;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.ObjectServiceMetaData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.EventMetaData.EventType;


class InstanceTable extends com.jrefinery.ui.SortableTable
    implements UpdateListener
{

  private final ObjectMetaData    meta;
  private final JPopupMenu        popup       = new JPopupMenu();

  private final InstanceViewModel model;
  private Collection<Object>      selectedPKs = null;

  private static String           defaultDir  = System.getProperty("user.dir");

  public InstanceTable ( final InstanceViewModel model )
  {
    super(model);
    this.model = model;
    model.addUpdateListener(this);
    meta = model.getMetaObject();
    setAutoResizeMode(AUTO_RESIZE_OFF);

    init();
  }

  private void init ()
  {
    // Replace renderer to prevent html parsing of strings
    setDefaultRenderer(String.class, new DefaultTableCellRenderer()
    {

      @Override
      public void setText ( final String text )
      {
        // Ignore exceptions caused by invalid html
        try
        {
          super.setText(text);
        }
        catch ( final Exception e )
        {
        }
        putClientProperty("html", null);
      }
    });

    setDefaultRenderer(Double.class, new DefaultTableCellRenderer()
    {

      @Override
      public void setValue ( final Object value )
      {
        setText(value.toString());
      }
    });

    addMouseListener(new MouseHandler());

    final JMenuItem refresh = new JMenuItem("Refresh");
    popup.add(refresh);
    refresh.addActionListener(new ActionListener()
    {

      public void actionPerformed ( final ActionEvent action )
      {
        final int[] rows = getSelectedRows();
        for ( final int row : rows )
        {
          model.refreshRow(row);
        }
      }
    });

    final JMenuItem displaySelected = new JMenuItem("Display Filtered");
    popup.add(displaySelected);
    displaySelected.addActionListener(new ActionListener()
    {

      public void actionPerformed ( final ActionEvent action )
      {
        final Object[] pks = model.getPrimaryKeys(getSelectedRows()).toArray();
        InstanceViewer.display(meta, pks);
      }
    });

    final JMenuItem displayDetail = new JMenuItem("Display Detail");
    popup.add(displayDetail);
    displayDetail.addActionListener(new ActionListener()
    {

      public void actionPerformed ( final ActionEvent action )
      {
        for ( final int row : getSelectedRows() )
        {
          (new EditDataDialog(model.getInstanceAt(row), false)).display();
        }
      }
    });


    if ( Capability.RUN_OBJECT_SERVICE.isAvailable() )
    {
      popup.addSeparator();
      final ObjectServiceMetaData[] instanceServices = meta.getInstanceServices();
      Arrays.sort(instanceServices);
      for ( final ObjectServiceMetaData instanceService : instanceServices )
      {
        final JMenuItem item = new JMenuItem("Run " + instanceService);
        popup.add(item);

        item.addActionListener(new InstanceServiceInvoker(instanceService));
      }
    }

    if ( Capability.FIRE_EVENT.isAvailable() )
    {
      popup.addSeparator();
      final EventMetaData[] events = meta.getInstanceEvents();
      Arrays.sort(events);
      for ( final EventMetaData event : events )
      {
        if ( event.getType() != EventType.Creation )
        {
          final JMenuItem item = new JMenuItem("Fire " + event);
          popup.add(item);

          item.addActionListener(new EventInvoker(event));
        }
      }
    }

    if ( Capability.DELETE_SINGLE_INSTANCE.isAvailable() )
    {
      popup.addSeparator();

      final JMenuItem remove = new JMenuItem("Remove...");
      popup.add(remove);
      remove.addActionListener(new ActionListener()
      {

        public void actionPerformed ( final ActionEvent action )
        {
          final int[] rows = getSelectedRows();
          for ( final int row : rows )
          {
            try
            {
              if ( JOptionPane.showConfirmDialog(InstanceTable.this,
                                                 "Are you sure you wish to remove this instance?",
                                                 "Remove " + meta.getName() + " " + model.getInstanceName(row),
                                                 JOptionPane.YES_NO_OPTION,
                                                 JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION )
              {
                ProcessConnection.getConnection().deleteSingleInstance(meta, model.getPrimaryKey(row));
              }
            }
            catch ( final Exception e )
            {
              e.printStackTrace();
            }
          }

          // Refresh in reverse order so row ids don't get moved.
          for ( int i = rows.length - 1; i >= 0; i-- )
          {
            model.refreshRow(rows[i]);
          }
        }
      });
    }

  }

  @Override
  public void setFont ( final Font font )
  {
    super.setFont(font);
    getTableHeader().setFont(font);
    setRowHeight(getFontMetrics(font).getAscent() + getFontMetrics(font).getDescent() + getRowMargin());
  }

  public void showInternalId ( final boolean showIt )
  {
    if ( showIt )
    {
      getColumnModel().getColumn(0).setWidth(model.getPreferredColumnWidth(0, this));
      getColumnModel().getColumn(0).setPreferredWidth(model.getPreferredColumnWidth(0, this));
    }
    else
    {
      getColumnModel().getColumn(0).setWidth(0);
      getColumnModel().getColumn(0).setPreferredWidth(0);
    }
    sizeColumnsToFit(0);
  }

  public void updateStarted ()
  {
    if ( selectedPKs == null )
    {
      selectedPKs = model.getPrimaryKeys(getSelectedRows());
      clearSelection();
    }
  }

  public void updateComplete ()
  {
    if ( selectedPKs != null )
    {
      selectRows(model.getRowsByPrimaryKey(selectedPKs));
      selectedPKs = null;
    }
  }

  public void setColumnWidths ()
  {
    int totalWidth = 0;
    for ( int i = 0; i < getModel().getColumnCount(); i++ )
    {
      final int width = model.getPreferredColumnWidth(i, this);
      totalWidth += width;
      getColumnModel().getColumn(i).setPreferredWidth(width);
      getColumnModel().getColumn(i).setMinWidth(1);
    }
    final Dimension d = getPreferredScrollableViewportSize();
    d.width = totalWidth;
    setPreferredScrollableViewportSize(d);
  }

  public void saveData ()
  {
    try
    {
      final JFileChooser chooser = new JFileChooser(defaultDir);
      if ( chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
      {
        defaultDir = chooser.getSelectedFile().getPath();
        final java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(chooser.getSelectedFile()));
        model.writeTable(out);

        out.close();
      }
    }
    catch ( final java.io.IOException exception )
    {
      System.out.println(exception);
    }
  }

  public void saveSelectedData ()
  {
    try
    {
      final JFileChooser chooser = new JFileChooser(defaultDir);
      if ( chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
      {
        defaultDir = chooser.getSelectedFile().getPath();
        final java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(chooser.getSelectedFile()));
        model.writeRows(getSelectedRows(), out);

        out.close();
      }
    }
    catch ( final java.io.IOException exception )
    {
      System.out.println(exception);
    }
  }

  public void selectRows ( final Collection<Integer> rows )
  {
    final Iterator<Integer> i = rows.iterator();
    while ( i.hasNext() )
    {
      final int row = i.next().intValue();
      addRowSelectionInterval(row, row);
    }
  }

  private void navigateToSelected ()
  {
    final int relationshipNo = model.getRelationshipNo(convertColumnIndexToModel(getSelectedColumn()));

    if ( relationshipNo >= 0 )
    {
      InstanceViewer.display(meta, getSelectedInstances()[0].getPrimaryKey(), relationshipNo);
    }
  }

  private InstanceData[] getSelectedInstances ()
  {
    final int[] selected = getSelectedRows();
    final InstanceData[] instances = new InstanceData[selected.length];
    for ( int i = 0; i < selected.length; i++ )
    {
      instances[i] = model.getInstanceAt(selected[i]);
    }
    return instances;
  }

  @Override
  public Dimension getPreferredScrollableViewportSize ()
  {
    final Dimension vs = super.getPreferredScrollableViewportSize();
    final Dimension ps = getPreferredSize();

    return new Dimension(Math.max(ps.width, vs.width), Math.max(Math.min(ps.height, vs.height), 200));
  }

  private class MouseHandler extends MouseAdapter
  {

    @Override
    public void mouseClicked ( final MouseEvent e )
    {
      if ( (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0 && e.getClickCount() > 1 )
      {
        navigateToSelected();
      }
      super.mouseClicked(e);
    }

    @Override
    public void mousePressed ( final MouseEvent e )
    {
      if ( e.isPopupTrigger() )
      {
        final int row = rowAtPoint(new Point(e.getX(), e.getY()));
        if ( !isRowSelected(row) )
        {
          setRowSelectionInterval(row, row);
        }

        if ( getSelectedRow() >= 0 )
        {
          popup.show(e.getComponent(), e.getX(), e.getY());
        }
      }
      super.mousePressed(e);
    }
  }

  private class InstanceServiceInvoker
      implements ActionListener
  {

    private final ObjectServiceMetaData service;

    public InstanceServiceInvoker ( final ObjectServiceMetaData service )
    {
      this.service = service;
    }

    public void actionPerformed ( final ActionEvent action )
    {
      final int[] rows = getSelectedRows();
      for ( final int row : rows )
      {
        new InvokeObjectServiceDialog(service, model.getPrimaryKey(row));
        // TODO add listener
        /*
         * dialog.addInvokeListener( new
         * InvokeObjectServiceDialog.InvokeListener(){ invoked() {
         * model.refreshRow(row) } });
         */
      }
    }
  }

  private class EventInvoker
      implements ActionListener
  {

    private final EventMetaData event;

    public EventInvoker ( final EventMetaData event )
    {
      this.event = event;
    }

    public void actionPerformed ( final ActionEvent action )
    {
      final int[] rows = getSelectedRows();
      for ( final int row : rows )
      {
        new FireEventDialog(event, meta, model.getPrimaryKey(row));
      }
    }
  }
}
