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
import org.xtuml.masl.javagen.ast.expr.PrefixExpression;

class PrefixExpressionImpl extends ExpressionImpl implements PrefixExpression {

    PrefixExpressionImpl(final ASTImpl ast, final Operator operator, final Expression expression) {
        super(ast);
        setOperator(operator);
        setExpression(expression);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitPrefixExpression(this);
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
        return 14; // Java in a Nutshell Operator Summary Table
    }

    private final ChildNode<ExpressionImpl> expression = new ChildNode<>(this);

    Operator operator;
}
