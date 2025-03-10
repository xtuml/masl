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

public class TimerType extends BuiltinType {

    public static TimerType create(final Position position, final boolean anonymous) {
        return new TimerType(position, anonymous);
    }

    private static final TimerType ANON = new TimerType(null, true);

    public static TimerType createAnonymous() {
        return ANON;
    }

    private TimerType(final Position position, final boolean anonymous) {
        super(position, "timer", anonymous);
    }

    @Override
    public TimerType getPrimitiveType() {
        return ANON;
    }

    @Override
    public TimerType getBasicType() {
        return this;
    }

    @Override
    public ActualType getActualType() {
        return ActualType.TIMER;
    }

}
