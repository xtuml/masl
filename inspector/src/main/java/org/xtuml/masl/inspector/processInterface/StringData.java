//
// Filename : StringData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public abstract class StringData extends SimpleDataValue<String> {

    private static String EMPTY = new String();

    @Override
    public Class<String> getValueClass() {
        return String.class;
    }

    @Override
    public void fromString(final String string) {
        setValue(string);
    }

    @Override
    protected void setToDefaultValue() {
        setValue(EMPTY);
    }
}
