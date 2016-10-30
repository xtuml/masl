//
// File: CollectionModel.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.inspector.gui;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.xtuml.masl.inspector.processInterface.CollectionData;
import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.processInterface.DictionaryData;
import org.xtuml.masl.inspector.processInterface.StructureData;
import org.xtuml.masl.inspector.processInterface.TypeMetaData;
import org.xtuml.masl.inspector.processInterface.TypeMetaData.BasicType;


class DictionaryTableModel extends AbstractTableModel
{

  public DictionaryTableModel ( final DictionaryData data, final boolean editable )
  {
    this.data = data;
    this.isStructure = data.getValueType().getCollectionDepth() == 0
                       && data.getValueType().getBasicType() == TypeMetaData.BasicType.Structure;
    this.editable = editable;
  }

  private final DictionaryData data;
  private final boolean        isStructure;
  private final boolean        editable;
  private DataValue<?>[]       keyArray = null;

  @Override
  public void setValueAt ( final Object value, final int rowIndex, final int columnIndex )
  {
    if ( columnIndex == 0 )
    {
      final DataValue<?> newKey = (DataValue<?>)value;
      final DataValue<?> oldKey = getKey(rowIndex);
      final DataValue<?> oldValue = getValue(rowIndex);
      data.getData().remove(oldKey);
      data.getData().put(newKey, oldValue);
      keyArray = null;

      fireTableDataChanged();
    }
    else
    {
      getAttribute(rowIndex, columnIndex - 1).setUncheckedValue(value);
    }
  }

  DataValue<?> getKey ( final int index )
  {
    if ( keyArray == null )
    {
      keyArray = data.getData().keySet().toArray(new DataValue<?>[0]);
    }
    return keyArray[index];
  }

  DataValue<?> getValue ( final int index )
  {
    return data.getData().get(keyArray[index]);
  }


  private TypeMetaData getAttributeType ( final int attIndex )
  {
    if ( isStructure )
    {
      return data.getValueType().getStructure().getAttributes()[attIndex].getType();
    }
    else
    {
      return data.getValueType();
    }

  }

  @Override
  public Class<?> getColumnClass ( final int columnIndex )
  {
    if ( columnIndex == 0 )
    {
      return data.getKeyType().getDataObject().getValue().getClass();
    }
    else
    {
      final TypeMetaData attType = getAttributeType(columnIndex - 1);
      if ( attType.getCollectionDepth() > 0 )
      {
        return String.class;
      }
      else if ( attType.getBasicType() == BasicType.Dictionary )
      {
        return String.class;
      }
      else
      {
        return attType.getDataObject().getValue().getClass();
      }
    }
  }

  public int getColumnCount ()
  {
    if ( isStructure )
    {
      return 1 + data.getValueType().getStructure().getAttributes().length;
    }
    else
    {
      return 2;
    }
  }

  @Override
  public String getColumnName ( final int columnIndex )
  {
    if ( columnIndex == 0 )
    {
      return "key";
    }
    else if ( isStructure )
    {
      return data.getValueType().getStructure().getAttributes()[columnIndex - 1].getName();
    }
    else
    {
      return "value";
    }
  }

  public int getRowCount ()
  {
    return data.getData().size();
  }

  public DataValue<?> getAttribute ( final int rowIndex, final int attIndex )
  {
    if ( isStructure )
    {
      return ((StructureData)getValue(rowIndex)).getAttributes()[attIndex];
    }
    else
    {
      return getValue(rowIndex);
    }

  }

  public Object getValueAt ( final int rowIndex, final int columnIndex )
  {
    if ( columnIndex == 0 )
    {
      return getKey(rowIndex);
    }
    else
    {
      final DataValue<?> att = getAttribute(rowIndex, columnIndex - 1);
      if ( att instanceof CollectionData )
      {
        final CollectionData coll = (CollectionData)att;
        return "[" + coll.getStartIndex() + ".." + coll.getEndIndex() + "]";
      }
      else if ( att instanceof DictionaryData )
      {
        final DictionaryData dict = (DictionaryData)att;
        return "[" + dict.getData().size() + "]";
      }
      else
      {
        return att.getValue();
      }
    }
  }

  @Override
  public boolean isCellEditable ( final int rowIndex, final int columnIndex )
  {
    if ( !editable )
    {
      return false;
    }

    if ( columnIndex == 0 )
    {
      return true;
    }
    else if ( getAttributeType(columnIndex - 1).getCollectionDepth() > 0
              || getAttributeType(columnIndex - 1).getBasicType() == TypeMetaData.BasicType.Structure
              || getAttributeType(columnIndex - 1).getBasicType() == TypeMetaData.BasicType.Dictionary )
    {
      return false;
    }
    else
    {
      return true;
    }
  }

  public int getPreferredColumnWidth ( final int col, final JTable parent )
  {
    final TableColumn column = parent.getColumnModel().getColumn(col);
    TableCellRenderer headerRenderer = column.getHeaderRenderer();
    if ( headerRenderer == null )
    {
      headerRenderer = parent.getTableHeader().getDefaultRenderer();
    }

    final TableCellRenderer cellRenderer = parent.getCellRenderer(0, col);

    final int headerWidth = headerRenderer.getTableCellRendererComponent(parent, getColumnName(col), false, false, 0, 0)
                                          .getPreferredSize().width;
    int cellWidth = 0;
    TypeMetaData cellType;
    if ( col == 0 )
    {
      cellType = data.getKeyType();
    }
    else
    {
      if ( data.getValueType().getCollectionDepth() == 0 && data.getValueType().getBasicType() == TypeMetaData.BasicType.Structure )
      {
        cellType = data.getValueType().getStructure().getAttributes()[col - 1].getType();
      }
      else
      {
        cellType = data.getValueType();
      }
    }

    if ( cellType.getBasicType() == TypeMetaData.BasicType.Enumeration )
    {
      for ( final String name : cellType.getEnumerate().getNames() )
      {
        cellWidth = Math.max(cellWidth, cellRenderer.getTableCellRendererComponent(parent, name, false, false, 0, 0)
                                                    .getPreferredSize().width);
      }
    }
    else
    {
      cellWidth = cellRenderer.getTableCellRendererComponent(parent, getWidthValue(cellType), false, false, 0, 0)
                              .getPreferredSize().width;
    }

    return Math.max(headerWidth, cellWidth) + parent.getColumnModel().getColumnMargin();
  }

  Object getWidthValue ( final TypeMetaData type )
  {
    if ( type.getCollectionDepth() > 0 )
    {
      return "[88..88]";
    }

    switch ( type.getBasicType() )
    {
      case Boolean:
        return Boolean.FALSE;
      case Byte:
        return new Byte((byte)200);
      case WCharacter:
      case Character:
        return new Character('W');
      case Integer:
      case LongInteger:
      case LongNatural:
      case Natural:
        return new Integer(88888888);
      case Real:
        return new Double(888.8888);
      case Dictionary:
        return "[88]";
      default:
        return "WWWWWWWWWWWWWWW";
    }
  }

  public void addRow ()
  {
    data.getData().put(data.getKeyType().getDataObject(), data.getValueType().getDataObject());
    keyArray = null;
    fireTableDataChanged();
  }

  public void removeRow ( final int position )
  {
    final DataValue<?> oldKey = getKey(position);
    data.getData().remove(oldKey);
    keyArray = null;
    fireTableRowsDeleted(position, position);
  }

  public boolean isEditable ()
  {
    return editable;
  }


}
