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
import org.xtuml.masl.javagen.ast.types.PrimitiveType;

public class PrimitiveTypeImpl extends TypeImpl implements PrimitiveType {

    PrimitiveTypeImpl(final ASTImpl ast, final Tag tag) {
        super(ast);
        this.tag = tag;
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitPrimitiveType(this);
    }

    @Override
    public Tag getTag() {
        return tag;
    }

    private final Tag tag;

    @Override
    public ClassLiteralImpl clazz() {
        return getAST().createClassLiteral(this);
    }

    @Override
    public PrimitiveTypeImpl deepCopy() {
        return new PrimitiveTypeImpl(getAST(), tag);
    }

}
