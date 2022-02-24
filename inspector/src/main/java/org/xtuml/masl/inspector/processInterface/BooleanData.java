//
// Filename : BooleanData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public abstract class BooleanData extends SimpleDataValue<Boolean> {

    @Override
    public Class<Boolean> getValueClass() {
        return Boolean.class;
    }

    @Override
    public void fromString(final String string) {
        setValue(Boolean.valueOf(string) || Integer.valueOf(string) != 0);
    }

    @Override
    protected void setToDefaultValue() {
        setValue(Boolean.FALSE);
    }

}
