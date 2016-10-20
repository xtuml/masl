//
// Filename : SourceCodeTableModel.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.xtuml.masl.inspector.BreakpointController;
import org.xtuml.masl.inspector.BreakpointEvent;
import org.xtuml.masl.inspector.BreakpointListener;
import org.xtuml.masl.inspector.WeakBreakpointListener;
import org.xtuml.masl.inspector.processInterface.ExecutableSource;
import org.xtuml.masl.inspector.processInterface.SourcePosition;


class SourceCodeTableModel
    extends AbstractTableModel
    implements BreakpointListener
{

  public final static int         INDICATOR_COL                  = 0;
  public final static int         LINE_NO_COL                    = 1;
  public final static int         CODE_COL                       = 2;
  public final static int         COL_COUNT                      = 3;

  private final static Class<?>[] colClasses                     = new Class[]
                                                                   { Character.class, Integer.class, String.class };

  private static final Character  CURRENT_LINE_SYMBOL            = new Character('\u25ba');                         // Filled
  // arrow
  private static final Character  STALE_CURRENT_LINE_SYMBOL      = new Character('>');                              // >
  private static final Character  BREAKPOINT_SYMBOL              = new Character('\u25cf');                         // Circle
  private static final Character  BREAKPOINT_CURRENT_LINE_SYMBOL = new Character('\u27b2');                         // Circled
  // arrow


  private ExecutableSource        source;
  private final ArrayList<String> lines                          = new ArrayList<String>();
  private int                     currentLine;
  private boolean                 staleCurrentLine               = false;

  public SourceCodeTableModel ()
  {
    this(null, SourcePosition.NO_LINE);
  }


  public SourceCodeTableModel ( final ExecutableSource source, final int lineNo )
  {
    setSource(source);

    if ( lineNo == SourcePosition.LAST_LINE )
    {
      currentLine = lines.size();
    }
    else
    {
      currentLine = lineNo;
    }

    BreakpointController.getInstance().addBreakpointListener(new WeakBreakpointListener(this));
  }

  private static String getMd5Sum ( final File file ) throws IOException
  {
    try
    {
      final MessageDigest md = MessageDigest.getInstance("MD5");
      final InputStream input = new BufferedInputStream(new FileInputStream(file));
      final byte[] bytes = new byte[1024];
      int read = input.read(bytes);
      while ( read != -1 )
      {
        md.update(bytes, 0, read);
        read = input.read(bytes);
      }
      return new BigInteger(1, md.digest()).toString(16);
    }
    catch ( final NoSuchAlgorithmException e )
    {
      assert false : "MD5 algorithm not found";
      return null;
    }
  }


  public void setSource ( final ExecutableSource source )
  {
    final ExecutableSource oldSource = this.source;

    this.source = source;

    if ( source != oldSource )
    {
      lines.clear();
      if ( source != null )
      {
        try
        {
          File sourceFile = source.getSourceFile();

          while ( sourceFile == null || !sourceFile.exists() )
          {
            final SourceCodeChooser fileChooser = new SourceCodeChooser(source);

            if ( fileChooser.showDialog() != JFileChooser.APPROVE_OPTION )
            {
              source.setSourceFile(null);
              throw new FileNotFoundException();
            }

            sourceFile = fileChooser.getSelectedFile();
            source.setSourceFile(sourceFile);

            if ( source.isSourceFileMismatch() )
            {
              final int selected = JOptionPane.showConfirmDialog(null,
                                                                 "The chosen file is different to the one that was compiled. Use Anyway?",
                                                                 "File Mismatch",
                                                                 JOptionPane.YES_NO_OPTION,
                                                                 JOptionPane.WARNING_MESSAGE);
              if ( selected != JOptionPane.YES_OPTION )
              {
                source.setSourceFile(null);
                sourceFile = null;
              }
            }

          }

          final BufferedReader code = new BufferedReader(new FileReader(sourceFile));

          while ( code.ready() )
          {
            lines.add(code.readLine());
          }
        }
        catch ( final IOException e )
        {
          lines.add("Error Reading File");
        }
      }
      fireTableDataChanged();
    }
  }

  public ExecutableSource getSource ()
  {
    return source;
  }


  public int getColumnCount ()
  {
    return COL_COUNT;
  }

  @Override
  public Class<?> getColumnClass ( final int col )
  {
    return colClasses[col];
  }

  public String getColumnName ()
  {
    return null;
  }

  public int getRowCount ()
  {
    return lines.size();
  }

  public Object getValueAt ( final int row, final int col )
  {
    final int line = row + 1;

    switch ( col )
    {
      case INDICATOR_COL:
        if ( line == currentLine )
        {
          if ( BreakpointController.getInstance()
                                   .breakpointSet(source.getSourcePosition(line == lines.size() ? SourcePosition.LAST_LINE : line)) )
          {
            return BREAKPOINT_CURRENT_LINE_SYMBOL;
          }
          else
          {
            return staleCurrentLine ? STALE_CURRENT_LINE_SYMBOL : CURRENT_LINE_SYMBOL;
          }
        }
        else if ( BreakpointController.getInstance()
                                      .breakpointSet(source.getSourcePosition(line == lines.size() ? SourcePosition.LAST_LINE
                                                                                                  : line)) )
        {
          return BREAKPOINT_SYMBOL;
        }
        else
        {
          return null;
        }
      case LINE_NO_COL:
        return new Integer(row + 1);
      case CODE_COL:
        return lines.get(row);
      default:
        throw new IllegalArgumentException("Invalid column: " + col);
    }
  }

  public int getPreferredColumnWidth ( final int col, final JTable parent )
  {
    final TableCellRenderer cellRenderer = parent.getCellRenderer(0, col);
    final int margin = parent.getColumnModel().getColumnMargin();
    switch ( col )
    {
      case INDICATOR_COL:
        return margin + Math.max(Math.max(
                                          cellRenderer.getTableCellRendererComponent(parent,
                                                                                     CURRENT_LINE_SYMBOL,
                                                                                     false,
                                                                                     false,
                                                                                     0,
                                                                                     0).getPreferredSize().width,
                                          cellRenderer.getTableCellRendererComponent(parent, BREAKPOINT_SYMBOL, false, false, 0, 0)
                                                      .getPreferredSize().width),
                                 cellRenderer.getTableCellRendererComponent(parent,
                                                                            BREAKPOINT_CURRENT_LINE_SYMBOL,
                                                                            false,
                                                                            false,
                                                                            0,
                                                                            0).getPreferredSize().width);
      case LINE_NO_COL:
      {
        // In proportional fonts 1 is narrower, so just
        // finding the width of the last number might stop
        // smaller numbers fitting (eg 9111 would stop 8888
        // fitting), so round up to the next power of 10 -1 to
        // ensure a good fit (assuming 9 is widest digit)
        // NB No log10 function in java so we'll have to do it
        // ourselves!
        int x = lines.size() + 1;
        int max = 10;
        while ( (x /= 10) > 0 )
        {
          max *= 10;
        }
        return margin + cellRenderer.getTableCellRendererComponent(parent, new Integer(max - 1), false, false, 0, 0)
                                    .getPreferredSize().width;
      }
      case CODE_COL:
      {
        int width = 0;
        for ( int i = 0; i < lines.size(); i++ )
        {
          width = Math.max(width, cellRenderer.getTableCellRendererComponent(parent, lines.get(i), false, false, 0, 0)
                                              .getPreferredSize().width);
        }
        return margin + width;
      }
      default:
        throw new IllegalArgumentException("Invalid column: " + col);
    }
  }

  public void breakpointChanged ( final BreakpointEvent e )
  {
    if ( e.getPosition().getSource() == source )
    {
      int line = e.getPosition().getLineNo();
      if ( line == SourcePosition.LAST_LINE )
      {
        line = lines.size();
      }
      fireTableCellUpdated(line - 1, INDICATOR_COL);
    }
  }

  public int getCurrentLine ()
  {
    return currentLine;
  }

  public void setStaleCurrentLine ( final boolean stale )
  {
    if ( currentLine != SourcePosition.NO_LINE )
    {
      final boolean oldStale = staleCurrentLine;
      staleCurrentLine = stale;
      if ( oldStale != stale )
      {
        fireTableCellUpdated(currentLine - 1, INDICATOR_COL);
      }
    }
  }

  public void setCurrentLine ( final int lineNo )
  {
    staleCurrentLine = false;
    final int oldLine = currentLine;

    if ( lineNo == SourcePosition.LAST_LINE )
    {
      currentLine = lines.size();
    }
    else
    {
      currentLine = lineNo;
    }

    if ( oldLine != currentLine )
    {
      if ( oldLine != SourcePosition.NO_LINE )
      {
        fireTableCellUpdated(oldLine - 1, INDICATOR_COL);
      }
      if ( currentLine != SourcePosition.NO_LINE )
      {
        fireTableCellUpdated(currentLine - 1, INDICATOR_COL);
      }
    }
  }
}
