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
import org.xtuml.masl.javagen.ast.def.EnumConstant;
import org.xtuml.masl.javagen.ast.expr.EnumConstantAccess;
import org.xtuml.masl.javagen.ast.expr.TypeQualifier;

public class EnumConstantAccessImpl extends ExpressionImpl implements EnumConstantAccess {

    public EnumConstantAccessImpl(final ASTImpl ast, final EnumConstant constant) {
        super(ast);
        setConstant(constant);
    }

    @Override
    protected int getPrecedence() {
        return 15;
    }

    @Override
    public void forceQualifier() {
        if (qualifier.get() == null) {
            setQualifier(new TypeQualifierImpl(getAST(), constant.getDeclaringType()));
        }

    }

    @Override
    public EnumConstantImpl getConstant() {
        return constant;
    }

    @Override
    public EnumConstant setConstant(final EnumConstant constant) {
        return this.constant = (EnumConstantImpl) constant;
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitEnumConstantAccess(this);
    }

    private EnumConstantImpl constant;

    @Override
    public TypeQualifierImpl getQualifier() {
        if (!(getParentNode() instanceof SwitchBlockImpl) && getEnclosingScope().requiresQualifier(this)) {
            forceQualifier();
        }

        return qualifier.get();
    }

    private void setQualifier(final TypeQualifier var) {
        this.qualifier.set((TypeQualifierImpl) var);
    }

    private final ChildNode<TypeQualifierImpl> qualifier = new ChildNode<>(this);
}
