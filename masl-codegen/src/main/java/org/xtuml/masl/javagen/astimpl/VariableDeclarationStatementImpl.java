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
import org.xtuml.masl.javagen.ast.code.LocalVariable;
import org.xtuml.masl.javagen.ast.code.LocalVariableDeclaration;

public class VariableDeclarationStatementImpl extends StatementImpl implements LocalVariableDeclaration {

    VariableDeclarationStatementImpl(final ASTImpl ast, final LocalVariableImpl declaration) {
        super(ast);
        this.declaration.set(declaration);

    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitLocalVariableDeclaration(this);
    }

    private final ChildNode<LocalVariableImpl> declaration = new ChildNode<>(this);

    @Override
    public LocalVariableImpl getLocalVariable() {
        return declaration.get();
    }

    @Override
    public void setLocalVariable(final LocalVariable declaration) {
        this.declaration.set((LocalVariableImpl) declaration);
    }

}
