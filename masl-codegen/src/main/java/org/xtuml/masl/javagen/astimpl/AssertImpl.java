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
import org.xtuml.masl.javagen.ast.code.Assert;
import org.xtuml.masl.javagen.ast.expr.Expression;

public class AssertImpl extends StatementImpl implements Assert {

    public AssertImpl(final ASTImpl ast, final ExpressionImpl condition) {
        super(ast);
        setCondition(condition);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitAssert(this);
    }

    @Override
    public ExpressionImpl getCondition() {
        return condition.get();
    }

    @Override
    public ExpressionImpl getMessage() {
        return message.get();
    }

    @Override
    public void setCondition(final Expression condition) {
        this.condition.set((ExpressionImpl) condition);
    }

    @Override
    public void setMessage(final Expression message) {
        this.message.set((ExpressionImpl) message);
    }

    private final ChildNode<ExpressionImpl> message = new ChildNode<>(this);
    private final ChildNode<ExpressionImpl> condition = new ChildNode<>(this);
}
