// 
// Filename : LocalVarNode.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.inspector.processInterface.CollectionData;
import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.processInterface.DictionaryData;
import org.xtuml.masl.inspector.processInterface.EventData;
import org.xtuml.masl.inspector.processInterface.InstanceData;
import org.xtuml.masl.inspector.processInterface.InstanceIdData;
import org.xtuml.masl.inspector.processInterface.LocalVarData;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.StringData;
import org.xtuml.masl.inspector.processInterface.StructureData;
import org.xtuml.masl.inspector.processInterface.TimerData;

public class LocalVarNode {

    private final String name;
    private final Object value;
    private final int index;

    private LocalVarNode[] children;

    LocalVarNode(final int index, final String name, final Object value) {
        this.index = index;
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        if (value instanceof LocalVarData[]) {
            return "Local Variables";
        } else if (value instanceof StructureData) {
            return name;
        } else if (value instanceof InstanceIdData) {
            final InstanceIdData data = (InstanceIdData) value;
            if (data.getId() == null) {
                return name + " = null";
            } else {
                return name + " = " + data.getMetaData().getName() + " "
                        + data.getMetaData().getInstanceIdentifier(data.getInstanceData());
            }
        } else if (value instanceof TimerData) {
            final TimerData data = (TimerData) value;
            return name + " = " + data;
        } else if (value instanceof EventData) {
            final EventData data = (EventData) value;
            return name + " = " + data.getEvent().getFullyQualifiedName();
        } else if (value instanceof CollectionData) {
            if (((CollectionData) value).getLength() == 0) {
                return name + " = []";
            } else {
                return name + " = [" + ((CollectionData) value).getStartIndex() + ".."
                        + ((CollectionData) value).getEndIndex() + "]";
            }
        } else if (value instanceof DictionaryData) {
            return name + " = [" + ((DictionaryData) value).getData().size() + "]";
        } else if (value instanceof StringData) {
            return name + " = '" + value + "'";
        } else {
            return name + " = " + value;
        }
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    public LocalVarNode getChild(final int index) {
        if (children == null) {
            children = new LocalVarNode[getChildCount()];
        }

        LocalVarNode child = children[index];
        if (child == null) {
            if (value instanceof LocalVarData[]) {
                child = new LocalVarNode(index, ((LocalVarData[]) value)[index].getName(),
                        ((LocalVarData[]) value)[index].getValue());
            } else if (value instanceof StructureData) {
                child = new LocalVarNode(index, ((StructureData) value).getMetaData().getAttributes()[index].getName(),
                        ((StructureData) value).getAttributes()[index]);
            } else if (value instanceof CollectionData) {
                child = new LocalVarNode(index, "[" + (index + ((CollectionData) value).getStartIndex()) + "]",
                        ((CollectionData) value).getData().get(index));
            } else if (value instanceof DictionaryData) {
                final DataValue<?> key = new ArrayList<DataValue<?>>(((DictionaryData) value).getData().keySet())
                        .get(index);
                child = new LocalVarNode(index, "[" + key + "]", ((DictionaryData) value).getData().get(key));
            } else if (value instanceof TimerData) {
                final TimerData timer = ((TimerData) value);

                final int expiryPos = 0;
                final int periodPos = expiryPos + (timer.getPeriod() == null ? 0 : 1);
                final int eventPos = periodPos + 1;

                if (index == expiryPos) {
                    return new LocalVarNode(index, "expiry", timer.getExpiryTime());
                } else if (index == periodPos) {
                    return new LocalVarNode(index, "period", timer.getPeriod());
                } else if (index == eventPos) {
                    return new LocalVarNode(index, "event", timer.getEventData());
                }
            } else if (value instanceof EventData) {
                final EventData event = (EventData) value;
                final int sourcePos = event.getSourceInstanceId() == null ? -1 : 0;
                final int destPos = sourcePos + (event.getDestInstanceId() == null ? 0 : 1);
                final int paramPos = destPos + 1;

                if (index == sourcePos) {
                    return new LocalVarNode(index, "source", event.getSourceInstanceId());
                } else if (index == destPos) {
                    return new LocalVarNode(index, "destination", event.getDestInstanceId());
                } else {
                    return new LocalVarNode(index, event.getEvent().getParameters()[index - paramPos].getName(),
                            event.getParameters()[index - paramPos]);
                }

            } else if (value instanceof InstanceIdData) {
                final ObjectMetaData meta = ((InstanceIdData) value).getMetaData();
                final InstanceData data = ((InstanceIdData) value).getInstanceData();

                if (index < meta.getAttributes().length) {
                    child = new LocalVarNode(index, meta.getAttributes()[index].getName(), data.getAttributes()[index]);
                } else {
                    child = new LocalVarNode(index, "Current State",
                            meta.getStates()[data.getCurrentState()].getName());
                }
            }
            children[index] = child;
        }
        return child;
    }

    public LocalVarNode getChild(final String name) {
        // Using a hash map for the search would be less
        // efficient, because this search is only done for
        // expanded nodes on each refresh and the hashmap would
        // have to be constructed for every node in the tree on
        // every refresh.
        int i = 0;
        final int max = getChildCount();
        while (i < max) {
            final LocalVarNode child = getChild(i++);
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    public int getChildCount() {
        if (value instanceof LocalVarData[]) {
            return ((LocalVarData[]) value).length;
        } else if (value instanceof StructureData) {
            return ((StructureData) value).getAttributes().length;
        } else if (value instanceof CollectionData) {
            return ((CollectionData) value).getData().size();
        } else if (value instanceof DictionaryData) {
            return ((DictionaryData) value).getData().size();
        } else if (value instanceof TimerData) {
            final TimerData data = ((TimerData) value);
            if (data.isScheduled()) {
                return data.getPeriod() == null ? 2 : 3;
            } else {
                return 0;
            }
        } else if (value instanceof EventData) {
            final EventData data = ((EventData) value);
            int result = data.getParameters().length;

            if (data.getSourceInstanceId() != null) {
                ++result;
            }
            if (data.getDestInstanceId() != null) {
                ++result;
            }
            return result;
        } else if (value instanceof InstanceIdData) {
            if (((InstanceIdData) value).getId() != null) {
                final ObjectMetaData meta = ((InstanceIdData) value).getMetaData();
                return meta.getAttributes().length + (meta.isActive() ? 1 : 0);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public List<Object> getContainedInstances() {
        final List<Object> list = new ArrayList<Object>();
        if (value instanceof InstanceIdData) {
            if (((InstanceIdData) value).getId() != null) {
                list.add(value);
            }
        } else if (value instanceof CollectionData) {
            final int max = getChildCount();
            for (int i = 0; i < max; i++) {
                list.addAll(getChild(i).getContainedInstances());
            }
        } else if (value instanceof DictionaryData) {
            final int max = getChildCount();
            for (int i = 0; i < max; i++) {
                list.addAll(getChild(i).getContainedInstances());
            }
        }
        return list;
    }

    public boolean isLeaf() {
        return !(value instanceof LocalVarData[] || value instanceof StructureData || value instanceof DictionaryData
                || value instanceof CollectionData || value instanceof InstanceIdData || value instanceof TimerData
                || value instanceof EventData);
    }

}
