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
import org.xtuml.masl.javagen.ast.types.ReferenceType;
import org.xtuml.masl.javagen.ast.types.WildcardType;

public class WildcardTypeImpl extends TypeImpl implements WildcardType {

    enum Direction {
        EXTENDS, SUPER
    }

    WildcardTypeImpl(final ASTImpl ast) {
        super(ast);
    }

    WildcardTypeImpl(final ASTImpl ast, final java.lang.reflect.WildcardType type) {
        super(ast);
        if (type.getLowerBounds().length > 0) {
            superBound.set((ReferenceTypeImpl) ast.createType(type.getLowerBounds()[0]));
        } else {
            extendsBound.set((ReferenceTypeImpl) ast.createType(type.getUpperBounds()[0]));
        }
    }

    @Override
    public ReferenceTypeImpl getSuperBound() {
        return superBound.get();
    }

    @Override
    public ReferenceTypeImpl getExtendsBound() {
        return extendsBound.get();
    }

    @Override
    public void setExtendsBound(final ReferenceType extendsBound) {
        this.extendsBound.set((ReferenceTypeImpl) extendsBound);
    }

    @Override
    public void setSuperBound(final ReferenceType superBound) {
        this.superBound.set((ReferenceTypeImpl) superBound);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitWildcardType(this);
    }

    private final ChildNode<ReferenceTypeImpl> superBound = new ChildNode<>(this);
    private final ChildNode<ReferenceTypeImpl> extendsBound = new ChildNode<>(this);

    @Override
    public TypeImpl deepCopy() {
        final WildcardTypeImpl result = new WildcardTypeImpl(getAST());
        if (getSuperBound() != null) {
            result.setSuperBound(getSuperBound().deepCopy());
        }
        if (getExtendsBound() != null) {
            result.setExtendsBound(getExtendsBound().deepCopy());
        }
        return result;
    }

}
