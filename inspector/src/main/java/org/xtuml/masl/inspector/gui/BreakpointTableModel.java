// 
// Filename : BreakpointTableModel.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.xtuml.masl.inspector.BreakpointController;
import org.xtuml.masl.inspector.BreakpointEvent;
import org.xtuml.masl.inspector.BreakpointListener;
import org.xtuml.masl.inspector.WeakBreakpointListener;
import org.xtuml.masl.inspector.processInterface.SourcePosition;


class BreakpointTableModel extends AutoSizingTableModel
    implements BreakpointListener
{

  public static final int                   ACTIVE_COL    = 0;
  public static final int                   NAME_COL      = 1;
  public static final int                   LINE_COL      = 2;
  public static final int                   COL_COUNT     = 3;

  private static final Class<?>[]           columnClasses = new Class<?>[]
                                                            { Boolean.class, String.class, String.class };
  private static final String[]             columnNames   = new String[]
                                                            { "", "Name", "Line" };

  private static final BreakpointController controller    = BreakpointController.getInstance();

  private List<SourcePosition>              breakpoints   = new ArrayList<SourcePosition>();

  public BreakpointTableModel ()
  {
    breakpoints = new ArrayList<SourcePosition>(controller.getBreakpoints().keySet());
    Collections.sort(breakpoints);
    controller.addBreakpointListener(new WeakBreakpointListener(this));
  }

  public int getColumnCount ()
  {
    return COL_COUNT;
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
    return col == ACTIVE_COL;
  }

  public int getRowCount ()
  {
    return breakpoints.size();
  }

  public Object getValueAt ( final int row, final int col )
  {
    final SourcePosition pos = breakpoints.get(row);

    switch ( col )
    {
      case ACTIVE_COL:
        return controller.breakpointSet(pos) ? Boolean.TRUE : Boolean.FALSE;
      case NAME_COL:
        return pos.getSource().getFullyQualifiedName();
      case LINE_COL:
        if ( pos.getLineNo() == SourcePosition.LAST_LINE )
        {
          return "exit";
        }
        else if ( pos.getLineNo() == SourcePosition.FIRST_LINE )
        {
          return "entry";
        }
        else
        {
          return "" + pos.getLineNo();
        }
      default:
        throw new IllegalArgumentException("Invalid column " + col);
    }
  }

  @Override
  public void setValueAt ( final Object val, final int row, final int col )
  {
    if ( col != ACTIVE_COL )
    {
      throw new IllegalArgumentException("Invalid column " + col);
    }
    final SourcePosition pos = breakpoints.get(row);

    if ( val.equals(Boolean.TRUE) )
    {
      controller.setBreakpoint(pos);
    }
    else
    {
      controller.clearBreakpoint(pos);
    }
  }

  public void breakpointChanged ( final BreakpointEvent e )
  {
    final int index = Collections.binarySearch(breakpoints, e.getPosition());

    if ( index < 0 )
    {
      if ( e.getActive() != null )
      {
        final int insertPos = -(index + 1);
        breakpoints.add(insertPos, e.getPosition());
        fireTableRowsInserted(insertPos, insertPos);
      }
    }
    else
    {
      if ( e.getActive() == null )
      {
        breakpoints.remove(index);
        fireTableRowsDeleted(index, index);
      }
      else
      {
        fireTableCellUpdated(index, ACTIVE_COL);
      }
    }
  }

  public SourcePosition getPosition ( final int row )
  {
    return breakpoints.get(row);
  }

  @Override
  public int getPreferredColumnWidth ( final int col, final JTable parent )
  {
    final TableCellRenderer cellRenderer = parent.getCellRenderer(0, col);
    final int margin = parent.getColumnModel().getColumnMargin();
    switch ( col )
    {
      case ACTIVE_COL:
        return margin + cellRenderer.getTableCellRendererComponent(parent, Boolean.TRUE, false, false, 0, 0).getPreferredSize().width;
      case LINE_COL:
        return margin + cellRenderer.getTableCellRendererComponent(parent, new Integer(99999), false, false, 0, 0)
                                    .getPreferredSize().width;
      case NAME_COL:
        return margin + cellRenderer.getTableCellRendererComponent(parent,
                                                                   "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW",
                                                                   false,
                                                                   false,
                                                                   0,
                                                                   0).getPreferredSize().width;
      default:
        throw new IllegalArgumentException("Invalid column: " + col);
    }
  }

}
