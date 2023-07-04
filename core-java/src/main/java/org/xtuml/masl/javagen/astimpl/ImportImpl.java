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
import org.xtuml.masl.javagen.ast.def.Import;
import org.xtuml.masl.javagen.ast.def.Package;
import org.xtuml.masl.javagen.ast.def.TypeDeclaration;

public class ImportImpl extends ASTNodeImpl implements Import {

    enum Mode {
        OnDemandImport, SingleImport
    }

    enum Scope {
        TypeImport, StaticImport
    }

    static ImportImpl createSingleStaticImport(final ASTImpl ast,
                                               final TypeDeclaration typeDeclaration,
                                               final String name) {
        return new ImportImpl(ast, typeDeclaration, name, Scope.StaticImport, Mode.SingleImport);
    }

    static ImportImpl createSingleTypeImport(final ASTImpl ast, final TypeDeclaration typeDeclaration) {
        return new ImportImpl(ast, typeDeclaration, null, Scope.TypeImport, Mode.SingleImport);
    }

    static ImportImpl createStaticImportOnDemand(final ASTImpl ast, final TypeDeclaration typeDeclaration) {
        return new ImportImpl(ast, typeDeclaration, null, Scope.StaticImport, Mode.OnDemandImport);
    }

    static ImportImpl createTypeImportOnDemand(final ASTImpl ast, final Package parentPackage) {
        return new ImportImpl(ast, parentPackage);
    }

    static ImportImpl createTypeImportOnDemand(final ASTImpl ast, final TypeDeclaration typeDeclaration) {
        return new ImportImpl(ast, typeDeclaration, null, Scope.TypeImport, Mode.OnDemandImport);
    }

    ImportImpl(final ASTImpl ast, final Package parentPackage) {
        super(ast);
        this.parentPackage = (PackageImpl) parentPackage;
        this.typeDeclaration = null;
        this.importedName = null;
        this.scope = Scope.TypeImport;
        this.mode = Mode.OnDemandImport;
    }

    ImportImpl(final ASTImpl ast,
               final TypeDeclaration typeDeclaration,
               final String importedName,
               final Scope scope,
               final Mode mode) {
        super(ast);
        this.parentPackage = null;
        this.typeDeclaration = (TypeDeclarationImpl) typeDeclaration;
        this.importedName = importedName;
        this.scope = scope;
        this.mode = mode;
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitImport(this);
    }

    @Override
    public String getImportedName() {
        return importedName;
    }

    @Override
    public PackageImpl getParentPackage() {
        return parentPackage;
    }

    @Override
    public TypeDeclarationImpl getTypeDeclaration() {
        return typeDeclaration;
    }

    public boolean imports(final FieldImpl fieldDeclaration) {
        return isStatic() &&
               fieldDeclaration.getModifiers().isStatic() &&
               fieldDeclaration.getModifiers().isPublic() &&
               fieldDeclaration.getDeclaringType() == typeDeclaration &&
               (isOnDemand() || fieldDeclaration.getName().equals(importedName));

    }

    public boolean imports(final EnumConstantImpl constantDeclaration) {
        return isStatic() &&
               constantDeclaration.getDeclaringType() == typeDeclaration &&
               (isOnDemand() || constantDeclaration.getName().equals(importedName));

    }

    public boolean imports(final MethodImpl methodDeclaration) {
        return isStatic() &&
               methodDeclaration.getModifiers().isStatic() &&
               methodDeclaration.getModifiers().isPublic() &&
               methodDeclaration.getDeclaringType() == typeDeclaration &&
               (isOnDemand() || methodDeclaration.getName().equals(importedName));
    }

    public boolean imports(final TypeDeclarationImpl typeDeclaration) {
        if (isStatic()) {
            if (!typeDeclaration.getModifiers().isStatic() || !typeDeclaration.getModifiers().isPublic()) {
                return false;
            }
            if (isOnDemand()) {
                return typeDeclaration.getDeclaringType() == this.typeDeclaration &&
                       typeDeclaration.getName().equals(importedName);
            } else {
                return this.typeDeclaration == typeDeclaration;
            }
        } else {
            if (isOnDemand()) {
                return typeDeclaration.getModifiers().isPublic() &&
                       typeDeclaration.getDeclaringType() == this.typeDeclaration &&
                       typeDeclaration.getEnclosingPackage() == parentPackage;
            } else {
                return this.typeDeclaration == typeDeclaration;
            }
        }
    }

    public boolean importsFieldNamed(final String name) {
        return isStatic() &&
               (isOnDemand() || name.equals(importedName)) &&
               typeDeclaration.getTypeBody().containsStaticPublicFieldNamed(name);
    }

    public boolean importsMethodNamed(final String name) {
        return isStatic() &&
               (isOnDemand() || name.equals(importedName)) &&
               typeDeclaration.getTypeBody().containsStaticPublicMethodNamed(name);
    }

    public boolean importsTypeNamed(final String name) {
        if (!(isOnDemand() || name.equals(importedName))) {
            return false;
        }
        if (isStatic()) {
            return typeDeclaration.getTypeBody().containsStaticPublicTypeNamed(name);
        } else {
            if (parentPackage == null) {
                return typeDeclaration.getTypeBody().containsPublicTypeNamed(name);
            } else {
                return parentPackage.containsPublicTypeNamed(name);
            }
        }
    }

    @Override
    public boolean isOnDemand() {
        return mode == Mode.OnDemandImport;
    }

    @Override
    public boolean isSingle() {
        return mode == Mode.SingleImport;
    }

    @Override
    public boolean isStatic() {
        return scope == Scope.StaticImport;
    }

    private final TypeDeclarationImpl typeDeclaration;
    private final PackageImpl parentPackage;
    private final String importedName;
    private final Mode mode;
    private final Scope scope;

}
