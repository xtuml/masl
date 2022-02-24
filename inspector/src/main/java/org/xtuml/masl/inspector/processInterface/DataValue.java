// 
// Filename : AttributeData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.text.ParseException;

public abstract class DataValue<T> implements org.xtuml.masl.inspector.processInterface.XMLSerializable {

    @SuppressWarnings("unused")
    public void fromString(final String value) throws ParseException {
    }

    public abstract T getValue();

    public abstract void setValue(T value);

    @SuppressWarnings("unchecked")
    public void setUncheckedValue(final Object value) {
        setValue((T) value);
    }
}
