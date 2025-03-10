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

public class ByteType extends NumericType {

    public static ByteType create(final Position position, final boolean anonymous) {
        return new ByteType(position, anonymous);
    }

    private static final ByteType ANON = new ByteType(null, true);

    public static ByteType createAnonymous() {
        return ANON;
    }

    private ByteType(final Position position, final boolean anonymous) {
        super(position, "byte", false, 8, anonymous);
    }

    @Override
    public IntegerType getPrimitiveType() {
        return IntegerType.createAnonymous();
    }

    @Override
    public ByteType getBasicType() {
        return this;
    }

    @Override
    public ActualType getActualType() {
        return ActualType.BYTE;
    }

}
