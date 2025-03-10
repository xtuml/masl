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
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.ParenthesizedExpression;

class ParenthesizedExpressionImpl extends ExpressionImpl implements ParenthesizedExpression {

    ParenthesizedExpressionImpl(final ASTImpl ast, final Expression expression) {
        super(ast);
        setExpression(expression);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitParenthesizedExpression(this);
    }

    @Override
    public ExpressionImpl getExpression() {
        return expression.get();
    }

    @Override
    public ExpressionImpl setExpression(final Expression expression) {
        this.expression.set((ExpressionImpl) expression);
        return (ExpressionImpl) expression;
    }

    @Override
    protected int getPrecedence() {
        return Integer.MAX_VALUE;
    }

    private final ChildNode<ExpressionImpl> expression = new ChildNode<>(this);

}
