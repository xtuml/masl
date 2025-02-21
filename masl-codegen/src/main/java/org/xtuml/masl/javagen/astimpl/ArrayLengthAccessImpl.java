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
import org.xtuml.masl.javagen.ast.expr.ArrayLengthAccess;
import org.xtuml.masl.javagen.ast.expr.Expression;

public class ArrayLengthAccessImpl extends ExpressionImpl implements ArrayLengthAccess {

    ArrayLengthAccessImpl(final ASTImpl ast, final Expression instance) {
        super(ast);
        setInstance(instance);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitArrayLengthAccess(this);
    }

    @Override
    public ExpressionImpl getInstance() {
        return instance.get();
    }

    @Override
    public ExpressionImpl setInstance(Expression instance) {
        if (((ExpressionImpl) instance).getPrecedence() < getPrecedence()) {
            instance = getAST().createParenthesizedExpression(instance);
        }
        this.instance.set((ExpressionImpl) instance);
        return (ExpressionImpl) instance;
    }

    @Override
    protected int getPrecedence() {
        return 15;
    }

    private final ChildNode<ExpressionImpl> instance = new ChildNode<>(this);

}
