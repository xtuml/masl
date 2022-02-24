// 
// Filename : EnumerateData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xtuml.masl.inspector.socketConnection.ObjectMetaData;
import org.xtuml.masl.inspector.socketConnection.StateMetaData;

public abstract class StateData extends DataValue<StateMetaData> implements Comparable<StateData> {

    protected StateData(final ObjectMetaData object) {
        this.object = object;
        this.state = object.getStates()[0];
    }

    @Override
    public int compareTo(final StateData o) {
        return getStateName().compareTo(o.getStateName());
    }

    @Override
    public void fromXML(final Node parent) {
        setStateName(parent.getFirstChild().getNodeValue());
    }

    public ObjectMetaData getObject() {
        return object;
    }

    public String getStateName() {
        return state.getName();
    }

    @Override
    public StateMetaData getValue() {
        return state;
    }

    public void setStateName(final String name) {
        state = object.getState(name);
    }

    @Override
    public void setValue(final StateMetaData value) {
        state = value;
    }

    @Override
    public String toString() {
        return getStateName();
    }

    @Override
    public Node toXML(final Document document) {
        return document.createTextNode(getStateName());
    }

    private final ObjectMetaData object;

    private StateMetaData state;

}
