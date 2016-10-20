// 
// Filename : EventQueueTableModel.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.xtuml.masl.inspector.processInterface.EventData;
import org.xtuml.masl.inspector.processInterface.InstanceData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.ProcessStatusEvent;
import org.xtuml.masl.inspector.processInterface.ProcessStatusListener;
import org.xtuml.masl.inspector.processInterface.TimerData;
import org.xtuml.masl.inspector.processInterface.WeakProcessStatusListener;


class EventQueueTableModel extends AutoSizingTableModel
    implements ProcessStatusListener
{

  public static final int         EVENT_COL     = 0;
  public static final int         SOURCE_COL    = 1;
  public static final int         DEST_COL      = 2;
  public static final int         EXPIRY_COL    = 3;
  public static final int         COL_COUNT     = 4;

  private static final Class<?>[] columnClasses = new Class[]
                                                { String.class, String.class, String.class, String.class };
  private static final String[]   columnNames   = new String[]
                                                { "Event", "Source", "Destination", "Expiry" };

  private boolean                 autoUpdate    = true;
  private EventData[]             eventQueue;
  private TimerData[]             timerQueue;

  public EventQueueTableModel ()
  {
    ProcessConnection.getConnection().addProcessStatusListener(new WeakProcessStatusListener(this));

    try
    {
      eventQueue = ProcessConnection.getConnection().getEventQueue();
      timerQueue = ProcessConnection.getConnection().getTimerQueue();
    }
    catch ( final java.rmi.RemoteException e )
    {
      e.printStackTrace();
      clear();
    }
  }

  public void setAutoUpdate ( final boolean auto )
  {
    autoUpdate = auto;
  }

  public int getColumnCount ()
  {
    return COL_COUNT;
  }

  public Object getEventAt ( final int row )
  {
    return (row < eventQueue.length) ? eventQueue[row] : timerQueue[row - eventQueue.length];
  }

  @Override
  public Class<?> getColumnClass ( final int col )
  {
    return columnClasses[col];
  }

  @Override
  public String getColumnName ( final int col )
  {
    return columnNames[col];
  }

  @Override
  public boolean isCellEditable ( final int row, final int col )
  {
    return false;
  }

  public int getRowCount ()
  {
    return eventQueue.length + timerQueue.length;
  }

  public Object getValueAt ( final int row, final int col )
  {
    String value = "";

    final EventData event = (row < eventQueue.length) ? eventQueue[row] : timerQueue[row - eventQueue.length].getEventData();

    switch ( col )
    {
      case EXPIRY_COL:
      {
        return (row < eventQueue.length) ? null : timerQueue[row - eventQueue.length].getExpiryTime().toString();
      }

      case EVENT_COL:
        return event.getEvent().toString();

      case SOURCE_COL:
      {
        if ( event.getSourceObject() != null )
        {
          value = event.getSourceObject().getDomain().getName() + "::"
                  + event.getSourceObject().getName();

          if ( event.getSourceInstanceId().getInstanceData() != null )
          {
            final InstanceData data = event.getSourceInstanceId().getInstanceData();
            value = value + " " + event.getSourceObject().getInstanceIdentifier(data);
          }
        }

      }
        break;

      case DEST_COL:
      {
        value = event.getDestObject().getDomain().getName() + "::"
                + event.getDestObject().getName();

        if ( event.getDestInstanceId().getId() != null )
        {
          final InstanceData data = event.getDestInstanceId().getInstanceData();
          value = value + " " + event.getDestObject().getInstanceIdentifier(data);
        }
      }
        break;
      default:
        throw new IllegalArgumentException("Invalid column " + col);
    }
    return value;
  }

  private final static EventData[] emptyEventQueue = new EventData[0];
  private final static TimerData[] emptyTimerQueue = new TimerData[0];

  public void clear ()
  {
    eventQueue = emptyEventQueue;
    timerQueue = emptyTimerQueue;
  }

  public void update ()
  {
    try
    {
      eventQueue = ProcessConnection.getConnection().getEventQueue();
      timerQueue = ProcessConnection.getConnection().getTimerQueue();
    }
    catch ( final java.rmi.RemoteException ex )
    {
      ex.printStackTrace();
      clear();
    }
    fireTableDataChanged();
  }

  public void processStatusChanged ( final ProcessStatusEvent e )
  {
    if ( autoUpdate )
    {
      if ( e.getStatus() == ProcessConnection.PAUSED )
      {
        update();
      }
    }
  }

  @Override
  public int getPreferredColumnWidth ( final int col, final JTable parent )
  {
    final TableCellRenderer cellRenderer = parent.getCellRenderer(0, col);
    final int margin = parent.getColumnModel().getColumnMargin();
    switch ( col )
    {
      case EXPIRY_COL:
        return margin
               + cellRenderer.getTableCellRendererComponent(parent, "9999-99-99T99:99:99.999", false, false, 0, 0)
                             .getPreferredSize().width;
      case EVENT_COL:
        return margin
               + cellRenderer.getTableCellRendererComponent(parent, "WWWWWWWWWWWWWWW", false, false, 0, 0).getPreferredSize().width;
      case SOURCE_COL:
        return margin
               + cellRenderer.getTableCellRendererComponent(parent, "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW", false, false, 0, 0)
                             .getPreferredSize().width;
      case DEST_COL:
        return margin
               + cellRenderer.getTableCellRendererComponent(parent, "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW", false, false, 0, 0)
                             .getPreferredSize().width;
      default:
        throw new IllegalArgumentException("Invalid column: " + col);
    }
  }

}
