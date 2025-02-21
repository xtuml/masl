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

public class WCharacterType extends BuiltinType {

    public static WCharacterType create(final Position position, final boolean anonymous) {
        return new WCharacterType(position, anonymous);
    }

    private static final WCharacterType ANON = new WCharacterType(null, true);

    public static WCharacterType createAnonymous() {
        return ANON;
    }

    private WCharacterType(final Position position, final boolean anonymous) {
        super(position, "wcharacter", anonymous);
    }

    @Override
    public WCharacterType getPrimitiveType() {
        return this;
    }

    @Override
    public WCharacterType getBasicType() {
        return this;
    }

    @Override
    public ActualType getActualType() {
        return ActualType.WCHARACTER;
    }

    @Override
    public boolean isCharacter() {
        return true;
    }

}
