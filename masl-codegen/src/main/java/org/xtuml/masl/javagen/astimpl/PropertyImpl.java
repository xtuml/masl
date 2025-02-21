/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.def.Field;
import org.xtuml.masl.javagen.ast.def.Method;
import org.xtuml.masl.javagen.ast.def.Property;

public class PropertyImpl implements Property {

    public PropertyImpl(final MethodImpl getter, final MethodImpl setter, final FieldImpl field) {
        this.getter = getter;
        this.setter = setter;
        this.field = field;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public Method getGetter() {
        return getter;
    }

    @Override
    public Method getSetter() {
        return setter;
    }

    private final MethodImpl getter;
    private final MethodImpl setter;
    private final FieldImpl field;

}
