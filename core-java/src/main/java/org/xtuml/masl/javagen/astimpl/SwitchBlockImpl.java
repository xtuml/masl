/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
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
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitSwitchBlock(this, p);
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

    private final ChildNodeList<ExpressionImpl> caseLabels = new ChildNodeList<ExpressionImpl>(this);

    private final ChildNodeList<StatementImpl> statements = new ChildNodeList<StatementImpl>(this);
    private boolean isDefault;
}
