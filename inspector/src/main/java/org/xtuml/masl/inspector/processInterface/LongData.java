//
// Filename : LongData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public abstract class LongData extends SimpleDataValue<Long> {

    private static Long ZERO = 0l;

    @Override
    public Class<Long> getValueClass() {
        return Long.class;
    }

    @Override
    public void fromString(final String string) {
        setValue(Long.decode(string));
    }

    @Override
    protected void setToDefaultValue() {
        setValue(ZERO);
    }

}
