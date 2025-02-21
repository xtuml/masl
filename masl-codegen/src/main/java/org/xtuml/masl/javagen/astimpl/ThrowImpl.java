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
import org.xtuml.masl.javagen.ast.code.Throw;
import org.xtuml.masl.javagen.ast.expr.Expression;

public class ThrowImpl extends StatementImpl implements Throw {

    public ThrowImpl(final ASTImpl ast, final ExpressionImpl thrownExpression) {
        super(ast);
        setThrownExpression(thrownExpression);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitThrow(this);
    }

    @Override
    public ExpressionImpl getThrownExpression() {
        return thrownExpression.get();
    }

    @Override
    public void setThrownExpression(final Expression thrownExpression) {
        this.thrownExpression.set((ExpressionImpl) thrownExpression);
    }

    private final ChildNode<ExpressionImpl> thrownExpression = new ChildNode<>(this);

}
