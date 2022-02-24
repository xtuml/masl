// 
// Filename : DeviceData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class AnyEventData extends DataValue<AnyEventData> implements Comparable<AnyEventData> {

    @Override
    public AnyEventData getValue() {
        return this;
    }

    @Override
    public void setValue(final AnyEventData value) {
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
        return "event";
    }

    @Override
    public int compareTo(final AnyEventData o) {
        return 0;
    }
}
