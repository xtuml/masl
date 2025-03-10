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
import org.xtuml.masl.javagen.ast.def.CompilationUnit;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

class PackageImpl extends ASTNodeImpl implements org.xtuml.masl.javagen.ast.def.Package {

    PackageImpl(final ASTImpl ast, final String name) {
        super(ast);
        this.name = name;
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitPackage(this);
    }

    @Override
    public CompilationUnitImpl addCompilationUnit(final CompilationUnit compilationUnit) {
        compilationUnits.add((CompilationUnitImpl) compilationUnit);
        return (CompilationUnitImpl) compilationUnit;
    }

    @Override
    public Collection<? extends CompilationUnitImpl> getCompilationUnits() {
        return Collections.unmodifiableList(compilationUnits);
    }

    @Override
    public String getName() {
        return name;
    }

    String toPathString() {
        return name.replace('.', System.getProperty("file.separator").charAt(0));
    }

    @Override
    public String toString() {
        return name;
    }

    boolean containsPublicTypeNamed(final String name) {
        for (final CompilationUnitImpl cu : compilationUnits) {
            if (cu.getName().equals(name) && cu.containsPublicTypeNamed(name)) {
                return true;
            }
        }
        return false;
    }

    boolean containsTypeNamed(final String name) {
        for (final CompilationUnitImpl cu : compilationUnits) {
            if (cu.getName().equals(name) && cu.containsTypeNamed(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public TypeDeclarationImpl addTypeDeclaration(final String name) {
        return addCompilationUnit(name).addTypeDeclaration(getAST().createTypeDeclaration(name));
    }

    @Override
    public CompilationUnitImpl addCompilationUnit(final String name) {
        return addCompilationUnit(getAST().createCompilationUnit(name));
    }

    private final List<CompilationUnitImpl> compilationUnits = new ChildNodeList<>(this);

    private final String name;

}
