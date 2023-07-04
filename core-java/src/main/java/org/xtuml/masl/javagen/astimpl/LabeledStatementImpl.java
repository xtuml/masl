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
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitLabeledStatement(this, p);
    }

    @Override
    public StatementImpl getStatement() {
        return statement.get();
    }

    @Override
    public void setStatement(final Statement statement) {
        this.statement.set((StatementImpl) statement);
    }

    private final ChildNode<StatementImpl> statement = new ChildNode<StatementImpl>(this);
    private String name;
}
