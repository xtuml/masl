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

import org.xtuml.masl.javagen.ast.expr.ArrayInitializer;
import org.xtuml.masl.javagen.ast.expr.Expression;

abstract class TypeImpl extends ASTNodeImpl implements org.xtuml.masl.javagen.ast.types.Type {

    TypeImpl(final ASTImpl ast) {
        super(ast);
    }

    @Override
    public NewArrayImpl newArray(final int noDimensions, final ArrayInitializer initialValue) {
        return getAST().createNewArray(this, noDimensions, initialValue);
    }

    @Override
    public NewArrayImpl newArray(final int noDimensions, final Expression... dimensionSizes) {
        return getAST().createNewArray(this, noDimensions, dimensionSizes);
    }

    @Override
    public CastImpl cast(final Expression expression) {
        return getAST().createCast(this, expression);
    }

    @Override
    public ClassLiteralImpl clazz() {
        return getAST().createClassLiteral(this);
    }

    abstract public TypeImpl deepCopy();

}
