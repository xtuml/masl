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
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitNewInstance(this);
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

    private final ChildNodeList<ExpressionImpl> arguments = new ChildNodeList<>(this);
    private final ChildNodeList<ReferenceTypeImpl> typeArguments = new ChildNodeList<>(this);
    private final ChildNode<TypeBodyImpl> typeBody = new ChildNode<>(this);
    private final ChildNode<DeclaredTypeImpl> instanceType = new ChildNode<>(this);
    private final ChildNode<ExpressionImpl> outerInstance = new ChildNode<>(this);
}
