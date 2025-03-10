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
import org.xtuml.masl.javagen.ast.code.Return;
import org.xtuml.masl.javagen.ast.expr.Expression;

public class ReturnImpl extends StatementImpl implements Return {

    public ReturnImpl(final ASTImpl ast, final ExpressionImpl thrownExpression) {
        super(ast);
        setReturnValue(thrownExpression);
    }

    public ReturnImpl(final ASTImpl ast) {
        super(ast);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitReturn(this);
    }

    @Override
    public ExpressionImpl getReturnValue() {
        return returnValue.get();
    }

    @Override
    public void setReturnValue(final Expression returnValue) {
        this.returnValue.set((ExpressionImpl) returnValue);
    }

    private final ChildNode<ExpressionImpl> returnValue = new ChildNode<>(this);

}
