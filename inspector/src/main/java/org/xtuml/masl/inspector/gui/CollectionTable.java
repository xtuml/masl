//
// File: CollectionTable.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.inspector.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

import org.xtuml.masl.inspector.processInterface.CollectionData;
import org.xtuml.masl.inspector.processInterface.EnumerateData;
import org.xtuml.masl.inspector.processInterface.EnumerateMetaData;


class CollectionTable extends JTable
{

  static class IndexRenderer extends DefaultTableCellRenderer
  {

    public IndexRenderer ()
    {
      setBackground(Color.LIGHT_GRAY);
      setHorizontalAlignment(SwingConstants.RIGHT);
    }

    @Override
    public Component getTableCellRendererComponent ( final JTable table,
                                                     final Object value,
                                                     final boolean isSelected,
                                                     final boolean hasFocus,
                                                     final int row,
                                                     final int column )
    {
      return super.getTableCellRendererComponent(table, "[" + value + "]", isSelected, hasFocus, row, column);
    }

  }


  public CollectionTable ( final CollectionData data, final boolean editable )
  {
    this(new CollectionTableModel(data, editable));
  }

  public CollectionTable ( final CollectionTableModel model )
  {
    super(model);
    addEditors();
    this.model = model;
    setShowHorizontalLines(true);
    setShowVerticalLines(true);
    setPreferredScrollableViewportSize(new Dimension(450, 80));
    calcSize();
    doLayout();
    setSize(getPreferredSize());
    getColumnModel().getColumn(0).setCellRenderer(new IndexRenderer());
    addMenus();
    putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
  }

  @Override
  public Component prepareEditor ( final TableCellEditor editor, final int row, final int column )
  {
    final Component comp = super.prepareEditor(editor, row, column);
    if ( comp instanceof JTextComponent )
    {
      final JTextComponent textArea = (JTextComponent)comp;
      final Border defaultBorder = textArea.getBorder();

      textArea.setInputVerifier(new InputVerifier()
      {

        @Override
        public boolean shouldYieldFocus ( final JComponent component )
        {
          if ( verify(component) )
          {
            textArea.setBorder(defaultBorder);
            final String text = ((JTextField)component).getText();
            try
            {
              getModel().setValueAt(getModel().getColumnClass(column).getConstructor(String.class).newInstance(text), row, column);
              textArea.setText(getModel().getValueAt(row, column).toString());
              textArea.setToolTipText(null);
            }
            catch ( final Exception e )
            {
              textArea.setBorder(new LineBorder(Color.red));
              textArea.setToolTipText(e.getMessage());
              return false;
            }
            return true;
          }
          else
          {
            textArea.setBorder(new LineBorder(Color.red));
            return false;
          }

        }

        @Override
        public boolean verify ( final JComponent component )
        {
          try
          {
            final String text = ((JTextField)component).getText();
            getModel().getColumnClass(column).getConstructor(String.class).newInstance(text);
            textArea.setToolTipText(null);
            return true;
          }
          catch ( final Exception e )
          {
            textArea.setToolTipText(e.getMessage());
            return false;
          }
        }

      });
    }
    return comp;

  }


  private class EnumerateEditor extends DefaultCellEditor
  {

    private final JComboBox comboBox;

    public EnumerateEditor ()
    {
      super(new JComboBox());
      comboBox = (JComboBox)getComponent();
    }

    @Override
    public Component getTableCellEditorComponent ( final JTable table,
                                                   final Object value,
                                                   final boolean isSelected,
                                                   final int row,
                                                   final int column )
    {
      final EnumerateMetaData metaData = ((EnumerateData)getModel().getValueAt(row, column)).getMetaData();
      comboBox.setModel(new DefaultComboBoxModel(metaData.getEnums().toArray()));
      comboBox.setSelectedItem(getModel().getValueAt(row, column));
      return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
  }


  private void addEditors ()
  {
    setDefaultEditor(EnumerateData.class, new EnumerateEditor());
  }

