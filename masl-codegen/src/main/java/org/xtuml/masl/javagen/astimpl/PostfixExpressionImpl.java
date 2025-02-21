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
import org.xtuml.masl.javagen.ast.expr.PostfixExpression;

class PostfixExpressionImpl extends ExpressionImpl implements PostfixExpression {

    PostfixExpressionImpl(final ASTImpl ast, final Expression expression, final Operator operator) {
        super(ast);
        setOperator(operator);
        setExpression(expression);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitPostfixExpression(this);
    }

    @Override
    public ExpressionImpl getExpression() {
        return expression.get();
    }

    @Override
    public Operator getOperator() {
        return operator;
    }

    @Override
    public ExpressionImpl setExpression(Expression expression) {
        if (((ExpressionImpl) expression).getPrecedence() < getPrecedence()) {
            expression = getAST().createParenthesizedExpression(expression);
        }

        this.expression.set((ExpressionImpl) expression);
        return (ExpressionImpl) expression;
    }

    @Override
    public void setOperator(final Operator operator) {
        this.operator = operator;
    }

    @Override
    protected int getPrecedence() {
        return 15; // Java in a Nutshell Operator Summary Table
    }

    private final ChildNode<ExpressionImpl> expression = new ChildNode<>(this);

    Operator operator;
}
