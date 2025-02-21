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
import org.xtuml.masl.javagen.ast.expr.StatementExpression;

public class ExpressionStatementImpl extends StatementImpl
        implements org.xtuml.masl.javagen.ast.code.ExpressionStatement {

    ExpressionStatementImpl(final ASTImpl ast, final StatementExpression expression) {
        super(ast);
        setExpression(expression);
    }

    @Override
    public ExpressionImpl getExpression() {
        return expression.get();
    }

    @Override
    public void setExpression(final StatementExpression expression) {
        this.expression.set((ExpressionImpl) expression);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitExpressionStatement(this);
    }

    private final ChildNode<ExpressionImpl> expression = new ChildNode<>(this);

}
