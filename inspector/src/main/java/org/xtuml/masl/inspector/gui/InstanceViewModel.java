//
// Filename : InstanceViewModel.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.xtuml.masl.inspector.processInterface.AttributeMetaData;
import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.processInterface.InstanceData;
import org.xtuml.masl.inspector.processInterface.InstanceDataListener;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.ObjectRelationshipMetaData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.StateMetaData;
import org.xtuml.masl.inspector.processInterface.TypeMetaData;

class InstanceViewModel extends com.jrefinery.ui.SortableTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    enum ColumnType {
        PK, ATTRIBUTE, CURRENT_STATE, RELATIONSHIP
    }

    class InstanceComparator implements Comparator<InstanceData> {

        public InstanceComparator(final int col, final boolean ascending) {
            type = getColumnType(col);
            if (type == ColumnType.ATTRIBUTE) {
                attNo = getAttributeNo(col);
                relNo = -1;
            } else if (type == ColumnType.RELATIONSHIP) {
                attNo = -1;
                relNo = getRelationshipNo(col);
            } else {
                attNo = -1;
                relNo = -1;
            }
            order = ascending ? 1 : -1;
        }

        @Override
        @SuppressWarnings("unchecked")
        public int compare(final InstanceData o1, final InstanceData o2) {
            Object f1 = null;
            Object f2 = null;

            switch (type) {
            case ATTRIBUTE:
                f1 = o1.getAttributes()[attNo];
                f2 = o2.getAttributes()[attNo];
                break;
            case RELATIONSHIP:
                f1 = Integer.valueOf(o1.getRelCounts()[relNo]);
                f2 = Integer.valueOf(o2.getRelCounts()[relNo]);
                break;
            case CURRENT_STATE:
                f1 = o1.getCurrentState();
                f2 = o2.getCurrentState();
                break;
            case PK:
                f1 = o1.getPrimaryKey();
                f2 = o2.getPrimaryKey();
                break;
            }

            if (f1 == null && f2 == null) {
                return 0;
            } else if (f1 == null) {
                return -order;
            } else if (f2 == null) {
                return order;
            } else if (f1 instanceof Comparable) {
                return ((Comparable) f1).compareTo(f2) * order;
            } else {
                return f1.toString().compareTo(f2.toString()) * order;
            }

        }

        final int attNo;

        final int relNo;

        final int order;

        private final ColumnType type;

    }

    class InstanceListenerThread extends Thread implements InstanceDataListener {

        @Override
        public boolean addInstanceData(final InstanceData instance) {
            if (instance != null) {
                data.add(instance);
                lastRow = data.size();
            }
            Thread.yield();
            return cancelLoad;
        }

        @Override
        public void finished() {
            finished = true;
        }

        @Override
        public void run() {
            setName(getTitle() + " Instance Data Listener");
            try {
                while (!finished) {
                    if (lastRow >= firstRow) {
                        SwingUtilities.invokeAndWait(new Runnable() {

                            @Override
                            public void run() {
                                final int localLast = lastRow;
                                rowCount = localLast;
                                fireTableRowsInserted(firstRow, localLast);
                                firstRow = localLast + 1;
                            }
                        });
                    }
                    Thread.sleep(100);
                }

                instanceCount = data.size();
                if (data.size() >= firstRow) {
                    SwingUtilities.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {
                            rowCount = data.size();
                            fireTableRowsInserted(firstRow, data.size());
                        }
                    });
                }

                if (getSortingColumn() >= 0) {
                    Collections.sort(data, new InstanceComparator(getSortingColumn(), getAscending()));
                    SwingUtilities.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {
                            fireTableRowsUpdated(0, data.size());
                        }
                    });
                }

                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        fireUpdateComplete();
                    }
                });

            } catch (final InterruptedException e) {
            } catch (final java.lang.reflect.InvocationTargetException e) {
            }
            refreshing = false;
        }

        @Override
        public void setInstanceCount(final int count) {
            if (count >= 0) {
                instanceCount = count;
                data = new Vector<InstanceData>(count);
            } else {
                data = new Vector<InstanceData>();
            }
            start();
        }

        private int firstRow = 0;

        private volatile int lastRow = -1;

        private volatile boolean finished = false;

    }

    InstanceViewModel(final ObjectMetaData meta) {
        this(meta, null);
    }

    InstanceViewModel(final ObjectMetaData sourceMeta, final Object pk, final int relNo) {
        super();
        metaRel = sourceMeta.getRelationships()[relNo];
        metaObj = metaRel.getDestObject();
        this.sourceMeta = sourceMeta;
        sourcePK = pk;
        sourcePKs = null;
        sourceRel = relNo;

        loadData();
    }

    InstanceViewModel(final ObjectMetaData meta, final Object[] pks) {
        super();
        metaObj = meta;
        metaRel = null;
        sourceMeta = null;
        sourcePK = null;
        sourcePKs = pks;
        sourceRel = -1;

        loadData();
    }

    public void addUpdateListener(final UpdateListener l) {
        listenerList.add(UpdateListener.class, l);
    }

    public void cancelLoad() {
        cancelLoad = true;
    }

    public void fireUpdateComplete() {
        // Guaranteed to return a non-null array
        final Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == UpdateListener.class) {
                ((UpdateListener) listeners[i + 1]).updateComplete();
            }
        }
    }

    public void fireUpdateStarted() {
        // Guaranteed to return a non-null array
        final Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == UpdateListener.class) {
                ((UpdateListener) listeners[i + 1]).updateStarted();
            }
        }
    }

    public int getAttributeNo(final int col) {
        return col < metaObj.getAttributes().length + 1 ? col - 1 : -1;
    }

    @Override
    public Class<?> getColumnClass(final int col) {
        switch (getColumnType(col)) {
        case ATTRIBUTE:
            return metaObj.getAttributes()[getAttributeNo(col)].getType().getDataObject().getValue().getClass();
        case RELATIONSHIP:
            return String.class;
        case CURRENT_STATE:
            return String.class;
        case PK:
            return metaObj.getPKClass();
        default:
            throw new IllegalStateException("Unrecognised column type");
        }
    }

    @Override
    public int getColumnCount() {
        return 1 + metaObj.getAttributes().length + (metaObj.isActive() ? 1 : 0) + metaObj.getRelationships().length;
    }

    @Override
    public String getColumnName(final int col) {
        switch (getColumnType(col)) {
        case ATTRIBUTE: {
            final AttributeMetaData attribute = metaObj.getAttributes()[getAttributeNo(col)];
            if (attribute.isReferential()) {
                return attribute.getName() + " (" + attribute.getRelationshipText() + ")";
            } else {
                return attribute.getName();
            }
        }
        case RELATIONSHIP: {
            final ObjectRelationshipMetaData rel = metaObj.getRelationships()[getRelationshipNo(col)];
            return rel.getNumber() + " : " + rel.getDescription();
        }
        case CURRENT_STATE:
            return "Current State";
        case PK:
            return "Id";
        default:
            throw new IllegalStateException("Unrecognised column type");
        }

    }

    public InstanceData getInstanceAt(final int row) {
        return data.get(row);
    }

    public int getInstanceCount() {
        return instanceCount;
    }

    public String getInstanceName(final int row) {
        return metaObj.getInstanceIdentifier(data.get(row));
    }

    public ObjectMetaData getMetaObject() {
        return metaObj;
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

        switch (getColumnType(col)) {
        case ATTRIBUTE:
            headerWidth = headerRenderer.getTableCellRendererComponent(parent, getColumnName(col), false, false, 0, 0)
                    .getPreferredSize().width;

            final TypeMetaData.BasicType type = metaObj.getAttributes()[getAttributeNo(col)].getType().getBasicType();
            if (type == TypeMetaData.BasicType.Enumeration) {
                for (final String name : metaObj.getAttributes()[getAttributeNo(col)].getType().getEnumerate()
                        .getNames()) {
                    cellWidth = Math.max(cellWidth, cellRenderer
                            .getTableCellRendererComponent(parent, name, false, false, 0, 0).getPreferredSize().width);
                }
            } else {
                cellWidth = cellRenderer.getTableCellRendererComponent(parent, getWidthValue(type), false, false, 0, 0)
                        .getPreferredSize().width;
            }
            break;
        case RELATIONSHIP:
            headerWidth = headerRenderer
                    .getTableCellRendererComponent(parent,
                            metaObj.getRelationships()[getRelationshipNo(col)].getNumber(), false, false, 0, 0)
                    .getPreferredSize().width;

            cellWidth = cellRenderer
                    .getTableCellRendererComponent(parent, new String("\u21e2 99999"), false, false, 0, 0)
                    .getPreferredSize().width;
            break;
        case CURRENT_STATE:
            for (final StateMetaData state : metaObj.getStates()) {
                cellWidth = Math.max(cellWidth,
                        cellRenderer.getTableCellRendererComponent(parent, state.getName(), false, false, 0, 0)
                                .getPreferredSize().width);
            }
            break;
        case PK:
            headerWidth = headerRenderer.getTableCellRendererComponent(parent, getColumnName(col), false, false, 0, 0)
                    .getPreferredSize().width;
            cellWidth = cellRenderer.getTableCellRendererComponent(parent, Integer.valueOf(10000), false, false, 0, 0)
                    .getPreferredSize().width;
            break;
        default:
            throw new IllegalStateException("Unrecognised column type");
        }

        return Math.max(headerWidth, cellWidth) + parent.getColumnModel().getColumnMargin();
    }

    public Integer getPrimaryKey(final int row) {
        return data.get(row).getPrimaryKey();
    }

    public Collection<Object> getPrimaryKeys(final int[] rows) {
        final Collection<Object> pks = new HashSet<Object>(rows.length);

        for (final int row : rows) {
            pks.add(data.get(row).getPrimaryKey());
        }
        return pks;
    }

    public int getRelationshipNo(final int col) {
        return col - metaObj.getAttributes().length - 1 - (metaObj.isActive() ? 1 : 0);
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    public Collection<Integer> getRowsByPrimaryKey(final Collection<Object> pks) {
        final Collection<Integer> rows = new HashSet<Integer>(pks.size());

        for (int row = 0; row < data.size(); row++) {
            if (pks.contains(data.get(row).getPrimaryKey())) {
                pks.remove(data.get(row).getPrimaryKey());
                rows.add(Integer.valueOf(row));
            }
        }
        return rows;
    }

    public String getTitle() {
        String title;

        if (metaRel == null) {
            title = metaObj.getDomain().getName() + "::" + metaObj.getName();
            if (sourcePKs != null) {
                if (sourcePKs.length == 1) {
                    InstanceData instance = null;
                    try {
                        instance = ProcessConnection.getConnection().getInstanceData(metaObj, sourcePKs[0]);
                    } catch (final java.rmi.RemoteException e) {
                        e.printStackTrace();
                    }
                    title += " " + metaObj.getInstanceIdentifier(instance);
                } else {
                    title += " (Filtered)";
                }
            }
        } else {
            InstanceData sourceInstance = null;
            try {
                sourceInstance = ProcessConnection.getConnection().getInstanceData(sourceMeta, sourcePK);
            } catch (final java.rmi.RemoteException e) {
                e.printStackTrace();
            }

            final String id = sourceInstance == null ? "!! Deleted !!"
                    : sourceMeta.getInstanceIdentifier(sourceInstance);

            title = sourceMeta.getDomain().getName() + "::" + sourceMeta.getName() + " " + id + " "
                    + metaRel.getDescription() + " (" + metaRel.getNumber() + ")";
        }

        return title;
    }

    @Override
    public Object getValueAt(final int row, final int col) {

        switch (getColumnType(col)) {
        case ATTRIBUTE: {
            final DataValue<?> att = data.get(row).getAttributes()[getAttributeNo(col)];
            if (att == null) {
                return null;
            } else {
                return att.getValue();
            }

        }
        case RELATIONSHIP:
            return "\u21e2 " + data.get(row).getRelCounts()[getRelationshipNo(col)];
        case CURRENT_STATE:
            return metaObj.getStates()[data.get(row).getCurrentState()].getName();
        case PK:
            return data.get(row).getPrimaryKey();
        default:
            throw new IllegalStateException("Unrecognised column type");
        }
    }

    @Override
    public boolean isCellEditable(final int row, final int col) {
        return false;
    }

    @Override
    public boolean isSortable(final int col) {
        return true;
    }

    public void refreshAll() {
        loadData();
    }

    public void refreshRow(final int row) {
        try {
            data.set(row, ProcessConnection.getConnection().getInstanceData(metaObj, data.get(row).getPrimaryKey()));

            if (data.get(row) == null) {
                data.remove(row);
                --instanceCount;
                --rowCount;
                fireTableRowsDeleted(row, row);
            } else {
                fireTableRowsUpdated(row, row);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void removeUpdateListener(final UpdateListener l) {
        listenerList.remove(UpdateListener.class, l);
    }

    @Override
    public void sortByColumn(final int col, boolean ascending) {
        // Force sort ascending if column has changed
        if (col != sortingColumn) {
            ascending = true;
            this.ascending = true;
        }

        super.sortByColumn(col, ascending);

        try {
            fireUpdateStarted();
            Collections.sort(data, new InstanceComparator(col, ascending));
            fireTableDataChanged();
            fireUpdateComplete();
        } catch (final ConcurrentModificationException e) { /* Ignore and let loader sort when complete */
        }
    }

    public void writeRows(final int[] rows, final java.io.PrintWriter out) {
        for (int c = 0; c < getColumnCount(); c++) {
            out.print(getColumnName(c));
            if (c == getColumnCount() - 1) {
                out.print("\n");
            } else {
                out.print("\t");
            }
        }

        for (final int r : rows) {
            for (int c = 0; c < getColumnCount(); c++) {
                out.print(getValueAt(r, c));
                if (c == getColumnCount() - 1) {
                    out.print("\n");
                } else {
                    out.print("\t");
                }
            }
        }
    }

    public void writeTable(final java.io.PrintWriter out) {
        for (int c = 0; c < getColumnCount(); c++) {
            out.print(getColumnName(c));
            if (c == getColumnCount() - 1) {
                out.print("\n");
            } else {
                out.print("\t");
            }
        }

        for (int r = 0; r < getRowCount(); r++) {
            for (int c = 0; c < getColumnCount(); c++) {
                out.print(getValueAt(r, c));
                if (c == getColumnCount() - 1) {
                    out.print("\n");
                } else {
                    out.print("\t");
                }
            }
        }
    }

    Object getWidthValue(final TypeMetaData.BasicType type) {
        switch (type) {
        case Boolean:
            return Boolean.FALSE;
        case Byte:
            return Byte.valueOf((byte) 200);
        case WCharacter:
        case Character:
            return Character.valueOf('W');
        case Integer:
        case LongInteger:
        case LongNatural:
        case Natural:
            return Integer.valueOf(8888888);
        case Real:
            return Double.valueOf(888.888888);
        default:
            return "WWWWWWWWWWWWWWW";
        }
    }

    private ColumnType getColumnType(final int col) {
        if (col == 0) {
            return ColumnType.PK;
        } else if (col - 1 < metaObj.getAttributes().length) {
            return ColumnType.ATTRIBUTE;
        } else if (metaObj.isActive() && (col == metaObj.getAttributes().length + 1)) {
            return ColumnType.CURRENT_STATE;
        } else {
            return ColumnType.RELATIONSHIP;
        }
    }

    private void loadData() {
        if (refreshing) {
            System.out.println("Refresh already in progress");
            return;
        }
        refreshing = true;

        fireUpdateStarted();
        cancelLoad = false;
        rowCount = 0;
        instanceCount = -1;
        data = null;

        final InstanceDataListener listener = new InstanceListenerThread();

        final Thread loader = new Thread(getTitle() + " Data Loader") {

            @Override
            public void run() {
                try {
                    if (sourceMeta == null) {
                        if (sourcePKs == null) {
                            ProcessConnection.getConnection().getInstanceData(metaObj, listener);
                        } else {
                            ProcessConnection.getConnection().getInstanceData(metaObj, sourcePKs, listener);
                        }
                    } else {
                        ProcessConnection.getConnection().getRelatedInstanceData(sourceMeta, sourcePK, sourceRel,
                                listener);
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        };

        loader.start();

    }

    protected EventListenerList listenerList = new EventListenerList();

    private final ObjectMetaData metaObj;

    private final ObjectMetaData sourceMeta;

    private final ObjectRelationshipMetaData metaRel;

    private Vector<InstanceData> data; // Need
    // thread
    // safety

    private final Object sourcePK;

    private final Object[] sourcePKs;

    private final int sourceRel;

    private volatile int rowCount;

    private volatile int instanceCount;

    private volatile boolean cancelLoad;

    private volatile boolean refreshing;

}