  private void addMenus ()
  {
    final JPopupMenu rowPopup = new JPopupMenu();
    class TableMouseHandler extends MouseAdapter
    {

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
            rowPopup.show(e.getComponent(), e.getX(), e.getY());
          }
        }
        super.mousePressed(e);
      }
    }
    addMouseListener(new TableMouseHandler());


    if ( model.isEditable() )
    {

      final JMenuItem addRow = new JMenuItem("Add row");
      final JPopupMenu tablePopup = new JPopupMenu();

      tablePopup.add(addRow);
      addRow.addActionListener(new ActionListener()
      {

        public void actionPerformed ( final ActionEvent action )
        {
          model.addRow();
        }
      });
      final JMenuItem insertRow = new JMenuItem("Insert row");
      rowPopup.add(insertRow);
      insertRow.addActionListener(new ActionListener()
      {

        public void actionPerformed ( final ActionEvent action )
        {
          model.insertRow(getSelectedRow());
        }
      });
      final JMenuItem editRow = new JMenuItem("Edit row");
      rowPopup.add(editRow);
      editRow.addActionListener(new ActionListener()
      {

        public void actionPerformed ( final ActionEvent action )
        {
          final EditDataDialog dialog = new EditDataDialog(model.getRow(getSelectedRow()), model.isEditable());
          dialog.display();
          model.fireTableRowsUpdated(getSelectedRow(), getSelectedRow());
        }
      });
      final JMenuItem removeRow = new JMenuItem("Remove element");
      rowPopup.add(removeRow);
      removeRow.addActionListener(new ActionListener()
      {

        public void actionPerformed ( final ActionEvent action )
        {
          model.removeRow(getSelectedRow());
        }
      });

      class HeaderMouseHandler extends MouseAdapter
      {

        @Override
        public void mousePressed ( final MouseEvent e )
        {
          if ( e.isPopupTrigger() )
          {
            tablePopup.show(e.getComponent(), e.getX(), e.getY());
          }
          super.mousePressed(e);
        }
      }
      headerMouseAdapter = new HeaderMouseHandler();
      getTableHeader().addMouseListener(headerMouseAdapter);
    }
    else
    {

      final JMenuItem editRow = new JMenuItem("View row");
      rowPopup.add(editRow);
      editRow.addActionListener(new ActionListener()
      {

        public void actionPerformed ( final ActionEvent action )
        {
          final EditDataDialog dialog = new EditDataDialog(model.getRow(getSelectedRow()), model.isEditable());
          dialog.display();
          model.fireTableRowsUpdated(getSelectedRow(), getSelectedRow());
        }
      });
    }
  }

  private void calcSize ()
  {
    int totalWidth = 0;
    for ( int i = 0; i < model.getColumnCount(); i++ )
    {
      final int width = model.getPreferredColumnWidth(i, this);
      totalWidth += width;
      getColumnModel().getColumn(i).setPreferredWidth(width);
      getColumnModel().getColumn(i).setMinWidth(1);
      if ( i == 0 )
      {
        getColumnModel().getColumn(i).setMaxWidth(width);
      }
    }
    setPreferredScrollableViewportSize(new Dimension(totalWidth, 80));
  }

  @Override
  public void tableChanged ( final TableModelEvent e )
  {
    super.tableChanged(e);
    if ( e.getType() == TableModelEvent.INSERT
         || e.getType() == TableModelEvent.DELETE
         || e.getColumn() == TableModelEvent.ALL_COLUMNS )
    {
      if ( getColumnModel() != null && getTableHeader() != null )
      {
        getColumnModel().getColumn(0).setHeaderValue(model.getColumnName(0));
        getTableHeader().repaint(getTableHeader().getHeaderRect(0));
      }
    }
  }

  private final CollectionTableModel model;

  private MouseAdapter               headerMouseAdapter;

  public MouseAdapter getHeaderMouseAdapter ()
  {
    return headerMouseAdapter;
  }
}
