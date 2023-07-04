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
import org.xtuml.masl.javagen.ast.code.LocalVariable;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.types.Type;

public class LocalVariableImpl extends VariableImpl implements LocalVariable {

    LocalVariableImpl(final ASTImpl ast, final Type type, final String name) {
        super(ast, type, name);
    }

    LocalVariableImpl(final ASTImpl ast, final Type type, final String name, final Expression initialValue) {
        super(ast, type, name);
        setInitialValue(initialValue);
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitLocalVariable(this, p);
    }

    private final ChildNode<ExpressionImpl> initialValue = new ChildNode<ExpressionImpl>(this);

    @Override
    public Expression getInitialValue() {
        return initialValue.get();
    }

    @Override
    public void setInitialValue(final Expression initialValue) {
        this.initialValue.set((ExpressionImpl) initialValue);
    }

    @Override
    public VariableDeclarationStatementImpl asStatement() {
        return getAST().createVariableDeclaration(this);
    }

}
