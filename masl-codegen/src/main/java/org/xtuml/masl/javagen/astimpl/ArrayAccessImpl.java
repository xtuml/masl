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
import org.xtuml.masl.javagen.ast.expr.ArrayAccess;
import org.xtuml.masl.javagen.ast.expr.Expression;

class ArrayAccessImpl extends ExpressionImpl implements ArrayAccess {

    ArrayAccessImpl(final ASTImpl ast, final Expression arrayExpression, final Expression indexExpression) {
        super(ast);
        setArrayExpression(arrayExpression);
        setIndexExpression(indexExpression);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitArrayAccess(this);
    }

    @Override
    public ExpressionImpl getArrayExpression() {
        return arrayExpression.get();
    }

    @Override
    public ExpressionImpl getIndexExpression() {
        return indexExpression.get();
    }

    @Override
    public ExpressionImpl setArrayExpression(Expression expression) {
        if (((ExpressionImpl) expression).getPrecedence() < getPrecedence()) {
            expression = getAST().createParenthesizedExpression(expression);
        }
        this.arrayExpression.set((ExpressionImpl) expression);
        return (ExpressionImpl) expression;
    }

    @Override
    public ExpressionImpl setIndexExpression(Expression expression) {
        // Left Associative, so need to parenthesize rhs if equal precedence
        if (((ExpressionImpl) expression).getPrecedence() <= getPrecedence()) {
            expression = getAST().createParenthesizedExpression(expression);
        }
        this.indexExpression.set((ExpressionImpl) expression);
        return (ExpressionImpl) expression;
    }

    @Override
    protected int getPrecedence() {
        // Values from Java in a Nutshell Operator Summary Table
        return 15;
    }

    private final ChildNode<ExpressionImpl> arrayExpression = new ChildNode<>(this);

    private final ChildNode<ExpressionImpl> indexExpression = new ChildNode<>(this);

}
