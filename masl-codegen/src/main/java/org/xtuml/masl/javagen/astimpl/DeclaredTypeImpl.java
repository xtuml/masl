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
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.types.DeclaredType;
import org.xtuml.masl.javagen.ast.types.Type;

import java.util.Collections;
import java.util.List;

public class DeclaredTypeImpl extends ReferenceTypeImpl implements DeclaredType {

    DeclaredTypeImpl(final ASTImpl ast, final TypeDeclarationImpl typeDeclaration, final Type... args) {
        super(ast);
        this.typeDeclaration = typeDeclaration;
        for (final Type arg : args) {
            addTypeArgument(arg);
        }
    }

    @Override
    public void addTypeArgument(final Type argument) {
        typeArguments.add((TypeImpl) argument);
    }

    @Override
    public List<TypeImpl> getTypeArguments() {
        return Collections.unmodifiableList(typeArguments);
    }

    @Override
    public TypeDeclarationImpl getTypeDeclaration() {
        return typeDeclaration;
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitDeclaredType(this);
    }

    private final TypeDeclarationImpl typeDeclaration;
    private final List<TypeImpl> typeArguments = new ChildNodeList<>(this);

    @Override
    public QualifierImpl getQualifier() {
        if (qualifier.get() == null && getEnclosingScope().requiresQualifier(typeDeclaration)) {
            forceQualifier();
        }

        return qualifier.get();
    }

    private final ChildNode<QualifierImpl> qualifier = new ChildNode<>(this);

    @Override
    public void forceQualifier() {
        if (typeDeclaration.getDeclaringCompilationUnit() != null) {
            qualifier.set(new PackageQualifierImpl(getAST(), typeDeclaration.getEnclosingPackage()));
        } else {
            qualifier.set(new TypeQualifierImpl(getAST(), typeDeclaration.getDeclaringType()));
        }
    }

    @Override
    public NewInstanceImpl newInstance(final Expression... args) {
        return getAST().createNewInstance(this, args);
    }

    @Override
    public DeclaredTypeImpl deepCopy() {
        final DeclaredTypeImpl result = new DeclaredTypeImpl(getAST(), typeDeclaration);
        for (final TypeImpl arg : typeArguments) {
            result.addTypeArgument(arg.deepCopy());
        }
        return result;
    }

}
