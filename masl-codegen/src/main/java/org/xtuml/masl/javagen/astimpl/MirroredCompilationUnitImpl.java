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

import org.xtuml.masl.javagen.ast.def.TypeDeclaration;

class MirroredCompilationUnitImpl extends CompilationUnitImpl {

    MirroredCompilationUnitImpl(final ASTImpl ast, final java.lang.Class<?> clazz) {
        super(ast, clazz.getSimpleName());
        super.addTypeDeclaration(new MirroredTypeDeclarationImpl(getAST(), clazz));
    }

    public MirroredTypeDeclarationImpl getTypeDeclaration() {
        return (MirroredTypeDeclarationImpl) super.getTypeDeclarations().get(0);
    }

    @Override
    public TypeDeclarationImpl addTypeDeclaration(final TypeDeclaration typeDeclaration) {
        throw new UnsupportedOperationException("Mirrored Compilation Unit");
    }

    @Override
    boolean containsPublicTypeNamed(final String name) {
        return getName().equals(name) && getTypeDeclaration().getModifiers().isPublic();
    }

    @Override
    boolean containsTypeNamed(final String name) {
        return getName().equals(name);
    }
}
