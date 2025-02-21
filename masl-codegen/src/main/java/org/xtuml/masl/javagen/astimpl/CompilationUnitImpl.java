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
import org.xtuml.masl.javagen.ast.def.Import;
import org.xtuml.masl.javagen.ast.def.TypeDeclaration;

import java.util.Collections;
import java.util.List;

class CompilationUnitImpl extends ASTNodeImpl implements CompilationUnit, Scoped {

    private final class CUScope extends org.xtuml.masl.javagen.astimpl.Scope {

        private final ImportImpl javaLangImport;

        CUScope() {
            super(CompilationUnitImpl.this);
            javaLangImport =
                    ImportImpl.createTypeImportOnDemand(getAST(), getAST().getPackage(Object.class.getPackage()));
        }

        @Override
        protected boolean requiresQualifier(final Scope baseScope,
                                            final FieldAccessImpl fieldAccess,
                                            boolean visible,
                                            boolean shadowed) {
            final FieldImpl fieldDeclaration = fieldAccess.getField();
            // Check single type imports
            for (final ImportImpl importDeclaration : getImportDeclarations()) {
                if (importDeclaration.isSingle()) {
                    if (importDeclaration.imports(fieldDeclaration)) {
                        visible = true;
                    } else if (importDeclaration.importsFieldNamed(fieldDeclaration.getName())) {
                        shadowed = true;
                    }
                }
            }

            if (!visible) {
                // Check on demand imports
                for (final ImportImpl importDeclaration : importDeclarations) {
                    if (importDeclaration.imports(fieldDeclaration)) {
                        visible = true;
                    } else if (importDeclaration.importsFieldNamed(fieldDeclaration.getName())) {
                        shadowed = true;
                    }

                }
            }
            return super.requiresQualifier(baseScope, fieldAccess, visible, shadowed);
        }

        @Override
        protected boolean requiresQualifier(final Scope baseScope,
                                            final EnumConstantAccessImpl enumAccess,
                                            boolean visible,
                                            boolean shadowed) {
            final EnumConstantImpl constantDeclaration = enumAccess.getConstant();
            // Check single type imports
            for (final ImportImpl importDeclaration : getImportDeclarations()) {
                if (importDeclaration.isSingle()) {
                    if (importDeclaration.imports(constantDeclaration)) {
                        visible = true;
                    } else if (importDeclaration.importsFieldNamed(constantDeclaration.getName())) {
                        shadowed = true;
                    }
                }
            }

            if (!visible) {
                // Check on demand imports
                for (final ImportImpl importDeclaration : importDeclarations) {
                    if (importDeclaration.imports(constantDeclaration)) {
                        visible = true;
                    } else if (importDeclaration.importsFieldNamed(constantDeclaration.getName())) {
                        shadowed = true;
                    }

                }
            }
            return super.requiresQualifier(baseScope, enumAccess, visible, shadowed);
        }

        @Override
        protected boolean requiresQualifier(final Scope baseScope,
                                            final MethodInvocationImpl methodCall,
                                            boolean visible,
                                            boolean shadowed) {
            final MethodImpl methodDeclaration = methodCall.getMethod();
            // Check single type imports
            for (final ImportImpl importDeclaration : getImportDeclarations()) {
                if (importDeclaration.isSingle()) {
                    if (importDeclaration.imports(methodDeclaration)) {
                        visible = true;
                    } else if (importDeclaration.importsMethodNamed(methodDeclaration.getName())) {
                        shadowed = true;
                    }
                }
            }

            if (!visible) {
                // Check on demand imports
                for (final ImportImpl importDeclaration : importDeclarations) {
                    if (importDeclaration.imports(methodDeclaration)) {
                        visible = true;
                    } else if (importDeclaration.importsMethodNamed(methodDeclaration.getName())) {
                        shadowed = true;
                    }

                }
            }
            return super.requiresQualifier(baseScope, methodCall, visible, shadowed);
        }

