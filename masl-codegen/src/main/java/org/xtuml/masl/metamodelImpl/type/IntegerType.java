/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.common.Position;

public class IntegerType extends NumericType {

    public static IntegerType create(final Position position, final boolean anonymous) {
        return new IntegerType(position, anonymous);
    }

    private static final IntegerType ANON = new IntegerType(null, true);

    public static IntegerType createAnonymous() {
        return ANON;
    }

    private IntegerType(final Position position, final boolean anonymous) {
        super(position, "long_integer", true, 64, anonymous);
    }

    @Override
    public IntegerType getPrimitiveType() {
        return ANON;
    }

    @Override
    public IntegerType getBasicType() {
        return this;
    }

    @Override
    protected boolean isConvertibleFromRelaxation(final BasicType rhs) {
        return rhs instanceof RealType || rhs.getDefinedType() instanceof EnumerateType;
    }

    @Override
    public ActualType getActualType() {
        return ActualType.INTEGER;
    }

}
