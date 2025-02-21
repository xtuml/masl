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

public class RealType extends NumericType {

    public static RealType create(final Position position, final boolean anonymous) {
        return new RealType(position, anonymous);
    }

    private static final RealType ANON = new RealType(null, true);

    public static RealType createAnonymous() {
        return ANON;
    }

    private RealType(final Position position, final boolean anonymous) {
        super(position, "real", anonymous);
    }

    @Override
    public RealType getPrimitiveType() {
        return ANON;
    }

    @Override
    public RealType getBasicType() {
        return this;
    }

    @Override
    protected boolean isAssignableFromRelaxation(final BasicType rhs) {
        return rhs.isAnonymousType() && rhs instanceof IntegerType;
    }

    @Override
    protected boolean isConvertibleFromRelaxation(final BasicType rhs) {
        return rhs instanceof IntegerType;
    }

    @Override
    public ActualType getActualType() {
        return ActualType.REAL;
    }

}
