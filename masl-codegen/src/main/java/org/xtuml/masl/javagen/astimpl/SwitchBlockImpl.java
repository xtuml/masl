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
import org.xtuml.masl.javagen.ast.code.BlockStatement;
import org.xtuml.masl.javagen.ast.code.Switch.SwitchBlock;
import org.xtuml.masl.javagen.ast.expr.Expression;

import java.util.Collections;
import java.util.List;

public class SwitchBlockImpl extends ASTNodeImpl implements SwitchBlock {

    public SwitchBlockImpl(final ASTImpl ast) {
        super(ast);

    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitSwitchBlock(this);
    }

    @Override
    public void addCaseLabel(final Expression caseLabel) {
        this.caseLabels.add((ExpressionImpl) caseLabel);
    }

    @Override
    public void addStatement(final BlockStatement statement) {
        this.statements.add((StatementImpl) statement);
    }

    @Override
    public List<? extends ExpressionImpl> getCaseLabels() {
        return Collections.unmodifiableList(caseLabels);
    }

    @Override
    public List<? extends StatementImpl> getStatements() {
        return Collections.unmodifiableList(statements);
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public void setDefault() {
        this.isDefault = true;
    }

    private final ChildNodeList<ExpressionImpl> caseLabels = new ChildNodeList<>(this);

    private final ChildNodeList<StatementImpl> statements = new ChildNodeList<>(this);
    private boolean isDefault;
}
