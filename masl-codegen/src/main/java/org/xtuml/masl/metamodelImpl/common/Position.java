/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.common;

import java.util.IdentityHashMap;

public abstract class Position {

    public abstract String getText();

    public abstract String getContext();

    public abstract int getLineNumber();

    @Override
    public String toString() {
        return getText();
    }

    public static Position getPosition(final String key) {
        return positionLookup.get(key);
    }

    public static void registerPosition(final String key, final Position position) {
        positionLookup.put(key, position);
    }

    private static final IdentityHashMap<String, Position> positionLookup = new IdentityHashMap<>();

    public static Position NO_POSITION = null;
}
