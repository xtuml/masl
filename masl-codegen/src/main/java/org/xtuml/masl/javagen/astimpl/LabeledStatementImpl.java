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

public class LabeledStatementImpl extends StatementImpl implements org.xtuml.masl.javagen.ast.code.LabeledStatement {

    public LabeledStatementImpl(final ASTImpl ast, final String name, final StatementImpl statement) {
        super(ast);

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitLabeledStatement(this);
    }

    @Override
    public StatementImpl getStatement() {
        return statement.get();
    }

    @Override
    public void setStatement(final Statement statement) {
        this.statement.set((StatementImpl) statement);
    }

    private final ChildNode<StatementImpl> statement = new ChildNode<>(this);
    private String name;
}
