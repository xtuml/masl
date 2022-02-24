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

class CollectionTableModel extends AbstractTableModel {

    public CollectionTableModel(final CollectionData data, final boolean editable) {
        this.data = data;
        this.isStructure = data.getType().getCollectionDepth() == 0
                && data.getType().getBasicType() == TypeMetaData.BasicType.Structure;
        this.editable = editable;
    }

    private final CollectionData data;
    private final boolean isStructure;
    private final boolean editable;

    @Override
    public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
        if (columnIndex == 0) {

            final int oldRow = rowIndex;
            final int newRow = (Integer) value - 1;
            if (newRow == oldRow) {
                return;
            } else {
                while (data.getData().size() < newRow) {
                    data.getData().add(data.getType().getDataObject());
                }
                final DataValue<?> rowData = getRow(rowIndex);
                if (newRow > oldRow) {
                    data.getData().add(newRow, rowData);
                    data.getData().remove(oldRow);
                } else {
                    data.getData().remove(oldRow);
                    data.getData().add(newRow, rowData);
                }
                fireTableDataChanged();
            }
        } else {
            getAttribute(rowIndex, columnIndex - 1).setUncheckedValue(value);
        }
    }

    private TypeMetaData getAttributeType(final int attIndex) {
        if (isStructure) {
            return data.getType().getStructure().getAttributes()[attIndex].getType();
        } else {
            return data.getType();
        }

    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        if (columnIndex == 0) {
            return Integer.class;
        } else {
            final TypeMetaData attType = getAttributeType(columnIndex - 1);
            if (attType.getCollectionDepth() > 0) {
                return String.class;
            } else if (attType.getBasicType() == BasicType.Dictionary) {
                return String.class;
            } else {
                return attType.getDataObject().getValue().getClass();
            }
        }
    }

    @Override
    public int getColumnCount() {
        if (isStructure) {
            return 1 + data.getType().getStructure().getAttributes().length;
        } else {
            return 2;
        }
    }

    @Override
    public String getColumnName(final int columnIndex) {
        if (columnIndex == 0) {
            if (data.getLength() == 0) {
                return "[ ]";
            } else {
                return "[" + data.getStartIndex() + ".." + data.getEndIndex() + "]";
            }
        } else if (isStructure) {
            return data.getType().getStructure().getAttributes()[columnIndex - 1].getName();
        } else {
            return "value";
        }
    }

    @Override
    public int getRowCount() {
        return data.getLength();
    }

    public DataValue<?> getRow(final int rowIndex) {
        return data.getData().get(rowIndex);
    }

    public DataValue<?> getAttribute(final int rowIndex, final int attIndex) {
        if (isStructure) {
            return ((StructureData) getRow(rowIndex)).getAttributes()[attIndex];
        } else {
            return getRow(rowIndex);
        }

    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if (columnIndex == 0) {
            return (data.getStartIndex() + rowIndex);
        } else {
            final DataValue<?> att = getAttribute(rowIndex, columnIndex - 1);
            if (att instanceof CollectionData) {
                final CollectionData coll = (CollectionData) att;
                return "[" + coll.getStartIndex() + ".." + coll.getEndIndex() + "]";
            }
            if (att instanceof DictionaryData) {
                final DictionaryData dict = (DictionaryData) att;
                return "[" + dict.getData().size() + "]";
            } else {
                return att.getValue();
            }
        }
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        if (!editable) {
            return false;
        }

        if (columnIndex == 0) {
            return true;
        } else if (getAttributeType(columnIndex - 1).getCollectionDepth() > 0
                || getAttributeType(columnIndex - 1).getBasicType() == TypeMetaData.BasicType.Structure
                || getAttributeType(columnIndex - 1).getBasicType() == TypeMetaData.BasicType.Dictionary) {
            return false;
        } else {
            return true;
        }
    }

    public int getPreferredColumnWidth(final int col, final JTable parent) {
        final TableColumn column = parent.getColumnModel().getColumn(col);
        TableCellRenderer headerRenderer = column.getHeaderRenderer();
        if (headerRenderer == null) {
            headerRenderer = parent.getTableHeader().getDefaultRenderer();
        }

        final TableCellRenderer cellRenderer = parent.getCellRenderer(0, col);

        int headerWidth = 0;
        int cellWidth = 0;
        if (col == 0) {
            headerWidth = headerRenderer.getTableCellRendererComponent(parent, "[88..88]", false, false, 0, 0)
                    .getPreferredSize().width;
        } else {
            headerWidth = headerRenderer.getTableCellRendererComponent(parent, getColumnName(col), false, false, 0, 0)
                    .getPreferredSize().width;
            TypeMetaData cellType;
            if (data.getType().getCollectionDepth() == 0
                    && data.getType().getBasicType() == TypeMetaData.BasicType.Structure) {
                cellType = data.getType().getStructure().getAttributes()[col - 1].getType();
            } else {
                cellType = data.getType();
            }

            if (cellType.getBasicType() == TypeMetaData.BasicType.Enumeration) {
                for (final String name : cellType.getEnumerate().getNames()) {
                    cellWidth = Math.max(cellWidth, cellRenderer
                            .getTableCellRendererComponent(parent, name, false, false, 0, 0).getPreferredSize().width);
                }
            } else {
                cellWidth = cellRenderer
                        .getTableCellRendererComponent(parent, getWidthValue(cellType), false, false, 0, 0)
                        .getPreferredSize().width;
            }
        }

        return Math.max(headerWidth, cellWidth) + parent.getColumnModel().getColumnMargin();
    }

    Object getWidthValue(final TypeMetaData type) {
        if (type.getCollectionDepth() > 0) {
            return "[88..88]";
        }

        switch (type.getBasicType()) {
        case Boolean:
            return Boolean.FALSE;
        case Byte:
            return new Byte((byte) 200);
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

    public void addRow() {
        data.getData().add(data.getType().getDataObject());
        fireTableRowsInserted(data.getData().size() - 1, data.getData().size() - 1);
    }

    public void insertRow(final int position) {
        data.getData().add(position, data.getType().getDataObject());
        fireTableRowsInserted(position, position);
    }

    public void removeRow(final int position) {
        data.getData().remove(position);
        fireTableRowsDeleted(position, position);
    }

    public boolean isEditable() {
        return editable;
    }

}
