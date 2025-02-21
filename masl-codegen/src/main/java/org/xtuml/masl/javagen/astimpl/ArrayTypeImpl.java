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
import org.xtuml.masl.javagen.ast.types.Type;

public class ArrayTypeImpl extends ReferenceTypeImpl implements org.xtuml.masl.javagen.ast.types.ArrayType {

    ArrayTypeImpl(final ASTImpl ast, final Type elementType) {
        super(ast);
        setElementType(elementType);
    }

    @Override
    public void setElementType(final Type elementType) {
        this.elementType.set((TypeImpl) elementType);
    }

    @Override
    public TypeImpl getElementType() {
        return elementType.get();
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitArrayType(this);
    }

    private final ChildNode<TypeImpl> elementType = new ChildNode<>(this);

    @Override
    public ArrayTypeImpl deepCopy() {
        return new ArrayTypeImpl(getAST(), elementType.get().deepCopy());
    }
}
