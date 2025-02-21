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
import org.xtuml.masl.javagen.ast.code.For;
import org.xtuml.masl.javagen.ast.code.LocalVariable;
import org.xtuml.masl.javagen.ast.code.Statement;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.StatementExpression;

import java.util.List;

public class ForImpl extends StatementImpl implements For {

    public ForImpl(final ASTImpl ast) {
        super(ast);
        setStatement(ast.createCodeBlock());
    }

    public ForImpl(final ASTImpl ast,
                   final StatementExpression start,
                   final Expression condition,
                   final StatementExpression update) {
        super(ast);
        addStartExpression(start);
        setCondition(condition);
        addUpdateExpression(update);
        setStatement(ast.createCodeBlock());
    }

    public ForImpl(final ASTImpl ast,
                   final LocalVariable variable,
                   final Expression condition,
                   final StatementExpression update) {
        super(ast);
        setVariable(variable);
        setCondition(condition);
        addUpdateExpression(update);
        setStatement(ast.createCodeBlock());
    }

    public ForImpl(final ASTImpl ast, final LocalVariable variable, final Expression collection) {
        super(ast);
        setVariable(variable);
        setCollection(collection);
        setStatement(ast.createCodeBlock());
    }

    @Override
    public void addStartExpression(final StatementExpression expression) {
        starts.add((ExpressionImpl) expression);
    }

    @Override
    public void addUpdateExpression(final StatementExpression expression) {
        updates.add((ExpressionImpl) expression);
    }

    @Override
    public Expression getCondition() {
        return condition.get();
    }

    @Override
    public Expression getCollection() {
        return collection.get();
    }

    @Override
    public List<? extends StatementExpression> getStartExpressions() {
        return starts;
    }

    @Override
    public LocalVariable getVariable() {
        return variable.get();
    }

    @Override
    public Statement getStatement() {
        return code.get();
    }

    @Override
    public List<? extends StatementExpression> getUpdateExpressions() {
        return updates;
    }

    @Override
    public void setCollection(final Expression collection) {
        this.collection.set((ExpressionImpl) collection);
    }

    @Override
    public void setCondition(final Expression condition) {
        this.condition.set((ExpressionImpl) condition);
    }

    @Override
    public void setVariable(final LocalVariable variable) {
        this.variable.set((LocalVariableImpl) variable);
    }

    @Override
    public void setStatement(final Statement statement) {
        this.code.set((StatementImpl) statement);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitFor(this);
    }

    private final ChildNode<StatementImpl> code = new ChildNode<>(this);
    private final ChildNode<ExpressionImpl> condition = new ChildNode<>(this);
    private final ChildNode<LocalVariableImpl> variable = new ChildNode<>(this);
    private final ChildNode<ExpressionImpl> collection = new ChildNode<>(this);
    private final ChildNodeList<ExpressionImpl> updates = new ChildNodeList<>(this);
    private final ChildNodeList<ExpressionImpl> starts = new ChildNodeList<>(this);

}
