// 
// Filename : AttributeData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class SimpleDataValue<T extends Comparable<T>> extends DataValue<T>
        implements Comparable<SimpleDataValue<T>> {

    private T value;

    public SimpleDataValue() {
        setToDefaultValue();
    }

    @Override
    public abstract void fromString(String value);

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(final T value) {
        this.value = value;
    }

    public abstract Class<T> getValueClass();

    protected abstract void setToDefaultValue();

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int compareTo(final SimpleDataValue<T> rhs) {
        return value.compareTo(rhs.value);
    }

    @Override
    public Node toXML(final Document document) {
        return document.createTextNode(value.toString());
    }

    @Override
    public void fromXML(final Node parent) {
        if (parent != null && parent.getFirstChild() != null) {
            fromString(parent.getFirstChild().getNodeValue());
        } else {
            setToDefaultValue();
        }
    }

}
