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

public class DeviceType extends BuiltinType {

    public static DeviceType create(final Position position, final boolean anonymous) {
        return new DeviceType(position, anonymous);
    }

    private static final DeviceType ANON = new DeviceType(null, true);

    public static DeviceType createAnonymous() {
        return ANON;
    }

    private DeviceType(final Position position, final boolean anonymous) {
        super(position, "device", anonymous);
    }

    @Override
    public DeviceType getPrimitiveType() {
        return ANON;
    }

    @Override
    public DeviceType getBasicType() {
        return this;
    }

    @Override
    public ActualType getActualType() {
        return ActualType.DEVICE;
    }

}
