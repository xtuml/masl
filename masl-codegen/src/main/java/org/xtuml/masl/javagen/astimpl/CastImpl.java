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
import org.xtuml.masl.javagen.ast.expr.Cast;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.types.Type;

class CastImpl extends ExpressionImpl implements Cast {

    CastImpl(final ASTImpl ast, final Type type, final Expression expression) {
        super(ast);
        setType(type);
        setExpression(expression);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitCast(this);
    }

    @Override
    public ExpressionImpl getExpression() {
        return expression.get();
    }

    @Override
    public TypeImpl getType() {
        return type.get();
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
    public TypeImpl setType(final Type type) {
        this.type.set((TypeImpl) type);
        return (TypeImpl) type;
    }

    @Override
    protected int getPrecedence() {
        // From operator precedence table in Java in a Nutshell
        return 13;
    }

    private final ChildNode<TypeImpl> type = new ChildNode<>(this);
    private final ChildNode<ExpressionImpl> expression = new ChildNode<>(this);
}
