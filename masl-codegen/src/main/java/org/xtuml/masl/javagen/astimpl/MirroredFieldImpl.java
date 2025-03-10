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

import org.xtuml.masl.javagen.ast.types.Type;

class MirroredFieldImpl extends FieldImpl {

    MirroredFieldImpl(final ASTImpl ast, final java.lang.reflect.Field field) {
        super(ast);
        super.setType(ast.createType(field.getGenericType()));
        super.setName(field.getName());
        getModifiers().setModifiers(field.getModifiers());
    }

    @Override
    public void setName(final String name) {
        throw new UnsupportedOperationException("Mirrored Field");
    }

    @Override
    public TypeImpl setType(final Type type) {
        throw new UnsupportedOperationException("Mirrored Field");
    }

    boolean mirrorPopulated = false;
}
