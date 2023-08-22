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
import org.xtuml.masl.javagen.ast.code.ConstructorInvocation;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.types.ReferenceType;

import java.util.Collections;
import java.util.List;

public class ConstructorInvocationImpl extends StatementImpl implements ConstructorInvocation {

    public ConstructorInvocationImpl(final ASTImpl ast, final boolean isSuper, final Expression[] args) {
        super(ast);
        this.isSuper = isSuper;
        for (final Expression arg : args) {
            addArgument(arg);
        }
    }

    public ConstructorInvocationImpl(final ASTImpl ast,
                                     final Expression enclosingInstance,
                                     final boolean isSuper,
                                     final Expression[] args) {
        super(ast);
        this.setEnclosingInstance(enclosingInstance);
        this.isSuper = isSuper;
        for (final Expression arg : args) {
            addArgument(arg);
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitConstructorInvocation(this);
    }

    @Override
    public ExpressionImpl addArgument(final Expression argument) {
        this.arguments.add((ExpressionImpl) argument);
        return (ExpressionImpl) argument;
    }

    @Override
    public List<? extends ExpressionImpl> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public boolean isSuper() {
        return isSuper;
    }

    @Override
    public void setSuper() {
        isSuper = true;
    }

    private boolean isSuper;

    private final ChildNodeList<ExpressionImpl> arguments = new ChildNodeList<>(this);

    @Override
    public ReferenceTypeImpl addTypeArgument(final ReferenceType argument) {
        typeArguments.add((ReferenceTypeImpl) argument);
        return (ReferenceTypeImpl) argument;
    }

    private final List<ReferenceTypeImpl> typeArguments = new ChildNodeList<>(this);

    @Override
    public ExpressionImpl getEnclosingInstance() {
        return enclosingInstance.get();
    }

    @Override
    public List<? extends ReferenceType> getTypeArguments() {
        return Collections.unmodifiableList(typeArguments);
    }

    @Override
    public ExpressionImpl setEnclosingInstance(final Expression instance) {
        this.enclosingInstance.set((ExpressionImpl) instance);
        return (ExpressionImpl) instance;
    }

    private final ChildNode<ExpressionImpl> enclosingInstance = new ChildNode<>(this);

}
