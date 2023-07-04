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
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitPackage(this, p);
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

    private final List<CompilationUnitImpl> compilationUnits = new ChildNodeList<CompilationUnitImpl>(this);

    private final String name;

}
