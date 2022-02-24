// 
// Filename : LongData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.math.BigInteger;

public abstract class LongNaturalData extends SimpleDataValue<BigInteger> {

    private static BigInteger ZERO = BigInteger.ZERO;

    @Override
    public void setValue(final BigInteger value) {
        if (value.signum() == -1) {
            throw new IllegalArgumentException("Long natural must be > 0");
        }
        super.setValue(value);
    }

    @Override
    public Class<BigInteger> getValueClass() {
        return BigInteger.class;
    }

    @Override
    public void fromString(final String string) {
        setValue(new BigInteger(string));
    }

    @Override
    protected void setToDefaultValue() {
        setValue(ZERO);
    }

}
