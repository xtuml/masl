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

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.expr.ClassLiteral;
import org.xtuml.masl.javagen.ast.types.Type;

public class ClassLiteralImpl extends ExpressionImpl implements ClassLiteral {

    public ClassLiteralImpl(final ASTImpl ast, final Type type) {
        super(ast);
        setType(type);

    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitClassLiteral(this);
    }

    @Override
    public TypeImpl getType() {
        return type.get();
    }

    @Override
    public TypeImpl setType(final Type type) {
        this.type.set((TypeImpl) type);
        return (TypeImpl) type;
    }

    @Override
    protected int getPrecedence() {
        return Integer.MAX_VALUE;
    }

    private final ChildNode<TypeImpl> type = new ChildNode<>(this);

}
