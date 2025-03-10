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

public class BooleanType extends BuiltinType {

    public static BooleanType create(final Position position, final boolean anonymous) {
        return new BooleanType(position, anonymous);
    }

    private static final BooleanType ANON = new BooleanType(null, true);

    public static BooleanType createAnonymous() {
        return ANON;
    }

    private BooleanType(final Position position, final boolean anonymous) {
        super(position, "boolean", anonymous);
    }

    @Override
    public BooleanType getPrimitiveType() {
        return this;
    }

    @Override
    public BooleanType getBasicType() {
        return this;
    }

    @Override
    public ActualType getActualType() {
        return ActualType.BOOLEAN;
    }

}