        @Override
        protected boolean requiresQualifier(final Scope baseScope,
                                            final TypeDeclarationImpl typeDeclaration,
                                            boolean visible,
                                            boolean shadowed) {
            // Check single type imports
            for (final ImportImpl importDeclaration : getImportDeclarations()) {
                if (importDeclaration.isSingle()) {
                    if (importDeclaration.imports(typeDeclaration)) {
                        visible = true;
                    } else if (importDeclaration.importsTypeNamed(typeDeclaration.getName())) {
                        shadowed = true;
                    }
                }
            }

            if (!visible) {
                if (typeDeclaration.getEnclosingCompilationUnit() == CompilationUnitImpl.this) {
                    visible = true;
                } else if (containsTypeNamed(typeDeclaration.getName())) {
                    shadowed = true;
                }
            }

            if (!visible) {
                // Check on demand imports
                for (final ImportImpl importDeclaration : importDeclarations) {
                    if (importDeclaration.imports(typeDeclaration)) {
                        visible = true;
                    } else if (importDeclaration.importsTypeNamed(typeDeclaration.getName())) {
                        shadowed = true;
                    }

                }
            }

            if (!visible) {
                if (typeDeclaration.getDeclaringCompilationUnit() != null &&
                    typeDeclaration.getEnclosingPackage() == getEnclosingPackage()) {
                    visible = true;
                } else if (getEnclosingPackage().containsTypeNamed(typeDeclaration.getName())) {
                    shadowed = true;
                }
            }

            if (!visible) {
                if (javaLangImport.imports(typeDeclaration)) {
                    visible = true;
                } else if (javaLangImport.importsTypeNamed(typeDeclaration.getName())) {
                    shadowed = true;
                }
            }

            return super.requiresQualifier(baseScope, typeDeclaration, visible, shadowed);
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitCompilationUnit(this);
    }

    @Override
    public TypeDeclarationImpl addTypeDeclaration(final TypeDeclaration declaration) {
        typeDeclarations.add((TypeDeclarationImpl) declaration);
        return (TypeDeclarationImpl) declaration;
    }

    @Override
    public String getFileName() {
        return (getEnclosingPackage() == null ? "" : getEnclosingPackage().toPathString() + "/") + name + extension;
    }

    @Override
    public List<ImportImpl> getImportDeclarations() {
        return Collections.unmodifiableList(importDeclarations);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PackageImpl getPackage() {
        return (PackageImpl) getParentNode();
    }

    @Override
    public CUScope getScope() {
        return scope;
    }

    @Override
    public List<TypeDeclarationImpl> getTypeDeclarations() {
        return Collections.unmodifiableList(typeDeclarations);
    }

    @Override
    public String toString() {
        return getFileName();
    }

    boolean containsPublicTypeNamed(final String name) {
        for (final TypeDeclaration decl : typeDeclarations) {
            if (decl.getName().equals(name) && decl.getModifiers().isPublic()) {
                return true;
            }
        }
        return false;
    }

    boolean containsTypeNamed(final String name) {
        for (final TypeDeclaration decl : typeDeclarations) {
            if (decl.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private final CUScope scope = new CUScope();

    private static final String extension = ".java";

    CompilationUnitImpl(final ASTImpl ast, final String name) {
        super(ast);

        this.name = name;
    }

    @Override
    public ImportImpl addImportDeclaration(final Import declaration) {
        importDeclarations.add((ImportImpl) declaration);
        return (ImportImpl) declaration;
    }

    @Override
    public TypeDeclarationImpl createTypeDeclaration(final String name) {
        final TypeDeclarationImpl result = getAST().createTypeDeclaration(name);
        addTypeDeclaration(result);
        return result;
    }

    private final String name;

    private final List<TypeDeclarationImpl> typeDeclarations = new ChildNodeList<>(this);

    private final List<ImportImpl> importDeclarations = new ChildNodeList<>(this);

}
