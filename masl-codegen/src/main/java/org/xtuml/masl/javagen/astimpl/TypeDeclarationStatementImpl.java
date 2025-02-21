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
import org.xtuml.masl.javagen.ast.code.TypeDeclarationStatement;
import org.xtuml.masl.javagen.ast.def.TypeDeclaration;

public class TypeDeclarationStatementImpl extends StatementImpl implements TypeDeclarationStatement {

    TypeDeclarationStatementImpl(final ASTImpl ast, final TypeDeclarationImpl declaration) {
        super(ast);
        setTypeDeclaration(declaration);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitTypeDeclarationStatement(this);
    }

    @Override
    public TypeDeclarationImpl getTypeDeclaration() {
        return typeDeclaration.get();
    }

    @Override
    public void setTypeDeclaration(final TypeDeclaration typeDeclaration) {
        this.typeDeclaration.set((TypeDeclarationImpl) typeDeclaration);
    }

    private final ChildNode<TypeDeclarationImpl> typeDeclaration = new ChildNode<>(this);

}
