//
// Filename : ShortData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public abstract class ShortData extends SimpleDataValue<Short> {

    private static Short ZERO = 0;

    @Override
    public Class<Short> getValueClass() {
        return Short.class;
    }

    @Override
    public void fromString(final String string) {
        setValue(Short.decode(string));
    }

    @Override
    protected void setToDefaultValue() {
        setValue(ZERO);
    }
}
