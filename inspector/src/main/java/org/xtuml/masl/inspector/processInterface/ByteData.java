// 
// Filename : ByteData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public abstract class ByteData extends SimpleDataValue<Byte> {

    private static Byte ZERO = 0;

    @Override
    public Class<Byte> getValueClass() {
        return Byte.class;
    }

    @Override
    public void fromString(final String string) {
        setValue(Byte.decode(string));
    }

    @Override
    protected void setToDefaultValue() {
        setValue(ZERO);
    }
}
