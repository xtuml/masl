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

import org.xtuml.masl.javagen.ast.def.TypeBody;
import org.xtuml.masl.javagen.ast.expr.Expression;

public class MirroredEnumConstantImpl extends EnumConstantImpl {

    public MirroredEnumConstantImpl(final ASTImpl ast, final Enum<?> value) {
        super(ast);
        this.mirroredValue = value;
    }

    @Override
    public String getName() {
        return mirroredValue.name();
    }

    @Override
    public void setName(final String name) {
        throw new UnsupportedOperationException("Mirrored Enum Constant");
    }

    @Override
    public TypeBodyImpl setTypeBody(final TypeBody body) {
        throw new UnsupportedOperationException("Mirrored Enum Constant");
    }

    @Override
    public TypeBodyImpl setTypeBody() {
        throw new UnsupportedOperationException("Mirrored Enum Constant");
    }

    @Override
    public ExpressionImpl addArgument(final Expression arg) {
        throw new UnsupportedOperationException("Mirrored Enum Constant");

    }

    private final Enum<?> mirroredValue;

}
