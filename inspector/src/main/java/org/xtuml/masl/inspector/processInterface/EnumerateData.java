//
// Filename : EnumerateData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.text.ParseException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class EnumerateData extends DataValue<EnumerateData> implements Comparable<EnumerateData> {

    @Override
    public EnumerateData getValue() {
        return this;
    }

    @Override
    public void setValue(final EnumerateData value) {
        index = value.index;
    }

    protected final EnumerateMetaData meta;
    protected int index = 0;

    public EnumerateData(final EnumerateMetaData meta) {
        this.meta = meta;
    }

    public EnumerateMetaData getMetaData() {
        return meta;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(final int value) {
        this.index = value;
    }

    public String getName() {
        return meta.decodeNameFromIndex(index);
    }

    @Override
    public Node toXML(final Document document) {
        return document.createTextNode(meta.decodeNameFromIndex(index));
    }

    @Override
    public void fromXML(final Node parent) {
        index = meta.decodeIndexFromName(parent.getFirstChild().getNodeValue());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void fromString(final String value) throws ParseException {
        this.index = meta.decodeIndexFromName(value);
    }

    @Override
    public int compareTo(final EnumerateData o) {
        return (index == o.index) ? 0 : ((index < o.index) ? -1 : 1);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        result = prime * result + ((meta == null) ? 0 : meta.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EnumerateData)) {
            return false;
        }
        final EnumerateData other = (EnumerateData) obj;
        if (index != other.index) {
            return false;
        }
        if (meta == null) {
            if (other.meta != null) {
                return false;
            }
        } else if (!meta.equals(other.meta)) {
            return false;
        }
        return true;
    }

}
