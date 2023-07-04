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
import org.xtuml.masl.javagen.ast.def.TypeBody;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.NewInstance;
import org.xtuml.masl.javagen.ast.types.DeclaredType;
import org.xtuml.masl.javagen.ast.types.ReferenceType;

import java.util.Collections;
import java.util.List;

class NewInstanceImpl extends ExpressionImpl implements NewInstance {

    NewInstanceImpl(final ASTImpl ast, final DeclaredType instanceType, final Expression... args) {
        super(ast);
        setInstanceType(instanceType);
        for (final Expression arg : args) {
            addArgument(arg);
        }
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitNewInstance(this, p);
    }

    @Override
    public ExpressionImpl addArgument(final Expression argument) {
        this.arguments.add((ExpressionImpl) argument);
        return (ExpressionImpl) argument;
    }

    @Override
    public ReferenceTypeImpl addTypeArgument(final ReferenceType typeArgument) {
        this.typeArguments.add((ReferenceTypeImpl) typeArgument);
        return (ReferenceTypeImpl) typeArgument;
    }

    @Override
    public List<? extends ExpressionImpl> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public DeclaredTypeImpl getInstanceType() {
        return instanceType.get();
    }

    @Override
    public ExpressionImpl getOuterInstance() {
        return outerInstance.get();
    }

    @Override
    public List<? extends ReferenceTypeImpl> getTypeArguments() {
        return Collections.unmodifiableList(typeArguments);
    }

    @Override
    public TypeBodyImpl getTypeBody() {
        return typeBody.get();
    }

    @Override
    public DeclaredTypeImpl setInstanceType(final DeclaredType instanceType) {
        this.instanceType.set((DeclaredTypeImpl) instanceType);
        return (DeclaredTypeImpl) instanceType;
    }

    @Override
    public ExpressionImpl setOuterInstance(Expression outerInstance) {
        if (((ExpressionImpl) outerInstance).getPrecedence() < getPrecedence()) {
            outerInstance = getAST().createParenthesizedExpression(outerInstance);
        }
        this.outerInstance.set((ExpressionImpl) outerInstance);
        return (ExpressionImpl) outerInstance;
    }

    @Override
    public TypeBody setTypeBody() {
        return setTypeBody(getAST().createTypeBody());
    }

    @Override
    public TypeBodyImpl setTypeBody(final TypeBody typeBody) {
        this.typeBody.set((TypeBodyImpl) typeBody);
        return (TypeBodyImpl) typeBody;
    }

    @Override
    protected int getPrecedence() {
        return 13;
    }

    private final ChildNodeList<ExpressionImpl> arguments = new ChildNodeList<ExpressionImpl>(this);
    private final ChildNodeList<ReferenceTypeImpl> typeArguments = new ChildNodeList<ReferenceTypeImpl>(this);
    private final ChildNode<TypeBodyImpl> typeBody = new ChildNode<TypeBodyImpl>(this);
    private final ChildNode<DeclaredTypeImpl> instanceType = new ChildNode<DeclaredTypeImpl>(this);
    private final ChildNode<ExpressionImpl> outerInstance = new ChildNode<ExpressionImpl>(this);
}
