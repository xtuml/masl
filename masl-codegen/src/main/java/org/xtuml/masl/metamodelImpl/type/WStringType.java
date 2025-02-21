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

public class WStringType extends BuiltinType {

    public static WStringType create(final Position position, final boolean anonymous) {
        return new WStringType(position, anonymous);
    }

    private static final WStringType ANON = new WStringType(null, true);

    public static WStringType createAnonymous() {
        return ANON;
    }

    private WStringType(final Position position, final boolean anonymous) {
        super(position, "wstring", anonymous);
    }

    @Override
    public BasicType getContainedType() {
        return isAnonymousType() ? WCharacterType.createAnonymous() : WCharacterType.create(null, false);
    }

    @Override
    public SequenceType getPrimitiveType() {
        return SequenceType.createAnonymous(getContainedType());
    }

    @Override
    public WStringType getBasicType() {
        return this;
    }

    @Override
    public ActualType getActualType() {
        return ActualType.WSTRING;
    }

    @Override
    public boolean isString() {
        return true;
    }

}
