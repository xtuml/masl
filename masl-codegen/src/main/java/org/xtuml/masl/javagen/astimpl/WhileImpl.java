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
import org.xtuml.masl.javagen.ast.code.Statement;
import org.xtuml.masl.javagen.ast.code.While;
import org.xtuml.masl.javagen.ast.expr.Expression;

public class WhileImpl extends StatementImpl implements While {

    public WhileImpl(final ASTImpl ast, final ExpressionImpl condition) {
        super(ast);
        statement.set(ast.createCodeBlock());
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitWhile(this);
    }

    @Override
    public ExpressionImpl getCondition() {
        return condition.get();
    }

    @Override
    public StatementImpl getStatement() {
        return statement.get();
    }

    @Override
    public void setCondition(final Expression condition) {
        this.condition.set((ExpressionImpl) condition);
    }

    @Override
    public void setStatement(final Statement statement) {
        this.statement.set((StatementImpl) statement);
    }

    private final ChildNode<ExpressionImpl> condition = new ChildNode<>(this);

    private final ChildNode<StatementImpl> statement = new ChildNode<>(this);
}
