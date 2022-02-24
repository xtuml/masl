// 
// Filename : DeviceData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class AnyInstanceData extends DataValue<AnyInstanceData> implements Comparable<AnyInstanceData> {

    @Override
    public AnyInstanceData getValue() {
        return this;
    }

    @Override
    public void setValue(final AnyInstanceData value) {
    }

    @Override
    public Node toXML(final Document document) {
        return document.createDocumentFragment();
    }

    @Override
    public void fromXML(final Node parent) {
    }

    @Override
    public String toString() {
        return isValid() ? "instance" : "null";
    }

    @Override
    public int compareTo(final AnyInstanceData o) {
        return 0;
    }

    public abstract boolean isValid();
}
