//
// Filename : DeviceData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class DeviceData extends DataValue<DeviceData> implements Comparable<DeviceData> {

    @Override
    public DeviceData getValue() {
        return this;
    }

    @Override
    public void setValue(final DeviceData value) {
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
        return "device";
    }

    @Override
    public int compareTo(final DeviceData o) {
        return 0;
    }
}
