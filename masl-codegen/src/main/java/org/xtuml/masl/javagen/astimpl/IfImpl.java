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
import org.xtuml.masl.javagen.ast.code.If;
import org.xtuml.masl.javagen.ast.code.Statement;
import org.xtuml.masl.javagen.ast.expr.Expression;

import java.util.HashMap;
import java.util.Map;

public class IfImpl extends StatementImpl implements If {

    public IfImpl(final ASTImpl ast, final ExpressionImpl condition) {
        super(ast);
        setCondition(condition);
        setThen(ast.createCodeBlock());
    }

    @Override
    public ExpressionImpl getCondition() {
        return condition.get();
    }

    @Override
    public StatementImpl getElse() {
        return elseStatement.get();
    }

    @Override
    public StatementImpl getThen() {
        return thenStatement.get();
    }

    @Override
    public void setCondition(final Expression condition) {
        this.condition.set((ExpressionImpl) condition);
    }

    @Override
    public void setElse(final Statement elseStatement) {
        this.elseStatement.set((StatementImpl) elseStatement);
    }

    @Override
    public void setThen(final Statement thenStatement) {
        this.thenStatement.set((StatementImpl) thenStatement);
    }

    @Override
    public Map<ExpressionImpl, StatementImpl> getIfElseChainStatements() {
        final Map<ExpressionImpl, StatementImpl> blocks = new HashMap<>();
        blocks.put(getCondition(), getThen());
        IfImpl curIf = this;
        while (curIf.getElse() instanceof If) {
            curIf = (IfImpl) curIf.getElse();
            blocks.put(curIf.getCondition(), curIf.getThen());
        }
        if (curIf.getElse() != null) {
            blocks.put(null, curIf.getElse());
        }
        return blocks;
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitIf(this);
    }

    private final ChildNode<ExpressionImpl> condition = new ChildNode<>(this);
    private final ChildNode<StatementImpl> thenStatement = new ChildNode<>(this);
    private final ChildNode<StatementImpl> elseStatement = new ChildNode<>(this);

}
